package com.example.DOTORY.global.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ErrorResponse {
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String message;
    private final String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Object result;
}
