package com.example.instagram.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    private final List<String> excludePath =  List.of(
            "/h2-console/**",
            "/api/users",
            "/v1/auth/refresh",
            "/login/oauth2/**",
            "/oauth2/**",
            "/error",
            "/swagger-ui/**"
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

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // allowCredentials와 함께 사용할 때는 allowedOriginPatterns 사용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

