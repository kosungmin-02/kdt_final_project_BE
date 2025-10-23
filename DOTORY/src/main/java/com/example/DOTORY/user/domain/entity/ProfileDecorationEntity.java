package com.example.DOTORY.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "profile_decoration")
public class ProfileDecorationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "decoration_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // UserEntity와의 관계

    @Column(name = "background_image_url")
    private String backgroundImageUrl;

    @Column(name = "background_opacity")
    private Double backgroundOpacity;

    // StickerEntity와 1:N 관계 설정 (cascade = ALL로 Decoration 삭제 시 스티커도 삭제)
    @OneToMany(mappedBy = "decoration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StickerEntity> stickers = new ArrayList<>();

    // 수정 로직
    public void updateDecoration(String backgroundImageUrl, Double backgroundOpacity) {
        this.backgroundImageUrl = backgroundImageUrl;
        this.backgroundOpacity = backgroundOpacity;
    }
}
