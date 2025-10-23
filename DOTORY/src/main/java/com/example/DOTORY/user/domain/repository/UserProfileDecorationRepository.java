// UserProfileDecorationRepository.java
package com.example.DOTORY.user.domain.repository;

import com.example.DOTORY.user.domain.entity.ProfileDecorationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileDecorationRepository extends JpaRepository<ProfileDecorationEntity, Long> {
    Optional<ProfileDecorationEntity> findByUser_userPK(int userPK);
}