package com.example.start.encrypt;

import com.alibaba.fastjson.JSON;
import com.example.vvpcommom.RSAUtil;
import com.example.vvpcommom.thread.ThreadLocalUtil;
import com.example.vvpweb.demand.model.DrsResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import com.example.vvpcommom.Encrypted;

import static com.example.vvpdomain.constants.UserConstant.CUR_REQUEST_USER_PUBLIC_KEY;

@Aspect
@Component
public class EncryptionAspect {

//    @Autowired
//    private EncryptionService encryptionService;

    @Around(value = "@annotation(com.example.vvpcommom.Encrypted)")
    public Object encryptResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//        String decryptedRequest = decryptRequest(request);
        Object result = joinPoint.proceed(); // 执行原方法
        return encryptResponse(result);
    }

//    private String decryptRequest(HttpServletRequest request) {
//        // 解密请求数据
////        String encryptedRequestData = request.get("data"); // 假设请求参数名为data
////        return RSAUtil.encrypt(encryptedRequestData);
//    }

    private Object encryptResponse(Object response) {
        // 加密响应数据
//        if (response instanceof DrsResponse) {
//            return RSAUtil.encrypt(JSON.toJSONString(response), ThreadLocalUtil.get(CUR_REQUEST_USER_PUBLIC_KEY));
//        }
        // 如果响应是其他类型,可以考虑序列化成字符串然后加密
        // 这里只处理String类型的响应作为示例
        return response;
    }
}

