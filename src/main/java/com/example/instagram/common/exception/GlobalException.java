package com.example.instagram.common.exception;

import org.springframework.http.HttpStatus;

public abstract class GlobalException extends RuntimeException {
    
    private final GlobalErrorConstant errorConstant;
    
    protected GlobalException(GlobalErrorConstant errorConstant) {
        super(errorConstant.getMessage());
        this.errorConstant = errorConstant;
    }

    public HttpStatus getHttpStatus() {
        return errorConstant.getHttpStatus();
    }
}