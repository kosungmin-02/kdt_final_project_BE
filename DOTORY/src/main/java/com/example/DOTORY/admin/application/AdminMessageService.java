package com.example.DOTORY.admin.application;

import com.example.DOTORY.admin.domain.entity.AdminMessageEntity;
import com.example.DOTORY.admin.domain.entity.MessageType;
import com.example.DOTORY.admin.repository.AdminMessageRepository;
import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
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

    public AdminMessageEntity sendMessageToUser(String userID, String title, String content, MessageType type) {
        UserEntity user = userRepository.findByUserID(userID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND, "User not found: " + userID));

        AdminMessageEntity message = AdminMessageEntity.builder()
                .user(user)
                .messageTitle(title)
                .messageContent(content)
                .messageType(type)
                .messageRead(false)
                .build();

        return adminMessageRepository.save(message);
    }

    public void sendMessageToUsers(List<String> userIDs, String title, String content, MessageType type) {
        if (userIDs == null || userIDs.isEmpty()) {
            sendNoticeToAllUsers(title, content);
            return;
        }

        userIDs.forEach(userID -> {
            UserEntity user = userRepository.findByUserID(userID)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND, "User not found: " + userID));
            AdminMessageEntity message = AdminMessageEntity.builder()
                    .user(user)
                    .messageTitle(title)
                    .messageContent(content)
                    .messageType(type)
                    .messageRead(false)
                    .build();
            adminMessageRepository.save(message);
        });
    }

    public void sendNoticeToAllUsers(String title, String content) {
        List<UserEntity> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND, "등록된 사용자가 없습니다.");
        }

        for (UserEntity user : users) {
            sendMessageToUser(user.getUserID(), title, content, MessageType.NOTICE);
        }
    }

    public List<AdminMessageEntity> searchMessagesByUserID(String userID) {
        if (userID == null || userID.isEmpty()) {
            return adminMessageRepository.findAll();
        }
        return adminMessageRepository.findByUser_UserIDContaining(userID);
    }

    public List<AdminMessageEntity> getAllMessages() {
        return adminMessageRepository.findAll();
    }

    public void markMessageAsRead(int messageId) {
        AdminMessageEntity message = adminMessageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "존재하지 않는 메시지입니다."));

        message.setMessageRead(true);
        adminMessageRepository.save(message);
    }
}