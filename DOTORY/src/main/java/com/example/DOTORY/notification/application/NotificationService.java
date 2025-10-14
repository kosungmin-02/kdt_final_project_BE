package com.example.DOTORY.notification.application;

import com.example.DOTORY.notification.api.dto.NotificationResponse;
import com.example.DOTORY.notification.application.port.NotificationPort;
import com.example.DOTORY.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

 @Service @RequiredArgsConstructor
public class NotificationService {

    private final NotificationPort notificationPort;

    public List<NotificationResponse> findMyNotifications(String userId) {
        List<Notification> notifications = notificationPort.findAllByUserId(userId);
        
        return notifications.stream()
                .map(NotificationResponse::fromDomain)
                .collect(Collectors.toList());
    }
}
