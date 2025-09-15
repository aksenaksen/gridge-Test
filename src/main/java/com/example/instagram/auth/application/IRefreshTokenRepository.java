package com.example.instagram.auth.application;

public interface IRefreshTokenRepository {
    void create(String username, String refreshToken, long ttl);
    void reissueToken(String username, String newRefreshToken, long ttl);
    void delete(String username);
//    boolean existsByUsername(String username);
    String read(String key);

}