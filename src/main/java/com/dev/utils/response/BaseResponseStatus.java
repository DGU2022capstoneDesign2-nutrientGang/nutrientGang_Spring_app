package com.dev.utils.response;

import lombok.Getter;

/**
 * BaseResponse 에 담을 Status
 * */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000: 성공
     * */
    SUCCESS(true,1000,"요청에 성공하였습니다."),

    /**
     * 2000: auth 관련오류
     * */
    REQUEST_ERROR(false,2000,"입력값을 확인해주세요"),
    EMPTY_JWT(false,2001,"JWT가 비어있습니다."),
    NOT_FOUND_PASSWORD(false,2002,"비밀번호를 찾지 못하였습니다."),
    WRONG_PASSWORD(false,2003,"비밀번호가 틀렸습니다."),
    NOT_FOUND_AUTHORITY(false,2004,"유저에게 권한이 없습니다."),
    DUPLICATE_USER(false,2005,"이미 회원가입된 email 입니다."),
    BAD_TOKEN(false,2006,"잘못된 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(false,2007,"토큰이 만료되었습니다."),
    LOGOUT_MEMBER(false,2008,"로그아웃된 사용자입니다."),
    NOT_EQUAL_TOKEN(false,2009,"토큰이 일치하지 않습니다."),
    NOT_FOUND_USER(false,2010,"유저를 찾을 수 없습니다."),
    NOT_FOUND_AUTHENTICATION(false,2011,"Security Context 에 인증 정보가 없습니다."),
    NOT_FOUND_AUTHORITY_IN_TOKEN(false,2012,"권한 정보가 없는 토큰입니다."),
    INVALID_TOKEN(false,2013,"Refresh Token이 유효하지 않습니다."),
    NOT_FOUND_HEALTH_STATUS(false,2014,"건강정보를 찾을 수 없습니다."),
    NOT_FOUND_NUTRIENT_STATUS(false,2015,"영양정보를 찾을 수 없습니다."),
    NOT_FOUND_FOOD(false,2016,"음식정보를 찾을 수 없습니다."),
    NOT_FOUND_INTAKE(false,2017,"식사 기록을 찾을 수 없습니다."),
    NOT_FOUND_MENU(false,2018,"음식점을 찾을 수 없습니다."),
    ZERO_DATA_INFO(false,2019,"데이터가 0입니다."),


    /**
     * 5000: 서버 내부 오류
     * */
    INTERNAL_SERVER_ERROR(false,5000,"서버 내부 로직 중 오류가 발생했습니다.");
    /**
     * 코드 추가해서 Status를 관리합니다.
     * */
    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
