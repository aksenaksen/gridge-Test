package com.example.instagram.user.constant;

public enum UserMessage {

    CANNOT_ACTIVATE("해당 유저를 활성화 상태로 만들 수 없습니다."),
    CANNOT_DEACTIVATE("해당 유저를 비활성화 상태로 만들 수 없습니다."),

    USER_NOTFOUND("해당하는 유저를 찾을 수 없습니다");


    private final String message;

    UserMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
