package com.example.DOTORY.admin.application;

import com.example.DOTORY.admin.api.dto.AdminCheckUserDTO;
import com.example.DOTORY.admin.api.dto.AdminCheckUserReportDTO;
import com.example.DOTORY.admin.api.dto.UserAgreeInfoDTO;
import com.example.DOTORY.global.code.status.ErrorStatus;
import com.example.DOTORY.global.exception.GeneralException;
import com.example.DOTORY.post.domain.entity.ReportCategory;
import com.example.DOTORY.post.domain.entity.ReportComment;
import com.example.DOTORY.post.domain.entity.ReportConfirm;
import com.example.DOTORY.post.domain.entity.ReportPost;
import com.example.DOTORY.post.domain.repository.ReportCommentRepository;
import com.example.DOTORY.post.domain.repository.ReportRepository;
import com.example.DOTORY.user.domain.entity.UserAgreeEntity;
import com.example.DOTORY.user.domain.entity.UserEntity;
import com.example.DOTORY.user.domain.entity.UserSNSEntity;
import com.example.DOTORY.user.domain.entity.UserStatus;
import com.example.DOTORY.user.domain.repository.UserAgreeRepository;
import com.example.DOTORY.user.domain.repository.UserRepository;
import com.example.DOTORY.user.domain.repository.UserSNSRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserSNSRepository snsRepository;
    private final UserAgreeRepository agreeRepository;
    private final ReportRepository reportPostRepository;
    private final ReportCommentRepository reportCommentRepository;

    private AdminCheckUserDTO toAdminCheckUserDTO(UserEntity user) {
        List<String> snsProviders = new ArrayList<>();
        for (UserSNSEntity sns : snsRepository.findByUser(user)) {
            snsProviders.add(sns.getProvider().name());
        }

        List<UserAgreeInfoDTO> agreements = new ArrayList<>();
        for (UserAgreeEntity agree : agreeRepository.findByUser(user)) {
            agreements.add(new UserAgreeInfoDTO(
                    agree.getAgree().getAgreeTitle(),
                    agree.isAgreed(),
                    agree.getUserAgreeDate()
            ));
        }


        List<AdminCheckUserReportDTO> reports = new ArrayList<>();
        for (ReportPost rp : reportPostRepository.findByReportedUser(user)) {
            reports.add(buildReportDTO(
                    rp.getReportId(),
                    "게시물",
                    rp.getPost().getPostId(),
                    rp.getCategory(),
                    rp.getReportContent(),
                    rp.getReportDate(),
                    rp.getReportConfirm(),
                    rp.getConfirmDate()
            ));
        }

        for (ReportComment rc : reportCommentRepository.findByReportedUser(user)) {
            reports.add(buildReportDTO(
                    rc.getReportId(),
                    "댓글",
                    rc.getComment().getCommentId(),
                    rc.getCategory(),
                    rc.getReportContent(),
                    rc.getReportDate(),
                    rc.getReportConfirm(),
                    rc.getConfirmDate()
            ));
        }

        return new AdminCheckUserDTO(
                user.getUserPK(),
                user.getUserName(),
                user.getUserNickname(),
                user.getUserEmail(),
                user.getCreatedDate(),
                user.getUpdatedDate(),
                user.getUserStatus(),
                snsProviders,
                agreements,
                reports
        );
    }

    private AdminCheckUserReportDTO buildReportDTO(
            Long reportId,
            String type,
            Long targetId,
            ReportCategory category,
            String reportContent,
            java.time.LocalDateTime reportDate,
            ReportConfirm confirmEnum,
            java.time.LocalDateTime confirmDate
    ) {
        String confirmReason = confirmEnum == ReportConfirm.COMPLETE ? "경고 사유 표시" : null;

        return new AdminCheckUserReportDTO(
                reportId,
                type,
                targetId,
                category != null ? category.getReason() : null,
                category != null ? category.getCategoryName() : null,
                reportContent,
                reportDate,
                confirmEnum != null ? confirmEnum.name() : null,
                confirmEnum != null ? confirmEnum.getDescription() : null,
                confirmDate,
                confirmReason
        );
    }

    @Transactional(readOnly = true)
    public Page<AdminCheckUserDTO> getAllUsers(Pageable pageable) {
        Page<UserEntity> userEntitiesPage = userRepository.findAll(pageable);
        return userEntitiesPage.map(this::toAdminCheckUserDTO);
    }

    @Transactional(readOnly = true)
    public AdminCheckUserDTO getUserDetail(int userPK) {
        UserEntity user = userRepository.findById(userPK)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return toAdminCheckUserDTO(user);
    }

    @Transactional
    public void DeleteUser(int userPK) {
        UserEntity user = userRepository.findById(userPK)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);
    }
}