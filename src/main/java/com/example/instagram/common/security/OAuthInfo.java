package com.example.instagram.common.security;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthInfo {
    
    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;
    
    private String oauthId;
}