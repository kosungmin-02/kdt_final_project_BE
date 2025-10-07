package com.example.DOTORY.admin.api.dto;

import com.example.DOTORY.post.domain.entity.ReportConfirm;

public record AdminReportUpdateRequestDTO(
        boolean isComment,
        String confirmMessage,
        ReportConfirm status
) {}

