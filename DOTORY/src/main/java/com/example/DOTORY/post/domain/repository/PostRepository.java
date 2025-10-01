package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserIdOrderByCreatedDateDesc(Long userId);

    // 특정 유저가 작성한 게시글 조회
    Page<Post> findByUserId(Long userId, Pageable pageable);


}
