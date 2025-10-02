package com.example.DOTORY.user.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// 약관 동의의 경우, 필수 약관은 무조건 동의해야 회원가입할 수 있다.
// 따라서 이 ERD는 사용자가 어떤 선택약관을 동의했는지 기록하기 위함이다.

@Entity
@Getter
@Setter
@Table(name="USERAGREE")
@EntityListeners(AuditingEntityListener.class)
public class UserAgreeEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int uaID;

    @ManyToOne  // 한 명의 사용자는 여러 약관 동의를 가질 수 있다.
    @JoinColumn(name="USERPK", nullable=false)
    private UserEntity user;

    @ManyToOne  // 하나의 약관동의는 여러 사용자를 가질 수 있다.
    @JoinColumn(name="AGREEID", nullable=false)
    private AgreeEntity agree;

    // 사용자가 동의했다면 true, 동의하지 않았다면 false
    @Column(name="AGREE", nullable=false)
    private boolean agreed = false; // 어떤걸 동의할지 모르니 기본값은 false

    // 최초 동의 날짜
    @CreatedDate
    @Column(name="AGREEDATE", nullable = false, updatable=false)
    private LocalDateTime userAgreeDate;

    // 동의 상태 변경시 날짜
    @LastModifiedDate
    @Column(name="UPDATEDDATE")
    private LocalDateTime userAgreeChangeDate;
}
