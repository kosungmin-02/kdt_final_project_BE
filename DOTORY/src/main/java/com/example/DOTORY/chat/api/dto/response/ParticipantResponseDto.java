package com.example.DOTORY.chat.api.dto.response;

import com.example.DOTORY.user.domain.entity.UserEntity;

public record ParticipantResponseDto(int userPk, String nickname) {

    public static ParticipantResponseDto from(UserEntity user) {
        return new ParticipantResponseDto(user.getUserPK(), user.getUserNickname());
    }
}
