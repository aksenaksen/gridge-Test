package com.example.instagram.common.config;


import com.example.instagram.auth.application.CustomUserDetailsService;
import com.example.instagram.auth.application.IRefreshTokenRepository;
import com.example.instagram.auth.application.OAuth2SuccessHandler;
import com.example.instagram.auth.application.OAuth2UserService;
import com.example.instagram.auth.filter.JwtRequestFilter;
import com.example.instagram.auth.filter.CustomLoginFilter;
import com.example.instagram.auth.filter.CustomLogoutFilter;
import com.example.instagram.common.util.JwtUtil;
import com.example.instagram.auth.application.JwtAuthenticationEntryPoint;
import com.example.instagram.user.infrastructor.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomLogoutFilter logoutFilter;
    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final OAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final IRefreshTokenRepository refreshTokenService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserRepository userRepository;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable())
                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/v1/auth/refresh").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/login/oauth2/**", "/oauth2/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling( ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint));

        http.oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint((userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService)))
                        .successHandler(oAuth2SuccessHandler))
                .userDetailsService(customUserDetailsService);


        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        CustomLoginFilter customLoginFilter = new CustomLoginFilter(authenticationManager(), refreshTokenService, jwtUtil, userRepository);
        customLoginFilter.setFilterProcessesUrl("/api/auth/login");
        
        http
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(logoutFilter, JwtRequestFilter.class)
                .addFilterAfter(customLoginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
