package com.example.vvpweb.chinasouthernpower.model;

import java.io.Serializable;


public class SouthernPowerResult<T> implements Serializable {

    private int code = 0;

    private String message;

    private T data;

    public SouthernPowerResult(RetCode retCode) {
        this.code = retCode.getCode();
        this.message = retCode.getMsg();
    }

    public SouthernPowerResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public SouthernPowerResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static SouthernPowerResult success() {
        return new SouthernPowerResult(200, "成功");
    }

    public static <T> SouthernPowerResult<T> success(T data) {
        return new SouthernPowerResult(200, "成功", data);
    }

    public static SouthernPowerResult error(String msg) {
        return new SouthernPowerResult(400, msg);
    }

    public static <T> SouthernPowerResult<T> error(int code, String msg, T data) {
        return new SouthernPowerResult(code, msg, data);
    }

    public static <T> SouthernPowerResult<T> error(RetCode code, T data) {
        return new SouthernPowerResult(code.getCode(), code.getMsg(), data);
    }

    public static <T> SouthernPowerResult<T> error(RetCode code) {
        return new SouthernPowerResult(code.getCode(), code.getMsg(), null);
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
