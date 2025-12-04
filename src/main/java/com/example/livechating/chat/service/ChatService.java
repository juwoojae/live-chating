package com.example.livechating.chat.service;

import com.example.livechating.chat.domain.ChatMessage;
import com.example.livechating.chat.domain.ChatParticipant;
import com.example.livechating.chat.domain.ChatRoom;
import com.example.livechating.chat.domain.ReadStatus;
import com.example.livechating.chat.dto.ChatMessageDto;
import com.example.livechating.chat.dto.ChatRoomListResDto;
import com.example.livechating.chat.dto.MyChatListResDto;
import com.example.livechating.chat.repository.ChatMessageRepository;
import com.example.livechating.chat.repository.ChatParticipantRepositorty;
import com.example.livechating.chat.repository.ChatRoomRepository;
import com.example.livechating.chat.repository.ReadStatusRepository;
import com.example.livechating.member.domain.Member;
import com.example.livechating.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j(topic = "ChatService")
@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    private final ChatParticipantRepositorty chatParticipantRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final ReadStatusRepository readStatusRepository;

    private final MemberRepository memberRepository;

    public void saveMessage(Long roomId, ChatMessageDto chatMessageDto) {
        //      채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new EntityNotFoundException("해당 room 이 없습니다."));
        //      보낸 사람 조회
        Member sender = memberRepository.findByEmail(chatMessageDto.getSenderEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 member 이 없습니다."));
        //      메세지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)  //누가 보낸 메세지인지
                .content(chatMessageDto.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);
        //      사용자별로 읽음 여부 저장
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for (ChatParticipant c : chatParticipants) {
            ReadStatus readStatus = ReadStatus.builder()
                    .member(c.getMember())
                    .chatMessage(chatMessage)
                    .isRead(c.getMember().equals(sender))   //보낸 사람에 한해서는 무조건 true
                    .build();
            readStatusRepository.save(readStatus);
        }
    }

    /**
     * ChatRoom 객체 생성
     * ChatParticipant 객체 생성
     */
    public void createGroupRoom(String chatRoomName) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName(); //@AuthenticationPricipal 이랑 같은것
        log.info("email :{}", email);
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Member 를 찾을수 없습니다"));

        //      채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomName)
                .isGroupChat("Y")
                .build();
        chatRoomRepository.save(chatRoom);
        //      채팅 참여자로 개설자를 추가
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatRoomListResDto> getGroupchatRooms() {

        List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");
        List<ChatRoomListResDto> results = new ArrayList<>();
        for (ChatRoom c : chatRooms) {
            ChatRoomListResDto chatRoomListResDto = ChatRoomListResDto
                    .builder()
                    .roomId(c.getId())
                    .roomName(c.getName())
                    .build();
            results.add(chatRoomListResDto);
        }
        return results;
    }

    /**
     * 그룹 채팅방 참여
     * 그룹 채팅인 경우에는 참여 가능. 1대1 채팅이면 참여 불가능
     */
    public void addParticipantToGroupChat(Long roomId) {
        //      채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new EntityNotFoundException("room cannot be found."));
        //      member 조회
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("member cannot be found."));

        if(chatRoom.getIsGroupChat().equals("N")) {
            throw new IllegalArgumentException("그룹채팅이 아닙니다.");
        }

        //      이미 참여자인지 검증
        Optional<ChatParticipant> participant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member);
        if (!participant.isPresent()) {
            addParticipantToRoom(chatRoom, member);
        }

    }

    public List<ChatMessageDto> getChatHistory(Long roomId) {
        //      내가 해당 채팅방의 참여자가 아닐 경우 에러  A,B 참여중인 단톡방에 C 의 요청이 들어오면 안됨
        //      해당 단톡방의 chatParticipant 확인하기
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName(); //@AuthenticationPricipal 이랑 같은것
        log.info("email :{}", email);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new EntityNotFoundException("Member 를 찾을수 없습니다"));

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Member 를 찾을수 없습니다"));

        List<ChatParticipant> chatParticipants = chatRoom.getChatParticipants();
        boolean check = false;
        for (ChatParticipant c : chatParticipants) {
            if (c.getMember().equals(member)) {
                check = true;
            }
        }
        if (!check) {
            throw new IllegalArgumentException("본인이 속하지 않는 채팅방 입니다.");
        }
        //      특정 room 에 대한 message 조회
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom);
        List<ChatMessageDto> results = new ArrayList<>();  //반횐 해야할 리스트
        for (ChatMessage c : messages) {
            ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                    .message(c.getContent())
                    .senderEmail(c.getMember().getEmail())
                    .build();
            results.add(chatMessageDto);
        }
        return results;
    }

    public void messageRead(Long roomId) {
        //      채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new EntityNotFoundException("room cannot be found."));
        //      member 조회
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("member cannot be found."));
        List<ReadStatus> readStatuses = readStatusRepository.findByMemberAndChatMessage_ChatRoom(member, chatRoom);

        for (ReadStatus r : readStatuses) {
            r.updateIsRead(true);  //더티 체킹 읽음 상태로 바꾸기
        }
    }

    /**
     * 내 채팅방 목록 조회
     *
     * @return
     */
    public List<MyChatListResDto> getMyChatRooms() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("member cannot be found."));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMember(member);
        List<MyChatListResDto> results = new ArrayList<>();

        for (ChatParticipant c : chatParticipants) {
            Long count = readStatusRepository.countByChatMessage_ChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);
            MyChatListResDto dto = MyChatListResDto.builder()
                    .roomId(c.getChatRoom().getId())
                    .roomName(c.getChatRoom().getName())
                    .isGroupChat(c.getChatRoom().getIsGroupChat())
                    .unReadCount(count)
                    .build();
            results.add(dto);
        }
        return results;
    }

    /**
     * 1. 참여자 객체 삭제 하기
     * 2. 모두가 나간 경우 모든 엔티티 삭제하기
     *
     * @param roomId
     */
    public void leaveGroupChatRoom(Long roomId) {
        //      채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new EntityNotFoundException("room cannot be found."));
        //      member 조회
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("member cannot be found."));
        if (chatRoom.getIsGroupChat().equals("N")) {
            throw new IllegalArgumentException("단체 체팅방이 아닙니다.");
        }
        ChatParticipant c = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member).orElseThrow(
                () -> new EntityNotFoundException("Participant cannot be found."));
        chatParticipantRepository.delete(c);

        //모두가 나간경우 모든 엔티티 삭제하기
        List<ChatParticipant> chatPartipants = chatParticipantRepository.findByChatRoom(chatRoom);
        if (chatPartipants.isEmpty()) {
            chatRoomRepository.delete(chatRoom);
        }

    }

    /**
     * 나와 otherMemberId 의 1:1 채팅방 만들기
     * 1. 이미 방이 있는 상태라면 roomId 만 리턴
     * 2. 없다면 ChatRoom 만든 후에
     */
    public Long getOrCreatePrivateRoom(Long otherMemberId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("member cannot be found."));
        Member otherMember = memberRepository.findById(otherMemberId).orElseThrow(
                () -> new EntityNotFoundException("orderMember cannot be found."));
        // 나와 상대방이 1:1 채팅에 이미 참석하고 있다면 해당 roomId return
        Optional<ChatRoom> chatRoom = chatParticipantRepository.findExistingPrivateRoom(member.getId(), otherMember.getId());
        if(chatRoom.isPresent()) {
            return chatRoom.get().getId();
        }
        // 만약에 1:1 채팅방이 없을 영우 기존 채팅방 개설
        ChatRoom newRoom = ChatRoom.builder()
                .isGroupChat("N")
                .name(member.getName() + "-" + otherMember.getName())
                .build();
        chatRoomRepository.save(newRoom);
        // 두사람 모두 참여자로 새롭게 추가하기
        addParticipantToRoom(newRoom, member);
        addParticipantToRoom(newRoom, otherMember);

        return newRoom.getId();
    }


    //      ChatParticipant  객체 생성후 저장
    private void addParticipantToRoom(ChatRoom chatRoom, Member member) {
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

}
