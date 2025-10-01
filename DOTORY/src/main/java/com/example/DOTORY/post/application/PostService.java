package com.example.DOTORY.post.application;

import com.example.DOTORY.post.api.dto.request.PostRequest;
import com.example.DOTORY.post.api.dto.response.FeedResponse;
import com.example.DOTORY.post.api.dto.response.PostDetailResponse;
import com.example.DOTORY.post.api.dto.response.PostResponse;
import com.example.DOTORY.post.domain.entity.Post;
import com.example.DOTORY.post.domain.repository.BookmarkRepository;
import com.example.DOTORY.post.domain.repository.CommentRepository;
import com.example.DOTORY.post.domain.repository.LikeRepository;
import com.example.DOTORY.post.domain.repository.PostRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;

    // 게시글 등록 (이미지 여러장)
    public PostResponse createPost(PostRequest request, MultipartFile[] images) {
        List<String> imageUrls = new ArrayList<>();
        String thumbnailUrl = null;

        if (images != null && images.length > 0) {
            for (int i = 0; i < images.length; i++) {
                MultipartFile image = images[i];
                if (image != null && !image.isEmpty()) {
                    try {
                        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                        Path path = Paths.get("uploads/" + fileName);
                        Files.createDirectories(path.getParent());
                        Files.write(path, image.getBytes());

                        String url = "/uploads/" + fileName;
                        imageUrls.add(url);

                        if (i == 0) { // 첫 번째 이미지를 썸네일로 지정
                            thumbnailUrl = url;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("파일 업로드 실패", e);
                    }
                }
            }
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrls(imageUrls)
                .thumbnailUrl(thumbnailUrl)
                .build();

        Post saved = postRepository.save(post);

        return PostResponse.builder()
                .postId(saved.getPostId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .imageUrls(saved.getImageUrls())
                .thumbnailUrl(saved.getThumbnailUrl())
                .build();
    }

    // 게시글 목록 조회 (페이징 네이션)
    public Page<PostResponse> getPostList(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(post -> PostResponse.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .imageUrls(post.getImageUrls())
                        .thumbnailUrl(post.getThumbnailUrl())
                        .build()
                );
    }

    // 게시글 단건 조회
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return PostResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrls(post.getImageUrls())
                .thumbnailUrl(post.getThumbnailUrl())
                .build();
    }

    // 게시글 수정
    public PostResponse updatePost(Long postId, PostRequest request, MultipartFile[] images) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        List<String> imageUrls = new ArrayList<>(post.getImageUrls() != null ? post.getImageUrls() : new ArrayList<>());
        String thumbnailUrl = post.getThumbnailUrl();

        if (images != null && images.length > 0) {
            imageUrls.clear(); // 기존 이미지 덮어쓰기
            for (int i = 0; i < images.length; i++) {
                MultipartFile image = images[i];
                if (image != null && !image.isEmpty()) {
                    try {
                        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                        Path path = Paths.get("uploads/" + fileName);
                        Files.createDirectories(path.getParent());
                        Files.write(path, image.getBytes());

                        String url = "/uploads/" + fileName;
                        imageUrls.add(url);

                        if (i == 0) {
                            thumbnailUrl = url;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("파일 업로드 실패", e);
                    }
                }
            }
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImageUrls(imageUrls);
        post.setThumbnailUrl(thumbnailUrl);

        Post updated = postRepository.save(post);

        return PostResponse.builder()
                .postId(updated.getPostId())
                .title(updated.getTitle())
                .content(updated.getContent())
                .imageUrls(updated.getImageUrls())
                .thumbnailUrl(updated.getThumbnailUrl())
                .build();
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        postRepository.delete(post);
    }


    // 게시글 상세 페이지
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId, Long viewerUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 레포지토리 기반 집계값
        long likeCount = likeRepository.countByPost_PostId(postId);
        long commentCount = commentRepository.countByPost_PostId(postId);

        boolean liked = false;
        boolean bookmarked = false;

        // 로그인 연동 전: viewerUserId 파라미터로 판단
        if (viewerUserId != null) {
            try {
                liked = likeRepository.existsByPost_PostIdAndUserId(postId, viewerUserId);
            } catch (Exception e) {
                liked = likeRepository.findByPost_PostIdAndUserId(postId, viewerUserId).isPresent();
            }
            try {
                bookmarked = bookmarkRepository.existsByPost_PostIdAndUserId(postId, viewerUserId);
            } catch (Exception e) {
                bookmarked = bookmarkRepository.findByPost_PostIdAndUserId(postId, viewerUserId).isPresent();
            }
        }

        return PostDetailResponse.builder()
                .postId(post.getPostId())
                .userId(post.getUserId()) // 엔티티에 userId 필드 있는 전제
                .title(post.getTitle())
                .content(post.getContent())
                // 컬렉션은 여기서 강제 초기화해서 Lazy 예외 방지
                .imageUrls(post.getImageUrls() != null ? List.copyOf(post.getImageUrls()) : List.of())
                .thumbnailUrl(post.getThumbnailUrl())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .liked(liked)
                .bookmarked(bookmarked)
                .createdDate(post.getCreatedDate())
                .updatedDate(post.getUpdatedDate())
                .build();
    }


    // 게시글 피드 조회
    @Transactional(readOnly = true)
    public Page<FeedResponse> getUserFeed(Long userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable)
                .map(post -> FeedResponse.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .thumbnailUrl(post.getThumbnailUrl())
                        .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                        .createdDate(post.getCreatedDate())
                        .build());


    }



}
