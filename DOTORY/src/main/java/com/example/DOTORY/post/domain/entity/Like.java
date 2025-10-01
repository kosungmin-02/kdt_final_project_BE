package com.example.DOTORY.post.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

//    @ManyToOne(fetch = FetchType.LAZY)
    private Long userId; // TODO 추후 USER 엔티티 연결

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;


}
