package com.example.loginapi.Response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),
    /**
     * 300번대(리다이렉트)
     */


    /**
     * 400번대(클라이언트 에러)
     */
    // 공통
    BAD_REQUEST(false, HttpStatus.BAD_REQUEST.value(), "요청 값을 확인해주세요."),
    BAD_REQUEST_PARAM(false, HttpStatus.BAD_REQUEST.value(), "요청 파라미터를 확인해주세요."),
    BAD_REQUST_ID(false, HttpStatus.BAD_REQUEST.value(),"유저의 id 가 존재하지 않습니다"),
    EMPTY_JWT(false, HttpStatus.BAD_REQUEST.value(), "빈 jwt 입니다."),
    INVALID_JWT(false, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 jwt 입니다."),
    JWT_MISMATCH(false, HttpStatus.BAD_REQUEST.value(), "jwt 정보가 일치하지 않습니다."),
    INVALID_USER_ID(false, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 user id 입니다."),
    INVALID_PROFILE_ID(false, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 profile id 입니다."),



    /**
     * 500번대(서버 에러)
     */
    // 공통
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상치 못한 서버 에러입니다. 제보 부탁드립니다.")

    ;

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
