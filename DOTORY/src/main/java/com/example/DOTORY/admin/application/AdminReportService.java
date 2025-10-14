package com.example.DOTORY.admin.application;

import com.example.DOTORY.admin.api.dto.AdminCheckUserReportDTO;
import com.example.DOTORY.admin.api.dto.AdminReportUpdateRequestDTO;
import com.example.DOTORY.admin.api.dto.AdminReportResponseDTO;
import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.post.domain.entity.*;
import com.example.DOTORY.post.domain.repository.ReportCommentRepository;
import com.example.DOTORY.post.domain.repository.ReportRepository;
import com.example.DOTORY.post.domain.repository.ReportCategoryRepository;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminReportService {

    private final ReportRepository reportRepository;
    private final ReportCommentRepository reportCommentRepository;
    private final ReportCategoryRepository reportCategoryRepository;
    private final UserRepository userRepository;

    public List<AdminReportResponseDTO> getAllReports(String categoryName) {
        List<AdminReportResponseDTO> result = new ArrayList<>();

        reportRepository.findAll().forEach(r -> {
            if (categoryName == null || categoryName.isEmpty() || r.getCategory().getCategoryName().equals(categoryName)) {
                result.add(new AdminReportResponseDTO(
                        r.getReportId(),
                        "게시글",
                        r.getUser().getUserNickname(),
                        r.getReportedUser().getUserNickname(),
                        r.getCategory().getCategoryName(),
                        r.getReportContent(),
                        r.getReportDate(),
                        r.getReportConfirm(),
                        r.getConfirmDate(),
                        null,
                        r.getPost().getPostId()
                ));
            }
        });

        reportCommentRepository.findAll().forEach(r -> {
            if (categoryName == null || categoryName.isEmpty() || r.getCategory().getCategoryName().equals(categoryName)) {
                result.add(new AdminReportResponseDTO(
                        r.getReportId(),
                        "댓글",
                        r.getUser().getUserNickname(),
                        r.getReportedUser().getUserNickname(),
                        r.getCategory().getCategoryName(),
                        r.getReportContent(),
                        r.getReportDate(),
                        r.getReportConfirm(),
                        r.getConfirmDate(),
                        null,
                        r.getComment().getCommentId()
                ));
            }
        });

        return result;
    }

    public List<String> getAllCategories() {
        return reportCategoryRepository.findAll()
                .stream()
                .map(ReportCategory::getCategoryName)
                .toList();
    }

    public AdminReportResponseDTO getReportById(Long reportId, boolean isComment) {
        if (isComment) {
            ReportComment r = reportCommentRepository.findById(reportId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "존재하지 않는 댓글 신고입니다."));
            return new AdminReportResponseDTO(
                    r.getReportId(),
                    "댓글",
                    r.getUser().getUserNickname(),
                    r.getReportedUser().getUserNickname(),
                    r.getCategory().getCategoryName(),
                    r.getReportContent(),
                    r.getReportDate(),
                    r.getReportConfirm(),
                    r.getConfirmDate(),
                    r.getConfirmMessage(),
                    r.getComment().getCommentId()
            );
        } else {
            ReportPost r = reportRepository.findById(reportId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "존재하지 않는 게시글 신고입니다."));
            return new AdminReportResponseDTO(
                    r.getReportId(),
                    "게시글",
                    r.getUser().getUserNickname(),
                    r.getReportedUser().getUserNickname(),
                    r.getCategory().getCategoryName(),
                    r.getReportContent(),
                    r.getReportDate(),
                    r.getReportConfirm(),
                    r.getConfirmDate(),
                    r.getConfirmMessage(),
                    r.getPost().getPostId()
            );
        }
    }

    public void updateReport(Long reportId, AdminReportUpdateRequestDTO dto) {
        if (dto.isComment()) {
            ReportComment r = reportCommentRepository.findById(reportId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "존재하지 않는 댓글 신고입니다."));
            r.setReportConfirm(dto.status());
            r.setConfirmMessage(dto.confirmMessage());
            r.setConfirmDate(LocalDateTime.now());
            reportCommentRepository.save(r);
        } else {
            ReportPost r = reportRepository.findById(reportId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND, "존재하지 않는 게시글 신고입니다."));
            r.setReportConfirm(dto.status());
            r.setConfirmMessage(dto.confirmMessage());
            r.setConfirmDate(LocalDateTime.now());
            reportRepository.save(r);
        }
    }

    public List<AdminCheckUserReportDTO> getReportsByUser(int userPk) {
        UserEntity user = userRepository.findById(userPk)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        List<AdminCheckUserReportDTO> result = new ArrayList<>();

        reportRepository.findByReportedUser(user).forEach(r -> result.add(
                new AdminCheckUserReportDTO(
                        r.getReportId(),
                        "게시글",
                        r.getPost().getPostId(),
                        r.getCategory().getReason(),
                        r.getCategory().getCategoryName(),
                        r.getReportContent(),
                        r.getReportDate(),
                        r.getReportConfirm().name(),
                        r.getReportConfirm().getDescription(),
                        r.getConfirmDate(),
                        null
                )
        ));

        reportCommentRepository.findByReportedUser(user).forEach(r -> result.add(
                new AdminCheckUserReportDTO(
                        r.getReportId(),
                        "댓글",
                        r.getComment().getCommentId(),
                        r.getCategory().getReason(),
                        r.getCategory().getCategoryName(),
                        r.getReportContent(),
                        r.getReportDate(),
                        r.getReportConfirm().name(),
                        r.getReportConfirm().getDescription(),
                        r.getConfirmDate(),
                        null
                )
        ));

        return result;
    }
}