package com.example.DOTORY.admin.repository;
import com.example.DOTORY.admin.domain.entity.AdminMessageEntity;
import com.example.DOTORY.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminMessageRepository extends JpaRepository<AdminMessageEntity, Integer> {
    // 특정 사용자에게 보낸 메시지 조회
    List<AdminMessageEntity> findByUser(UserEntity user);

    // 읽지 않은 메시지 조회
    List<AdminMessageEntity> findByUserAndMessageReadFalse(UserEntity user);

    List<AdminMessageEntity> findByUser_UserIDContaining(String userId);
}
