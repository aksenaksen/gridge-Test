package com.example.instagram.common.exception;

import com.example.instagram.auth.exception.JwtValidationException;
import com.example.instagram.user.domain.AgreementType;
import com.example.instagram.user.exception.NotAgreedRequireAgreement;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = stringify(exception);
        log.warn("",exception);
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException exception) {
        String message = stringify(exception);
        log.warn("",exception);
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(NotAgreedRequireAgreement.class)
    public ProblemDetail handleNotAgreedRequireAgreement(NotAgreedRequireAgreement exception) {
        String notAgreed = exception.getNotAgreedTypes().stream()
                .map(AgreementType::getDescription)
                .collect(Collectors.joining(", "));

        String message = exception.getMessage()+ " : " + notAgreed;

        log.warn("NotAgreedRequireAgreement : [{}] {}",  exception.getMessage(),exception);
        return build(exception.getHttpStatus(), message);
    }

    @ExceptionHandler(JwtValidationException.class)
    public ProblemDetail handleJwtValidationException(JwtValidationException exception) {
        log.debug("JWT validation failed: {}", exception.getMessage());
        return build(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException exception) {
        log.debug("Authentication failed: {}", exception.getMessage());
        return build(HttpStatus.UNAUTHORIZED, "인증이 필요합니다");
    }


    @ExceptionHandler(GlobalException.class)
    public ProblemDetail handleGlobalException(GlobalException exception) {
        log.warn("",exception);
        return build(exception.getHttpStatus(), exception.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNotFoundException(NoResourceFoundException exception) {
        log.warn("",exception);
        return build(ServerErrorConstant.NOT_FOUND.getHttpStatus(), ServerErrorConstant.NOT_FOUND.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.warn("",exception);
        return build(ServerErrorConstant.BAD_REQUEST.getHttpStatus(), ServerErrorConstant.BAD_REQUEST.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception exception) {
        log.warn("Exception type: {}, message: {}", exception.getClass().getName(), exception.getMessage(), exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private String stringify(MethodArgumentNotValidException exception) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errorMessageBuilder.append(fieldError.getField()).append(": ");
            errorMessageBuilder.append(fieldError.getDefaultMessage()).append(", ");
        }
        errorMessageBuilder.deleteCharAt(errorMessageBuilder.length() - 2);
        return errorMessageBuilder.toString();
    }

    private String stringify(StackTraceElement stackTraceElement) {
        String className = stackTraceElement.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);

        return String.format("[%s.%s(%s:%d)]",
                className,
                stackTraceElement.getMethodName(),
                stackTraceElement.getFileName(),
                stackTraceElement.getLineNumber()
        );
    }


    private String stringify(ConstraintViolationException exception) {
        StringBuilder errorMessageBuilder = new StringBuilder();

        for (ConstraintViolation<?> fieldError : exception.getConstraintViolations()) {
            String propertyPath = fieldError.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            errorMessageBuilder.append(fieldName).append(": ");
            errorMessageBuilder.append(fieldError.getMessage()).append(", ");
        }

        errorMessageBuilder.deleteCharAt(errorMessageBuilder.length() - 2);
        return errorMessageBuilder.toString();
    }

    private ProblemDetail build(HttpStatus status, String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle(status.getReasonPhrase());
        return problemDetail;
    }
}
