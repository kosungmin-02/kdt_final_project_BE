package com.example.DOTORY.post.api.dto.request;

public record PostCategoryDTO(
        String categoryName,
        Long parentId,   // 최상위 카테고리는 null
        Integer sortOrder
) {}
