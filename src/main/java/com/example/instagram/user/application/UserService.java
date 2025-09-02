package com.example.instagram.user.application;

import com.example.instagram.user.application.dto.in.UserDeleteCommand;
import com.example.instagram.user.application.dto.in.UserRegisterCommand;
import com.example.instagram.user.application.dto.in.UserSuspendCommand;
import com.example.instagram.user.application.dto.in.UserUpdatePasswordCommand;
import com.example.instagram.user.application.dto.out.UserDto;
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
    public UserDto findByUsername(String username){
        User user = userFinder.findByUsername(username);
        return UserDto.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll(RequestFindAllUserCondition condition){

        return userFinder.findByCondition(condition).stream()
                .map(UserDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public void existsByUsername(String username){
        userFinder.existsByUserName(username);
    }

    @Transactional(readOnly = true)
    public UserDto findByName(String name){
        User user = userFinder.findByName(name);
        return UserDto.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findByRegisterDate(LocalDate date){
        LocalDateTime start =  date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);

        List<User> user = userFinder.findByRegisterDateTime(start, end);
        return user.stream()
                .map(UserDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long userId){
        User user = userFinder.findById(userId);
        return UserDto.from(user);
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
