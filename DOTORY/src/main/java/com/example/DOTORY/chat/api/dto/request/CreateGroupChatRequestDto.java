package com.example.DOTORY.chat.api.dto.request;

import java.util.List;

public record CreateGroupChatRequestDto(String roomName, String roomImage, String description, List<Integer> userPks) {
}
