package com.example.instagram.user.presentation.in;

import com.example.instagram.user.application.dto.in.UserUpdatePasswordCommand;
import com.example.instagram.user.constant.UserMessageConstant;
import jakarta.validation.constraints.Size;

import static com.example.instagram.user.constant.UserMessageConstant.NOT_ALLOWED_PASSWORD_UPDATE_OBJECT;

public record RequestUpdatePasswordUser(
        String username,
        String phoneNumber,
        @Size(min = 6, max = 20, message = UserMessageConstant.PASSWORD_SIZE_INVALID)
        String password
) {

    public RequestUpdatePasswordUser {
        if (username == null && phoneNumber == null) {
            throw new IllegalArgumentException(NOT_ALLOWED_PASSWORD_UPDATE_OBJECT);
        }
    }

    public UserUpdatePasswordCommand toCommand(){
        return new UserUpdatePasswordCommand(
                this.username,
                this.phoneNumber,
                this.password
        );
    }
}
