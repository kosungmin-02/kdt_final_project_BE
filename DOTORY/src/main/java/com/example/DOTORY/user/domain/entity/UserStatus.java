package com.example.DOTORY.user.domain.entity;

public enum UserStatus {
    ACTIVE,  // 활동중
    NOACTIVE, // 탈퇴한 회원 (스스로 탈퇴)
    DELETED   // 신고 받고 관리자가 강제 탈퇴시킨 회원.
}
