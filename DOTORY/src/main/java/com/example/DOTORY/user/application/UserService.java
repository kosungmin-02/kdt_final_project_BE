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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

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

    // 이메일 중복 체크
    public void checkEmailDuplicate(String userEmail) {
        if (userRepository.findByUserEmail(userEmail).isPresent()) {
            throw new GeneralException(ErrorStatus.DUPLICATE_RESOURCE, "이미 사용 중인 이메일입니다.");
        }
    }


    public UserDTO profileChange(String loginedID) {
        log.info("profileChange()");

        UserEntity userEntity = userRepository.findByUserID(loginedID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        String avatar = userEntity.getUserAvatar();

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
                new ArrayList<>(),
                avatar
        );
    }

    public void profileChangeConfirm(UserDTO userDTO, MultipartFile userAvatar) {
        log.info("profileChangeConfirm()");

        UserEntity userEntity = userRepository.findByUserPK(userDTO.userPK())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 비밀번호 업데이트
        if (userDTO.userPassword() != null && !userDTO.userPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(userDTO.userPassword());
            userEntity.setUserPassword(encodedPassword);
        }

        // 이름/닉네임 업데이트
        userEntity.setUserName(userDTO.userName());
        userEntity.setUserNickname(userDTO.userNickname());
        userEntity.setUpdatedDate(LocalDateTime.now());

        // 아바타 처리
        if (userAvatar != null) {
            if (!userAvatar.isEmpty()) {
                // 새 이미지 업로드: 기존 이미지 삭제
                deleteAvatarFile(userEntity.getUserAvatar());
                String avatarUrl = saveAvatarFile(userAvatar, userEntity.getUserID());
                userEntity.setUserAvatar(avatarUrl);
            } else {
                // 빈 MultipartFile로 넘어온 경우 → 아바타 제거 후 기본 이미지로
                deleteAvatarFile(userEntity.getUserAvatar());
                userEntity.setUserAvatar(null); // 프론트에서 Optional로 기본 이미지 처리
            }
        }

        userRepository.save(userEntity);
    }

    // 기존 아바타 삭제
    private void deleteAvatarFile(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) return;
        try {
            Path path = Paths.get("avatars/" + avatarPath.substring("/avatars/".length()));
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (Exception e) {
            log.warn("기존 아바타 삭제 실패: " + avatarPath, e);
        }
    }


    // 프로필 이미지 파일 저장 메서드
    private String saveAvatarFile(MultipartFile file, String userID) {
        String filename = userID + "_" + System.currentTimeMillis() + ".png";
        Path path = Paths.get("avatars/" + filename);
        try {
            Files.createDirectories(path.getParent());
            file.transferTo(path);
            return "/avatars/" + filename; // 프론트에서 접근 가능한 URL
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAIL, "아바타 저장 실패");
        }
    }

    // 비번 변경
    @Transactional
    public void changePassword(String userID, String currentPassword, String newPassword) {
        log.info("UserService - changePassword");

        UserEntity userEntity = userRepository.findByUserID(userID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 1) 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, userEntity.getUserPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
        }

        // 2) 새 비밀번호 암호화 후 저장
        String encodedNew = passwordEncoder.encode(newPassword);
        userEntity.setUserPassword(encodedNew);
        userEntity.setUpdatedDate(LocalDateTime.now());

        userRepository.save(userEntity);
    }
    /**
     * 프로필 배경 이미지 파일 저장 메서드
     * @param file 업로드할 파일
     * @param userID 파일명 식별자로 사용할 사용자 ID
     * @return 프론트에서 접근 가능한 URL
     */
    public String saveDecorationFile(MultipartFile file, String userID) {
        if (file == null || file.isEmpty()) {
            throw new GeneralException(ErrorStatus.FILE_NOT_FOUND, "업로드할 파일이 없습니다.");
        }

        // decorations 폴더 아래에 저장
        String filename = userID + "_bg_" + System.currentTimeMillis() + ".png";
        Path path = Paths.get("decorations/" + filename); // 경로 변경

        try {
            Files.createDirectories(path.getParent());
            file.transferTo(path);
            return "/decorations/" + filename; // 프론트에서 접근 가능한 URL
        } catch (IOException e) {
            log.error("배경 이미지 저장 실패", e);
            throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAIL, "배경 이미지 저장 실패");
        }
    }

    /**
     * 기존 프로필 배경 이미지 파일을 삭제하는 메서드
     * @param decorationPath 삭제할 파일의 URL 경로
     */
    public void deleteDecorationFile(String decorationPath) {
        if (decorationPath == null || decorationPath.isBlank()) return;

        try {
            // URL에서 실제 파일 시스템 경로로 변환 (예: /decorations/... -> decorations/...)
            String relativePath = decorationPath.startsWith("/") ? decorationPath.substring(1) : decorationPath;
            Path path = Paths.get(relativePath);

            if (Files.exists(path)) {
                Files.delete(path);
                log.info("기존 배경 이미지 삭제 성공: {}", decorationPath);
            }
        } catch (Exception e) {
            log.warn("기존 배경 이미지 삭제 실패: " + decorationPath, e);
        }
    }
}