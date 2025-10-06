package com.example.DOTORY.post.api.dto.request;

import com.example.DOTORY.post.domain.entity.ReportReason;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReportCommentRequest {
    private Long commentId;      // 신고 대상 댓글
    private int reporterUserPK;  // 신고자
    private ReportReason reason; // 신고 이유
    private String reportContent; // 신고 상세 내용
    private LocalDateTime reportDate; // 신고 날짜
}
