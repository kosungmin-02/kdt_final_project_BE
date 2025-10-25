package com.example.DOTORY.admin.api.controller;

import com.example.DOTORY.admin.api.dto.AdminCheckUserDTO;
import com.example.DOTORY.admin.api.dto.AdminMessageDTO;
import com.example.DOTORY.admin.api.dto.AdminMessageRequestDTO;
import com.example.DOTORY.admin.api.dto.AdminMessageResultDTO;
import com.example.DOTORY.admin.application.AdminMessageService;
import com.example.DOTORY.admin.domain.entity.AdminMessageEntity;
import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.user.application.EmailSendService;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin/messages")
@RequiredArgsConstructor
@Tag(name = "Admin Message API", description = "관리자 메시지 관련 API")
public class AdminMessageController {

    private final AdminMessageService adminMessageService;
    private final UserRepository userRepository;
    private final EmailSendService emailSendService;

     // 1. 특정 사용자 / 여러명 / 전체에게 메시지 보내기
    @Operation(summary = "메시지 전송", description = "특정 사용자 또는 여러명, 전체에게 메시지를 전송. 타입에서 공지 / 경고 선택 가능.")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<List<AdminMessageResultDTO>>> sendMessage(
            @RequestBody AdminMessageRequestDTO request
    ) {
        List<AdminMessageResultDTO> results = adminMessageService.sendMessage(
                request.getUserIDs(),
                request.getMessageTitle(),
                request.getMessageContent(),
                request.getMessageType()
        );
        return ResponseEntity.ok(ApiResponse.onSuccess(results));
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
    public ResponseEntity<ApiResponse<List<AdminMessageDTO>>> getAllMessages() {
        List<AdminMessageDTO> dtos = adminMessageService.getAllMessages().stream()
                .map(msg -> new AdminMessageDTO(
                        msg.getAdminMessageID(),
                        msg.getUser() != null ? msg.getUser().getUserID() : null,
                        msg.getMessageTitle(),
                        msg.getMessageContent(),
                        msg.getMessageType() != null ? msg.getMessageType().name() : null,
                        msg.isIssent(),
                        msg.getCreatedDate() != null ? msg.getCreatedDate().toString() : null
                ))
                .toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(dtos));
    }




    // 4. 사용자 ID 자동완성 (입력 시 유저 목록 조회)
    @Operation(summary = "사용자 검색", description = "입력한 키워드에 해당하는 사용자 목록을 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AdminCheckUserDTO>>> searchUsers(@RequestParam String keyword) {
        List<AdminCheckUserDTO> dtos = userRepository.findByUserIDContainingIgnoreCase(keyword)
                .stream()
                .map(user -> new AdminCheckUserDTO(
                        user.getUserPK(),
                        user.getUserID(),
                        user.getUserName(),
                        user.getUserNickname(),
                        user.getUserEmail(),
                        user.getCreatedDate(),
                        user.getUpdatedDate(),
                        user.getUserStatus(),
                        List.of(), // snsProviders
                        List.of(), // agreements
                        List.of()  // reports
                ))
                .toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(dtos));
    }

}
