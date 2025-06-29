package com.example.start.Interceptor;

import com.example.vvpcommom.RequestHeaderContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 声明拦截器
 */
public class RequestHeaderContextInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        initHeaderContext(request);
        return super.preHandle(request, response, handler);
    }

    private void initHeaderContext(HttpServletRequest request) {

        String userId = request.getHeader("authorizationCode");

        new RequestHeaderContext.RequestHeaderContextBuild()
                .userId(userId)
                .bulid();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        RequestHeaderContext.clean();
        super.postHandle(request, response, handler, modelAndView);
    }
}

