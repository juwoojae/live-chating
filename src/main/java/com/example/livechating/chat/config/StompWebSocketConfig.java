package com.example.livechating.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 메세지 브로커가 있으면 stomp 이다
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public  void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOrigins("http://localhost:3000")
                // ws://가 아닌 http:// 엔드포인트를 사용할수 있게 해주는 sockJs 라이브러리를 통한 요청을 허용하는 설정.
                .withSockJS();
                //여기서 주의할점. http 를 처음 요청할때는 허용하지만, 3way handshake 이후에는 바로 웹소켓 프로토콜로 바뀜.

    }

    /**
     * 메시지가 어디로 가야하는지 라우팅 규칙을 정하는 메서드
     */
    @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
//      /publish/1    메세지 발행 조건
//      /publish로 시작하는 url 패턴으로 메시지가 발행 되면 @Controller 객체의 @MessaMapping이 붙은 메서드로 라우팅된다.
            registry.setApplicationDestinationPrefixes("/publish");
//      /topic/1 형태로 메세지를 수신(subscribe) 해야 함을 설정 room (구독 하기)
        registry.enableSimpleBroker("/topic");
    }

    /**
     * 웹소켓 요청(connect, subscribe, disconnect) 등의 요청 시에는 http header 등 http 메세지를 넣어올수 있고,
     * 이를 interceptor 를 통해 가로채 토큰등을 검증할수 있다.
     * @param registration
     */

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

}

