package com.example.instagram.user.constant;

public class UserMessageConstant {

    public static final String PHONE_NUMBER_PATTERN = "^010-\\d{4}-\\d{4}";
    public static final String USERNAME_PATTERN = "^[a-z._]{1,20}$";

    public static final String USERNAME_REQUIRED = "아이디를 입력해주세요";
    public static final String USERNAME_SIZE_INVALID = "아이디는 1자 이상 20자 이하여야 합니다";
    public static final String PASSWORD_REQUIRED = "비밀번호를 입력해주세요";
    public static final String PASSWORD_SIZE_INVALID = "비밀번호는 6자 이상 20자 이하여야 합니다";
    public static final String NAME_REQUIRED = "이름을 입력해주세요";
    public static final String PHONE_NUMBER_REQUIRED = "휴대폰 번호가 정확하지 않습니다 국가 번호를 포함하여 전체 전화번호를 입력해주세요";
    public static final String BIRTHDAY_REQUIRED = "생년월일을 입력해주세요";
    public static final String AGREEMENTS_REQUIRED = "약관 동의를 선택해주세요";

    public static final String NOT_MATCHED_PHONE_NUMBER = "휴대폰 번호가 정확하지 않습니다 국가 번호를 포함하여 전체 전화번호를 입력해주세요";
    public static final String NOT_MATCHED_AGREEMENT_TYPE = "해당하는 동의 항목이 없습니다";
    public static final String NOT_MATCHED_USERNAME = "사용자 이름에는 문자,숫자 ,밑줄 및 마침표만 사용할 수 있습니다";
    public static final String NOT_ALLOWED_PASSWORD_UPDATE_OBJECT = "사용자 이름 또는 전화번호 중 하나는 입력해야합니다";

    public static final String ALREADY_EXIST_USER = "해당하는 유저가 이미 존재합니다";

}