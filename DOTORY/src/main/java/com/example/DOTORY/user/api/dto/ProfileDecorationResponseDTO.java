// ProfileDecorationResponseDTO.java
package com.example.DOTORY.user.api.dto;

import com.example.DOTORY.user.domain.entity.ProfileDecorationEntity;

import java.util.List;
import java.util.stream.Collectors;

// 프로필 꾸미기 설정 조회 응답 DTO
public record ProfileDecorationResponseDTO(
        String backgroundImageUrl,
        Double backgroundOpacity,
        List<StickerResponseDTO> stickers
) {
    public static ProfileDecorationResponseDTO from(ProfileDecorationEntity entity) {
        List<StickerResponseDTO> stickerResponses = entity.getStickers().stream()
                .map(StickerResponseDTO::from)
                .collect(Collectors.toList());

        return new ProfileDecorationResponseDTO(
                entity.getBackgroundImageUrl(),
                entity.getBackgroundOpacity(),
                stickerResponses
        );
    }
}