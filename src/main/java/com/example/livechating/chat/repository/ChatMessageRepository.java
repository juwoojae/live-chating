package com.example.livechating.chat.repository;

import com.example.livechating.chat.domain.ChatMessage;
import com.example.livechating.chat.domain.ChatRoom;
import com.example.livechating.chat.dto.ChatMessageDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomOrderByCreatedTimeAsc(ChatRoom chatRoom);
}
