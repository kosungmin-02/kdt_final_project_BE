package com.example.DOTORY.post.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Decoration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long decorationId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "postId")
    private Post post;


    private String frameColor;       // ex: #ffffff
    private String framePattern;     // none | dots | stripe | grid | paper
    private String patternColor;     // ex: #000000
    private double patternOpacity;   // 0~1
    private int rotation;            // 0, 90, 180, 270
    private String keywords;         // JSON 또는 CSV 형태로 저장 가능
}
