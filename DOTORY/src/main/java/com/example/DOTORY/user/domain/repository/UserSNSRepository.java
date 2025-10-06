package com.example.DOTORY.user.domain.repository;

import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.entity.UserSNSEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserSNSRepository extends JpaRepository<UserSNSEntity, Long> {

    Optional<UserSNSEntity> findByProviderAndProviderID(UserSNSEntity.Provider provider, String providerId);

    UserSNSEntity[] findByUser(UserEntity user);
}
