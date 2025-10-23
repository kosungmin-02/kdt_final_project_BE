package com.example.DOTORY.notification.infrastructure;

import com.example.DOTORY.notification.application.port.NotificationPort;
import com.example.DOTORY.notification.domain.Notification;
import com.example.DOTORY.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("notificationPortImpl")
@Primary
@RequiredArgsConstructor
public class NotificationPortImpl implements NotificationPort {

    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> findAllByUser_UserPK(Integer userPK) {
        return notificationRepository.findAllByUser_UserPK(userPK);
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }
}
