package com.example.instagram.user.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.example.instagram.user.constant.UserMessageConstant.NOT_MATCHED_USERNAME;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
public @interface Username {

    String message() default NOT_MATCHED_USERNAME;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
