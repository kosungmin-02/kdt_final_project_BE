package com.example.DOTORY.admin.api.controller;

import com.example.DOTORY.admin.api.dto.UpdateAgreeDTO;
import com.example.DOTORY.admin.api.dto.UserInfoDTO;
import com.example.DOTORY.user.api.dto.AgreeDTO;
import com.example.DOTORY.admin.application.AdminAgreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Admin Agree API", description = "관리자 기능 - 약관동의 관리하기 ( 전체 약관 조회 / 약관 수정 / 약관 동의 회원 목록 조회)")
@RestController
@RequestMapping("/api/admin/agree")
@RequiredArgsConstructor
public class AdminAgreeController {

    private final AdminAgreeService adminAgreeService;

    // 약관 전체 목록 조회하기
    @Operation(summary = "전체 약관 목록 조회", description = "관리자가 전체 약관 목록을 조회할 수 있음.")
    @GetMapping
    public List<AgreeDTO> getAllAgreements() {
        return adminAgreeService.getAllAgree();
    }

    // 약관 수정하기
    @Operation(summary = "약관 수정", description = "관리자가 약관 내용을 수정할 수 있고, 제목 / 내용 / 타입 모두 변경 가능하지만 만약 타입이 FORADMIN이면 해당 약관은 아직 수정중인것으로 회원가입시 노출 X ")
    @PutMapping("/{agreeId}")
    public AgreeDTO updateAgreement(
            @Parameter(description = "수정할 약관 ID") @PathVariable int agreeId,
            @RequestBody UpdateAgreeDTO dto
    ) {
        return adminAgreeService.updateAgree(
                agreeId,
                dto.title(),
                dto.content(),
                dto.type()
        );
    }

    // 선택약관 동의한 사용자 목록 조회하기
    @Operation(summary = "선택약관 동의 사용자 조회", description = "특정 선택약관에 동의한 사용자 목록 조회 가능.")
    @GetMapping("/{agreeId}/users")
    public List<UserInfoDTO> getUsersAgreedForOptional(
            @Parameter(description = "약관 ID") @PathVariable int agreeId
    ) {
        return adminAgreeService.getUsersAgreedForOptional(agreeId);
    }
}
