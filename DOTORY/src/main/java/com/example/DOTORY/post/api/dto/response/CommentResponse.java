package com.example.DOTORY.post.api.dto.response;

import com.example.DOTORY.post.domain.entity.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {

    private Long commentId;
    private Long parentId;
    private String content;
    private String author;
    private boolean isDeleted;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.isIsdeleted() ? "삭제된 댓글입니다." : comment.getContent())
                .author(comment.getUser().getUserID())
                .isDeleted(comment.isIsdeleted())
                .build();
    }
}
