package com.example.instagram.user.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.example.instagram.user.constant.UserMessageConstant.NOT_MATCHED_AGREEMENT_TYPE;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgreementValidator.class)
public @interface Agreement {

    String message() default NOT_MATCHED_AGREEMENT_TYPE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}