package com.example.instagram.user.application;

import com.example.instagram.user.application.dto.UserDto;
import com.example.instagram.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserFinder userFinder;

    @Transactional(readOnly = true)
    public UserDto findByUsername(String username){
        User user = userFinder.findByUsername(username);
        return UserDto.from(user);
    }

    @Transactional(readOnly = true)
    public UserDto findByName(String name){
        User user = userFinder.findByName(name);
        return UserDto.from(user);
    }

    @Transactional(readOnly = true)
    public UserDto findByRegisterDateTime(LocalDateTime start, LocalDateTime end){
        User user = userFinder.findByRegisterDateTime(start, end);
        return UserDto.from(user);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long userId){
        User user = userFinder.findById(userId);
        return UserDto.from(user);
    }

}
