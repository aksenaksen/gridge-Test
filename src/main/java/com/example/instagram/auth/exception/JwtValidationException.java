package com.example.instagram.auth.exception;

import com.example.instagram.auth.constant.AuthErrorConstant;
import com.example.instagram.common.exception.GlobalException;

public class JwtValidationException extends GlobalException {
    public JwtValidationException(AuthErrorConstant errorConstant) {
        super(errorConstant);
    }
}
