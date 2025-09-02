package com.example.instagram.auth.domain;

public interface OAuth2Info {
    String getProviderId();
    String getProvider();
    String getName();
    String getEmail();
    String getMobNo();
    String getBirthday();
    String getBirthYear();
}
