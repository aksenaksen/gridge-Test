package com.example.instagram.user.application;

import com.example.instagram.user.application.dto.in.UserDeleteCommand;
import com.example.instagram.user.application.dto.in.UserRegisterCommand;
import com.example.instagram.user.application.dto.in.UserSuspendCommand;
import com.example.instagram.user.application.dto.in.UserUpdatePasswordCommand;
import com.example.instagram.user.application.dto.out.ResponseUserDto;
import com.example.instagram.user.presentation.in.RequestFindAllUserCondition;

import java.time.LocalDate;
import java.util.List;

public interface IUserService {
    
    ResponseUserDto findByUsername(String username);
    
    ResponseUserDto findByName(String name);
    
    List<ResponseUserDto> findByRegisterDate(LocalDate date);
    
    ResponseUserDto findById(Long userId);
    
    void register(UserRegisterCommand command);

    void existsByUsername(String username);

    List<ResponseUserDto> findAll(RequestFindAllUserCondition condition);

    void changePassword(UserUpdatePasswordCommand command);

    void suspend(UserSuspendCommand userId);

    void delete(UserDeleteCommand userId);
}
