package com.example.DOTORY.post.api.dto.response;

import java.time.LocalDateTime;

// 게시글 목록의 각 항목을 담는 DTO
public record PostListResponse(
        Long postId,
        String imageUrl, // 이미지 주소 (목록 썸네일)
        String contentSummary, // 게시글 내용 요약
        String authorUsername, // 작성자 이름
        Long likesCount, // 좋아요 수
        LocalDateTime createdDate // 생성 일시 (최신순 정렬에 사용)
) {}