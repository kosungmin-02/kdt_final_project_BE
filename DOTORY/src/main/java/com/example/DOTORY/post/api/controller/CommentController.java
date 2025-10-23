package com.example.DOTORY.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import com.example.DOTORY.post.api.dto.request.CommentRequest;
import com.example.DOTORY.post.api.dto.response.CommentResponse;
import com.example.DOTORY.post.application.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    // CommentController.java 내부에 추가
    @GetMapping("/test/{postId}")
    @Operation(summary = "테스트용 엔드포인트", description = "404 문제 진단을 위한 단순 테스트")
    public ResponseEntity<ApiResponse<String>> testMapping(@PathVariable Long postId) {
        log.info("Test Mapping Called with postId: {}", postId);
        return ResponseEntity.ok(ApiResponse.onSuccess("Mapping Success for: " + postId));
    }

    /** 댓글 작성 (로그인 필요) */
    @Operation(summary = "댓글 작성(추가)", description = "로그인한 사용자인 경우 댓글을 추가할 수 있음. (대댓글 포함 기능)")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        log.info("CommentController 호출");
        int userPK = principal.getUser().getUserPK();
        CommentResponse response = commentService.addComment(postId, request, userPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    /** 댓글 수정 (로그인 필요) */
    @Operation(summary = "댓글 수정", description = "로그인한 사용자만 댓글을 수정할 수 있음. 단, 해당 댓글을 수정할 수 있는 권한은 작성자 / 관리자")
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
    @Operation(summary = "댓글 삭제", description = "로그인한 사용자 중에서 댓글 작성자 / 관리자만이 댓글을 삭제할 수 있음. 수정과 동일.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        int userPK = principal.getUser().getUserPK();
        commentService.deleteComment(commentId, userPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    /** 댓글 조회 (로그인 불필요) */
    @Operation(summary = "댓글 조회", description = "게시글에 댓글이 몇 개 달려있는지 조회 가능. 게시글 고유번호인 postId를 사용.")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Iterable<CommentResponse>>> getComments(@PathVariable Long postId){
        return ResponseEntity.ok(ApiResponse.onSuccess(commentService.getComments(postId)));
    }
}
