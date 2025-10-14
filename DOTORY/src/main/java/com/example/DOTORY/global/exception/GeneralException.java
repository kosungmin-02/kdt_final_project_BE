package com.example.DOTORY.global.exception;

import com.example.DOTORY.global.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final BaseErrorCode code;
    private final String message;

    public GeneralException(BaseErrorCode code) {
        super(code.getMessage());
        this.code = code;
        this.message = code.getMessage();
    }

    public GeneralException(BaseErrorCode code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String getErrorCode() {
        return code.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }
}