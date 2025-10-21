package com.example.DOTORY.notification.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.notification.api.dto.NotificationResponse;
import com.example.DOTORY.notification.api.dto.FcmTokenRequestDto;
import com.example.DOTORY.notification.application.NotificationService;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

 @Tag(name = "Notifications", description = "알림 관련 API")
 @RestController @RequestMapping("/api/notifications")
 @RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "FCM 토큰 등록")
    @PostMapping("/register-fcm")
    public ResponseEntity<ApiResponse<String>> registerFcmToken(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody FcmTokenRequestDto requestDto) {
        String currentUserId = String.valueOf(principal.getUser().getUserPK());
        notificationService.registerFcmToken(currentUserId, requestDto.getToken());
        return ResponseEntity.ok(ApiResponse.onSuccess("FCM token registered successfully."));
    }

    @Operation(summary = "내 알림 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(@AuthenticationPrincipal CustomUserPrincipal principal) {
        String currentUserId = String.valueOf(principal.getUser().getUserPK());
        List<NotificationResponse> notifications = notificationService.findMyNotifications(currentUserId);
        return ResponseEntity.ok(ApiResponse.onSuccess(notifications));
    }
}
