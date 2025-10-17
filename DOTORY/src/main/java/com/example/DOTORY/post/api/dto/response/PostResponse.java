package com.example.DOTORY.post.api.dto.response;

import java.util.List;

public record PostResponse(
        Long postId,
        String caption,
        List<String> imageUrls,
        String thumbnailUrl,
        // Decoration 관련 필드
        String frameColor,
        String framePattern,
        String patternColor,
        Double patternOpacity,
        Integer rotation,
        String keywords
) {}
