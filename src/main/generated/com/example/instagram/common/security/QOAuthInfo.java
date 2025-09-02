package com.example.instagram.common.security;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOAuthInfo is a Querydsl query type for OAuthInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QOAuthInfo extends BeanPath<OAuthInfo> {

    private static final long serialVersionUID = -844977939L;

    public static final QOAuthInfo oAuthInfo = new QOAuthInfo("oAuthInfo");

    public final StringPath oauthId = createString("oauthId");

    public final EnumPath<OAuthProvider> provider = createEnum("provider", OAuthProvider.class);

    public QOAuthInfo(String variable) {
        super(OAuthInfo.class, forVariable(variable));
    }

    public QOAuthInfo(Path<? extends OAuthInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOAuthInfo(PathMetadata metadata) {
        super(OAuthInfo.class, metadata);
    }

}

