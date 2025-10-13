package com.example.DOTORY.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import com.example.DOTORY.post.api.dto.request.ReportCommentRequest;
import com.example.DOTORY.post.api.dto.request.ReportRequest;
import com.example.DOTORY.post.api.dto.response.SimpleResponse;
import com.example.DOTORY.post.application.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 게시글 신고 (로그인 필요)
    @Operation(summary = "부적절한 게시글 신고", description = "로그인한 사용자만이 게시글을 신고할 수 있음.")
    @PostMapping
    public ResponseEntity<ApiResponse<SimpleResponse>> reportPost(
            @RequestBody ReportRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        request.setReporterUserPK(userPK);

        SimpleResponse response = reportService.reportPost(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    // 댓글 신고 (로그인 필요)
    @Operation(summary = "부적절한 댓글 신고", description = "로그인한 사용자만이 댓글을 신고할 수 있음.")
    @PostMapping("/comment")
    public ResponseEntity<ApiResponse<SimpleResponse>> reportComment(
            @RequestBody ReportCommentRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        request.setReporterUserPK(userPK); // 로그인한 사용자의 PK 설정

        SimpleResponse response = reportService.reportComment(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    // 사용자의 신고 내역 조회용 컨트롤러
    @Operation(summary = "사용자 신고 내역 조회", description = "사용자가 어떤 게시물/댓글을 신고했는지 조회할 수 있음.")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SimpleResponse>>> getUserReportHistory(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        int userPK = principal.getUser().getUserPK();
        List<SimpleResponse> history = reportService.getUserReportHistory(userPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(history));
    }
}
