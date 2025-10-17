package com.example.DOTORY.post.application;

import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.post.api.dto.request.PostRequest;
import com.example.DOTORY.post.api.dto.response.FeedResponse;
import com.example.DOTORY.post.api.dto.response.PostDetailResponse;
import com.example.DOTORY.post.api.dto.response.PostResponse;
import com.example.DOTORY.post.domain.entity.Decoration;
import com.example.DOTORY.post.domain.entity.Post;
import com.example.DOTORY.post.domain.repository.BookmarkRepository;
import com.example.DOTORY.post.domain.repository.CommentRepository;
import com.example.DOTORY.post.domain.repository.LikeRepository;
import com.example.DOTORY.post.domain.repository.PostRepository;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.entity.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                        throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
                    }
                }
            }
        }
        return imageUrls;
    }

    public PostResponse createPost(PostRequest request, MultipartFile[] images, UserEntity user) {
        List<String> imageUrls = saveImages(images);
        String thumbnailUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

        // Post 생성
        Post post = Post.builder()
                .caption(request.getCaption())
                .user(user)
                .imageUrls(imageUrls)
                .thumbnailUrl(thumbnailUrl)
                .build();

        // Decoration 생성
        if (request.getFrameColor() != null || request.getFramePattern() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String keywordsJson = null;
            if (request.getKeywords() != null) {
                try {
                    keywordsJson = objectMapper.writeValueAsString(request.getKeywords());
                } catch (JsonProcessingException e) {
                    throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR, "키워드 저장 중 오류 발생");
                }
            }

            post.setDecoration(
                    Decoration.builder()
                            .post(post)
                            .frameColor(request.getFrameColor())
                            .framePattern(request.getFramePattern())
                            .patternColor(request.getPatternColor())
                            .patternOpacity(request.getPatternOpacity() != null ? request.getPatternOpacity() : 1.0)
                            .rotation(request.getRotation() != null ? request.getRotation() : 0)
                            .keywords(keywordsJson)
                            .build()
            );
        }

        Post saved = postRepository.save(post);
        return toPostResponse(saved);
    }



    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request, MultipartFile[] images, UserEntity user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        if (post.getUser().getUserPK() != user.getUserPK() && user.getUserRole() != UserRole.ADMIN) {
            throw new GeneralException(ErrorStatus.ACCESS_DENIED, "본인의 게시글만 수정할 수 있습니다.");
        }

        // 이미지 처리
        List<String> imageUrls = images != null && images.length > 0 ? saveImages(images) : post.getImageUrls();
        String thumbnailUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

        post.setCaption(request.getCaption());
        post.setImageUrls(imageUrls);
        post.setThumbnailUrl(thumbnailUrl);

        // Decoration 업데이트
        Decoration decoration = post.getDecoration();
        if (decoration == null) {
            decoration = Decoration.builder().post(post).build();
        }

        decoration.setFrameColor(request.getFrameColor());
        decoration.setFramePattern(request.getFramePattern());
        decoration.setPatternColor(request.getPatternColor());
        decoration.setPatternOpacity(request.getPatternOpacity() != null ? request.getPatternOpacity() : 1.0);
        decoration.setRotation(request.getRotation() != null ? request.getRotation() : 0);

        // keywords 배열 -> CSV 문자열 변환
        String keywordsCsv = null;
        if (request.getKeywords() != null && !request.getKeywords().isEmpty()) {
            keywordsCsv = String.join(",", request.getKeywords()); // 배열을 CSV로 변환
        }
        decoration.setKeywords(keywordsCsv);

        post.setDecoration(decoration);

        Post updated = postRepository.save(post);
        return toPostResponse(updated);
    }



    @Transactional
    public void deletePost(Long postId, UserEntity user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        if (post.getUser().getUserPK() != user.getUserPK() &&  user.getUserRole() != UserRole.ADMIN) {
            throw new GeneralException(ErrorStatus.ACCESS_DENIED, "본인의 게시글만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        return toPostResponse(post);
    }

    public Page<PostResponse> getPostList(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(this::toPostResponse);
    }

    public PostDetailResponse getPostDetail(Long postId, Integer viewerUserPK) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        long likeCount = likeRepository.countByPost_PostId(postId);
        long commentCount = commentRepository.countByPost_PostId(postId);

        boolean liked = viewerUserPK != null && likeRepository.existsByPost_PostIdAndUser_UserPK(postId, viewerUserPK);
        boolean bookmarked = viewerUserPK != null && bookmarkRepository.existsByPost_PostIdAndUser_UserPK(postId, viewerUserPK);

        return PostDetailResponse.builder()
                .postId(post.getPostId())
                .userPK(post.getUser().getUserPK())
                .content(post.getCaption())
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
    public Page<FeedResponse> getUserFeed(int userPK, Pageable pageable) {
        return postRepository.findByUser_UserPK(userPK, pageable)
                .map(post -> {
                    var deco = post.getDecoration();
                    return new FeedResponse(
                            post.getPostId(),
                            post.getCaption(),
                            post.getImageUrls(),
                            post.getThumbnailUrl(),
                            post.getUser().getUserID(),
                            post.getLikes() != null ? post.getLikes().size() : 0,
                            post.getComments() != null ? post.getComments().size() : 0,
                            post.getCreatedDate(),
                            deco != null ? deco.getFrameColor() : null,
                            deco != null ? deco.getFramePattern() : null,
                            deco != null ? deco.getPatternColor() : null,
                            deco != null ? deco.getPatternOpacity() : 1.0,
                            deco != null ? deco.getRotation() : 0,
                            deco != null ? deco.getKeywords() : null
                    );
                });
    }



    private PostResponse toPostResponse(Post post) {
        Decoration deco = post.getDecoration();

        return new PostResponse(
                post.getPostId(),
                post.getCaption(),
                post.getImageUrls() != null ? List.copyOf(post.getImageUrls()) : List.of(),
                post.getThumbnailUrl(),
                deco != null ? deco.getFrameColor() : null,
                deco != null ? deco.getFramePattern() : null,
                deco != null ? deco.getPatternColor() : null,
                deco != null ? deco.getPatternOpacity() : 1.0,
                deco != null ? deco.getRotation() : 0,
                deco != null ? deco.getKeywords() : null
        );
    }




}