package com.example.start;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @description: 基于SpringBoot2.0的ErrorPageConfig页面拦截跳转
 **/
@Component
public class ErrorPageRegistrarImpl implements ErrorPageRegistrar {
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        /*错误类型为404，找不到地址，默认跳转请求/error/404 */
        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
        registry.addErrorPages(errorPage404);
    }
}
