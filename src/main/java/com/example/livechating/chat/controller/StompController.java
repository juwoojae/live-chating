package com.example.livechating.chat.controller;

import com.example.livechating.chat.dto.ChatMessageDto;
import com.example.livechating.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;

    private final ChatService chatService;
//   방법 1.  MessageMapping(수신) 과  sendTo(topic 에 메세지 전달) 한꺼번에 처리

//    @MessageMapping("/{roomId}")  //클라이언트에서 특정 publish/roomId 형태로 메세지를 발행시 MessageMapping 수신
//    @SendTo("/topic/{roomId}") //해당 roomId 에 메세지를 발행하여 구독중인 클라이언트에게 메세지 전송(return)
//    // DestinationVariable : @MessageMapping 어노테이션으로 정의된 WebSocket Controller 내에서만 사용
//    public String sendMessage(@DestinationVariable Long roomId, String message) {
//        log.info("message: {}", message);
//        return message;
//    }

//    방법 2. MessageMapping 어노테이션만 활용.

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto) {

        log.info("message: {}", chatMessageDto.getMessage());
        chatService.saveMessage(roomId, chatMessageDto);   //메세지 db 에 저장하기
        messageTemplate.convertAndSend("/topic/" + roomId, chatMessageDto);  // @SendTo("/topic/{roomId}") 와 완전히 같은뜻임
    }
}
