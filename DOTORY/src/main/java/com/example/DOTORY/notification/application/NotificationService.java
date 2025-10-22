package com.example.DOTORY.notification.application;

import com.example.DOTORY.notification.api.dto.NotificationResponse;
import com.example.DOTORY.notification.application.port.NotificationPort;
import com.example.DOTORY.notification.domain.Notification;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

 @Service @RequiredArgsConstructor
 @Slf4j
public class NotificationService {

    private final NotificationPort notificationPort;
    private final UserRepository userRepository;
    private final FirebaseMessaging firebaseMessaging;

    public List<NotificationResponse> findMyNotifications(String userId) {
        UserEntity user = userRepository.findByUserID(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        List<Notification> notifications = notificationPort.findAllByUser_UserPK(user.getUserPK());
        
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

    @Transactional
    public void sendNotification(UserEntity recipient, String title, String body, String type, String relatedUrl) {
        if (recipient.getFcmToken() != null && !recipient.getFcmToken().isEmpty()) {
            com.google.firebase.messaging.Notification firebaseNotification = com.google.firebase.messaging.Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setToken(recipient.getFcmToken())
                    .setNotification(firebaseNotification)
                    .putData("type", type)
                    .putData("relatedUrl", relatedUrl != null ? relatedUrl : "")
                    .build();

            try {
                String response = firebaseMessaging.send(message);
                log.info("Successfully sent message to user {}: {}", recipient.getUserID(), response);
            } catch (Exception e) {
                log.error("Failed to send message to user {}: {}", recipient.getUserID(), e.getMessage());
            }
        } else {
            log.warn("User {} has no FCM token, skipping notification.", recipient.getUserID());
        }

        // Save notification to database
        Notification notification = Notification.builder()
                .user(recipient)
                .title(title)
                .body(body)
                .type(type)
                .relatedUrl(relatedUrl)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationPort.save(notification);
    }
}
