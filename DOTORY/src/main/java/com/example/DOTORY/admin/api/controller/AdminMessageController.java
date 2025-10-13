package com.example.DOTORY.admin.api.controller;

import com.example.DOTORY.admin.api.dto.AdminMessageRequestDTO;
import com.example.DOTORY.admin.application.AdminMessageService;
import com.example.DOTORY.admin.domain.entity.AdminMessageEntity;
import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin/messages")
@RequiredArgsConstructor
@Tag(name = "Admin Message API", description = "관리자 메시지 관련 API")
public class AdminMessageController {

    private final AdminMessageService adminMessageService;
    private final UserRepository userRepository;

     // 1. 특정 사용자 / 여러명 / 전체에게 메시지 보내기
    @Operation(summary = "메시지 전송", description = "특정 사용자 또는 여러명, 전체에게 메시지를 전송합니다.")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendMessage(
            @RequestBody AdminMessageRequestDTO request
    ) {
        adminMessageService.sendMessageToUsers(
                request.getUserIDs(),
                request.getMessageTitle(),
                request.getMessageContent(),
                request.getMessageType()
        );
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 2-1. 특정 사용자 메시지 조회
    @Operation(summary = "사용자 메시지 조회", description = "특정 사용자의 메시지 목록을 조회합니다.")
    @GetMapping("/user/search")
    public ResponseEntity<ApiResponse<List<AdminMessageEntity>>> searchMessagesByUserID(
            @RequestParam String userID
    ) {
        List<AdminMessageEntity> messages = adminMessageService.searchMessagesByUserID(userID);
        return ResponseEntity.ok(ApiResponse.onSuccess(messages));
    }

    // 2-2. 전체 메시지 조회
    @Operation(summary = "전체 메시지 조회", description = "모든 메시지를 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AdminMessageEntity>>> getAllMessages() {
        List<AdminMessageEntity> messages = adminMessageService.getAllMessages();
        return ResponseEntity.ok(ApiResponse.onSuccess(messages));
    }

    // 3. 메시지 읽음 처리
    @Operation(summary = "메시지 읽음 처리", description = "해당 메시지를 읽음 상태로 변경합니다.")
    @PostMapping("/{messageId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable int messageId) {
        adminMessageService.markMessageAsRead(messageId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 4. 사용자 ID 자동완성 (입력 시 유저 목록 조회)
    @Operation(summary = "사용자 검색", description = "입력한 키워드에 해당하는 사용자 목록을 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserEntity>>> searchUsers(@RequestParam String keyword) {
        List<UserEntity> users = userRepository.findByUserIDContainingIgnoreCase(keyword);
        return ResponseEntity.ok(ApiResponse.onSuccess(users));
    }
}
