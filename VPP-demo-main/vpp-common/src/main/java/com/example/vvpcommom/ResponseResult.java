package com.example.vvpcommom;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
public class ResponseResult<T> implements Serializable {

    private int code = 0;

    private String msg;

    private T data;

    public ResponseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResponseResult success() {
        return new ResponseResult(200, "成功");
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "成功", data);
    }

    public static <T> ResponseResult<T> error(String msg) {
        return new ResponseResult<>(400, msg);
    }
    public static ResponseResult errorHoldOn(String msg) {return new ResponseResult(202, msg);}


    public static <T> ResponseResult<T> error(int code, String msg, T data) {
        return new ResponseResult(code, msg, data);
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
