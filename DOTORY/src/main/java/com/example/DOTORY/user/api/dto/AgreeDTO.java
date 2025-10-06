package com.example.DOTORY.user.api.dto;

import com.example.DOTORY.user.domain.entity.AgreeEntity;
import com.example.DOTORY.user.domain.entity.AgreeType;

import java.time.LocalDateTime;

public record AgreeDTO(
        int agreeId,
        String agreeTitle,
        String agreeContent,
        AgreeType agreeType,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {
    // Entity -> DTO 변환하기 위한 생성자
    public AgreeDTO(AgreeEntity agreeEntity) {
        this(
                agreeEntity.getAgreeID(),
                agreeEntity.getAgreeTitle(),
                agreeEntity.getAgreeContent(),
                agreeEntity.getAgreeType(),
                agreeEntity.getCreatedDate(),
                agreeEntity.getUpdatedDate()
        );
    }

    // DTO -> Entity 변환 함수
    public AgreeEntity toEntity() {
        AgreeEntity agreeEntity = new AgreeEntity();
        agreeEntity.setAgreeTitle(agreeTitle);
        agreeEntity.setAgreeContent(agreeContent);
        agreeEntity.setAgreeType(agreeType);
        return agreeEntity;
    }
}
