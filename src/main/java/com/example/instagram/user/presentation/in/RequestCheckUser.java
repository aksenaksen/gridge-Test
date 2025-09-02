package com.example.instagram.user.presentation.in;

import com.example.instagram.user.constant.UserMessageConstant;
import com.example.instagram.user.valid.PhoneNumber;
import com.example.instagram.user.valid.Username;
import jakarta.validation.constraints.Size;

public record RequestCheckUser(
        @PhoneNumber
        String phoneNumber,
        @Username
        String username,
        @Size(min = 6, max = 20, message = UserMessageConstant.PASSWORD_SIZE_INVALID)
        String password
) {
}
