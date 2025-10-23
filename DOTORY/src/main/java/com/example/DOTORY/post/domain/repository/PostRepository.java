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

    // 특정 유저가 작성한 게시글 조회 (UserEntity의 USERPK 사용)
    Page<Post> findByUser_UserPK(int userPK, Pageable pageable);

    // 1. 최신순 정렬
    @Query("""
        SELECT new com.example.DOTORY.post.api.dto.response.PostListResponse(
            p.postId,
            p.thumbnailUrl,
            p.caption,
            p.user.userName,
            COUNT(DISTINCT l.likeId),
            p.createdDate
        )
        FROM Post p
        LEFT JOIN p.likes l
        WHERE p.caption LIKE CONCAT('%', :keyword, '%')
        GROUP BY
            p.postId,
            p.thumbnailUrl,
            p.caption,
            p.user.userName,
            p.createdDate
        ORDER BY p.createdDate DESC
    """)
    List<PostListResponse> findByHashtagKeywordOrderByLatest(@Param("keyword") String keyword);

    // 2. 좋아요순 정렬
    @Query("""
        SELECT new com.example.DOTORY.post.api.dto.response.PostListResponse(
            p.postId,
            p.thumbnailUrl,
            p.caption,
            p.user.userName,
            COUNT(DISTINCT l.likeId),
            p.createdDate
        )
        FROM Post p
        LEFT JOIN p.likes l
        WHERE p.caption LIKE CONCAT('%', :keyword, '%')
        GROUP BY
            p.postId,
            p.thumbnailUrl,
            p.caption,
            p.user.userName,
            p.createdDate
        ORDER BY COUNT(DISTINCT l.likeId) DESC, p.createdDate DESC
    """)
    List<PostListResponse> findByHashtagKeywordOrderByPopular(@Param("keyword") String keyword);


    // 키워드로 검색
    // 최신순
    @Query("""
    SELECT new com.example.DOTORY.post.api.dto.response.PostListResponse(
        p.postId,
        p.thumbnailUrl,
        p.caption,
        p.user.userName,
        COUNT(DISTINCT l.likeId),
        p.createdDate
    )
    FROM Post p
    LEFT JOIN p.likes l
    LEFT JOIN p.decoration d
    WHERE p.caption LIKE CONCAT('%', :keyword, '%') OR d.keywords LIKE CONCAT('%', :keyword, '%')
    GROUP BY
        p.postId,
        p.thumbnailUrl,
        p.caption,
        p.user.userName,
        p.createdDate
    ORDER BY p.createdDate DESC
""")
    List<PostListResponse> findByCaptionOrDecorationKeywordsOrderByLatest(@Param("keyword") String keyword);

    // 좋아요순
    @Query("""
    SELECT new com.example.DOTORY.post.api.dto.response.PostListResponse(
        p.postId,
        p.thumbnailUrl,
        p.caption,
        p.user.userName,
        COUNT(DISTINCT l.likeId),
        p.createdDate
    )
    FROM Post p
    LEFT JOIN p.likes l
    LEFT JOIN p.decoration d
    WHERE p.caption LIKE CONCAT('%', :keyword, '%') OR d.keywords LIKE CONCAT('%', :keyword, '%')
    GROUP BY
        p.postId,
        p.thumbnailUrl,
        p.caption,
        p.user.userName,
        p.createdDate
    ORDER BY COUNT(DISTINCT l.likeId) DESC, p.createdDate DESC
""")
    List<PostListResponse> findByCaptionOrDecorationKeywordsOrderByPopular(@Param("keyword") String keyword);

}