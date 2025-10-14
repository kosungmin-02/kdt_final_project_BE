package com.example.DOTORY.global.exception;

import com.example.DOTORY.global.code.BaseErrorCode;
import com.example.DOTORY.global.code.dto.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ GeneralException.class })
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(GeneralException e, WebRequest request) {
        BaseErrorCode code = e.getCode();
        return handleExceptionInternal(e, code);
    }

    private ResponseEntity<ApiResponse<Object>> handleExceptionInternal(Exception e, BaseErrorCode code) {
        return ResponseEntity.status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code.getCode(), code.getMessage(), null));
    }
}
