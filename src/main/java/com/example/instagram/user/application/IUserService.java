package com.example.instagram.user.application;

import com.example.instagram.user.application.dto.in.UserDeleteCommand;
import com.example.instagram.user.application.dto.in.UserRegisterCommand;
import com.example.instagram.user.application.dto.in.UserSuspendCommand;
import com.example.instagram.user.application.dto.in.UserUpdatePasswordCommand;
import com.example.instagram.user.application.dto.out.UserDto;
import com.example.instagram.user.presentation.in.RequestFindAllUserCondition;

import java.time.LocalDate;
import java.util.List;

public interface IUserService {
    
    UserDto findByUsername(String username);
    
    UserDto findByName(String name);
    
    List<UserDto> findByRegisterDate(LocalDate date);
    
    UserDto findById(Long userId);
    
    void register(UserRegisterCommand command);

    void existsByUsername(String username);

    List<UserDto> findAll(RequestFindAllUserCondition condition);

    void changePassword(UserUpdatePasswordCommand command);

    void suspend(UserSuspendCommand userId);

    void delete(UserDeleteCommand userId);
}
