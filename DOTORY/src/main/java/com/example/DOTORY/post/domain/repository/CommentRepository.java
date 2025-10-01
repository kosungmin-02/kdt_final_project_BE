package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost_PostId(Long postId);
    List<Comment> findByParent_CommentId(Long parentId);

    // 카운트/존재 체크 메서드
    long countByPost_PostId(Long postId);



}
