package com.example.DOTORY.user.domain.repository;

import com.example.DOTORY.user.domain.entity.UserAgreeEntity;
import com.example.DOTORY.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAgreeRepository  extends JpaRepository<UserAgreeEntity, Integer> {
    UserAgreeEntity[] findByUser(UserEntity user);
}
