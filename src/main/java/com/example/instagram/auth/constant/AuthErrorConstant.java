package com.example.instagram.auth.constant;

import com.example.instagram.common.exception.GlobalErrorConstant;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorConstant implements GlobalErrorConstant {

    NOT_ACTIVATE_USER(AuthMessageConstant.NOT_ACTIVATE_USER, HttpStatus.UNAUTHORIZED),
    INVALID_SIGNATURE(AuthMessageConstant.INVALID_JWT_SIGNATURE, HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN(AuthMessageConstant.EXPIRED_JWT_TOKEN, HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN(AuthMessageConstant.UNSUPPORTED_JWT_TOKEN, HttpStatus.UNAUTHORIZED),
    EMPTY_CLAIMS(AuthMessageConstant.JWT_CLAIMS_EMPTY, HttpStatus.UNAUTHORIZED);

    private final String message;
    private final HttpStatus httpStatus;

    AuthErrorConstant(String message, HttpStatus httpStatus){
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
