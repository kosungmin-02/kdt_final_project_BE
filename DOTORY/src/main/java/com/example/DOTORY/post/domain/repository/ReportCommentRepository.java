package com.example.DOTORY.post.domain.repository;

import com.example.DOTORY.post.domain.entity.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {

    // 중복 신고 확인
    boolean existsByComment_CommentIdAndUser_UserPK(Long postId, int userPK);

    // 댓글 신고 내역 조회
    List<ReportComment> findByUser_UserPK(int userPK);
}
