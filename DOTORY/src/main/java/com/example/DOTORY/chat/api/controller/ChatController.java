package com.example.DOTORY.chat.api.controller;

import com.example.DOTORY.chat.api.dto.response.ChatRoomResponseDto;
import com.example.DOTORY.chat.api.dto.request.CreateGroupChatRequestDto;
import com.example.DOTORY.chat.application.ChatService;
import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat", description = "채팅 관련 API")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "그룹 채팅방 생성")
    @PostMapping("/rooms/group")
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> createGroupChatRoom(
            @RequestBody CreateGroupChatRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        ChatRoomResponseDto chatRoom = chatService.createGroupChatRoom(request, principal.getUser().getUserPK());
        return ResponseEntity.ok(ApiResponse.onSuccess(chatRoom));
    }

    @Operation(summary = "내 채팅방 목록 조회")
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomResponseDto>>> getMyChatRooms(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        List<ChatRoomResponseDto> chatRooms = chatService.findMyChatRooms(principal.getUser().getUserPK());
        return ResponseEntity.ok(ApiResponse.onSuccess(chatRooms));
    }

    @Operation(summary = "채팅방 ID로 조회")
    @GetMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> getChatRoomById(@PathVariable Long id) {
        ChatRoomResponseDto chatRoom = chatService.findChatRoomById(id);
        return ResponseEntity.ok(ApiResponse.onSuccess(chatRoom));
    }

    @Operation(summary = "그룹 채팅방 검색")
    @GetMapping("/rooms/search")
    public ResponseEntity<ApiResponse<List<ChatRoomResponseDto>>> searchGroupChatRooms(
            @RequestParam String roomName,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        List<ChatRoomResponseDto> chatRooms = chatService.searchGroupChatRooms(roomName, principal.getUser().getUserPK());
        return ResponseEntity.ok(ApiResponse.onSuccess(chatRooms));
    }

    @Operation(summary = "그룹 채팅방 참여")
    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<ApiResponse<Void>> joinGroupChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        chatService.joinGroupChatRoom(roomId, principal.getUser().getUserPK());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
