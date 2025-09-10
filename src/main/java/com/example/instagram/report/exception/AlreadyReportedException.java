package com.example.instagram.report.exception;

import com.example.instagram.common.exception.GlobalException;
import com.example.instagram.report.constant.ReportErrorConstant;

public class AlreadyReportedException extends GlobalException {
    public AlreadyReportedException() {
        super(ReportErrorConstant.ALREADY_REPORTED);
    }
}