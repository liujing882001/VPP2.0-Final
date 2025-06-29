package com.example.start.jwt;

import com.example.vvpcommom.*;
import com.example.vvpdomain.UserRepository;
import com.example.vvpdomain.entity.Menu;
import com.example.vvpdomain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    private UserRepository userService;
    @Resource
    private RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             Object object) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            return true;
        }

        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }

        String uri = httpServletRequest.getRequestURI();
        Method method = ((HandlerMethod) object).getMethod();

        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查是否有PrivateMethod注释，有则验证IP是否和本机一致
        if (method.isAnnotationPresent(PrivateMethod.class)) {
            PrivateMethod privateMethod = method.getAnnotation(PrivateMethod.class);
            String ipAddress = IPUtils.getRealIP(httpServletRequest);
            if (privateMethod.required() && IPUtils.getLocalIp().equals(ipAddress)) {
                return true;
            }
        }


        String token = httpServletRequest.getHeader("authorization");// 从 http 请求头中取出 token
        String userId = httpServletRequest.getHeader("authorizationCode");
        String tokenKey = "TOKEN_EXPIRE_" + userId;

//        //按钮拦截
//        if (method.isAnnotationPresent(PreAuthorize.class)) {
//
//            PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
//            if (!StringUtils.isEmpty(preAuthorize.value())) {
//                // 执行认证
//                if (StringUtils.isEmpty(token)) {
//                    httpServletResponse.setStatus(HttpStatus.FOUND.value());
//                    throw new RedirectPageException("该用户token为空，请重新登录");
//                }
//                if (StringUtils.isEmpty(userId)) {
//                    httpServletResponse.setStatus(HttpStatus.FOUND.value());
//                    throw new RedirectPageException("该用户id为空，请重新登录");
//                }
//                if (!redisUtils.hasKey(tokenKey)) {
//                    throw new TokenAuthException("该用户token过期，请重新登录");
//                }
//                if (!token.equals(redisUtils.get(tokenKey).toString())) {
//                    throw new TokenAuthException("用户token不一致，请重新登陆！");
//                }
//                User user = userService.findById(userId).orElse(null);
//                if (null == user) {
//                    httpServletResponse.setStatus(HttpStatus.FOUND.value());
//                    throw new RedirectPageException("该用户不存在，请重新登录");
//                }
//                if (user.getRole() == null) {
//                    throw new PermsException("该用户角色权限不完整，请联系管理员授权！");
//                }
//                List<Menu> menuList = user.getRole().getMenuList();
//                if (menuList == null || menuList.size() == 0) {
//                    throw new PermsException("该用户角色菜单权限不完整，请联系管理员授权！");
//                }
//
//                String permission = preAuthorize.value();
//                List<Menu> menus = menuList.stream()
//                        .filter(m -> "F".equals(m.getMenuType()) && permission.equals(m.getPerms()))
//                        .collect(Collectors.toList());
//                if (menus == null || menus.size() == 0) {
//
//                    throw new BtnAuthException("该用户该操作被拦截，如有需要请联系管理员授权！");
//                }
//
//                //刷新token有效期
//                redisUtils.expire(tokenKey, 24, TimeUnit.HOURS);
//                return true;
//            }
//
//        }

//        //检查有没有需要用户权限的注解
//        if (method.isAnnotationPresent(UserLoginToken.class)) {
//            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
//            if (userLoginToken.required()) {
//                // 执行认证
//                if (StringUtils.isEmpty(token)) {
//                    httpServletResponse.setStatus(HttpStatus.FOUND.value());
//                    throw new RedirectPageException("该用户token为空，请重新登录");
//                }
//                if (StringUtils.isEmpty(userId)) {
//                    httpServletResponse.setStatus(HttpStatus.FOUND.value());
//                    throw new RedirectPageException("该用户id为空，请重新登录");
//                }
//                if (!redisUtils.hasKey(tokenKey)) {
//                    throw new TokenAuthException("该用户token过期，请重新登录");
//                }
//                if (!token.equals(redisUtils.get(tokenKey).toString())) {
//                    throw new TokenAuthException("用户token不一致，请重新登陆！");
//                }
//                User user = userService.findById(userId).orElse(null);
//                if (null == user) {
//                    httpServletResponse.setStatus(HttpStatus.FOUND.value());
//                    throw new RedirectPageException("该用户不存在，请重新登录");
//                }
//                //刷新token有效期
//                redisUtils.expire(tokenKey, 24, TimeUnit.HOURS);
//                return true;
//            }
//        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }
}