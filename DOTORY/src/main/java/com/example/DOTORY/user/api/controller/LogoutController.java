package com.example.DOTORY.user.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.user.application.LogoutService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class LogoutController {
    @Autowired
    private LogoutService logoutService;

    @Operation(summary = "로그아웃", description = "사용자의 로그인 상태를 로그아웃으로 변경합니다.")
    @PostMapping("/logoutConfirm")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        logoutService.logoutAndUpdateStatus(token);
        return ResponseEntity.ok(ApiResponse.onSuccess("로그아웃 완료"));
    }

}
