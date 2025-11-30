package com.example.livechating.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final SimpleWebSocketHandler simpleWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //     /connect url 로 websocket 연결 요청이 들어오면, 핸들러 클래스가 처리
        registry.addHandler(simpleWebSocketHandler, "/connect")
                 //securityconfig 에서의 http 요청에 대한 예외. 따라서 websocket 프로토콜에 대한 요청에 대해서는 별도의 cors 설정 필요
                .setAllowedOrigins("http://localhost:3000");
    }
}
