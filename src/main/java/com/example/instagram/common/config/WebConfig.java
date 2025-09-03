package com.example.instagram.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    private final List<String> excludePath =  List.of(
            "/v1/members",
            "/h2-console/**",
            "/v1/users",
            "/v1/auth/refresh",
            "/login/oauth2/**",
            "/oauth2/**",
            "/error"
    );
    private final AgreementCheckInterceptor agreementCheckInterceptor;

    public WebConfig(AgreementCheckInterceptor agreementCheckInterceptor) {
        this.agreementCheckInterceptor = agreementCheckInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(agreementCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
    }
}

