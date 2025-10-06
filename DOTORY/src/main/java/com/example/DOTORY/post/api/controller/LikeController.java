package com.example.DOTORY.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import com.example.DOTORY.post.application.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<String>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        String result = likeService.toggleLike(postId, userPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }


    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<ApiResponse<Long>> countLikes(@PathVariable Long postId) {
        Long count = likeService.countLikes(postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(count));
    }
}