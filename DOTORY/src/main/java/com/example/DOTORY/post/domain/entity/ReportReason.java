package com.example.DOTORY.post.domain.entity;

public enum ReportReason {

    SPAM("스팸/광고성 게시물"),
    ABUSE("욕설/모욕/괴롭힘"),
    ILLEGAL("불법/유해 컨텐츠"),
    SEXUAL("성적인 내용"),
    PERSONAL("개인정보 노출"),
    OTHER("기타");

    // 한글 설명을 보고 싶은 경우에 사용할 메소드
    private final String description;

    ReportReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
