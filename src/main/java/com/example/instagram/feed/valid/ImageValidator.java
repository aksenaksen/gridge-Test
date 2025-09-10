package com.example.instagram.feed.valid;

import com.example.instagram.user.domain.AgreementType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ImageValidator implements ConstraintValidator<Image, List<String>> {

    @Override
    public boolean isValid(List<String> agreements, ConstraintValidatorContext context) {
        if (agreements == null || agreements.isEmpty()) {
            return false;
        }
        
        for (String agreement : agreements) {
            if (!AgreementType.exists(agreement)) {
                return false;
            }
        }
        
        return true;
    }
}