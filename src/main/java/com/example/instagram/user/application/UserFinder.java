package com.example.instagram.user.application;

import com.example.instagram.user.domain.User;
import com.example.instagram.user.exception.UserNotFoundException;
import com.example.instagram.user.infrastructor.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserFinder {

    private final UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    public User findByName(String name){
        return userRepository.findByName(name)
                .orElseThrow(UserNotFoundException::new);
    }


    public List<User> findByRegisterDateTime(LocalDateTime start, LocalDateTime end){
        return userRepository.findByCreatedAtBetween(start, end);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

}
