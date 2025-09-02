package com.example.instagram.user.presentation.in;

import com.example.instagram.user.application.dto.in.UserRegisterCommand;
import com.example.instagram.user.constant.UserMessageConstant;
import com.example.instagram.user.domain.AgreementType;
import com.example.instagram.user.valid.Agreement;
import com.example.instagram.user.valid.PhoneNumber;
import com.example.instagram.user.valid.Username;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record RequestRegisterUser(

        @NotBlank(message = UserMessageConstant.USERNAME_REQUIRED)
        @Username
        String username,
        
        @NotBlank(message = UserMessageConstant.PASSWORD_REQUIRED)
        @Size(min = 6, max = 20, message = UserMessageConstant.PASSWORD_SIZE_INVALID)
        String password,
        
        @NotBlank(message = UserMessageConstant.NAME_REQUIRED)
        String name,

        @NotNull(message = UserMessageConstant.PHONE_NUMBER_REQUIRED)
        @PhoneNumber
        String phoneNumber,
        
        @NotNull(message = UserMessageConstant.BIRTHDAY_REQUIRED)
        LocalDate birthDay,

        @Agreement
        List<String> agreements

) {
    public UserRegisterCommand toCommand(){
        return new UserRegisterCommand(
                this.username,
                this.name,
                this.phoneNumber,
                this.password,
                this.birthDay,
                toAgreements()
        );
    }
    private List<AgreementType> toAgreements(){
        return this.agreements.stream()
                .map(AgreementType::getAgreementType)
                .toList();
    }
}
