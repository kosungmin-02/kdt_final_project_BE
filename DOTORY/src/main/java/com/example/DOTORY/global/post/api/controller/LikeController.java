package com.example.DOTORY.global.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.post.application.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<String>> toggleLike(@PathVariable Long postId,
                                                          @RequestParam Long userId) {
        String result = likeService.toggleLike(postId, userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<ApiResponse<Long>> countLikes(@PathVariable Long postId) {
        Long count = likeService.countLikes(postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(count));
    }
}