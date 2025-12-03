package com.example.livechating.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  ChatRoomListResDto {

    private Long roomId;
    private String roomName;
}
