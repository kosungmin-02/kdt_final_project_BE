package com.example.DOTORY.notification.application;

import com.example.DOTORY.notification.api.dto.NotificationResponse;
import com.example.DOTORY.notification.application.port.NotificationPort;
import com.example.DOTORY.notification.domain.Notification;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

 @Service @RequiredArgsConstructor
 @Slf4j
public class NotificationService {

    private final NotificationPort notificationPort;
    private final UserRepository userRepository;


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
            try {
                String accessToken = getAccessToken();
                String fcmUrl = "https://fcm.googleapis.com/v1/projects/dotory-7c1fe/messages:send";

                String jsonPayload = "{" +
                        "\"message\": {" +
                        "\"token\": \"" + recipient.getFcmToken() + "\"," +
                        "\"notification\": {" +
                        "\"title\": \"" + title + "\"," +
                        "\"body\": \"" + body + "\"" +
                        "}," +
                        "\"data\": {" +
                        "\"type\": \"" + type + "\"," +
                        "\"relatedUrl\": \"" + (relatedUrl != null ? relatedUrl : "") + "\"" +
                        "}" +
                        "}" +
                        "}";

                URL url = new URL(fcmUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                conn.setRequestProperty("Content-Type", "application/json; UTF-8");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                log.info("FCM response code: " + responseCode);

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    log.info("Successfully sent message to user {}: {}", recipient.getUserID(), response.toString());
                }

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

    private String getAccessToken() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ClassPathResource("dotory-7c1fe-firebase-adminsdk-fbsvc-3aa390a50b.json").getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }
}
