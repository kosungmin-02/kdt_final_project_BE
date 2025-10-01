package com.example.DOTORY.post.api.dto.request;

import com.example.DOTORY.post.domain.ReportReason;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequest {

    private Long userId;        // 신고자
    private Long postId;        // 신고할 게시글 ID
    private ReportReason reason; // 신고 사유 (ENUM)
    private String description; // 상세 설명



}
