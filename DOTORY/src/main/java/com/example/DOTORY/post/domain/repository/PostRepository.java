package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.api.dto.response.PostListResponse;
import com.example.DOTORY.post.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUser_UserPKOrderByCreatedDateDesc(int userPK);

    Page<Post> findByUser_UserPK(int userPK, Pageable pageable);

    // 최신순 정렬
    @Query("""
        SELECT new com.example.DOTORY.post.api.dto.response.PostListResponse(
            p.postId,
            p.thumbnailUrl,
            p.caption,
            p.user.userName,
            COALESCE(COUNT(DISTINCT l.likeId), 0L),
            p.createdDate
        )
        FROM Post p
        LEFT JOIN p.decoration d
        LEFT JOIN p.likes l
        WHERE (
            (d IS NOT NULL AND d.keywords IS NOT NULL AND LOWER(d.keywords) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (p.caption IS NOT NULL AND LOWER(p.caption) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (p.user.userName IS NOT NULL AND LOWER(p.user.userName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        )
        GROUP BY p.postId, p.thumbnailUrl, p.caption, p.user.userName, p.createdDate
        ORDER BY p.createdDate DESC
    """)
    List<PostListResponse> findByHashtagKeywordOrderByLatest(@Param("keyword") String keyword);

    // 인기순 정렬
    @Query("""
        SELECT new com.example.DOTORY.post.api.dto.response.PostListResponse(
            p.postId,
            p.thumbnailUrl,
            p.caption,
            p.user.userName,
            COALESCE(COUNT(DISTINCT l.likeId), 0L),
            p.createdDate
        )
        FROM Post p
        LEFT JOIN p.decoration d
        LEFT JOIN p.likes l
        WHERE (
            (d IS NOT NULL AND d.keywords IS NOT NULL AND LOWER(d.keywords) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (p.caption IS NOT NULL AND LOWER(p.caption) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR
            (p.user.userName IS NOT NULL AND LOWER(p.user.userName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        )
        GROUP BY p.postId, p.thumbnailUrl, p.caption, p.user.userName, p.createdDate
        ORDER BY COUNT(DISTINCT l.likeId) DESC, p.createdDate DESC
    """)
    List<PostListResponse> findByHashtagKeywordOrderByPopular(@Param("keyword") String keyword);
}