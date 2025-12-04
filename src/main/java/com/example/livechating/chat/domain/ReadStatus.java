package com.example.livechating.chat.domain;

import com.example.livechating.common.domain.BaseTimeEntity;
import com.example.livechating.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ReadStatus extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_id", nullable = false)
    private ChatMessage chatMessage;

    @Column(nullable = false)
    private Boolean isRead;

	// 안 읽음 상태를 읽음 상태로 바꾸는 메서드
    public void updateIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}