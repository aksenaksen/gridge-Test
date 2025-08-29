package com.example.instagram.user.exception;

import com.example.instagram.user.constant.UserMessage;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super(UserMessage.USER_NOTFOUND.getMessage());
    }
}
