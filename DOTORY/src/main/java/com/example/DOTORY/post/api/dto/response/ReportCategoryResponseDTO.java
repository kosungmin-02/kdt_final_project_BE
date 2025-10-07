package com.example.DOTORY.post.api.dto.response;

public record ReportCategoryResponseDTO(
        Long categoryId,
        String categoryName,
        String reason  // Enum 대신 DB 컬럼 그대로 사용
) {}


//    SPAM("스팸/광고성 게시물"),
//    ABUSE("욕설/모욕/괴롭힘"),
//    ILLEGAL("불법/유해 컨텐츠"),
//    SEXUAL("성적인 내용"),
//    PERSONAL("개인정보 노출"),
//    OTHER("기타");
