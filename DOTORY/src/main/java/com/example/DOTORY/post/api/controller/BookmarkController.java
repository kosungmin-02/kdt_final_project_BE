package com.example.DOTORY.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import com.example.DOTORY.post.application.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /** 로그인한 유저 기준으로 북마크 토글 */
    @PostMapping("/{postId}/bookmarks")
    public ResponseEntity<ApiResponse<String>> toggleBookmark(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        String result = bookmarkService.toggleBookmark(postId, userPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    /** 게시글 북마크 수 조회 (로그인 필요 없음) */
    @GetMapping("/{postId}/bookmarks/count")
    public ResponseEntity<ApiResponse<Long>> countBookmarks(@PathVariable Long postId) {
        Long count = bookmarkService.countBookmarks(postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(count));
    }
}
