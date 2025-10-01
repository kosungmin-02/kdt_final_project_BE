package com.example.DOTORY.global.post.application;

import com.example.DOTORY.global.post.api.dto.request.CommentRequest;
import com.example.DOTORY.global.post.api.dto.response.CommentResponse;
import com.example.DOTORY.global.post.domain.entity.Comment;
import com.example.DOTORY.global.post.domain.entity.Post;
import com.example.DOTORY.global.post.domain.repository.CommentRepository;
import com.example.DOTORY.global.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 작성
    public CommentResponse addComment(Long postId, CommentRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .userId(userId)
                .post(post)
                .parent(parent)
                .isdeleted(false)
                .build();

        return CommentResponse.from(commentRepository.save(comment));
    }

    // 댓글 수정
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        comment.setContent(request.getContent());
        return CommentResponse.from(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        comment.setIsdeleted(true); // 실제 삭제 대신 표시만
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPost_PostId(postId).stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }




}
