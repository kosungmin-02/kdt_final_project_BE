package com.example.DOTORY.user.api.dto;

import java.util.ArrayList;
import java.util.List;

public record ProfileDecorationRequestDTO(
        String backgroundImageUrl,
        Double backgroundOpacity,
        List<StickerRequestDTO> stickers
) {
    // 기본 생성자에서 stickers가 null이면 빈 리스트로 초기화
    public ProfileDecorationRequestDTO {
        if (stickers == null) {
            stickers = new ArrayList<>();
        }
    }
}
