package com.example.instagram.auth.exception;

import com.example.instagram.auth.constant.AuthErrorConstant;
import com.example.instagram.common.exception.GlobalException;

public class NotActiveUserException extends GlobalException {

    public NotActiveUserException() {
        super(AuthErrorConstant.NOT_ACTIVATE_USER);
    }
}
