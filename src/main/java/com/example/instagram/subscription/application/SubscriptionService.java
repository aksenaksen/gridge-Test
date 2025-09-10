package com.example.instagram.subscription.application;

import com.example.instagram.common.Page;
import com.example.instagram.subscription.application.dto.in.SubscriptionBlockCommand;
import com.example.instagram.subscription.application.dto.in.SubscriptionFollowCommand;
import com.example.instagram.subscription.application.dto.in.SubscriptionSearchCondition;
import com.example.instagram.subscription.application.dto.in.SubscriptionUnFollowCommand;
import com.example.instagram.subscription.application.dto.out.ResponseSubscriptionDto;
import com.example.instagram.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionFinder subscriptionFinder;
    private final SubscriptionCommander subscriptionCommander;

    private final UserService userService;

    @Transactional(readOnly = true)
    public List<ResponseSubscriptionDto> findAllWithCondition(SubscriptionSearchCondition condition, Page page) {

        if(condition.name() != null){
            Long userId = userService.findByName(condition.name()).userId();
            condition = new SubscriptionSearchCondition(userId, condition.name(), condition.status(), condition.date());
        }

        return subscriptionFinder.findAllWithCondition(
                        condition,
                        page == null ? null : page.lastId(),
                        page == null ? null : page.pageSize())
                .stream()
                .map(ResponseSubscriptionDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResponseSubscriptionDto> findFollowingList(Long userId){
        return subscriptionFinder.findByUserId(userId).stream()
                .map(ResponseSubscriptionDto::from)
                .toList();
    }

    @Transactional
    public void follow(SubscriptionFollowCommand command) {
        userValidation(command.followerId(), command.followeeId());

        subscriptionCommander.follow(command);
    }

    @Transactional
    public void unfollow(SubscriptionUnFollowCommand command) {
        userValidation(command.followerId(), command.followeeId());

        subscriptionCommander.unfollow(command);
    }

    @Transactional
    public void block(SubscriptionBlockCommand command) {
        userValidation(command.followerId(), command.userId());

        subscriptionCommander.block(command);
    }

    private void userValidation(Long followerId, Long followeeId) {
        userService.findById(followerId);
        userService.findById(followeeId);
    }
}
