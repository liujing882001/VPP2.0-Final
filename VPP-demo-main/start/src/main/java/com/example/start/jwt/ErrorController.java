package com.example.start.jwt;

import com.example.vvpcommom.*;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class ErrorController {

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = AuthException.class)
    public ResponseResult error(AuthException e) {
        return ResponseResult.error(401, e.getMessage(), null);
    }

    //用户token权限
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = TokenAuthException.class)
    public ResponseResult error(TokenAuthException e) {
        return ResponseResult.error(401, e.getMessage(), null);
    }

    //用户角色权限
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = PermsException.class)
    public ResponseResult error(PermsException e) {
        return ResponseResult.error(402, e.getMessage(), null);
    }

    //用户按钮权限
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = BtnAuthException.class)
    public ResponseResult error(BtnAuthException e) {

        return ResponseResult.error(403, e.getMessage(), null);
    }

    //用户按钮权限
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = RedirectPageException.class)
    public ResponseResult error(RedirectPageException e) {

        return ResponseResult.error(302, e.getMessage(), null);
    }

    //参数校验
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseResult error(MethodArgumentNotValidException  e) {
        List<ObjectError> list = e.getBindingResult().getAllErrors();
        if (!list.isEmpty()) {
            return ResponseResult.error(list.get(list.size() - 1).getDefaultMessage());
        } else {
            return ResponseResult.error("请重新选择相关选项");
        }

    }

}
