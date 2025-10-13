package com.example.DOTORY.admin.application;

import com.example.DOTORY.admin.api.dto.AdminCheckUserDTO;
import com.example.DOTORY.admin.api.dto.AdminCheckUserReportDTO;
import com.example.DOTORY.admin.api.dto.UserAgreeInfoDTO;
import com.example.DOTORY.post.domain.entity.*;
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

    // [공통] - 회원 DTO 변환용 메소드
    private AdminCheckUserDTO toAdminCheckUserDTO(UserEntity user) {

        // SNS 연결 여부
        List<String> snsProviders = new ArrayList<>();
        for (UserSNSEntity sns : snsRepository.findByUser(user)) {
            snsProviders.add(sns.getProvider().name());
        }

        // 선택약관 어떤거 동의했는지 목록
        List<UserAgreeInfoDTO> agreements = new ArrayList<>();
        for (UserAgreeEntity agree : agreeRepository.findByUser(user)) {
            agreements.add(new UserAgreeInfoDTO(
                    agree.getAgree().getAgreeTitle(),
                    agree.isAgreed(),
                    agree.getUserAgreeDate()
            ));
        }

        // 신고당한 내역이 뭐있는지 목록
        List<AdminCheckUserReportDTO> reports = new ArrayList<>();
        // 신고당한 목록
        // 게시글 신고
        for (ReportPost rp : reportPostRepository.findByReportedUser(user)) {
            reports.add(buildReportDTO(
                    rp.getReportId(),
                    "게시물",
                    rp.getPost().getPostId(),
                    rp.getCategory(),         // ReportReason Enum
                    rp.getReportContent(),
                    rp.getReportDate(),
                    rp.getReportConfirm(),  // ReportConfirm Enum
                    rp.getConfirmDate()
            ));
        }

        // 댓글 신고
        for (ReportComment rc : reportCommentRepository.findByReportedUser(user)) {
            reports.add(buildReportDTO(
                    rc.getReportId(),
                    "댓글",
                    rc.getComment().getCommentId(),
                    rc.getCategory(),        // ReportReason enum
                    rc.getReportContent(),
                    rc.getReportDate(),
                    rc.getReportConfirm(), // ReportConfirm enum
                    rc.getConfirmDate()
            ));
        }


        // dto로 반환
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

    // [공통] - 신고 DTO 생성
    private AdminCheckUserReportDTO buildReportDTO(
            Long reportId,
            String type,
            Long targetId,
            ReportCategory category,   // enum -> entity
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
                category != null ? category.getReason() : null,       // 영어 코드 → reason
                category != null ? category.getCategoryName() : null, // 한글 설명 -> categoryName
                reportContent,
                reportDate,
                confirmEnum != null ? confirmEnum.name() : null,
                confirmEnum != null ? confirmEnum.getDescription() : null,
                confirmDate,
                confirmReason
        );
    }



    // 전체 회원 조회하기
    @Transactional(readOnly = true)
    public List<AdminCheckUserDTO> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        List<AdminCheckUserDTO> result = new ArrayList<>();
        for (UserEntity user : users) {
            result.add(toAdminCheckUserDTO(user));
        }
        return result;
    }

    // 회원 상세 조회 - [공통] toAdminCheckUserDTO 활용
    @Transactional(readOnly = true)
    public AdminCheckUserDTO getUserDetail(int userPK) {
        UserEntity user = userRepository.findById(userPK)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        return toAdminCheckUserDTO(user); // DTO 변환 호출
    }

    // 회원 강제 탈퇴
    @Transactional
    public void DeleteUser(int userPK) {
        UserEntity user = userRepository.findById(userPK)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);
    }
}