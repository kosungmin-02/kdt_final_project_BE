package com.example.DOTORY.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import com.example.DOTORY.post.api.dto.request.CommentRequest;
import com.example.DOTORY.post.api.dto.response.CommentResponse;
import com.example.DOTORY.post.application.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** 댓글 작성 (로그인 필요) */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        CommentResponse response = commentService.addComment(postId, request, userPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    /** 댓글 수정 (로그인 필요) */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        CommentResponse response = commentService.updateComment(commentId, request, userPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    /** 댓글 삭제 (로그인 필요) */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        commentService.deleteComment(commentId, userPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    /** 댓글 조회 (로그인 불필요) */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Iterable<CommentResponse>>> getComments(@PathVariable Long postId){
        return ResponseEntity.ok(ApiResponse.onSuccess(commentService.getComments(postId)));
    }
}
