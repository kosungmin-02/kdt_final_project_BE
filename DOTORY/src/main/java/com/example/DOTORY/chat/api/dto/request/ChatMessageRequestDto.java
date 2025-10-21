package com.example.DOTORY.chat.api.dto.request;

import com.example.DOTORY.chat.domain.entity.MessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequestDto {
    private Long roomId;
    private String senderName;
    private String content;
    private MessageType messageType;
}
