package com.example.instagram.common.exception;

import org.springframework.http.HttpStatus;

public interface GlobalErrorConstant {
    String getMessage();
    HttpStatus getHttpStatus();
}
