package com.example.DOTORY.user.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.user.api.dto.UserDTO;
import com.example.DOTORY.user.application.LoginService;
import com.example.DOTORY.user.application.SNSLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "로그인 API", description = "일반 로그인과 SNS 로그인 (카카오 / 네이버)")
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")   // 프론트와 연결을 위해 3000 -> 5173으로 변경.
@Slf4j
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SNSLoginService snsLoginService;

    // 일반 로그인
    @Operation(summary = "로그인 - 일반 로그인", description = "아이디, 비밀번호를 입력하는 일반 로그인 기능")
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
    @Operation(summary = "로그인 - 카카오 로그인", description = "sns로그인 중 카카오 로그인.")
    @PostMapping("/sns/kakao")
    public ResponseEntity<ApiResponse<Map<String, String>>> kakaoLogin(@RequestBody Map<String, String> body) {
        String token = snsLoginService.loginWithKakaoAndReturnJwt(
                body.get("providerID"), body.get("email"), body.get("userName")
        );
        return ResponseEntity.ok(ApiResponse.onSuccess(Map.of("token", token)));
    }

    // 네이버 SNS 로그인
    @Operation(summary = "로그인 - 네이버 로그인", description = "sns 로그인 중 네이버 로그인.")
    @PostMapping("/sns/naver")
    public ResponseEntity<ApiResponse<Map<String, String>>> naverLogin(@RequestBody Map<String, String> body) {
        String token = snsLoginService.loginWithNaverAndReturnJwt(
                body.get("providerID"), body.get("email"), body.get("userName")
        );
        return ResponseEntity.ok(ApiResponse.onSuccess(Map.of("token", token)));
    }
}
