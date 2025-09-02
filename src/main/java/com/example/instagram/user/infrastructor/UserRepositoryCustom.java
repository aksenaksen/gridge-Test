package com.example.instagram.user.infrastructor;

import com.example.instagram.user.domain.User;
import com.example.instagram.user.presentation.in.RequestFindAllUserCondition;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> findByCondition(RequestFindAllUserCondition condition);
}