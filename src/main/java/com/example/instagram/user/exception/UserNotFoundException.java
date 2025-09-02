package com.example.instagram.user.exception;

import com.example.instagram.common.exception.GlobalException;
import com.example.instagram.user.constant.UserErrorConstant;

public class UserNotFoundException extends GlobalException {

    public UserNotFoundException() {
        super(UserErrorConstant.USER_NOTFOUND);
    }
}
