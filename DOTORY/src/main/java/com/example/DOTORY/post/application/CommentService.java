package com.example.DOTORY.post.application;

import com.example.DOTORY.post.api.dto.request.CommentRequest;
import com.example.DOTORY.post.api.dto.response.CommentResponse;
import com.example.DOTORY.post.domain.entity.Comment;
import com.example.DOTORY.post.domain.entity.Post;
import com.example.DOTORY.post.domain.repository.CommentRepository;
import com.example.DOTORY.post.domain.repository.PostRepository;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.entity.UserRole;
import com.example.DOTORY.user.domain.repository.UserRepository;
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
    private final UserRepository userRepository;

    /** 댓글 작성 */
    public CommentResponse addComment(Long postId, CommentRequest request, int userPK) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }

        UserEntity user = userRepository.findById(userPK)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .post(post)
                .parent(parent)
                .isdeleted(false)
                .build();

        return CommentResponse.from(commentRepository.save(comment));
    }

    /** 댓글 수정 */
    public CommentResponse updateComment(Long commentId, CommentRequest request, int userPK) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        UserEntity user = userRepository.findByUserPK(userPK)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 권한 체크
        // 댓글 작성자도 아니고 관리자도 아니라면 댓글 수정 불가능.
        if (comment.getUser().getUserPK() != user.getUserPK() && user.getUserRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("본인의 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(request.getContent());
        return CommentResponse.from(comment);
    }

    /** 댓글 삭제 */
    public void deleteComment(Long commentId, int userPK) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        UserEntity user = userRepository.findByUserPK(userPK)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 권한 체크
        if (comment.getUser().getUserPK() != userPK && user.getUserRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("본인의 댓글만 삭제할 수 있습니다.");
        }

        comment.setIsdeleted(true); // 실제 삭제 대신 표시만
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPost_PostId(postId).stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }
}
