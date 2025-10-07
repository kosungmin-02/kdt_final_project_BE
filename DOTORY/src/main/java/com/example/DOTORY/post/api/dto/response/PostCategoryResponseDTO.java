package com.example.DOTORY.post.api.dto.response;

public record PostCategoryResponseDTO(
        Long categoryId,
        String categoryName,
        Long parentId,
        Integer sortOrder
) {}
