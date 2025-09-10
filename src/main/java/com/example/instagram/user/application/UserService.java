package com.example.instagram.user.application;

import com.example.instagram.user.application.dto.in.UserDeleteCommand;
import com.example.instagram.user.application.dto.in.UserRegisterCommand;
import com.example.instagram.user.application.dto.in.UserSuspendCommand;
import com.example.instagram.user.application.dto.in.UserUpdatePasswordCommand;
import com.example.instagram.user.application.dto.out.ResponseUserDto;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.presentation.in.RequestFindAllUserCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserFinder userFinder;
    private final UserCommander userCommander;

    @Transactional(readOnly = true)
    public ResponseUserDto findByUsername(String username){
        User user = userFinder.findByUsername(username);
        return ResponseUserDto.from(user);
    }

    @Transactional(readOnly = true)
    public List<ResponseUserDto> findAll(RequestFindAllUserCondition condition){

        return userFinder.findByCondition(condition).stream()
                .map(ResponseUserDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public void existsByUsername(String username){
        userFinder.existsByUserName(username);
    }

    @Transactional(readOnly = true)
    public ResponseUserDto findByName(String name){
        User user = userFinder.findByName(name);
        return ResponseUserDto.from(user);
    }

    @Transactional(readOnly = true)
    public List<ResponseUserDto> findByRegisterDate(LocalDate date){
        LocalDateTime start =  date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);

        List<User> user = userFinder.findByRegisterDateTime(start, end);
        return user.stream()
                .map(ResponseUserDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResponseUserDto findById(Long userId){
        User user = userFinder.findById(userId);
        return ResponseUserDto.from(user);
    }

    @Transactional
    public void register(UserRegisterCommand command){
        userCommander.register(command);
    }

    @Transactional
    public void changePassword(UserUpdatePasswordCommand command){
        userCommander.updatePassword(command);
    }

    @Override
    @Transactional
    public void suspend(UserSuspendCommand command) {
        userCommander.delete(command);
    }

    @Override
    @Transactional
    public void delete(UserDeleteCommand command) {
        userCommander.delete(command);
    }

}
