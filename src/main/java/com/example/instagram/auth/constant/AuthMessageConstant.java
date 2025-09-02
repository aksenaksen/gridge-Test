package com.example.instagram.auth.constant;

public class AuthMessageConstant {

    // Token
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    
    // Error Messages
    public static final String UNAUTHORIZED_ERROR = "Unauthorized";
    public static final String UNAUTHORIZED_MESSAGE = "인증이 필요합니다";
    public static final String EXPIRED_TOKEN_MESSAGE = "엑세스 토큰이 만료되었습니다";
    public static final String INVALID_TOKEN_MESSAGE = "유효하지 않은 토큰입니다";
    public static final String UNKNOWN_TOKEN_TYPE_MESSAGE = "알 수 없는 토큰 타입입니다";
    public static final String AUTHENTICATION_FAILED_MESSAGE = "아이디나 비밀번호가 잘못되었습니다";

    public static final String NOT_FOUND_USERNAME = "해당하는 유저를 찾을 수 없습니다";
    public static final String NOT_SUPPORTED_PROVIDER_MESSAGE = "지원하지 않는 OAuth 공급자입니다: ";
    public static final String OAUTH_PROVIDER_NAVER = "naver";

    public static final String NOT_ACTIVATE_USER = "유저가 비활성화된 상태이거나 정지된 상태입니다";

    public static final String INVALID_JWT_SIGNATURE = "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.";
    public static final String EXPIRED_JWT_TOKEN = "Expired JWT token, 만료된 JWT token 입니다.";
    public static final String UNSUPPORTED_JWT_TOKEN = "Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.";
    public static final String JWT_CLAIMS_EMPTY = "JWT claims is empty, 잘못된 JWT 토큰 입니다.";
    
    // Endpoints
    public static final String LOGOUT_URI = "logout";
}
