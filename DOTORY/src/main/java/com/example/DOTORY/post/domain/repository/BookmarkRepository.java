package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.domain.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Long countByPost_PostId(Long postPostId);

    // 카운트 / 존재 체크
    Optional<Bookmark> findByPost_PostIdAndUser_UserPK(Long postId, int userPK);

    boolean existsByPost_PostIdAndUser_UserPK(Long postId, int viewerUserPK);

    Optional<Object> findByPost_PostIdAndUser_UserPK(Long postId, Integer viewerUserPK);

    Collection<Bookmark> findByUser_UserPK(int userPK);
}
