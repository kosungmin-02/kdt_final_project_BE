package com.example.DOTORY.chat.application;

import com.example.DOTORY.chat.api.dto.response.ChatRoomResponseDto;
import com.example.DOTORY.chat.api.dto.request.CreateGroupChatRequestDto;
import com.example.DOTORY.chat.domain.entity.ChatParticipant;
import com.example.DOTORY.chat.domain.entity.ChatRoom;
import com.example.DOTORY.chat.domain.repository.ChatParticipantRepository;
import com.example.DOTORY.chat.domain.repository.ChatRoomRepository;
import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
// --- ⬇️ (필수) Import 추가 ---
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.io.IOException;
// --- ⬆️ (필수) Import 추가 ---

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {


    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @Transactional
    // 💡 2. 파라미터에 MultipartFile 추가
    public ChatRoomResponseDto createGroupChatRoom(CreateGroupChatRequestDto request, MultipartFile roomImage, int ownerPk) {
        UserEntity owner = userRepository.findById(ownerPk)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND, "Owner not found"));

        // 💡 3. 파일 저장 로직 추가
        String roomImageUrl = null; // 기본값 null
        if (roomImage != null && !roomImage.isEmpty()) {
            try {
                // (파일 저장 로직은 별도 서비스로 분리하는 것이 좋습니다)
                roomImageUrl = saveFile(roomImage);
            } catch (IOException e) {
                // 파일 저장 실패 시 예외 처리 (혹은 기본 이미지 사용)
                throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR, "Failed to save image.");
            }
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(request.roomName())
                .roomImage(roomImageUrl) // 💡 4. 저장된 파일 경로(URL)를 DB에 저장
                .description(request.description())
                .build();

        // ... (이하 참여자 추가 로직 동일) ...
        // ...

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponseDto.from(savedChatRoom);
    }

    // 💡 5. 파일 저장 헬퍼 메서드 추가
    private String saveFile(MultipartFile file) throws IOException {
        // 디렉토리가 없으면 생성
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 고유한 파일 이름 생성
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // 파일 저장
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // 💡 6. DB에 저장될 경로/URL 반환 (예: /uploads/unique-name.jpg)
        // (주의: 클라이언트가 접근 가능하도록 WebConfig 설정이 필요할 수 있습니다)
        return "/" + UPLOAD_DIR + uniqueFilename;
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