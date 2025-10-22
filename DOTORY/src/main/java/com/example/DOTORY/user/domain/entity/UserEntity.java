package com.example.DOTORY.user.domain.entity;

import com.example.DOTORY.post.domain.entity.ReportComment;
import com.example.DOTORY.post.domain.entity.ReportPost;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

// 데이터베이스 테이블과 1:1 매칭되는 테이블
@Entity
@Table(name="USER")    // 매칭되는 테이블은 USER라는 이름의 테이블.
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
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

    // 쿼리에서 사용할 수 있도록 getter 메소드 명시적으로 정의
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

    @Column(name="USERAVATAR", length = 255)
    private String userAvatar;  // 프로필 사진 URL

    @Column(name="FCM_TOKEN", length = 255)
    private String fcmToken; // FCM 토큰


    @BatchSize(size = 10)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserSNSEntity> snsList = new ArrayList<>();


    @BatchSize(size = 10)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserAgreeEntity> agreeList = new ArrayList<>();

    // 💡 ReportPost (가정: mappedBy가 reportedUser라고 가정)
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "reportedUser", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReportPost> reportedPosts = new ArrayList<>();

    // 💡 ReportComment (가정: mappedBy가 reportedUser라고 가정)
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "reportedUser", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReportComment> reportedComments = new ArrayList<>();


}
