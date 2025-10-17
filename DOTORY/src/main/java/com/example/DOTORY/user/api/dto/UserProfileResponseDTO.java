package com.example.DOTORY.user.api.dto;

import com.example.DOTORY.user.domain.entity.UserProfileEntity;

public record UserProfileResponseDTO(
        String birth,
        String mbti,
        String bio,
        int userPK,
        String userID,
        String userAvatar
) {
    public static UserProfileResponseDTO fromEntity(UserProfileEntity entity) {
        return new UserProfileResponseDTO(
                entity.getBirth(),
                entity.getMbti(),
                entity.getBio(),
                entity.getUser().getUserPK(),      // UserEntity 안에 있는 PK
                entity.getUser().getUserID(),      // UserEntity 안에 있는 ID
                entity.getUser().getUserAvatar()       // UserEntity 안에 있는 avatar
        );
    }
}
