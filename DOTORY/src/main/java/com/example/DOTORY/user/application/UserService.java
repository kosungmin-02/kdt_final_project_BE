package com.example.DOTORY.user.application;

import com.example.DOTORY.user.api.dto.UserDTO;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailSendService emailSendService;

    public static final int USER_REGISTER_SUCCESS = 1;
    public static final int USER_REGISTER_FAIL = 0;

    public static final int USER_UPDATE_SUCCESS = 1;
    public static final int USER_UPDATE_FAIL = 0;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // 회원가입 로직 처리
    public int registerConfirm(UserDTO userDTO){
        log.info("UserService - registerConfirm");

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDTO.userPassword());

        // UserDTO를 UserEntity로 변환
        UserEntity userEntity = UserEntity.builder()
                .userPK(userDTO.userPK())
                .userID(userDTO.userID())
                .userPassword(encodedPassword) // 암호화된 비밀번호 저장
                .userEmail(userDTO.userEmail())
                .userName(userDTO.userName())
                .userNickname(userDTO.userNickname())
                .build();

        try {
            userRepository.save(userEntity);
            return USER_REGISTER_SUCCESS;
        } catch (Exception e) {
            log.error("회원 가입 중 오류 발생", e);
            return USER_REGISTER_FAIL;
        }
    }

    // 회원 정보 가져오기
    public UserDTO profileChange(String loginedID){
        log.info("profileChange()");

        Optional<UserEntity> optionalUser = userRepository.findByUserID(loginedID);
        if(optionalUser.isPresent()){
            UserEntity userEntity = optionalUser.get();

            return new UserDTO(
                    userEntity.getUserPK(),
                    userEntity.getUserID(),
                    userEntity.getUserPassword(),
                    userEntity.getUserName(),
                    userEntity.getUserNickname(),
                    userEntity.getUserEmail(),
                    userEntity.getUserRole(),
                    userEntity.getCreatedDate().toString(),
                    userEntity.getUpdatedDate().toString()
            );
        }
        return null;
    }

    // 회원 정보 수정
    public int profileChangeConfirm(UserDTO userDTO){
        log.info("profileChangeConfirm()");

        String encodedPassword = passwordEncoder.encode(userDTO.userPassword());

        Optional<UserEntity> optionalUser = userRepository.findByUserPK(userDTO.userPK());
        if(optionalUser.isPresent()){
            UserEntity userEntity = optionalUser.get();

            userEntity.setUserPassword(encodedPassword);
            userEntity.setUserName(userDTO.userName());
            userEntity.setUserNickname(userDTO.userNickname());
            userEntity.setUpdatedDate(LocalDateTime.now());

            userRepository.save(userEntity);
            return USER_UPDATE_SUCCESS;
        }

        return USER_UPDATE_FAIL;
    }
}
