package com.example.DOTORY.post.api.dto.response;

import com.example.DOTORY.post.domain.entity.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {

    private Long commentId;
    private String content;
    private int userPK;
    private boolean isDeleted;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.isIsdeleted() ? "삭제된 댓글입니다." : comment.getContent())
                .userPK(comment.getUser().getUserPK())
                .isDeleted(comment.isIsdeleted())
                .build();
    }
}
