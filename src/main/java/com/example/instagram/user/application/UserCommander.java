package com.example.instagram.user.application;

import com.example.instagram.user.application.dto.in.UserRegisterCommand;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.infrastructor.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCommander {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(UserRegisterCommand command){
        User user = User.createUser(command.username(), command.name(), command.password() , passwordEncoder);
        userRepository.save(user);
    }
}
