package com.example.DOTORY.post.api.dto.response;

import java.time.LocalDateTime;
import java.util.List;

// Java 17+ Record 사용
public record FeedResponse(
        Long postId,
        String caption,
        List<String> imageUrls,
        String thumbnailUrl,  // 첫 번째 이미지
        String userID,
        int likeCount,
        int commentCount,
        LocalDateTime createdDate,

        // Decoration 관련 필드 추가
        String frameColor,
        String framePattern,
        String patternColor,
        Double patternOpacity,
        Integer rotation,
        String keywords
) {}
