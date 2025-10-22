package com.example.DOTORY.post.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// 검색에 필요한 키워드와 정렬 기준을 담는 DTO
public record PostSearchRequest(
        @NotBlank(message = "검색 키워드는 필수입니다.")
        String keyword,

        // 정렬 기준: "LATEST" (최신순) 또는 "POPULAR" (좋아요순)
        @NotNull(message = "정렬 기준은 필수입니다.")
        String sort
) {}