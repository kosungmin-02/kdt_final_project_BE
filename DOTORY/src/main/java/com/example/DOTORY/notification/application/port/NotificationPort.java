package com.example.DOTORY.notification.application.port;

import com.example.DOTORY.notification.domain.Notification;
import java.util.List;

public interface NotificationPort {
    List<Notification> findAllByUserId(String userId);
}
