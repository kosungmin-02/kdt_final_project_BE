package com.example.DOTORY.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "sticker")
public class StickerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sticker_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decoration_id", nullable = false)
    private ProfileDecorationEntity decoration; // ProfileDecorationEntity와 N:1 관계

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "x_position", nullable = false)
    private Double xPosition;

    @Column(name = "y_position", nullable = false)
    private Double yPosition;

    @Column(name = "scale_factor", nullable = false)
    private Double scale; // scale 필드명 변경 (DB 예약어 충돌 방지)

    @Column(name = "rotation_degree", nullable = false)
    private Double rotation;

    @Column(name = "z_index", nullable = false)
    private Integer zIndex;

    // 연관 관계 편의 메서드
    public void setDecoration(ProfileDecorationEntity decoration) {
        this.decoration = decoration;
    }

}