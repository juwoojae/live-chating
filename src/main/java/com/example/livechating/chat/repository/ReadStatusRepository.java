package com.example.livechating.chat.repository;

import java.util.List;

import com.example.livechating.chat.domain.ChatParticipant;
import com.example.livechating.chat.domain.ChatRoom;
import com.example.livechating.chat.domain.ReadStatus;
import com.example.livechating.member.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, Long> {

	//message 의 의존관계를 타고 내려가서 chatRoom 으로 조회
	List<ReadStatus> findByMemberAndChatMessage_ChatRoom(Member member ,ChatRoom chatRoom);   //이거 순서 ㄹㅇ 개중요함

	Long countByChatMessage_ChatRoomAndMemberAndIsReadFalse(ChatRoom chatRoom, Member member);
}
