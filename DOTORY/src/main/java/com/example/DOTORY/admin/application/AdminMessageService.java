package com.example.DOTORY.admin.application;

import com.example.DOTORY.admin.api.dto.AdminMessageResultDTO;
import com.example.DOTORY.admin.domain.entity.AdminMessageEntity;
import com.example.DOTORY.admin.domain.entity.MessageType;
import com.example.DOTORY.admin.repository.AdminMessageRepository;
import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminMessageService {

    private final AdminMessageRepository adminMessageRepository;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

   // 실제 이메일 전송 역할 (sendMail)
    private boolean sendEmailFromAdmin(String email, String title, String content) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("setilvereun@gmail.com");
        mail.setTo(email);
        mail.setSubject(title);
        mail.setText(content);

        try {
            javaMailSender.send(mail);
            log.info("메일 전송 성공: {}", email);
            return true;
        } catch (Exception e) {
            log.error("메일 전송 실패: {} - {}", email, e.getMessage(), e);
            return false;
        }
    }

    // 통합 메일 발송 로직
    // 특정 사용자 목록이 있으면 해당 사용자에게만 메일 전송
    // 목록이 없으면 전체 사용자에게 메일 전송
    public List<AdminMessageResultDTO> sendMessage(List<String> userIDs, String title, String content, MessageType type) {
        List<UserEntity> targets;

        if (userIDs == null || userIDs.isEmpty()) {
            targets = userRepository.findAll();
            if (targets.isEmpty()) {
                throw new GeneralException(ErrorStatus.USER_NOT_FOUND, "등록된 사용자가 없습니다.");
            }
        } else {
            targets = userRepository.findAllByUserIDIn(userIDs);
            if (targets.isEmpty()) {
                throw new GeneralException(ErrorStatus.USER_NOT_FOUND, "해당 ID의 사용자가 없습니다.");
            }
        }

        List<AdminMessageResultDTO> results = new ArrayList<>();

        for (UserEntity user : targets) {
            boolean emailSent = sendEmailFromAdmin(user.getUserEmail(), title, content);

            AdminMessageEntity message = AdminMessageEntity.builder()
                    .user(user)
                    .messageTitle(title)
                    .messageContent(content)
                    .messageType(type)
                    .issent(emailSent)
                    .build();

            adminMessageRepository.save(message);

            results.add(new AdminMessageResultDTO(user.getUserID(), emailSent));
        }

        return results;
    }


    // 사용자 ID로 메시지 검색
    public List<AdminMessageEntity> searchMessagesByUserID(String userID) {
        if (userID == null || userID.isEmpty()) {
            return adminMessageRepository.findAll();
        }
        return adminMessageRepository.findByUser_UserIDContaining(userID);
    }

    // 전체 메세지 조회
    public List<AdminMessageEntity> getAllMessages() {
        return adminMessageRepository.findAll();
    }


}
