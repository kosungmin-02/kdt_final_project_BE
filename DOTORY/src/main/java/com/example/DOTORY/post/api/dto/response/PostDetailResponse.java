package com.example.DOTORY.post.api.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private Long postId;
    private Long userId;              // 추후 User 연동 전까지 Long 유지
    private String title;
    private String content;

    // 여러 장 이미지 + 썸네일 (첫 장)
    private List<String> imageUrls;
    private String thumbnailUrl;

    // 카운트/상태
    private long likeCount;
    private long commentCount;
    private boolean liked;            // userId가 누른 좋아요 여부
    private boolean bookmarked;       // userId가 북마크 했는지

    // 메타
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
