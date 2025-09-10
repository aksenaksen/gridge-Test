package com.example.instagram.auth.filter;


import com.example.instagram.auth.constant.AuthMessageConstant;
import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.auth.domain.TokenType;
import com.example.instagram.auth.exception.JwtValidationException;
import com.example.instagram.common.util.JwtUtil;
import com.example.instagram.user.domain.User;
import com.example.instagram.common.security.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(AuthMessageConstant.BEARER_TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(AuthMessageConstant.BEARER_TOKEN_PREFIX.length()).trim();

        try {
            if (valid(response, token) || isNotAccessToken(response, token)) return;
        }
        catch (JwtValidationException e){
            request.setAttribute("Exception", e);
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        Long userId = jwtUtil.getUserId(token);

        User user = User.builder()
                .userId(userId)
                .role(UserRole.from(role))
                .username(username)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());


        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);

    }

    private boolean valid(HttpServletResponse response, String token) throws IOException {
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().print(AuthMessageConstant.EXPIRED_TOKEN_MESSAGE);
            return true;
        }
        return false;
    }

    private boolean isNotAccessToken(HttpServletResponse response, String token) throws IOException {
        try {
            TokenType type = TokenType.valueOf(jwtUtil.getTokenType(token));
            if (type != TokenType.ACCESS) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().print(AuthMessageConstant.INVALID_TOKEN_MESSAGE);
                return true;
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().print(AuthMessageConstant.UNKNOWN_TOKEN_TYPE_MESSAGE);
            return true;
        }
        return false;
    }
}
