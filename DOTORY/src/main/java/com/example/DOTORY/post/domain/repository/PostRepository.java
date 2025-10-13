package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUser_UserPKOrderByCreatedDateDesc(int userPK);

    // 특정 유저가 작성한 게시글 조회 (UserEntity의 USERPK 사용)
    Page<Post> findByUser_UserPK(int userPK, Pageable pageable);



}
