package com.example.vvpweb.chinasouthernpower.model;

public enum RetCode {
    RET_200(200, "成 功"),
    RET_401(401, "token 验证失败"),
    RET_500(500, "服务器异常"),
    RET_5000(5000, "输入参数不完整");

    private int code;
    private String msg;

    RetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
