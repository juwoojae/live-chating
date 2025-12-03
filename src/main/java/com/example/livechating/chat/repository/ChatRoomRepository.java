package com.example.livechating.chat.repository;

import com.example.livechating.chat.domain.ChatParticipant;
import com.example.livechating.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByIsGroupChat(String isGroupChat); // 채널 room 중에 isGroupChat 으로 리스트 조회하기
}
