package com.example.instagram.user.exception;

import com.example.instagram.common.exception.GlobalException;
import com.example.instagram.user.constant.UserErrorConstant;
import lombok.Getter;

@Getter
public class UserAlreadyExistException extends GlobalException {

    public UserAlreadyExistException(){
        super(UserErrorConstant.ALREADY_EXIST_USER);
    }
}
