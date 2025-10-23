// ProfileDecorationController.java
package com.example.DOTORY.user.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.global.jwt.JwtProvider;
import com.example.DOTORY.user.api.dto.ProfileDecorationRequestDTO;
import com.example.DOTORY.user.api.dto.ProfileDecorationResponseDTO;
import com.example.DOTORY.user.application.ProfileDecorationService;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
// ... (기존 import) ...

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/decorations")
@Tag(name = "프로필 꾸미기 API", description = "사용자 프로필 배경 및 스티커 설정 관리") // 컨트롤러 전체에 대한 태그 추가
public class ProfileDecorationController {

    private final ProfileDecorationService decorationService;
    private final JwtProvider jwtProvider; // 주입
    private final UserRepository userRepository; // 주입

    // 기존 UserController의 패턴을 따라 토큰에서 userID(String)를 추출하는 유틸리티 메서드
    private String getCurrentUserID(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED, "로그인 정보가 없습니다."); // 예시 에러 처리
        }
        String token = authHeader.substring(7);
        log.info("Extracted Token: {}", token);
        return jwtProvider.getUserIdFromToken(token);
    }


    // UserPK(int)가 필요한 경우를 위해 ID로 엔티티 조회
    private int getCurrentUserPK(String userID) {
        UserEntity user = userRepository.findByUserID(userID)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return user.getUserPK();
    }


    /**
     * POST /api/decorations/upload
     * 배경 또는 스티커 이미지를 서버에 업로드하고 URL을 반환합니다.
     */
    @Operation(summary = "이미지 업로드", description = "배경 또는 스티커로 사용할 이미지 파일을 서버에 업로드하고 **URL을 반환**합니다.")
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) {
        
        log.info("ProfileDecorationController에서 uploadImage 호출");
        String userID = getCurrentUserID(authHeader);

        String imageUrl = decorationService.uploadDecorationImage(userID, file); // String userId 전달

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(imageUrl));
    }

    // 2. 설정 저장/수정 (PUT /api/decorations)
    @Operation(summary = "프로필 꾸미기 설정 저장/수정", description = "현재 사용자의 배경 및 스티커 정보를 저장 또는 덮어씁니다.")
    @PutMapping
    public ResponseEntity<ApiResponse<ProfileDecorationResponseDTO>> saveOrUpdateDecoration(
            @RequestBody ProfileDecorationRequestDTO requestDTO,
            @RequestHeader("Authorization") String authHeader
    ) {
        String userID = getCurrentUserID(authHeader);
        int userPK = getCurrentUserPK(userID); // UserPK(Long)가 Service에 필요하다면

        ProfileDecorationResponseDTO responseDTO = decorationService.saveOrUpdateDecoration(userPK, requestDTO);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(responseDTO));
    }

    // ... (getDecoration 및 deleteDecoration 메서드에서도 인증 로직을 위 getCurrentUserID/PK로 대체하여 사용)

    @Operation(summary = "프로필 꾸미기 설정 조회", description = "특정 사용자의 현재 꾸미기 설정을 조회합니다.")
    @GetMapping("/{userPK}") // <--- 바로 이 메서드입니다.
    public ResponseEntity<ApiResponse<ProfileDecorationResponseDTO>> getDecoration(
            @Parameter(description = "조회 대상 사용자 ID", required = true) @PathVariable int userPK) {
        ProfileDecorationResponseDTO responseDTO = decorationService.getDecoration(userPK);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(responseDTO));
    }

    /**
     * DELETE /api/decorations
     * 현재 사용자의 모든 꾸미기 설정을 초기화합니다.
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteDecoration(
            @RequestHeader("Authorization") String authHeader
    ) {
        String userID = getCurrentUserID(authHeader);
        int userPK = getCurrentUserPK(userID);

        decorationService.deleteDecoration(userPK);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(null));
    }
}



