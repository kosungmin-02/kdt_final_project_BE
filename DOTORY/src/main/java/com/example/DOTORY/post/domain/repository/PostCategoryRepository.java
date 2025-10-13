package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.domain.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {

    // 부모 ID 기준으로 하위 카테고리 조회
    List<PostCategory> findAllByParent_CategoryId(Long parentId);


}


