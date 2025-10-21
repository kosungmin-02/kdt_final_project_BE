package com.example.DOTORY.notification.application;

import com.example.DOTORY.notification.api.dto.NotificationResponse;
import com.example.DOTORY.notification.application.port.NotificationPort;
import com.example.DOTORY.notification.domain.Notification;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

 @Service @RequiredArgsConstructor
public class NotificationService {

    private final NotificationPort notificationPort;
    private final UserRepository userRepository;

    public List<NotificationResponse> findMyNotifications(String userId) {
        List<Notification> notifications = notificationPort.findAllByUserId(userId);
        
        return notifications.stream()
                .map(NotificationResponse::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public void registerFcmToken(String userId, String fcmToken) {
        UserEntity user = userRepository.findByUserPK(Integer.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }
}
