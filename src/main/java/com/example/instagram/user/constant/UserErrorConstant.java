package com.example.instagram.user.constant;

import com.example.instagram.common.exception.GlobalErrorConstant;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorConstant implements GlobalErrorConstant {

    CANNOT_ACTIVATE("해당 유저를 활성화 상태로 만들 수 없습니다", HttpStatus.BAD_REQUEST),
    CANNOT_DEACTIVATE("해당 유저를 비활성화 상태로 만들 수 없습니다", HttpStatus.BAD_REQUEST),
    CANNOT_SUSPEND("해당 유저를 정지 상태로 만들 수 없습니다", HttpStatus.BAD_REQUEST),

    NOT_AGREED("필수 약관 동의가 누락 되었습니다", HttpStatus.BAD_REQUEST),

    USER_NOTFOUND("해당하는 유저를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    ALREADY_EXIST_USER(UserMessageConstant.ALREADY_EXIST_USER, HttpStatus.CONFLICT),
    
    USERNAME_REQUIRED(UserMessageConstant.USERNAME_REQUIRED, HttpStatus.BAD_REQUEST),
    USERNAME_SIZE_INVALID(UserMessageConstant.USERNAME_SIZE_INVALID, HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(UserMessageConstant.PASSWORD_REQUIRED, HttpStatus.BAD_REQUEST),
    PASSWORD_SIZE_INVALID(UserMessageConstant.PASSWORD_SIZE_INVALID, HttpStatus.BAD_REQUEST),
    NAME_REQUIRED(UserMessageConstant.NAME_REQUIRED, HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_REQUIRED(UserMessageConstant.PHONE_NUMBER_REQUIRED, HttpStatus.BAD_REQUEST),
    BIRTHDAY_REQUIRED(UserMessageConstant.BIRTHDAY_REQUIRED, HttpStatus.BAD_REQUEST),
    AGREEMENTS_REQUIRED(UserMessageConstant.AGREEMENTS_REQUIRED, HttpStatus.BAD_REQUEST),

    NOT_MATCHED_PHONE_NUMBER(UserMessageConstant.NOT_MATCHED_PHONE_NUMBER, HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    UserErrorConstant(String message, HttpStatus httpStatus){
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
