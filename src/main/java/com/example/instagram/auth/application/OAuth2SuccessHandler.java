package com.example.instagram.auth.application;


import com.example.instagram.auth.constant.AuthMessageConstant;
import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.auth.domain.Expiration;
import com.example.instagram.auth.domain.TokenType;
import com.example.instagram.common.util.JwtUtil;
import com.example.instagram.user.infrastructor.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final IRefreshTokenService refreshTokenPort;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = userDetails.getUser().getUsername();
        String role = userDetails.getUser().getRole().getRoleName();
        Long userId = userDetails.getUser().getUserId();

        String accessToken = jwtUtil.createToken(TokenType.ACCESS.name(), username, userId, role, Expiration.ACCESS.getTtl());
        String refreshToken = jwtUtil.createToken(TokenType.REFRESH.name(), username, userId, role, Expiration.REFRESH.getTtl());
        userDetails.getUser().updateLastLoginAt();

        userRepository.save(userDetails.getUser());
        refreshTokenPort.create(username,refreshToken,Expiration.REFRESH.getTtl());

        response.addHeader(HttpHeaders.AUTHORIZATION, AuthMessageConstant.BEARER_TOKEN_PREFIX + accessToken);
        response.addCookie(createCookie(TokenType.REFRESH.name(), refreshToken));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(accessToken);
    }

    private Cookie createCookie(String name, String refreshToken) {
        Cookie cookie = new Cookie(name, refreshToken);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(1));
        return cookie;
    }

}
