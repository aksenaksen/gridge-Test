package com.example.instagram.common.security;

public enum OAuthProvider {

    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao");

    private final String providerName;

    OAuthProvider(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderName() {
        return this.providerName;
    }

    public static OAuthProvider from(String providerName) {
        for (OAuthProvider provider : values()) {
            if (provider.getProviderName().equalsIgnoreCase(providerName)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown OAuth provider: " + providerName);
    }

}