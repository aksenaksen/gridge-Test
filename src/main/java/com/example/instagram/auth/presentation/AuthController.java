package com.example.instagram.auth.presentation;

import com.example.instagram.auth.application.AuthService;
import com.example.instagram.auth.constant.AuthMessageConstant;
import com.example.instagram.auth.domain.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/v1/auth/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = extractRefreshToken(request);

        authService.validateRefreshToken(refreshToken);

        String username = authService.getUsername(refreshToken);
        String role = authService.getRole(refreshToken);
        Long userId = authService.getUserId(refreshToken);

        String accessToken = authService.createAccessToken(username, userId, role);
        String newRefreshToken = authService.createRefreshToken(username, userId, role);

        authService.reissueRefreshToken(username, newRefreshToken);
        
        response.addHeader(HttpHeaders.AUTHORIZATION, AuthMessageConstant.BEARER_TOKEN_PREFIX + accessToken);
        response.addCookie(createCookie(TokenType.REFRESH.name(), newRefreshToken));

        return ResponseEntity.ok().build();
    }



    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> TokenType.REFRESH.name().equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private Cookie createCookie(String name, String refreshToken) {
        Cookie cookie = new Cookie(name, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(1));
        return cookie;
    }
}