package com.example.instagram.report.constant;

public class ReportMessageConstant {
    public static final String REPORT_SUCCESS = "신고가 성공적으로 접수되었습니다.";
    public static final String REPORT_BLOCK_COMPLETED = "신고 처리가 완료되었습니다.";
    public static final String REPORT_REJECTED = "신고가 기각되었습니다.";
    public static final String EMPTY_REASON = "신고 사유를 입력해주세요.";
    public static final String INVALID_REASON_LENGTH = "신고 사유는 10자 이상 500자 이하로 입력해주세요.";
    
    // 도메인 validation 메시지
    public static final String INVALID_REPORT_TARGET_NULL = "신고 대상(피드 또는 댓글) 중 하나는 반드시 지정되어야 합니다.";
    public static final String CANNOT_REPORT_BOTH_TARGET = "피드와 댓글을 동시에 신고할 수 없습니다.";
    
    // 에러 메시지
    public static final String REPORT_NOT_FOUND = "신고를 찾을 수 없습니다.";
    public static final String INVALID_REPORT_TARGET = "유효하지 않은 신고 대상입니다.";
    public static final String ALREADY_REPORTED = "이미 신고한 대상입니다.";
    public static final String CANNOT_REPORT_OWN_CONTENT = "본인의 콘텐츠는 신고할 수 없습니다.";
    public static final String INVALID_REPORT_STATUS = "유효하지 않은 신고 상태입니다.";
}