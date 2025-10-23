package com.example.DOTORY.post.application;

import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.notification.application.NotificationService;
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
    private final NotificationService notificationService;

    public CommentResponse addComment(Long postId, CommentRequest request, int userPK) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND, "Parent comment not found."));
        }

        UserEntity user = userRepository.findById(userPK)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .post(post)
                .parent(parent)
                .isdeleted(false)
                .build();

        Comment savedComment = commentRepository.save(comment);

        UserEntity postAuthor = post.getUser();
        // Send notification if the commenter is not the post author
        if (postAuthor.getUserPK() != user.getUserPK()) {
            String title = "새로운 댓글";
            String body = user.getUserNickname() + "님이 회원님의 게시글에 댓글을 남겼습니다.";
            String type = "COMMENT";
            String relatedUrl = "/posts/" + post.getPostId();
            notificationService.sendNotification(postAuthor, title, body, type, relatedUrl);
        }

        return CommentResponse.from(savedComment);
    }

    public CommentResponse updateComment(Long commentId, CommentRequest request, int userPK) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        UserEntity user = userRepository.findByUserPK(userPK)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (comment.getUser().getUserPK() != user.getUserPK() && user.getUserRole() != UserRole.ADMIN) {
            throw new GeneralException(ErrorStatus.ACCESS_DENIED, "본인의 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(request.getContent());
        return CommentResponse.from(comment);
    }

    public void deleteComment(Long commentId, int userPK) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        UserEntity user = userRepository.findByUserPK(userPK)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (comment.getUser().getUserPK() != userPK && user.getUserRole() != UserRole.ADMIN) {
            throw new GeneralException(ErrorStatus.ACCESS_DENIED, "본인의 댓글만 삭제할 수 있습니다.");
        }

        comment.setIsdeleted(true);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPost_PostId(postId).stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }
}