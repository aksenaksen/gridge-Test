package com.example.instagram.user.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static com.example.instagram.user.constant.UserMessageConstant.PHONE_NUMBER_PATTERN;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {

        if(phoneNumber == null) return true;

        return isValidPhoneNumber(phoneNumber);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {

        return phoneNumber.matches(PHONE_NUMBER_PATTERN);
    }
}