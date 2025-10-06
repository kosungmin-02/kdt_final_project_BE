package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.domain.entity.ReportPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ReportRepository extends JpaRepository<ReportPost, Long> {

    // 이미 신고를 했는지 안했는지 확인을 위해 중복 확인하기.
    boolean existsByPost_PostIdAndUser_UserPK(Long postId, int userPK);

    // 사용자 PK로 신고 내역 조회
    List<ReportPost> findByUser_UserPK(int userPK);
}
