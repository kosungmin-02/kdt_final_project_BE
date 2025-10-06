package com.example.DOTORY.post.domain.entity;

import com.example.DOTORY.user.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    // 신고자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userPK")
    private UserEntity user;

    // 피신고자 (댓글 작성자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_userPK")
    private UserEntity reportedUser;

    // 신고 대상 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Comment comment;  // Reply 엔티티를 FK로 연결

    // 신고 카테고리 (총 6개)
    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    // 신고 상세 이유
    private String reportContent;

    // 신고 날짜
    private LocalDateTime reportDate;

    // 신고 처리 여부
    @Enumerated(EnumType.STRING)
    private ReportConfirm reportConfirm = ReportConfirm.WAITING;

    // 신고 처리 완료 날짜
    private LocalDateTime confirmDate;

    // 처리 완료(COMPLETE)되면 confirmDate 자동 저장
    public void setReportConfirm(ReportConfirm reportConfirm) {
        this.reportConfirm = reportConfirm;
        if (reportConfirm == ReportConfirm.COMPLETE) {
            this.confirmDate = LocalDateTime.now();
        }
    }
}
