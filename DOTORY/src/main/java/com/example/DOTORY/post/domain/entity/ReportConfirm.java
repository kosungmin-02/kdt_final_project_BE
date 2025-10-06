package com.example.DOTORY.post.domain.entity;

import lombok.Getter;

@Getter
public enum ReportConfirm {
    COMPLETE("처리 완료"),
    WAITING("대기"),
    REJECTED("반려");

    // enum을 생성하면 설명(한글 텍스트)를 description 변수가 보관해줌.
    private final String description;
    ReportConfirm(String description){
        this.description = description;
    }
}
