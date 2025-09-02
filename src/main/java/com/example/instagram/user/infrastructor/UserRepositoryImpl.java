package com.example.instagram.user.infrastructor;

import com.example.instagram.user.domain.QUser;
import com.example.instagram.user.domain.User;
import com.example.instagram.user.presentation.in.RequestFindAllUserCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor  
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<User> findByCondition(RequestFindAllUserCondition condition) {
        QUser user = QUser.user;

        return jpaQueryFactory.selectFrom(user)
                .where(condition.name() != null ? user.profile.name.eq(condition.name()) : null,
                        condition.status() != null ? user.status.eq(condition.status()) : null,
                        condition.username() != null ? user.username.eq(condition.username()) : null,
                        condition.date() != null ? user.createdAt.between(
                                condition.date().atStartOfDay(),
                                condition.date().atStartOfDay().plusDays(1).minusSeconds(1)
                        ) : null)
                .orderBy(user.createdAt.desc())
                .fetch();
    }
}