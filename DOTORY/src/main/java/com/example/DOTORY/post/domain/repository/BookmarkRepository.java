package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.domain.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByPost_PostIdAndUserId(Long postId, Long userId);
    Long countByPost_PostId(Long postPostId);

    // 카운트 / 존재 체크
    boolean existsByPost_PostIdAndUserId(Long postId, Long userId);

}
