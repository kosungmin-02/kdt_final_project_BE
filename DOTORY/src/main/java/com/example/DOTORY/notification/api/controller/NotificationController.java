package com.example.DOTORY.notification.api.controller;

import com.example.DOTORY.notification.api.dto.NotificationResponse;
import com.example.DOTORY.notification.application.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

 @Tag(name = "Notifications", description = "알림 관련 API")
 @RestController @RequestMapping("/api/notifications")
 @RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "내 알림 목록 조회")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        String currentUserId = "some-user-uid";
        List<NotificationResponse> notifications = notificationService.findMyNotifications(currentUserId);
        return ResponseEntity.ok(notifications);
    }
}
