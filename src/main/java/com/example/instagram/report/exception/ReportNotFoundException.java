package com.example.instagram.report.exception;

import com.example.instagram.common.exception.GlobalException;
import com.example.instagram.report.constant.ReportErrorConstant;

public class ReportNotFoundException extends GlobalException {
    public ReportNotFoundException() {
        super(ReportErrorConstant.REPORT_NOT_FOUND);
    }
}