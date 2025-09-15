package com.example.instagram.auth.presentation;

public record LoginRequest(
        String username,
        String password
) {
}
