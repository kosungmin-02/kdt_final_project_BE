package com.example.DOTORY.admin.api.dto;

public record AdminMessageDTO(
        int adminMessageID,
        String userID,
        String messageTitle,
        String messageContent,
        String messageType,
        boolean issent,
        String createdDate
) {}
