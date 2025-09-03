package com.example.instagram.common.util;

import com.example.instagram.auth.constant.AuthErrorConstant;
import com.example.instagram.auth.exception.JwtValidationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;

    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String CLAIM_EMAIL = "username";
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";

    public JwtUtil(@Value("${jwt.secret-key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String createToken(String tokenType, String username, Long userId, String role,Long ttl) {
        return Jwts.builder()
                .setClaims(createClaims(tokenType, username, userId, role))
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + ttl))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims createClaims(String tokenType, String username, Long userId, String role) {
        Map<String, Object> claimMap = Map.of(
                CLAIM_TOKEN_TYPE, tokenType,
                CLAIM_EMAIL, username,
                CLAIM_USER_ID, userId,
                CLAIM_ROLE, role
        );
        Claims claims = Jwts.claims();
        claims.putAll(claimMap);
        return claims;
    }

    public String getTokenType(String token) {
        return getClaim(token, CLAIM_TOKEN_TYPE);
    }

    public Long getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(CLAIM_USER_ID, Long.class);
    }

    public String getRole(String token) {
        return getClaim(token, CLAIM_ROLE);
    }

    public String getUsername(String token) {
        return getClaim(token, CLAIM_EMAIL);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtValidationException(AuthErrorConstant.INVALID_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new JwtValidationException(AuthErrorConstant.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new JwtValidationException(AuthErrorConstant.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new JwtValidationException(AuthErrorConstant.EMPTY_CLAIMS);
        }
    }

    private String getClaim(String token, String claimKey) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(claimKey, String.class);
    }
}
