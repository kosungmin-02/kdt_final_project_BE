package com.example.DOTORY.user.application;

import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.user.api.dto.ProfileDecorationRequestDTO;
import com.example.DOTORY.user.api.dto.ProfileDecorationResponseDTO;
import com.example.DOTORY.user.domain.entity.ProfileDecorationEntity;
import com.example.DOTORY.user.domain.entity.StickerEntity;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserProfileDecorationRepository;
import com.example.DOTORY.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileDecorationService {

    private final UserProfileDecorationRepository decorationRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * 이미지 파일을 업로드하고 접근 가능한 URL을 반환합니다. (배경/스티커 공용)
     */
    public String uploadDecorationImage(String userId, MultipartFile file) {
        return userService.saveDecorationFile(file, userId);
    }

    /**
     * 프로필 꾸미기 설정을 저장하거나 업데이트합니다.
     */
    public ProfileDecorationResponseDTO saveOrUpdateDecoration(Long userId, ProfileDecorationRequestDTO requestDTO) {
        // 1. userRepository.findById()에 Long 타입 PK를 직접 전달하도록 수정
        UserEntity user = userRepository.findById(Math.toIntExact(userId)) // TODO: findById랑 타입이 안 맞아서 강제 형변환. 숫자 유의
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        ProfileDecorationEntity decoration = decorationRepository.findByUser_userPK(userId)
                .orElseGet(() -> ProfileDecorationEntity.builder()
                        .user(user)
                        .backgroundImageUrl(requestDTO.backgroundImageUrl())
                        .backgroundOpacity(requestDTO.backgroundOpacity())
                        .stickers(new ArrayList<>())
                        .build());

        // 3. Decoration 기본 정보 업데이트
        decoration.updateDecoration(requestDTO.backgroundImageUrl(), requestDTO.backgroundOpacity());

        // 4. 기존 스티커 제거 및 새로운 스티커 추가
        decoration.getStickers().clear();

        List<StickerEntity> newStickers = requestDTO.stickers() == null ? List.of() :
                requestDTO.stickers().stream()
                        .map(stickerDTO -> StickerEntity.builder()
                                .imageUrl(stickerDTO.imageUrl())
                                .xPosition(stickerDTO.xPosition())
                                .yPosition(stickerDTO.yPosition())
                                .scale(stickerDTO.scale())
                                .rotation(stickerDTO.rotation())
                                .zIndex(stickerDTO.zIndex())
                                .decoration(decoration)
                                .build())
                        .collect(Collectors.toList());


        decoration.getStickers().addAll(newStickers);

        // 5. 저장
        ProfileDecorationEntity savedDecoration = decorationRepository.save(decoration);
        return ProfileDecorationResponseDTO.from(savedDecoration);
    }

    /**
     * 프로필 꾸미기 설정을 초기화하고 관련 파일을 삭제합니다.
     */
    public void deleteDecoration(Long userId) {
        ProfileDecorationEntity decoration = decorationRepository.findByUser_userPK(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DATABASE_ERROR));

        // 1. 배경 이미지 파일 삭제 (getBackgroundImageUrl()로 수정)
        if (decoration.getBackgroundImageUrl() != null) {
            userService.deleteDecorationFile(decoration.getBackgroundImageUrl());
        }

        // 2. 모든 스티커 이미지 파일 삭제
        for (StickerEntity sticker : decoration.getStickers()) {
            userService.deleteDecorationFile(sticker.getImageUrl());
        }

        // 3. DB 레코드 삭제
        decorationRepository.delete(decoration);
    }

    // 프로필 조회하기
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ProfileDecorationResponseDTO getDecoration(Long userPK) {
        ProfileDecorationEntity decoration = decorationRepository.findByUser_userPK(userPK)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DECORATION_NOT_FOUND));

        return ProfileDecorationResponseDTO.from(decoration);
    }

}