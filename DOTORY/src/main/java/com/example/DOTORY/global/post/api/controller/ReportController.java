package com.example.DOTORY.global.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.post.api.dto.request.ReportRequest;
import com.example.DOTORY.global.post.api.dto.response.SimpleResponse;
import com.example.DOTORY.global.post.application.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 게시글 신고
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> reportPost(@RequestBody ReportRequest request) {
        SimpleResponse response = reportService.reportPost(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response.getReportId()));
    }
}
