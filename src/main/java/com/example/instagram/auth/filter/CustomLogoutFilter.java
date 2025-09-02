package com.example.instagram.auth.filter;


import com.example.instagram.auth.application.IRefreshTokenService;
import com.example.instagram.auth.constant.AuthMessageConstant;
import com.example.instagram.auth.domain.TokenType;
import com.example.instagram.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final IRefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(!request.getRequestURI().endsWith(AuthMessageConstant.LOGOUT_URI)|| !request.getMethod().equals(HttpMethod.POST.name())) {
            filterChain.doFilter(request, response);
            return;
        }

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals(TokenType.REFRESH.name())) {
                refresh = cookie.getValue();
            }
        }

        if(refresh == null || jwtUtil.validateToken(refresh) || !jwtUtil.getTokenType(refresh).equals(TokenType.REFRESH.name())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        String username = jwtUtil.getUsername(refresh);
        if(!refresh.equals(refreshTokenService.read(username))) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        refreshTokenService.delete(refresh);

        response.setStatus(HttpStatus.OK.value());
        response.addCookie(createCookie(TokenType.REFRESH.name()));
    }

    private Cookie createCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }
}
