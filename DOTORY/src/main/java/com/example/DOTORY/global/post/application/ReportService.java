package com.example.DOTORY.global.post.application;

import com.example.DOTORY.global.post.api.dto.request.ReportRequest;
import com.example.DOTORY.global.post.api.dto.response.SimpleResponse;
import com.example.DOTORY.global.post.domain.entity.Post;
import com.example.DOTORY.global.post.domain.entity.Report;
import com.example.DOTORY.global.post.domain.repository.PostRepository;
import com.example.DOTORY.global.post.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;

    @Transactional
    public SimpleResponse reportPost(ReportRequest request) {
        // 게시글 찾기
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 중복 신고 방지
        if (reportRepository.existsByPost_PostIdAndUserId(request.getPostId(), request.getUserId())) {
            throw new IllegalArgumentException("이미 신고한 게시글입니다.");
        }

        Report report = Report.builder()
                .userId(request.getUserId())
                .post(post)
                .reason(request.getReason())
                .description(request.getDescription())
                .build();

        Report saved = reportRepository.save(report);

        return SimpleResponse.builder()
                .reportId(saved.getReportId())
                .postId(saved.getPost().getPostId())
                .userId(saved.getUserId())
                .reason(saved.getReason())
                .description(saved.getDescription())
                .createdDate(saved.getPost().getCreatedDate())
                .build();
    }

}
