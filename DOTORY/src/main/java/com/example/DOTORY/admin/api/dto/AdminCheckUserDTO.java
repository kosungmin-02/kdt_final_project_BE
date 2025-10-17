package com.example.DOTORY.admin.api.dto;

import com.example.DOTORY.user.domain.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public record AdminCheckUserDTO(
        int userPK,
        String userName,
        String userNickname,
        String userEmail,
        LocalDateTime createdDate,
        LocalDateTime updatedDate,
        UserStatus userStatus,
        List<String> snsProviders,
        List<UserAgreeInfoDTO> agreements,
        List<AdminCheckUserReportDTO> reports
) {

}
