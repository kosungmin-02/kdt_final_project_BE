package com.example.DOTORY.admin.api.dto;

import java.time.LocalDateTime;

public record AdminCheckUserAgreeDTO(
        String agreementName,
        boolean agreed,
        LocalDateTime agreedAt
){

}


