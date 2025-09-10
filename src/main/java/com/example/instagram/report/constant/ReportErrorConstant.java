package com.example.instagram.report.constant;

import com.example.instagram.common.exception.GlobalErrorConstant;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReportErrorConstant implements GlobalErrorConstant {

    REPORT_NOT_FOUND(ReportMessageConstant.REPORT_NOT_FOUND, HttpStatus.NOT_FOUND),
    INVALID_REPORT_TARGET(ReportMessageConstant.INVALID_REPORT_TARGET, HttpStatus.BAD_REQUEST),
    ALREADY_REPORTED(ReportMessageConstant.ALREADY_REPORTED, HttpStatus.CONFLICT),
    CANNOT_REPORT_OWN_CONTENT(ReportMessageConstant.CANNOT_REPORT_OWN_CONTENT, HttpStatus.BAD_REQUEST),
    INVALID_REPORT_STATUS(ReportMessageConstant.INVALID_REPORT_STATUS, HttpStatus.BAD_REQUEST),
    
    INVALID_REPORT_TARGET_NULL(ReportMessageConstant.INVALID_REPORT_TARGET_NULL, HttpStatus.BAD_REQUEST),
    CANNOT_REPORT_BOTH_TARGET(ReportMessageConstant.CANNOT_REPORT_BOTH_TARGET, HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    ReportErrorConstant(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}