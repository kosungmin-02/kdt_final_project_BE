package com.example.DOTORY.chat.application;

import com.example.DOTORY.chat.api.dto.request.ChatMessageRequestDto;
import com.example.DOTORY.chat.api.dto.response.ChatRoomResponseDto;
import com.example.DOTORY.chat.api.dto.request.CreateGroupChatRequestDto;
import com.example.DOTORY.chat.api.dto.response.ChatMessageResponseDto;
import com.example.DOTORY.chat.domain.entity.ChatParticipant;
import com.example.DOTORY.chat.domain.entity.ChatMessage;
import com.example.DOTORY.chat.domain.entity.ChatRoom;
import com.example.DOTORY.chat.domain.entity.MessageType;
import com.example.DOTORY.chat.domain.repository.ChatParticipantRepository;
import com.example.DOTORY.chat.domain.repository.ChatMessageRepository;
import com.example.DOTORY.chat.domain.repository.ChatRoomRepository;
import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatRoomResponseDto createGroupChatRoom(CreateGroupChatRequestDto request, int ownerPk) {
        UserEntity owner = userRepository.findById(ownerPk)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND, "Owner not found"));

        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(request.roomName())
                .roomImage(request.roomImage())
                .description(request.description())
                .build();

        ChatParticipant ownerParticipant = ChatParticipant.builder().user(owner).chatRoom(chatRoom).build();
        chatRoom.getParticipants().add(ownerParticipant);

        if (request.userPks() != null) {
            for (Integer userPk : request.userPks()) {
                UserEntity user = userRepository.findById(userPk)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
                if (!user.equals(owner)) {
                    ChatParticipant participant = ChatParticipant.builder().user(user).chatRoom(chatRoom).build();
                    chatRoom.getParticipants().add(participant);
                }
            }
        }

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponseDto.from(savedChatRoom);
    }

    @Transactional
    public ChatMessageResponseDto saveChatMessage(ChatMessageRequestDto chatMessageDto, String senderName) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageDto.getRoomId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "Chat room not found."));

        UserEntity sender = null;
        if (!senderName.equals("System")) { // System messages don't have a user entity
            sender = userRepository.findByUserNickname(senderName)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND, "Sender not found."));
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(chatMessageDto.getContent())
                .messageType(chatMessageDto.getMessageType())
                .timestamp(LocalDateTime.now())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        return ChatMessageResponseDto.from(savedMessage);
    }

    @Transactional
    public ChatMessageResponseDto saveSystemMessage(Long roomId, String content, MessageType messageType) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "Chat room not found."));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(null) // System messages have no sender
                .content(content)
                .messageType(messageType)
                .timestamp(LocalDateTime.now())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        return ChatMessageResponseDto.from(savedMessage);
    }

    public List<ChatRoomResponseDto> findMyChatRooms(int userPk) {
        return chatParticipantRepository.findByUser_UserPK(userPk).stream()
                .map(ChatParticipant::getChatRoom)
                .map(ChatRoomResponseDto::from)
                .collect(Collectors.toList());
    }

    public ChatRoomResponseDto findChatRoomById(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "Chat room not found."));
        return ChatRoomResponseDto.from(chatRoom);
    }

    public List<ChatRoomResponseDto> searchGroupChatRooms(String roomName, int userPk) {
        List<ChatRoom> allGroupRooms = chatRoomRepository.findByRoomNameContaining(roomName);
        List<ChatRoom> myRooms = chatParticipantRepository.findByUser_UserPK(userPk).stream()
                .map(ChatParticipant::getChatRoom)
                .toList();

        return allGroupRooms.stream()
                .filter(room -> !myRooms.contains(room))
                .map(ChatRoomResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void joinGroupChatRoom(Long roomId, int userPk) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "Chat room not found."));

        UserEntity user = userRepository.findById(userPk)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        boolean isAlreadyParticipant = chatRoom.getParticipants().stream()
                .anyMatch(p -> p.getUser().getUserPK() == userPk);

        if (isAlreadyParticipant) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST, "User is already a participant in this chat room.");
        }

        ChatParticipant participant = ChatParticipant.builder()
                .user(user)
                .chatRoom(chatRoom)
                .build();

        chatRoom.getParticipants().add(participant);
        chatRoomRepository.save(chatRoom);
    }
}