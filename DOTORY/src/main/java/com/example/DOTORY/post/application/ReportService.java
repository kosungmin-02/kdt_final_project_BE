package com.example.DOTORY.post.application;

import com.example.DOTORY.post.api.dto.request.ReportRequest;
import com.example.DOTORY.post.api.dto.response.SimpleResponse;
import com.example.DOTORY.post.domain.entity.Post;
import com.example.DOTORY.post.domain.entity.ReportPost;
import com.example.DOTORY.post.domain.repository.PostRepository;
import com.example.DOTORY.post.domain.repository.ReportRepository;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    @Transactional
    public SimpleResponse reportPost(ReportRequest request) {
        // 게시글 찾기
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 신고자 (로그인한 사용자)
        UserEntity reporter = userRepository.findById(request.getReporterUserPK())
                .orElseThrow(() -> new IllegalArgumentException("신고자를 찾을 수 없습니다."));

        // 피신고자 (게시글 작성자)
        UserEntity reported = post.getUser();

        // 작성자는 자신의 글을 신고할 수 없다.
        if(reporter.getUserPK() == reported.getUserPK()){
            throw new IllegalArgumentException("본인 작성글은 신고할 수 없습니다.");
        }

        // 중복 신고 방지
        if (reportRepository.existsByPost_PostIdAndUser_UserPK(request.getPostId(), reporter.getUserPK())) {
            throw new IllegalArgumentException("이미 신고한 게시글입니다.");
        }

        // 신고 객체 생성
        ReportPost report = ReportPost.builder()
                .user(reporter)               // 신고자
                .reportedUser(reported)       // 피신고자
                .post(post)
                .reason(request.getReason())
                .reportContent(request.getReportContent())
                .reportDate(request.getReportDate())
                .build();

        ReportPost saved = reportRepository.save(report);

        return SimpleResponse.builder()
                .reportId(saved.getReportId())
                .postId(saved.getPost().getPostId())
                .userPK(saved.getUser().getUserPK())
                .reportedUserPK(saved.getReportedUser().getUserPK())
                .reason(saved.getReason())
                .reportContent(saved.getReportContent())
                .reportDate(saved.getReportDate())
                .build();
    }


}
