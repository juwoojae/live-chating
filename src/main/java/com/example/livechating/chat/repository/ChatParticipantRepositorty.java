package com.example.livechating.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.livechating.chat.domain.ChatParticipant;
import com.example.livechating.chat.domain.ChatRoom;
import com.example.livechating.member.domain.Member;

public interface ChatParticipantRepositorty extends JpaRepository<ChatParticipant, Long> {

	List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

	Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

	List<ChatParticipant> findAllByMember(Member member);
}
