package com.example.DOTORY.chat.api.dto.response;

import com.example.DOTORY.chat.domain.entity.ChatRoom;
import com.example.DOTORY.chat.api.dto.response.ParticipantResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public record ChatRoomResponseDto(
        Long id,
        String roomName,
        ChatRoom.RoomType roomType,
        List<ParticipantResponseDto> participants
) {
    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomResponseDto(
                chatRoom.getId(),
                chatRoom.getRoomName(),
                chatRoom.getRoomType(),
                chatRoom.getParticipants().stream()
                        .map(p -> ParticipantResponseDto.from(p.getUser()))
                        .collect(Collectors.toList())
        );
    }
}
