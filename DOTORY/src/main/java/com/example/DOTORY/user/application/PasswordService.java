package com.example.DOTORY.user.application;

import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.user.api.dto.UserDTO;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSendService emailSendService;

    public void findPassword(UserDTO userDTO) {
        log.info("findPassword()");

        UserEntity userEntity = userRepository.findByUserIDAndUserEmail(userDTO.userID(), userDTO.userEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        String tempPassword = createTempPassword();
        userEntity.setUserPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(userEntity);
        emailSendService.sendTempPasswordByEmail(userDTO.userEmail(), tempPassword);
    }

    private String createTempPassword() {
        log.info("createTempPassword()");
        char[] chars = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z',
                '!', '@', '#', '$', '%', '^', '&', '*'
        };

        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        int index = 0;
        int length = chars.length;
        for (int i = 0; i < 10; i++) {
            index = secureRandom.nextInt(length);
            stringBuilder.append(chars[index]);
        }
        return stringBuilder.toString();
    }
}