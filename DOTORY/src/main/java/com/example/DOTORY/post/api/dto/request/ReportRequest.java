// 사용자용 - 사용자가 신고할때 서버로 보내는 요청 DTO

package com.example.DOTORY.post.api.dto.request;

import com.example.DOTORY.post.domain.entity.ReportReason;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequest {

    private int reporterUserPK;      // 신고자 (로그인한 사용자)
    private int reportedUserPK;      // 신고 당한 사람 (게시글 작성자)
    private Long postId;             // 신고할 게시글 ID
    private ReportReason reason;     // 신고 사유 (ENUM)
    private String reportContent;      // 상세 설명
    private LocalDateTime reportDate; // 신고한 날짜
}
