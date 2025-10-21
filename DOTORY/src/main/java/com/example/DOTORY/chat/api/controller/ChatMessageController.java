package com.example.DOTORY.chat.api.controller;

import com.example.DOTORY.chat.api.dto.request.ChatMessageRequestDto;
import com.example.DOTORY.chat.api.dto.response.ChatMessageResponseDto;
import com.example.DOTORY.chat.application.ChatService;
import com.example.DOTORY.chat.domain.entity.MessageType;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequestDto chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        chatMessage.setMessageType(MessageType.CHAT);
        ChatMessageResponseDto savedMessage = chatService.saveChatMessage(chatMessage, chatMessage.getSenderName());
        messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getRoomId(), savedMessage);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageRequestDto chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username and roomId in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderName());
        headerAccessor.getSessionAttributes().put("roomId", chatMessage.getRoomId());

        // Save and broadcast JOIN message (handled by WebSocketEventListener now)
        // The event listener will pick up the session attributes and send the JOIN message
    }
}
