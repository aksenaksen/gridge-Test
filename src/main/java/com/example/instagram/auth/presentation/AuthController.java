package com.example.instagram.auth.presentation;

import com.example.instagram.auth.application.AuthService;
import com.example.instagram.auth.constant.AuthMessageConstant;
import com.example.instagram.auth.domain.TokenType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    @Operation(
        summary = "토큰 갱신",
        description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "토큰 갱신 성공"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 Refresh Token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "만료된 Refresh Token"
        )
    })
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

    @PostMapping("/login")
    @Operation(
        summary = "로그인",
        description = "사용자 로그인을 수행합니다. 실제 인증은 Spring Security CustomLoginFilter에서 처리됩니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "로그인 실패 - 잘못된 사용자명 또는 비밀번호"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "계정이 비활성화되었거나 잠겨있음"
        )
    })
    public void login(
        @RequestBody LoginRequest request
    ) {}


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