package com.example.DOTORY.user.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.user.api.dto.UserDTO;
import com.example.DOTORY.user.application.LoginService;
import com.example.DOTORY.user.application.SNSLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@Slf4j
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SNSLoginService snsLoginService;

    // 일반 로그인
    @PostMapping("/loginConfirm")
    public ResponseEntity<ApiResponse<Map<String, String>>> loginConfirm(@RequestBody UserDTO userDTO) {
        String token = loginService.loginConfirm(userDTO);
        if (token != null) {
            return ResponseEntity.ok(ApiResponse.onSuccess(Map.of("token", token)));
        } else {
            return ResponseEntity.status(401)
                    .body(ApiResponse.onFailure("LOGIN_FAIL", "로그인 실패", null));
        }
    }

    // 카카오 SNS 로그인
    @PostMapping("/sns/kakao")
    public ResponseEntity<ApiResponse<Map<String, String>>> kakaoLogin(@RequestBody Map<String, String> body) {
        String token = snsLoginService.loginWithKakaoAndReturnJwt(
                body.get("providerID"), body.get("email"), body.get("userName")
        );
        return ResponseEntity.ok(ApiResponse.onSuccess(Map.of("token", token)));
    }

    // 네이버 SNS 로그인
    @PostMapping("/sns/naver")
    public ResponseEntity<ApiResponse<Map<String, String>>> naverLogin(@RequestBody Map<String, String> body) {
        String token = snsLoginService.loginWithNaverAndReturnJwt(
                body.get("providerID"), body.get("email"), body.get("userName")
        );
        return ResponseEntity.ok(ApiResponse.onSuccess(Map.of("token", token)));
    }
}
