package com.example.instagram.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1527940481L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final com.example.instagram.common.QBaseEntity _super = new com.example.instagram.common.QBaseEntity(this);

    public final ListPath<UserAgreement, QUserAgreement> agreements = this.<UserAgreement, QUserAgreement>createList("agreements", UserAgreement.class, QUserAgreement.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> lastLoginAt = createDateTime("lastLoginAt", java.time.LocalDateTime.class);

    public final com.example.instagram.common.security.QOAuthInfo oAuthInfo;

    public final StringPath password = createString("password");

    public final QUserProfile profile;

    public final EnumPath<com.example.instagram.common.security.UserRole> role = createEnum("role", com.example.instagram.common.security.UserRole.class);

    public final EnumPath<UserStatus> status = createEnum("status", UserStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath username = createString("username");

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.oAuthInfo = inits.isInitialized("oAuthInfo") ? new com.example.instagram.common.security.QOAuthInfo(forProperty("oAuthInfo")) : null;
        this.profile = inits.isInitialized("profile") ? new QUserProfile(forProperty("profile")) : null;
    }

}

