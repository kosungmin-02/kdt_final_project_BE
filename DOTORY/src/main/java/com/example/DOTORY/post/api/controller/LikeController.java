package com.example.DOTORY.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import com.example.DOTORY.post.application.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 누르는 기능", description = "좋아요를 누르면 게시글의 좋아요 개수가 증가(어떤 게시글인지, 누가 좋아요 클릭했는지 등이 기록될 수 있음)")
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<String>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        String result = likeService.toggleLike(postId, userPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }


    @Operation(summary = "좋아요 개수 조회", description = "게시글이 좋아요 몇 개 눌렸는지를 조회할 수 있음.")
    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<ApiResponse<Long>> countLikes(@PathVariable Long postId) {
        Long count = likeService.countLikes(postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(count));
    }
}