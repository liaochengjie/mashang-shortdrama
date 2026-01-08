package com.lfy.kcat.user.exception;


import lombok.Data;


public enum UserServiceExceptionEnume {
    VERIFY_CODE_ERROR(10001,"验证码错误"),
    USER_NOT_EXIST(10002, "用户不存在"),
    USER_NOT_LOGIN(10003, "用户未登录");
    private String message;
    private int code;
    UserServiceExceptionEnume(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
