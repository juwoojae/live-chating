//package com.example.livechating.chat.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.*;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
////connect 로 웹소켓 연결 요청이 들어 왔을때 이를 처리할 클래스
//@Slf4j
//@Component
//public class SimpleWebSocketHandler extends TextWebSocketHandler {
//
//    //WebSocketSession 에는 클라이언트에대한 정보가 있다.
//    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet(); //currentSafe 멀티 스레드환경 처리가능한 set
//    //연결된 직후에 무엇을 할것인지
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//
//         sessions.add(session); //서버의 세션에 저장하기
//         log.info("Connected to {}", session.getId());
//    }
//
//    //사용자에게 메세지를 보내주는 메서드
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//
//        String payload = message.getPayload();
//        log.info("Received text message {}", payload );
//        for(WebSocketSession s : sessions){
//            if(s.isOpen()){
//                s.sendMessage(new TextMessage(payload));
//            }
//        }
//    }
//
//    //연결이 끊긴 직후 무엇을 할것인지
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//
//        sessions.remove(session);
//        log.info("Disconnected from {}", session.getId());
//    }
//
//}
