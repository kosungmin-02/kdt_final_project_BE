package com.example.DOTORY.post.domain.entity;

import com.example.DOTORY.user.domain.entity.UserEntity;
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

    // Comment의 경우에도 UserEntity와 연결하였을때 외래키 이름은 userPK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userPK")
    private UserEntity user;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent; //대댓글

    @Builder.Default
    private boolean isdeleted = false; // 삭제 여부

}
