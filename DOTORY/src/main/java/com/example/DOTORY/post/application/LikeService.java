package com.example.DOTORY.post.application;

import com.example.DOTORY.notification.application.port.NotificationPort;
import com.example.DOTORY.notification.domain.Notification;
import com.example.DOTORY.post.domain.entity.Like;
import com.example.DOTORY.post.domain.entity.Post;
import com.example.DOTORY.post.domain.repository.LikeRepository;
import com.example.DOTORY.post.domain.repository.PostRepository;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationPort notificationPort;

    @Transactional
    public String toggleLike(Long postId, int userPK) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Optional<Like> existing = likeRepository.findByPost_PostIdAndUser_UserPK(postId, userPK);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return "좋아요 취소";
        } else {
            UserEntity liker = userRepository.findById(userPK)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Like like = Like.builder()
                    .post(post)
                    .user(liker)
                    .build();
            likeRepository.save(like);

            // 알림 로직 추가
            UserEntity postAuthor = post.getUser();
            if (postAuthor.getUserPK() != liker.getUserPK()) {
                String message = liker.getUserNickname() + "님이 회원님의 게시글을 좋아합니다.";
                Notification notification = Notification.builder()
                        .message(message)
                        .relatedUrl("/posts/" + post.getPostId())
                        .isRead(false)
                        .createdAt(System.currentTimeMillis())
                        .build();
                notificationPort.save(notification, String.valueOf(postAuthor.getUserPK()));
            }

            return "좋아요 추가";
        }
    }

    public Long countLikes(Long postId) {
        return likeRepository.countByPost_PostId(postId);
    }
}
