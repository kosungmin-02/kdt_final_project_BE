package com.example.DOTORY.admin.api.controller;

import com.example.DOTORY.admin.api.dto.AdminCheckUserReportDTO;
import com.example.DOTORY.admin.api.dto.AdminReportResponseDTO;
import com.example.DOTORY.admin.api.dto.AdminReportUpdateRequestDTO;
import com.example.DOTORY.admin.application.AdminReportService;
import com.example.DOTORY.global.code.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@Tag(name = "AdminReport", description = "관리자 신고 관리 API")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @Operation(summary = "전체 신고 목록 조회", description = "게시글 및 댓글 신고 목록 조회, 카테고리 필터 가능")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminReportResponseDTO>>> getAllReports(
            @Parameter(description = "카테고리 이름") @RequestParam(required = false) String category
    ) {
        List<AdminReportResponseDTO> reports = adminReportService.getAllReports(category);
        return ResponseEntity.ok(ApiResponse.onSuccess(reports));
    }

    @Operation(summary = "신고 카테고리 목록 조회", description = "신고 가능한 모든 카테고리 목록 조회")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getReportCategories() {
        List<String> categories = adminReportService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.onSuccess(categories));
    }

    @Operation(summary = "신고 상세 조회", description = "신고 ID와 댓글 여부를 기반으로 신고 상세 정보 조회")
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<AdminReportResponseDTO>> getReportById(
            @Parameter(description = "신고 ID") @PathVariable Long reportId,
            @Parameter(description = "댓글 신고 여부 (true = 댓글, false = 게시글)") @RequestParam boolean isComment
    ) {
        AdminReportResponseDTO report = adminReportService.getReportById(reportId, isComment);
        return ResponseEntity.ok(ApiResponse.onSuccess(report));
    }

    @Operation(summary = "신고 처리 / 답변 등록", description = "신고 상태 및 처리 내용을 등록")
    @PostMapping("/{reportId}/update")
    public ResponseEntity<ApiResponse<Void>> updateReport(
            @Parameter(description = "신고 ID") @PathVariable Long reportId,
            @Parameter(description = "신고 처리 DTO") @RequestBody AdminReportUpdateRequestDTO dto
    ) {
        adminReportService.updateReport(reportId, dto);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "특정 유저 신고 내역 조회", description = "특정 유저가 신고당한 게시글/댓글 목록 조회")
    @GetMapping("/user/{userPk}")
    public ResponseEntity<ApiResponse<List<AdminCheckUserReportDTO>>> getReportsByUser(
            @Parameter(description = "사용자 PK") @PathVariable int userPk
    ) {
        List<AdminCheckUserReportDTO> reports = adminReportService.getReportsByUser(userPk);
        return ResponseEntity.ok(ApiResponse.onSuccess(reports));
    }
}
