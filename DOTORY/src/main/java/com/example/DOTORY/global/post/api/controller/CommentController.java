package com.example.DOTORY.global.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.post.api.dto.request.CommentRequest;
import com.example.DOTORY.global.post.api.dto.response.CommentResponse;
import com.example.DOTORY.global.post.application.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(@PathVariable Long postId, @RequestBody CommentRequest request){
        CommentResponse response = commentService.addComment(postId, request, 1L); // userId 임시
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest request){
        return ResponseEntity.ok(ApiResponse.onSuccess(commentService.updateComment(commentId, request)));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId){
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Iterable<CommentResponse>>> getComments(@PathVariable Long postId){
        return ResponseEntity.ok(ApiResponse.onSuccess(commentService.getComments(postId)));
    }


}
