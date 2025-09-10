package com.example.instagram.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ServerErrorConstant {

    NOT_FOUND(ServerErrorMessage.NOT_FOUND, HttpStatus.NOT_FOUND),
    BAD_REQUEST(ServerErrorMessage.BAD_REQUEST, HttpStatus.BAD_REQUEST),;

    private final String message;
    private final HttpStatus httpStatus;

    ServerErrorConstant(String message, HttpStatus httpStatus){
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
