package com.example.DOTORY.notification.api.dto;

import com.example.DOTORY.notification.domain.Notification;
import com.example.DOTORY.user.domain.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.format.DateTimeFormatter;

@Schema(description = "알림 응답 DTO")
public record NotificationResponse(
        Long id,
        String title,
        String body,
        String type,
        String relatedUrl,
        boolean isRead,
        String createdAt,
        Long actorId,
        String actorName
) {
    public static NotificationResponse fromDomain(Notification notification) {
        UserEntity actor = notification.getActor();
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getBody(),
                notification.getType(),
                notification.getRelatedUrl(),
                notification.getIsRead(),
                notification.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                actor != null ? Long.valueOf(actor.getUserPK()) : null,
                actor != null ? actor.getUserNickname() : null
        );
    }
}
