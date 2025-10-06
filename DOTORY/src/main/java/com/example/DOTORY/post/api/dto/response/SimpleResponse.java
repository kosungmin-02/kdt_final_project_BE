// 사용자용 - 신고가 잘 되었는지 결과를 볼 때 서버가 반환하는 응답용 신고 dto
package com.example.DOTORY.post.api.dto.response;

import com.example.DOTORY.post.domain.entity.ReportConfirm;
import com.example.DOTORY.post.domain.entity.ReportReason;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleResponse {

    private String message;

    private Long reportId;
    private Long postId;      // 게시글 신고일 경우 사용
    private Long commentId;   // 댓글 신고일 경우 사용
    private int userPK;       // 신고한 사람
    private int reportedUserPK;  // 신고 당한 사람
    private ReportReason reason;
    private String reportContent;
    private LocalDateTime reportDate;
    private ReportConfirm reportConfirm;
}

