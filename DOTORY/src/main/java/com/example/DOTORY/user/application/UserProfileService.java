package com.example.DOTORY.user.application;

import com.example.DOTORY.user.api.dto.UserProfileUpdateRequestDTO;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.entity.UserProfileEntity;
import com.example.DOTORY.user.domain.repository.UserProfileRepository;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public UserProfileEntity updateProfile(UserEntity user, UserProfileUpdateRequestDTO request) {

        // 기존 프로필이 있으면 가져오고, 없으면 새로 생성
        UserProfileEntity profile = userProfileRepository.findByUser(user)
                .orElse(UserProfileEntity.builder().user(user).build());

        profile.setBio(request.bio());
        profile.setBirth(request.birth());
        profile.setMbti(request.mbti());

        return userProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public UserProfileEntity getProfile(UserEntity user) {
        return userProfileRepository.findByUser(user)
                .orElse(UserProfileEntity.builder().user(user).build());
    }

}

