package com.example.DOTORY.user.api.controller;

import com.example.DOTORY.global.jwt.JwtProvider;
import com.example.DOTORY.user.api.dto.UserProfileUpdateRequestDTO;
import com.example.DOTORY.user.application.UserProfileService;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.entity.UserProfileEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService profileService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    // GET API: 로그인 유저 프로필 조회
    @Operation(summary = "로그인 유저 프로필 조회 - 프론트 연동용", description = "로그인한 유저의 프로필 조회 - mbti, bio(한줄소개), birth(생년월일), avatar는 프로필 사진")
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("로그인 정보가 없습니다.");
        }

        try {
            String token = authHeader.substring(7);
            String userId = jwtProvider.getUserIdFromToken(token);
            UserEntity user = userRepository.findByUserID(userId)
                    .orElseThrow(() -> new RuntimeException("유저가 없습니다"));

            UserProfileEntity profile = profileService.getProfile(user);

            // 프론트와 맞는 구조로 변환
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userPK", user.getUserPK());
            userMap.put("userID", user.getUserID());
            userMap.put("userAvatar", user.getUserAvatar());

            result.put("user", userMap);
            result.put("birth", profile.getBirth());
            result.put("mbti", profile.getMbti());
            result.put("bio", profile.getBio());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 오류");
        }
    }

    @Operation(summary = "프로필 업데이트", description = "프론트의 프로필 업데이트용 (mbti, bio, birth)")
    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserProfileUpdateRequestDTO request
    ) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("로그인 정보가 없습니다.");
        }

        String token = authHeader.substring(7);
        String userId = jwtProvider.getUserIdFromToken(token);
        UserEntity user = userRepository.findByUserID(userId)
                .orElseThrow(() -> new RuntimeException("유저가 없습니다"));

        profileService.updateProfile(user, request);
        return ResponseEntity.ok("프로필이 성공적으로 저장되었습니다.");
    }


}
