package com.example.DOTORY.admin.api.dto;

import com.example.DOTORY.post.domain.entity.ReportConfirm;
import java.time.LocalDateTime;

public record AdminReportResponseDTO(
        Long reportId,
        String reportType,     // 게시글 / 댓글
        String reporterName,
        String reportedName,
        String categoryName,
        String reportContent,
        LocalDateTime reportDate,
        ReportConfirm reportConfirm,
        LocalDateTime confirmDate,
        String confirmMessage,
        Long targetId          // 게시글 ID 또는 댓글 ID
) {
    public static AdminReportResponseDTO of(
            Long reportId,
            String reportType,
            String reporterName,
            String reportedName,
            String categoryName,
            String reportContent,
            LocalDateTime reportDate,
            ReportConfirm reportConfirm,
            LocalDateTime confirmDate,
            String confirmMessage,
            Long targetId
    ) {
        return new AdminReportResponseDTO(
                reportId,
                reportType,
                reporterName,
                reportedName,
                categoryName,
                reportContent,
                reportDate,
                reportConfirm,
                confirmDate,
                confirmMessage,
                targetId
        );
    }
}
