package com.example.DOTORY.post.application;

import com.example.DOTORY.post.api.dto.request.ReportCommentRequest;
import com.example.DOTORY.post.api.dto.request.ReportRequest;
import com.example.DOTORY.post.api.dto.response.SimpleResponse;
import com.example.DOTORY.post.domain.entity.*;
import com.example.DOTORY.post.domain.repository.CommentRepository;
import com.example.DOTORY.post.domain.repository.PostRepository;
import com.example.DOTORY.post.domain.repository.ReportCommentRepository;
import com.example.DOTORY.post.domain.repository.ReportRepository;
import com.example.DOTORY.user.application.UserService;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReportCommentRepository reportCommentRepository;

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
                .reportConfirm(ReportConfirm.WAITING)       // 기본값으로 WAITING 넣음.
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
                .reportConfirm(saved.getReportConfirm())
                .build();
    }


    // 댓글 신고
    @Transactional
    public SimpleResponse reportComment(ReportCommentRequest request) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        UserEntity reporter = userRepository.findById(request.getReporterUserPK())
                .orElseThrow(() -> new IllegalArgumentException("신고자를 찾을 수 없습니다."));

        UserEntity reported = comment.getUser();

        if (reporter.getUserPK() == reported.getUserPK()) {
            throw new IllegalArgumentException("본인 댓글은 신고할 수 없습니다.");
        }

        if (reportCommentRepository.existsByComment_CommentIdAndUser_UserPK(
                request.getCommentId(), reporter.getUserPK()
        )) {
            throw new IllegalArgumentException("이미 신고한 댓글입니다.");
        }

        ReportComment report = ReportComment.builder()
                .user(reporter)
                .reportedUser(reported)
                .comment(comment)
                .reason(request.getReason())
                .reportContent(request.getReportContent())
                .reportDate(request.getReportDate())
                .reportConfirm(ReportConfirm.WAITING)
                .build();

        ReportComment saved = reportCommentRepository.save(report);

        return SimpleResponse.builder()
                .reportId(saved.getReportId())
                .commentId(saved.getComment().getCommentId())
                .userPK(saved.getUser().getUserPK())
                .reportedUserPK(saved.getReportedUser().getUserPK())
                .reason(saved.getReason())
                .reportContent(saved.getReportContent())
                .reportDate(saved.getReportDate())
                .reportConfirm(saved.getReportConfirm())
                .build();
    }


    @Transactional(readOnly = true)
    public List<SimpleResponse> getUserReportHistory(int userPK) {
        // 1. 게시글 신고 내역
        List<SimpleResponse> postReports = reportRepository.findByUser_UserPK(userPK).stream()
                .map(report -> SimpleResponse.builder()
                        .reportId(report.getReportId())
                        .postId(report.getPost().getPostId())
                        .userPK(report.getUser().getUserPK())
                        .reportedUserPK(report.getReportedUser().getUserPK())
                        .reason(report.getReason())
                        .reportContent(report.getReportContent())
                        .reportDate(report.getReportDate())
                        .reportConfirm(report.getReportConfirm())
                        .message("게시글 신고 내역")
                        .build())
                .collect(Collectors.toList());

        // 2. 댓글 신고 내역
        List<SimpleResponse> commentReports = reportCommentRepository.findByUser_UserPK(userPK).stream()
                .map(report -> SimpleResponse.builder()
                        .reportId(report.getReportId())
                        .commentId(report.getComment().getCommentId())
                        .userPK(report.getUser().getUserPK())
                        .reportedUserPK(report.getReportedUser().getUserPK())
                        .reason(report.getReason())
                        .reportContent(report.getReportContent())
                        .reportDate(report.getReportDate())
                        .reportConfirm(report.getReportConfirm())
                        .message("댓글 신고 내역")
                        .build())
                .collect(Collectors.toList());

        // 3. 게시글 신고 + 댓글 신고 합치기
        postReports.addAll(commentReports);
        return postReports;
    }
}
