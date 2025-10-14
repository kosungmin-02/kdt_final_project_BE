package com.example.DOTORY.user.application;

import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.user.api.dto.UserDTO;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserEntity registerConfirm(UserDTO userDTO) {
        log.info("UserService - registerConfirm");

        if (userRepository.findByUserID(userDTO.userID()).isPresent()) {
            throw new GeneralException(ErrorStatus.DUPLICATE_RESOURCE, "이미 존재하는 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.userPassword());

        UserEntity userEntity = UserEntity.builder()
                .userID(userDTO.userID())
                .userPassword(encodedPassword)
                .userEmail(userDTO.userEmail())
                .userName(userDTO.userName())
                .userNickname(userDTO.userNickname())
                .build();

        try {
            return userRepository.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            throw new GeneralException(ErrorStatus.DUPLICATE_RESOURCE, "아이디 또는 이메일이 중복됩니다.");
        } catch (Exception e) {
            log.error("회원 가입 중 오류 발생", e);
            throw new GeneralException(ErrorStatus.DATABASE_ERROR);
        }
    }

    public UserDTO profileChange(String loginedID) {
        log.info("profileChange()");

        UserEntity userEntity = userRepository.findByUserID(loginedID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return new UserDTO(
                userEntity.getUserPK(),
                userEntity.getUserID(),
                null, // 비밀번호는 절대 반환하지 않음
                userEntity.getUserName(),
                userEntity.getUserNickname(),
                userEntity.getUserEmail(),
                userEntity.getUserRole(),
                userEntity.getCreatedDate().toString(),
                userEntity.getUpdatedDate().toString(),
                new ArrayList<>()
        );
    }

    public void profileChangeConfirm(UserDTO userDTO) {
        log.info("profileChangeConfirm()");

        UserEntity userEntity = userRepository.findByUserPK(userDTO.userPK())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        String encodedPassword = passwordEncoder.encode(userDTO.userPassword());

        userEntity.setUserPassword(encodedPassword);
        userEntity.setUserName(userDTO.userName());
        userEntity.setUserNickname(userDTO.userNickname());
        userEntity.setUpdatedDate(LocalDateTime.now());

        userRepository.save(userEntity);
    }
}