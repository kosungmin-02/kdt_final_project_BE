package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.domain.entity.ReportCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportCategoryRepository extends JpaRepository<ReportCategory, Long> {
}
