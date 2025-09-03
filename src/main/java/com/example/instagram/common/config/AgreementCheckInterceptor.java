package com.example.instagram.common.config;

import com.example.instagram.auth.domain.CustomUserDetails;
import com.example.instagram.common.security.UserRole;
import com.example.instagram.user.application.UserAgreementChecker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class AgreementCheckInterceptor implements HandlerInterceptor {

    private final UserAgreementChecker checker;
    private final PlatformTransactionManager transactionManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(userDetails == null) {
            throw new AccessDeniedException("deined");
        }
        if(userDetails.getUser().getRole().equals(UserRole.ADMIN)){
            return true;
        }
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);

        txTemplate.execute(status -> {
            checker.checkRequiredAgreement(userDetails.getUser().getUserId());
            return null;
        });
        return true;
    }

}
