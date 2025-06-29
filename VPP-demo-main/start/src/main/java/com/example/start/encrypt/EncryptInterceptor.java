package com.example.start.encrypt;

import com.example.vvpcommom.StringUtils;
import com.example.vvpcommom.thread.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.example.vvpdomain.constants.UserConstant.CUR_REQUEST_USER_PUBLIC_KEY;

/**
 * 声明拦截器
 */
@Component
@Slf4j
public class EncryptInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String baseToken = request.getHeader("Authorization");
        log.info("token==={}", baseToken);
        if (StringUtils.isEmpty(baseToken)) {
            // TODO 过度时期，兼容两种,后续将下面代码放开
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false;
        } else {
            if (StringUtils.isEmpty(ThreadLocalUtil.get(CUR_REQUEST_USER_PUBLIC_KEY))) {
                log.info("用户未登录");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }

        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
        super.postHandle(request, response, handler, modelAndView);
    }

}

