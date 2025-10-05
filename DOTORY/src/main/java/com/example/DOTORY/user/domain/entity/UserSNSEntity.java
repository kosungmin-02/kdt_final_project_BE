package com.example.DOTORY.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="USERSNS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSNSEntity extends BaseEntity {   // ← BaseEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SOCIALPK")
    private Long socialPK;

    // 한 명의 사용자는 여러 SNS 로그인(여기선 2개) 가질 수 있다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERPK", nullable = false)
    private UserEntity user;

    // 로그인 제공자 (카카오 / 네이버)
    @Enumerated(EnumType.STRING)
    @Column(name = "PROVIDER", nullable = false)
    private Provider provider;

    // 네이버와 카카오에서 발급하는 고유 ID
    @Column(name = "PROVIDERID", nullable = false, unique = true)
    private String providerID;

    // createdDate, updatedDate는 BaseEntity에서 관리되므로 삭제

    // Enum: SNS 제공자
    public enum Provider {
        KAKAO,
        NAVER
    }
}
