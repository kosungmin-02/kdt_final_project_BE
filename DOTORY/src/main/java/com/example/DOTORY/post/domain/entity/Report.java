package com.example.DOTORY.post.domain.entity;

import com.example.DOTORY.post.domain.ReportReason;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private Long userId; // TODO 추후 USER 엔티티와 연결, 신고자 id

    @Enumerated(EnumType.STRING)
    private ReportReason reason; // Enum: SPAM, ABUSE, COPYRIGHT

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    private String description; // 신고 상세 사유




}
