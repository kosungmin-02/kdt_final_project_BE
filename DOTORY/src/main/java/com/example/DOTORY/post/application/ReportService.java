package com.example.DOTORY.post.application;

import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.post.api.dto.request.ReportCommentRequest;
import com.example.DOTORY.post.api.dto.request.ReportRequest;
import com.example.DOTORY.post.api.dto.response.SimpleResponse;
import com.example.DOTORY.post.domain.entity.*;
import com.example.DOTORY.post.domain.repository.*;
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
    private final ReportCategoryRepository reportCategoryRepository;

    @Transactional
    public SimpleResponse reportPost(ReportRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        UserEntity reporter = userRepository.findById(request.getReporterUserPK())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        UserEntity reported = post.getUser();

        if (reporter.getUserPK() == reported.getUserPK()) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST, "본인 작성글은 신고할 수 없습니다.");
        }

        if (reportRepository.existsByPost_PostIdAndUser_UserPK(request.getPostId(), reporter.getUserPK())) {
            throw new GeneralException(ErrorStatus.DUPLICATE_RESOURCE, "이미 신고한 게시글입니다.");
        }

        ReportCategory category = reportCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "신고 카테고리를 찾을 수 없습니다."));

        ReportPost report = ReportPost.builder()
                .user(reporter)
                .reportedUser(reported)
                .post(post)
                .category(category)
                .reportContent(request.getReportContent())
                .reportDate(request.getReportDate())
                .reportConfirm(ReportConfirm.WAITING)
                .build();

        ReportPost saved = reportRepository.save(report);

        return SimpleResponse.builder()
                .reportId(saved.getReportId())
                .postId(saved.getPost().getPostId())
                .userPK(saved.getUser().getUserPK())
                .reportedUserPK(saved.getReportedUser().getUserPK())
                .categoryName(saved.getCategory().getCategoryName())
                .reportContent(saved.getReportContent())
                .reportDate(saved.getReportDate())
                .reportConfirm(saved.getReportConfirm())
                .build();
    }

    @Transactional
    public SimpleResponse reportComment(ReportCommentRequest request) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        UserEntity reporter = userRepository.findById(request.getReporterUserPK())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        UserEntity reported = comment.getUser();

        if (reporter.getUserPK() == reported.getUserPK()) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST, "본인 댓글은 신고할 수 없습니다.");
        }

        if (reportCommentRepository.existsByComment_CommentIdAndUser_UserPK(
                request.getCommentId(), reporter.getUserPK())) {
            throw new GeneralException(ErrorStatus.DUPLICATE_RESOURCE, "이미 신고한 댓글입니다.");
        }

        ReportCategory category = reportCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "신고 카테고리를 찾을 수 없습니다."));

        ReportComment report = ReportComment.builder()
                .user(reporter)
                .reportedUser(reported)
                .comment(comment)
                .category(category)
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
                .categoryName(saved.getCategory().getCategoryName())
                .reportContent(saved.getReportContent())
                .reportDate(saved.getReportDate())
                .reportConfirm(saved.getReportConfirm())
                .build();
    }

    @Transactional(readOnly = true)
    public List<SimpleResponse> getUserReportHistory(int userPK) {
        List<SimpleResponse> postReports = reportRepository.findByUser_UserPK(userPK).stream()
                .map(report -> SimpleResponse.builder()
                        .reportId(report.getReportId())
                        .postId(report.getPost().getPostId())
                        .userPK(report.getUser().getUserPK())
                        .reportedUserPK(report.getReportedUser().getUserPK())
                        .categoryName(report.getCategory().getCategoryName())
                        .reportContent(report.getReportContent())
                        .reportDate(report.getReportDate())
                        .reportConfirm(report.getReportConfirm())
                        .message("게시글 신고 내역")
                        .build())
                .collect(Collectors.toList());

        List<SimpleResponse> commentReports = reportCommentRepository.findByUser_UserPK(userPK).stream()
                .map(report -> SimpleResponse.builder()
                        .reportId(report.getReportId())
                        .commentId(report.getComment().getCommentId())
                        .userPK(report.getUser().getUserPK())
                        .reportedUserPK(report.getReportedUser().getUserPK())
                        .categoryName(report.getCategory().getCategoryName())
                        .reportContent(report.getReportContent())
                        .reportDate(report.getReportDate())
                        .reportConfirm(report.getReportConfirm())
                        .message("댓글 신고 내역")
                        .build())
                .collect(Collectors.toList());

        postReports.addAll(commentReports);
        return postReports;
    }
}