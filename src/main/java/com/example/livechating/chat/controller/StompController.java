package com.example.livechating.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class StompController {

    @MessageMapping("/{roomId}")  //클라이언트에서 특정 publish/roomId 형태로 메세지를 발행시 MessageMapping 수신
    @SendTo("/topic/{roomId}") //해당 roomId 에 메세지를 발행하여 구독중인 클라이언트에게 메세지 전송(return)
    // DestinationVariable : @MessageMapping 어노테이션으로 정의된 WebSocket Controller 내에서만 사용
    public String sendMessage(@DestinationVariable Long roomId, String message) {
        log.info("message: {}", message);
        return message;
    }
}
