package com.example.DOTORY.global.post.api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedResponse {

    private Long postId;
    private String title;
    private String thumbnailUrl; // 썸네일 (첫 번째 이미지)
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdDate;

}
