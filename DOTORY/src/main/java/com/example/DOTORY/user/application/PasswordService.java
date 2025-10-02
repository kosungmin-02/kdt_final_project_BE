package com.example.DOTORY.user.application;

import com.example.DOTORY.user.api.dto.UserDTO;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@Slf4j
public class PasswordService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailSendService emailSendService;

    // 임시 비밀번호 발급이 성공했다면 1, 실패라면 0
    public final static int USER_SENDPASSWORD_SUCCESS = 1;
    public final static int USER_SENDPASSWORD_FAIL = 0;

    // 비밀번호 찾기 - 아이디, 이메일 인증을 해야만 임시 비밀번호 발급.
    public int findpassword(UserDTO userDTO){
        log.info("findpassword()");

        Optional<UserEntity> optionalUser =
                userRepository.findByUserIDAndUserEmail(userDTO.getUserID(), userDTO.getUserEmail());

        if(optionalUser.isPresent()) {
            // 임시 비밀번호 발급
            String tempPassword = createTempPassword();
            UserEntity userEntity = optionalUser.get();

            // 새로운 비밀번호 업데이트
            userEntity.setUserPassword(passwordEncoder.encode(tempPassword));
            UserEntity updateUser = userRepository.save(userEntity);
            if (updateUser != null)
            {
                // 이메일 보내기
                emailSendService.sendTempPasswordByEmail(userDTO.getUserEmail(), tempPassword);
            }
            return USER_SENDPASSWORD_SUCCESS;
        }
        return USER_SENDPASSWORD_FAIL;
    }


    // 10자리 임시 비밀번호 발급
    private String createTempPassword(){
        log.info("createTempPassword()");
        char[] chars = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k',  'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
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
        for(int i = 0; i<10; i++){
            index = secureRandom.nextInt(length);
            stringBuilder.append(chars[index]);
        }
        return stringBuilder.toString();
    }
}
