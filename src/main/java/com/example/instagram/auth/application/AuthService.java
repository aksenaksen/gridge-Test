package com.example.instagram.auth.application;

import com.example.instagram.auth.domain.Expiration;
import com.example.instagram.auth.domain.TokenType;
import com.example.instagram.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final JwtUtil jwtUtil;
    private final IRefreshTokenRepository refreshTokenPort;
    
    public void validateRefreshToken(String refreshToken) {
        jwtUtil.validateToken(refreshToken);
    }
    
    public String createAccessToken(String username, Long userId, String role) {
        return jwtUtil.createToken(TokenType.ACCESS.name(), username, userId, role, Expiration.ACCESS.getTtl());
    }
    
    public String createRefreshToken(String username, Long userId, String role) {
        return jwtUtil.createToken(TokenType.REFRESH.name(), username, userId, role, Expiration.REFRESH.getTtl());
    }
    
    public void reissueRefreshToken(String username, String newRefreshToken) {
        refreshTokenPort.reissueToken(username, newRefreshToken, Expiration.REFRESH.getTtl());
    }
    
    public String getUsername(String token) {
        return jwtUtil.getUsername(token);
    }
    
    public String getRole(String token) {
        return jwtUtil.getRole(token);
    }
    
    public Long getUserId(String token) {
        return jwtUtil.getUserId(token);
    }
}