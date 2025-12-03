package com.example.livechating.chat.controller;

import com.example.livechating.chat.dto.ChatRoomListResDto;
import com.example.livechating.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 그룹 채팅방 개설 / 새로운 리소스 를 생성해서 서버의 상태를 변경, db 에 insert 쿼리가 나가므로 post 가 맞다
    @PostMapping("/room/group/create")
    public ResponseEntity<?> createGroupRoom(@RequestParam String roomName){
        chatService.createGroupRoom(roomName);
        return ResponseEntity.ok().build();
    }

    // 그룹채팅 목록 조회
    @GetMapping("/room/group/list")
    public ResponseEntity<?> getGroupRooms(){
        List<ChatRoomListResDto> chatRooms = chatService.getGroupchatRooms();
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    // 그룹채팅방 참여
    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<?> joinGroupChatRoom(@PathVariable Long roomId){
        System.out.println("요청");
        chatService.addParticipantToGroupChat(roomId);
        return ResponseEntity.ok().build();
    }
}
