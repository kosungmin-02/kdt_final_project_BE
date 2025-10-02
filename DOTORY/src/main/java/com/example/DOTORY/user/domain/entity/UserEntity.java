package com.example.DOTORY.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

// 데이터베이스 테이블과 1:1 매칭되는 테이블
@Entity
@Table(name="USER")    // 매칭되는 테이블은 USER라는 이름의 테이블.
@Data // getter, setter등 사용하기 위함

// 모든 멤버 필드들을 파라미터로 받아서 객체 생성하고자 생성자 어노테이션 사용.
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserEntity extends BaseEntity {

    @Id
    @Column(name="USERPK")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int userPK;

    @Column(name="USERID", nullable=false)
    private String userID;

    @Column(name="USERPASSWORD", nullable=false, length=100)
    private String userPassword;

    @Column(name = "USERNAME", nullable=false, length = 20)
    private String userName;

    @Column(name="USERNICKNAME", nullable=false, length = 20)
    private String userNickname;

    @Column(name="USEREMAIL", nullable=false, length = 25)
    private String userEmail;

    @Column(name="USERROLE", nullable=false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole userRole = UserRole.USER;

    @Column(name="USERLOGIN", nullable=false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserLogin userLogin = UserLogin.LOGOUT;

    @Column(name="USERSTATUS", nullable=false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus userStatus = UserStatus.ACTIVE;
}
