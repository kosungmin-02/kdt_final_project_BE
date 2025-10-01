package com.example.DOTORY.post.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false, length = 1000)
    private String content;

//    @ManyToOne(fetch = FetchType.LAZY)
    private Long userId; //TODO 추후 USER 엔티티 연동 예정

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent; //대댓글

    private boolean isdeleted = false; // 삭제 여부

}
