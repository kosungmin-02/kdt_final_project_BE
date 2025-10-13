package com.example.DOTORY.notification.api.dto;

import com.example.DOTORY.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;

 @Schema(description = "알림 응답 DTO")
public record NotificationResponse(
    String notificationId,
    String message,
    String relatedUrl,
    boolean isRead,
    long createdAt
) {
    public static NotificationResponse fromDomain(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getMessage(),
            notification.getRelatedUrl(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }
}
