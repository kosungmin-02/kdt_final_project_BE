package com.example.DOTORY.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import com.example.DOTORY.post.application.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /** 로그인한 유저 기준으로 북마크 토글 **/
    @Operation(summary = "로그인한 유저가 북마크 누를 수 있음,", description = "userPK를 기준으로 사용자가 맞는지 확인할 수 있고, 로그인을 했다면 북마크를 누를 수 있다. ")
    @PostMapping("/{postId}/bookmarks")
    public ResponseEntity<ApiResponse<String>> toggleBookmark(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        String result = bookmarkService.toggleBookmark(postId, userPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    /** 게시글 북마크 수 조회 (로그인 필요 없음) */
    @Operation(summary = "북마크 수 조회하기", description = "게시글별로 북마크가 몇 개 입력되었는지 조회함.")
    @GetMapping("/{postId}/bookmarks/count")
    public ResponseEntity<ApiResponse<Long>> countBookmarks(@PathVariable Long postId) {
        Long count = bookmarkService.countBookmarks(postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(count));
    }

    // 내가 누른 북마크 조회
    /** 로그인한 유저가 북마크한 게시글 리스트 조회 */
    @Operation(summary = "내가 북마크한 게시글 조회", description = "로그인한 유저가 북마크한 게시글 리스트를 조회함")
    @GetMapping("/bookmarks/mine")
    public ResponseEntity<ApiResponse<Iterable<Long>>> getMyBookmarks(
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        Iterable<Long> postIds = bookmarkService.getBookmarksByUser(userPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(postIds));
    }

}
