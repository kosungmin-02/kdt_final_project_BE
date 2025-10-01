package com.example.DOTORY.global.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.post.application.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class BookmarkController {


    private final BookmarkService bookmarkService;

    @PostMapping("/{postId}/bookmarks")
    public ResponseEntity<ApiResponse<String>> toggleBookmark(@PathVariable Long postId,
                                                          @RequestParam Long userId) {
        String result = bookmarkService.toggleBookmark(postId, userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @GetMapping("/{postId}/bookmarks/count")
    public ResponseEntity<ApiResponse<Long>> countBookmarks(@PathVariable Long postId) {
        Long count = bookmarkService.countBookmarks(postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(count));
    }

}
