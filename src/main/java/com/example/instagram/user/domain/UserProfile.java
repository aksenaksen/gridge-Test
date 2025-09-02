package com.example.instagram.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "birth_day", nullable = false)
    private LocalDate birthDay;
    
    @Builder
    public UserProfile(String name, String phoneNumber, LocalDate birthDay) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
    }

    public static UserProfile createProfile(String name, String phoneNumber, LocalDate birthDay) {
        UserProfile profile = new UserProfile();
        profile.name = name;
        profile.phoneNumber = phoneNumber;
        profile.birthDay = birthDay;
        return profile;
    }
    
    public void updateName(String name) {
        this.name = name;
    }
    
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public void updateBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }
}