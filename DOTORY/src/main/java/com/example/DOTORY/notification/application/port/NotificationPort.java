package com.example.DOTORY.notification.application.port;

import com.example.DOTORY.notification.domain.Notification;
import java.util.List;

public interface NotificationPort {
    List<Notification> findAllByUser_UserPK(Integer userPK);
    Notification save(Notification notification);
}
