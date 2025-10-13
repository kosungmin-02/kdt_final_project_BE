package com.example.DOTORY.post.api.controller;

import com.example.DOTORY.global.code.dto.ApiResponse;
import com.example.DOTORY.global.security.CustomUserPrincipal;
import com.example.DOTORY.post.api.dto.request.PostRequest;
import com.example.DOTORY.post.api.dto.response.FeedResponse;
import com.example.DOTORY.post.api.dto.response.PostDetailResponse;
import com.example.DOTORY.post.api.dto.response.PostResponse;
import com.example.DOTORY.post.application.PostService;
import com.example.DOTORY.user.domain.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /** 게시글 업로드 (텍스트만, 테스트용) */
    @Operation(summary = "게시글 작성 기능", description = "회원은 게시글을 작성할 수 있음. 텍스트만을 작성할 수 있음.")
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @RequestBody PostRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        UserEntity user = principal.getUser();
        PostResponse response = postService.createPost(request, null, user);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    /** 파일 업로드 API (여러장) */
    @Operation(summary = "사진 업로드용 기능", description = "게시글을 작성할때 텍스트 뿐만 아니라 이미지 업로드도 가능. 여러 사진 올리기 가능함.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> uploadFile(
            @RequestPart("post") PostRequest request,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        UserEntity user = principal.getUser();
        PostResponse response = postService.createPost(request, images, user);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    /** 게시글 목록 조회 */
    @Operation(summary = "게시글 목록 조회하기", description = "게시글 목록을 나타내주는 기능으로 페이지네이션 적용.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> list(
            @PageableDefault(size = 10, sort = "postId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponse> posts = postService.getPostList(pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(posts));
    }

    /** 게시글 수정 (여러장 업로드 가능) */
    @Operation(summary = "게시글 수정 기능.", description = "작성자 또는 관리자만이 게시글을 수정할 수 있음. 사진의 경우 여러 장 업로드 가능함.")
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> update(
            @PathVariable Long postId,
            @RequestPart("post") PostRequest request,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        UserEntity user = principal.getUser();
        PostResponse response = postService.updatePost(postId, request, images, user);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    /** 게시글 삭제 */
    @Operation(summary = "게시글 삭제하기", description = "작성자, 관리자만이 게시글을 삭제할 수 있음.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        UserEntity user = principal.getUser();
        postService.deletePost(postId, user);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    /** 게시글 상세 조회 */
    @Operation(summary = "게시글 상세 조회하기", description = "게시글을 클릭하면 해당 게시글 내용을 상세하게 볼 수 있음.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getDetail(
            @PathVariable Long postId,
            @RequestParam(value = "viewerUserPK", required = false) Integer viewerUserPK
    ) {
        PostDetailResponse detail = postService.getPostDetail(postId, viewerUserPK);
        return ResponseEntity.ok(ApiResponse.onSuccess(detail));
    }

    /** 내 피드 조회 */
    @GetMapping("/feed/{userPK}")
    @Operation(summary = "나의 게시글 조회하기", description = "내가 어떤 게시물들을 올렸는지 확인 가능. 생성일자 순서대로 페이지네이션 적용함.")
    public ResponseEntity<ApiResponse<Page<FeedResponse>>> getUserFeed(
            @PathVariable int userPK,
            @PageableDefault(size = 12, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<FeedResponse> feed = postService.getUserFeed(userPK, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(feed));
    }
}
