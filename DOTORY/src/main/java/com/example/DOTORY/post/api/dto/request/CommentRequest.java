package com.example.DOTORY.post.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {

    private String content;
    private Long parentId; // 대댓글 : 부모 ID

}
