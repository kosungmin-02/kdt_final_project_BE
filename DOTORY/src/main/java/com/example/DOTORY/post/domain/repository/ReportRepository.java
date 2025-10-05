package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.domain.entity.ReportPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportPost, Long> {

    boolean existsByPost_PostIdAndUser_UserPK(Long postId, int userPK);
}
