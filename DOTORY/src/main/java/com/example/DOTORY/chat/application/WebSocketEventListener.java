package com.example.DOTORY.chat.application;

import com.example.DOTORY.chat.api.dto.request.ChatMessageRequestDto;
import com.example.DOTORY.chat.domain.entity.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");

        if (username != null && roomId != null) {
            log.info("User connected: {} to room {}", username, roomId);
            // Save and broadcast JOIN message
            ChatMessageRequestDto joinMessage = new ChatMessageRequestDto();
            joinMessage.setRoomId(roomId);
            joinMessage.setSenderName("System");
            joinMessage.setContent(username + " 님이 입장했습니다.");
            joinMessage.setMessageType(MessageType.JOIN);

            chatService.saveSystemMessage(roomId, joinMessage.getContent(), MessageType.JOIN);
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, chatService.saveChatMessage(joinMessage, "System"));
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");

        if (username != null && roomId != null) {
            log.info("User disconnected: {} from room {}", username, roomId);
            // Save and broadcast LEAVE message
            ChatMessageRequestDto leaveMessage = new ChatMessageRequestDto();
            leaveMessage.setRoomId(roomId);
            leaveMessage.setSenderName("System");
            leaveMessage.setContent(username + " 님이 퇴장했습니다.");
            leaveMessage.setMessageType(MessageType.LEAVE);

            chatService.saveSystemMessage(roomId, leaveMessage.getContent(), MessageType.LEAVE);
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, chatService.saveChatMessage(leaveMessage, "System"));
        }
    }
}
