package com.example.DOTORY.admin.api.dto;

public record UserInfoDTO(
        int userPK,
        String userName,
        String userNickname,
        String userEmail
) {}