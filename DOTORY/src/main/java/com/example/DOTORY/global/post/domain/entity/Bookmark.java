package com.example.DOTORY.global.post.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;

//    @ManyToOne(fetch = FetchType.LAZY)
    private Long userId; // TODO 추후 USER 엔티티와 연결

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;




}
