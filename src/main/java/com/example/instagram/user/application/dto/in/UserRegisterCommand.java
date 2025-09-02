package com.example.instagram.user.application.dto.in;

import com.example.instagram.user.domain.AgreementType;
import com.example.instagram.user.domain.UserProfile;

import java.time.LocalDate;
import java.util.List;

public record UserRegisterCommand(
        String username,
        String name,
        String phoneNumber,
        String password,
        LocalDate birthDay,
        List<AgreementType> agreements
) {
    public UserProfile toProfile(){
        return UserProfile.createProfile(
                this.name,
                this.phoneNumber,
                this.birthDay
        );
    }

}
