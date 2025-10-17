package com.example.DOTORY.user.api.dto;

public record UserProfileUpdateRequestDTO(
        String birth,
        String mbti,
        String bio
) {}

