package com.example.instagram.auth.filter;

import com.example.instagram.auth.application.IRefreshTokenService;
import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.auth.domain.Expiration;
import com.example.instagram.auth.domain.TokenType;
import com.example.instagram.common.util.JwtUtil;
import com.example.instagram.user.infrastructor.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.instagram.auth.constant.AuthMessageConstant.*;

@Slf4j
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final IRefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> body = null;

        try {
            body = mapper.readValue(request.getInputStream(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String username = body.get("username");
        String password = body.get("password");

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    @Transactional
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult
    ){

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        String username = userDetails.getUser().getUsername();
        String role = userDetails.getUser().getRole().getRoleName();
        Long userId = userDetails.getUser().getUserId();

        userDetails.getUser().updateLastLoginAt();
        userRepository.save(userDetails.getUser());

        String accessToken = jwtUtil.createToken(TokenType.ACCESS.name(), username, userId, role, Expiration.ACCESS.getTtl());
        String refreshToken = jwtUtil.createToken(TokenType.REFRESH.name(), username, userId, role, Expiration.REFRESH.getTtl());

        refreshTokenService.create(username,refreshToken,Expiration.REFRESH.getTtl());

        response.addHeader(HttpHeaders.AUTHORIZATION, BEARER_TOKEN_PREFIX + accessToken);
        response.addCookie(createCookie(TokenType.REFRESH.name(), refreshToken));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ObjectMapper mapper = new ObjectMapper();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, NOT_FOUND_USERNAME);
        problemDetail.setTitle(HttpStatus.UNAUTHORIZED.getReasonPhrase());

        response.getWriter().write(mapper.writeValueAsString(problemDetail));
    }
    private Cookie createCookie(String name, String refreshToken) {
        Cookie cookie = new Cookie(name, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(1));
        return cookie;
    }
}
