package com.example.DOTORY.global.post.domain.repository;

import com.example.DOTORY.global.post.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByPost_PostIdAndUserId(Long postId, Long userId);

}
