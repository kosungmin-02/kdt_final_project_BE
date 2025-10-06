package com.example.DOTORY.post.application;

import com.example.DOTORY.post.domain.entity.Like;
import com.example.DOTORY.post.domain.entity.Post;
import com.example.DOTORY.post.domain.repository.LikeRepository;
import com.example.DOTORY.post.domain.repository.PostRepository;
import com.example.DOTORY.user.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Transactional
    public String toggleLike(Long postId, int userPK) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Optional<Like> existing = likeRepository.findByPost_PostIdAndUser_UserPK(postId, userPK);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return "좋아요 취소";
        } else {
            UserEntity user = new UserEntity();
            user.setUserPK(userPK);

            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);
            return "좋아요 추가";
        }
    }

    public Long countLikes(Long postId) {
        return likeRepository.countByPost_PostId(postId);
    }
}
