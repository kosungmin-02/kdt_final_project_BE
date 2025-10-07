package com.example.DOTORY.admin.application;

import com.example.DOTORY.admin.domain.entity.AdminMessageEntity;
import com.example.DOTORY.admin.domain.entity.MessageType;
import com.example.DOTORY.admin.repository.AdminMessageRepository;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminMessageService {

    private final AdminMessageRepository adminMessageRepository;
    private final UserRepository userRepository;

    // 1. 메시지 전송 (단일 사용자)
    public AdminMessageEntity sendMessageToUser(String userID, String title, String content, MessageType type) {
        UserEntity user = userRepository.findByUserID(userID)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userID));

        AdminMessageEntity message = AdminMessageEntity.builder()
                .user(user)
                .messageTitle(title)
                .messageContent(content)
                .messageType(type)
                .messageRead(false)
                .build();

        return adminMessageRepository.save(message);
    }

    // 2. 여러 사용자에게 메시지 전송 (or 전체 공지)
    public void sendMessageToUsers(List<String> userIDs, String title, String content, MessageType type) {
        // userIDs가 비어있으면 전체 공지로 전송
        if (userIDs == null || userIDs.isEmpty()) {
            sendNoticeToAllUsers(title, content);
            return;
        }

        // 선택 사용자에게 개별 전송
        userIDs.forEach(userID -> {
            userRepository.findByUserID(userID).ifPresentOrElse(
                    user -> {
                        AdminMessageEntity message = AdminMessageEntity.builder()
                                .user(user)
                                .messageTitle(title)
                                .messageContent(content)
                                .messageType(type)
                                .messageRead(false)
                                .build();
                        adminMessageRepository.save(message);
                    },
                    () -> {
                        throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + userID);
                    }
            );
        });
    }

    // 3. 전체 사용자에게 공지 전송
    public void sendNoticeToAllUsers(String title, String content) {
        List<UserEntity> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new IllegalStateException("등록된 사용자가 없습니다.");
        }

        for (UserEntity user : users) {
            sendMessageToUser(user.getUserID(), title, content, MessageType.NOTICE);
        }
    }

    // 4. 특정 사용자 메시지 조회
    public List<AdminMessageEntity> searchMessagesByUserID(String userID) {
        if (userID == null || userID.isEmpty()) {
            return adminMessageRepository.findAll();
        }
        return adminMessageRepository.findByUser_UserIDContaining(userID);
    }

    // 5. 전체 메시지 조회
    public List<AdminMessageEntity> getAllMessages() {
        return adminMessageRepository.findAll();
    }

    // 6. 메시지 읽음 처리
    public void markMessageAsRead(int messageId) {
        AdminMessageEntity message = adminMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        message.setMessageRead(true);
        adminMessageRepository.save(message);
    }
}
