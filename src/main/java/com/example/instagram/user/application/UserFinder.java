package com.example.instagram.user.application;

import com.example.instagram.user.domain.User;
import com.example.instagram.user.exception.UserAlreadyExistException;
import com.example.instagram.user.exception.UserNotFoundException;
import com.example.instagram.user.infrastructor.UserRepository;
import com.example.instagram.user.presentation.in.RequestFindAllUserCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
        return userRepository.findByProfile_NameOrderByCreatedAt(name)
                .orElseThrow(UserNotFoundException::new);
    }

    public List<User> findByRegisterDateTime(LocalDateTime start, LocalDateTime end){
        return userRepository.findByCreatedAtBetweenOrderByCreatedAt(start, end);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public void existsByUserName(String username) {
        if(userRepository.existsByUsername(username)){
            throw new UserAlreadyExistException();
        }
    }
    
    public List<User> findByCondition(RequestFindAllUserCondition condition) {
        return userRepository.findByCondition(condition);
    }

}
