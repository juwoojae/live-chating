package com.example.livechating.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.livechating.chat.domain.ChatParticipant;
import com.example.livechating.chat.domain.ChatRoom;
import com.example.livechating.member.domain.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatParticipantRepositorty extends JpaRepository<ChatParticipant, Long> {

	List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

	Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

	List<ChatParticipant> findAllByMember(Member member);

    /**
     * JPQL
     * ON 은 JOIN 할때 두 테이블을 어떤 조건으로 연결할지 정하는 키워드.
     */
    @Query("SELECT cp1.chatRoom FROM ChatParticipant cp1 JOIN ChatParticipant cp2 ON cp1.chatRoom.id = cp2.chatRoom.id WHERE cp1.member.id = :myId AND cp2.member.id = :otherMemberId AND cp1.chatRoom.isGroupChat = 'N'")
    Optional<ChatRoom> findExistingPrivateRoom(@Param("myId") Long myId, @Param("otherMemberId") Long otherMemberId);



}
