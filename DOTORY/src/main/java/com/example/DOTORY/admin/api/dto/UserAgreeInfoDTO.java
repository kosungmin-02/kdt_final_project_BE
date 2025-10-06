package com.example.DOTORY.admin.api.dto;

import java.time.LocalDateTime;

public record UserAgreeInfoDTO(
        String agreeTitle,
        boolean agreed,
        LocalDateTime agreeDate
) {

}
