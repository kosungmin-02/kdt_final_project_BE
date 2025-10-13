package com.example.DOTORY.admin.api.dto;

import com.example.DOTORY.user.domain.entity.AgreeType;

public record UpdateAgreeDTO(
        String title,
        String content,
        AgreeType type
) {}
