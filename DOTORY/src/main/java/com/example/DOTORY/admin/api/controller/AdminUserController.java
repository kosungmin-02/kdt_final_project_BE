package com.example.DOTORY.admin.api.controller;

import com.example.DOTORY.admin.api.dto.AdminCheckUserDTO;
import com.example.DOTORY.admin.application.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "AdminUserController", description = "관리자 기능 - 회원 관리 시스템")
public class AdminUserController {

    private final AdminUserService adminUserService;

    // 전체 회원 조회하기
    @Operation(summary = "전체 회원 조회",
            description = "관리자가 전체 회원 목록 조회할 수 있습니다.")

    @GetMapping
    public ResponseEntity<List<AdminCheckUserDTO>> getAllUsers() {
        List<AdminCheckUserDTO> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 특정 회원 조회하기
    @Operation(summary = "회원 상세 조회", description = "회원 PK가 회원 고유번호로, 관리자가 회원 목록에서 특정 회원의 상세 정보를 조회할 수 있게 해줍니다.")
    @GetMapping("/{userPK}")
    public ResponseEntity<AdminCheckUserDTO> getUserDetail(@PathVariable int userPK) {
        AdminCheckUserDTO userDetail = adminUserService.getUserDetail(userPK);
        return ResponseEntity.ok(userDetail);
    }

    // 회원 강제 탈퇴
    @Operation(summary = "회원 강제 탈퇴", description = "회원 PK를 사용해서 회원 강제 탈퇴 시킬 수 있습니다. 도토리는 강제탈퇴 / 자진탈퇴 / 활동중 을 갖습니다. - UserEntity에 UserStatus 참고")
    @DeleteMapping("/{userPK}")
    public ResponseEntity<String> deleteUser(@PathVariable int userPK) {
        adminUserService.DeleteUser(userPK);
        return ResponseEntity.ok("강제 탈퇴 처리 완료");
    }
}
