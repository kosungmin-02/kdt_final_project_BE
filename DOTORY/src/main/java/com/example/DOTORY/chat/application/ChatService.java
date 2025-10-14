package com.example.DOTORY.chat.application;

import com.example.DOTORY.chat.api.dto.response.ChatRoomResponseDto;
import com.example.DOTORY.chat.api.dto.request.CreateGroupChatRequestDto;
import com.example.DOTORY.chat.domain.entity.ChatParticipant;
import com.example.DOTORY.chat.domain.entity.ChatRoom;
import com.example.DOTORY.chat.domain.repository.ChatParticipantRepository;
import com.example.DOTORY.chat.domain.repository.ChatRoomRepository;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoomResponseDto createGroupChatRoom(CreateGroupChatRequestDto request, int ownerPk) {
        UserEntity owner = userRepository.findById(ownerPk)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(request.roomName())
                .roomType(ChatRoom.RoomType.GROUP)
                .build();

        // Add owner to participants
        ChatParticipant ownerParticipant = ChatParticipant.builder().user(owner).chatRoom(chatRoom).build();
        chatRoom.getParticipants().add(ownerParticipant);

        // Add other users to participants
        if (request.userPks() != null) {
            for (Integer userPk : request.userPks()) {
                UserEntity user = userRepository.findById(userPk)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userPk));
                if (!user.equals(owner)) { // Avoid adding owner twice
                    ChatParticipant participant = ChatParticipant.builder().user(user).chatRoom(chatRoom).build();
                    chatRoom.getParticipants().add(participant);
                }
            }
        }

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponseDto.from(savedChatRoom);
    }

    @Transactional
    public ChatRoomResponseDto findOrCreateOneOnOneChatRoom(int user1Pk, int user2Pk) {
        if (user1Pk == user2Pk) {
            throw new IllegalArgumentException("Cannot create a chat room with yourself.");
        }

        // Ensure consistent ordering of user IDs to find the room
        int smallerPk = Math.min(user1Pk, user2Pk);
        int largerPk = Math.max(user1Pk, user2Pk);

        Optional<ChatRoom> existingRoom = chatRoomRepository.findOneOnOneRoom(smallerPk, largerPk)
                .filter(room -> room.getParticipants().size() == 2);

        if (existingRoom.isPresent()) {
            return ChatRoomResponseDto.from(existingRoom.get());
        }

        UserEntity user1 = userRepository.findById(user1Pk)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user1Pk));
        UserEntity user2 = userRepository.findById(user2Pk)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user2Pk));

        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(null) // 1:1 chat has no name
                .roomType(ChatRoom.RoomType.ONE_ON_ONE)
                .build();

        ChatParticipant participant1 = ChatParticipant.builder().user(user1).chatRoom(chatRoom).build();
        ChatParticipant participant2 = ChatParticipant.builder().user(user2).chatRoom(chatRoom).build();

        chatRoom.getParticipants().add(participant1);
        chatRoom.getParticipants().add(participant2);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponseDto.from(savedChatRoom);
    }

    public List<ChatRoomResponseDto> findMyChatRooms(int userPk) {
        return chatParticipantRepository.findByUser_UserPK(userPk).stream()
                .map(ChatParticipant::getChatRoom)
                .map(ChatRoomResponseDto::from)
                .collect(Collectors.toList());
    }

    public ChatRoomResponseDto findChatRoomById(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        return ChatRoomResponseDto.from(chatRoom);
    }
}
