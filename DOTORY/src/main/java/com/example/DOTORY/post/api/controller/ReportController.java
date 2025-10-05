package com.example.DOTORY.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import com.example.DOTORY.post.api.dto.request.ReportRequest;
import com.example.DOTORY.post.api.dto.response.SimpleResponse;
import com.example.DOTORY.post.application.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 게시글 신고 (로그인 필요)
    @PostMapping
    public ResponseEntity<ApiResponse<SimpleResponse>> reportPost(
            @RequestBody ReportRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        request.setReporterUserPK(userPK);

        SimpleResponse response = reportService.reportPost(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

}
