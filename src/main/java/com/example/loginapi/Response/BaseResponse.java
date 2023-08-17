package com.example.loginapi.Response;

import com.example.loginapi.exception.BaseException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final int code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;


    // result 없을 시
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    // result 있을 시
    public BaseResponse(BaseResponseStatus status, T result) {
        this.isSuccess = status.isSuccess();
        this.code = status.getCode();
        this.message = status.getMessage();
        this.result = result;
    }

    // BaseException 발생 시
    public BaseResponse(BaseException exception) {
        this.isSuccess = exception.getStatus().isSuccess();
        this.code = exception.getStatus().getCode();
        this.message = exception.getStatus().getMessage();
    }

}

