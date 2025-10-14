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
public class ReportPost {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    // 신고자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userPK")
    private UserEntity user;

    // 피신고자 (게시글 작성자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_userPK")
    private UserEntity reportedUser;

    // 신고 대상 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ReportCategory category;  // DB에서 관리되는 신고 카테고리


    // 신고 상세 이유
    private String reportContent;

    // 신고 날짜
    private LocalDateTime reportDate;

    // 처리 내용
    private String confirmMessage;

    // 신고 처리 여부
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportConfirm reportConfirm = ReportConfirm.WAITING;

    // 신고 처리 완료 날짜
    private LocalDateTime confirmDate;

    // 처리 완료(COMPLETE)되면 바로 처리 완료 날짜에 날짜 자동으로 저장하기.
    public void setReportConfirm(ReportConfirm reportConfirm) {
        this.reportConfirm = reportConfirm;
        if (reportConfirm == ReportConfirm.COMPLETE) {
            this.confirmDate = LocalDateTime.now();
        }
    }
}

