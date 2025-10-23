package com.example.DOTORY.notification.api.dto;

import com.example.DOTORY.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.format.DateTimeFormatter;

 @Schema(description = "알림 응답 DTO")
public record NotificationResponse(
    String notificationId,
    String title,
    String body,
    String type,
    String relatedUrl,
    boolean isRead,
    String createdAt
) {
    public static NotificationResponse fromDomain(Notification notification) {
        return new NotificationResponse(
            notification.getId().toString(),
            notification.getTitle(),
            notification.getBody(),
            notification.getType(),
            notification.getRelatedUrl(),
            notification.getIsRead(),
            notification.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}
