package com.example.instagram.user.valid;

import com.example.instagram.user.domain.AgreementType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

import static com.example.instagram.user.constant.UserMessageConstant.USERNAME_PATTERN;

public class UsernameValidator implements ConstraintValidator<Username, String>{

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if(username == null){
            return true;
        }
        
        return USERNAME_PATTERN.matches(username);
    }
}