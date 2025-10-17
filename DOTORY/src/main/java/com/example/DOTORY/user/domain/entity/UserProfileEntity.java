package com.example.DOTORY.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int profileID;

    // UserEntity와 1:1 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERPK", nullable = false)
    private UserEntity user;

    // 변수명은 프론트랑 동일하게 했어요
    private String birth;
    private String mbti;
    private String bio;
}
