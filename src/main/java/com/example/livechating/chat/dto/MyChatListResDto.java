package com.example.livechating.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyChatListResDto {

	private Long roomId;
	private String roomName;
	private String isGroupChat;
	private Long unReadCound;
}
