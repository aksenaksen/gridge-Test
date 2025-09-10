package com.example.instagram.report.exception;

import com.example.instagram.common.exception.GlobalException;
import com.example.instagram.report.constant.ReportErrorConstant;

public class InvalidReportTargetException extends GlobalException {
    public InvalidReportTargetException() {
        super(ReportErrorConstant.INVALID_REPORT_TARGET);
    }
}