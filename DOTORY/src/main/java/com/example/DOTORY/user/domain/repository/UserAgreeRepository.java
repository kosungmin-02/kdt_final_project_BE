package com.example.DOTORY.user.domain.repository;

import com.example.DOTORY.user.domain.entity.UserAgreeEntity;
import com.example.DOTORY.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAgreeRepository  extends JpaRepository<UserAgreeEntity, Integer> {
    UserAgreeEntity[] findByUser(UserEntity user);

        // 특정 약관에 동의한 회원만 조회
        List<UserAgreeEntity> findByAgree_AgreeIDAndAgreedTrue(int agreeId);

    void deleteAllByAgree_AgreeID(int agreeId);
}
