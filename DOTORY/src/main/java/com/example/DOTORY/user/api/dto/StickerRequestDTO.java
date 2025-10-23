package com.example.DOTORY.user.api.dto;

// 스티커 정보 요청 DTO
public record StickerRequestDTO(
        String imageUrl,
        Double xPosition,
        Double yPosition,
        Double scale,
        Double rotation,
        Integer zIndex
) {
}