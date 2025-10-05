package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.domain.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // 카운트 / 존재 체크
    Long countByPost_PostId(Long postId);
    Optional<Like> findByPost_PostIdAndUser_UserPK(Long postId, int userPK);

    boolean existsByPost_PostIdAndUser_UserPK(Long postId, int viewerUserPK);
}
