package com.example.DOTORY.chat.api.dto.response;

import com.example.DOTORY.chat.domain.entity.ChatMessage;
import com.example.DOTORY.chat.domain.entity.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatMessageResponseDto {
    private Long roomId;
    private String senderName;
    private String content;
    private MessageType messageType;
    private LocalDateTime timestamp;

    public static ChatMessageResponseDto from(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .roomId(chatMessage.getChatRoom().getId())
                .senderName(chatMessage.getSender() != null ? chatMessage.getSender().getUserNickname() : "System") // System for JOIN/LEAVE
                .content(chatMessage.getContent())
                .messageType(chatMessage.getMessageType())
                .timestamp(chatMessage.getTimestamp())
                .build();
    }
}
