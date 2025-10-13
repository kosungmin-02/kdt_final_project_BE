package com.example.DOTORY.admin.api.dto;

import java.time.LocalDateTime;

public record AdminCheckUserReportDTO(
        Long reportId,
        String targetType,
        Long targetId,
        String reason,                 // enum 이름 ( 신고 사유 카테고리 )
        String categoryName,      // 한글 설명
        String reportContent,
        LocalDateTime reportDate,
        String reportConfirm,          // enum 이름 ( 신고 처리 여부 )
        String confirmDescription,     // 한글 설명
        LocalDateTime confirmDate,
        String confirmReason
) {
}
