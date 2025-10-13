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
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;

    private static final String UPLOAD_DIR = "uploads/";

    /** 이미지 업로드 공통 메서드 */
    private List<String> saveImages(MultipartFile[] images) {
        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    try {
                        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                        Path path = Paths.get(UPLOAD_DIR + fileName);
                        Files.createDirectories(path.getParent());
                        Files.write(path, image.getBytes());
                        imageUrls.add("/" + UPLOAD_DIR + fileName);
                    } catch (IOException e) {
                        throw new RuntimeException("파일 업로드 실패", e);
                    }
                }
            }
        }
        return imageUrls;
    }

    /** 게시글 생성 */
    public PostResponse createPost(PostRequest request, MultipartFile[] images, UserEntity user) {
        List<String> imageUrls = saveImages(images);
        String thumbnailUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .imageUrls(imageUrls)
                .thumbnailUrl(thumbnailUrl)
                .build();

        Post saved = postRepository.save(post);

        return toPostResponse(saved);
    }

    /** 게시글 수정 */
    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request, MultipartFile[] images, UserEntity user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 권한 체크
        // 작성자도 아니고 관리자도 아니면 게시글 수정 불가능.
        if (post.getUser().getUserPK() != user.getUserPK() && user.getUserRole() != UserRole.ADMIN )   {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }

        List<String> imageUrls = images != null && images.length > 0 ? saveImages(images) : post.getImageUrls();
        String thumbnailUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImageUrls(imageUrls);
        post.setThumbnailUrl(thumbnailUrl);

        Post updated = postRepository.save(post);
        return toPostResponse(updated);
    }

    /** 게시글 삭제 */
    @Transactional
    public void deletePost(Long postId, UserEntity user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 권한 체크
        // 작성자도 아니고 관리자도 아니라면 글 삭제 불가능.
        if (post.getUser().getUserPK() != user.getUserPK() &&  user.getUserRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    /** 게시글 단건 조회 */
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return toPostResponse(post);
    }


    /** 게시글 목록 조회 */
    public Page<PostResponse> getPostList(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(this::toPostResponse);
    }

    /** 게시글 상세 조회 */
    public PostDetailResponse getPostDetail(Long postId, Integer viewerUserPK) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        long likeCount = likeRepository.countByPost_PostId(postId);
        long commentCount = commentRepository.countByPost_PostId(postId);

        boolean liked = viewerUserPK != null && likeRepository.existsByPost_PostIdAndUser_UserPK(postId, viewerUserPK);
        boolean bookmarked = viewerUserPK != null && bookmarkRepository.existsByPost_PostIdAndUser_UserPK(postId, viewerUserPK);

        return PostDetailResponse.builder()
                .postId(post.getPostId())
                .userPK(post.getUser().getUserPK())
                .title(post.getTitle())
                .content(post.getContent())
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

    @Transactional
    /** 사용자 피드 조회 */
    public Page<FeedResponse> getUserFeed(int userPK, Pageable pageable) {
        return postRepository.findByUser_UserPK(userPK, pageable)
                .map(post -> FeedResponse.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .thumbnailUrl(post.getThumbnailUrl())
                        .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                        .createdDate(post.getCreatedDate())
                        .build());
    }

    /** Post -> PostResponse 변환 공통 메서드 */
    private PostResponse toPostResponse(Post post) {
        return PostResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrls(post.getImageUrls() != null ? List.copyOf(post.getImageUrls()) : List.of())
                .thumbnailUrl(post.getThumbnailUrl())
                .build();
    }
}
