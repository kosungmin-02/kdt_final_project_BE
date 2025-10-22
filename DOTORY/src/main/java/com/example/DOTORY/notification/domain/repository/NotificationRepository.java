package com.example.DOTORY.notification.domain.repository;

import com.example.DOTORY.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUser_UserPK(Integer userPK);
}