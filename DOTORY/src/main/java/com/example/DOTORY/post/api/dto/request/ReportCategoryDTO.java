package com.example.DOTORY.post.api.dto.request;

public record ReportCategoryDTO(
        String categoryName,
        String reason  // Enum 대신 DB 컬럼 그대로 사용
) {}
