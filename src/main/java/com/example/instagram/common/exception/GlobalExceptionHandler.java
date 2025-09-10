package com.example.instagram.common.exception;

import com.example.instagram.user.domain.AgreementType;
import com.example.instagram.user.exception.NotAgreedRequireAgreement;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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

        String location = stringify(exception.getStackTrace()[0]);
        String message = stringify(exception);
        log.warn("Location : {} MethodArgumentNotValidException : [{}]",location, message);
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException exception) {
        String location = stringify(exception.getStackTrace()[0]);
        String message = stringify(exception);
        log.warn("Location : {} ConstraintViolationException : [{}]",location, message);
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(NotAgreedRequireAgreement.class)
    public ProblemDetail handleNotAgreedRequireAgreement(NotAgreedRequireAgreement exception) {
        String location = stringify(exception.getStackTrace()[0]);
        String notAgreed = exception.getNotAgreedTypes().stream()
                .map(AgreementType::getDescription)
                .collect(Collectors.joining(", "));

        String message = exception.getMessage()+ " : " + notAgreed;

        log.warn("Location : {} NotAgreedRequireAgreement : [{}]", location, exception.getMessage());
        return build(exception.getHttpStatus(), message);
    }

    @ExceptionHandler(GlobalException.class)
    public ProblemDetail handleUserNotFoundException(GlobalException exception) {
        String location = stringify(exception.getStackTrace()[0]);
        log.warn("Location : {} UserAlreadyExistException : [{}]",location, exception.getMessage());
        return build(exception.getHttpStatus(), exception.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNotFoundException(NoResourceFoundException exception) {
        String location = stringify(exception.getStackTrace()[0]);
        log.warn("Location : {} UserAlreadyExistException : [{}] , {}",location, exception.getMessage(), exception);
        return build(ServerErrorConstant.NOT_FOUND.getHttpStatus(), ServerErrorConstant.NOT_FOUND.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String location = stringify(exception.getStackTrace()[0]);
        log.warn("Location : {} UserAlreadyExistException : [{}]",location, exception.getMessage());
        return build(ServerErrorConstant.BAD_REQUEST.getHttpStatus(), ServerErrorConstant.BAD_REQUEST.getMessage());
    }
//    @ExceptionHandler(Exception.class)
//    public ProblemDetail handleException(Exception exception) {
//        String location = stringify(exception.getStackTrace()[0]);
//        log.warn("Location : {} Exception : [{}]",location, exception.getMessage());
//        return build(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
//    }

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
