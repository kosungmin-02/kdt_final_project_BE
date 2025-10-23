package com.example.DOTORY.user.api.dto;

import com.example.DOTORY.user.domain.entity.StickerEntity;

// 스티커 정보 응답 DTO
public record StickerResponseDTO(
        String imageUrl,
        Double xPosition,
        Double yPosition,
        Double scale,
        Double rotation,
        Integer zIndex
) {
    public static StickerResponseDTO from(StickerEntity entity) {
        return new StickerResponseDTO(
                entity.getImageUrl(),
                entity.getXPosition(),
                entity.getYPosition(),
                entity.getScale(),
                entity.getRotation(),
                entity.getZIndex()
        );
    }
}