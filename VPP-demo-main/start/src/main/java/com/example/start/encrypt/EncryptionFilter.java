package com.example.start.encrypt;

import com.example.vvpcommom.RSAUtil;
import com.example.vvpcommom.RedisUtils;
import com.example.vvpcommom.StringUtils;
import com.example.vvpcommom.thread.ThreadLocalUtil;
import com.example.vvpdomain.UserRepository;
import com.example.vvpdomain.UserSecretRepository;
import com.example.vvpdomain.entity.UserSecret;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.vvpdomain.constants.UserConstant.*;

/**
 * 自定义Filter
 * qxc
 * 20240622
 */
@Component
public class EncryptionFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(EncryptionFilter.class);

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSecretRepository userSecretRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        EncryptionRequestWrapper encryptionRequestWrapper = null;
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            encryptionRequestWrapper = new EncryptionRequestWrapper(req);

            preHandle(encryptionRequestWrapper);
        } catch (Exception e) {
            log.warn("customHttpServletRequestWrapper Error:", e);
        }
        // 创建一个自定义的响应包装器
        boolean isEncryption = ThreadLocalUtil.get(CUR_REQUEST_USER_PUBLIC_KEY) == null;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        EncryptionResponseWrapper wrapper = new EncryptionResponseWrapper(httpResponse);

        chain.doFilter((isEncryption || Objects.isNull(encryptionRequestWrapper)  ? request : encryptionRequestWrapper),
                isEncryption || Objects.isNull(encryptionRequestWrapper) || !encryptionRequestWrapper.hasAnnotation() ? response : wrapper);

        if (isEncryption) {
            ThreadLocalUtil.clear();
            return;
        }
        if (encryptionRequestWrapper.hasAnnotation()) {
            // 获取响应内容
            byte[] responseData = wrapper.getByteArray();

            if (responseData.length < 1) {
                return;
            }
            // 这里进行加密操作
            String resData = new String(responseData);
//            log.info("返回数据={}", resData);
            String encrypt = encrypt(resData);
//            log.info("返回加密数据==={}", encrypt);
            if (StringUtils.isEmpty(encrypt)) {
                return;
            }

            // 清除原来的响应内容
            wrapper.resetBuffer();

            // 写入加密后的内容
            httpResponse.getOutputStream().write(encrypt.getBytes());

        }
        ThreadLocalUtil.clear();
    }

    public void preHandle(EncryptionRequestWrapper request) throws IOException {
        //仅当请求方法为POST时修改请求体
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            return;
        }
        if (!request.hasAnnotation()) {
            String encryptedBody = request.getReader().lines().reduce("", String::concat);
//            log.info("请求数据 = {}", encryptedBody);

            request.setBody(encryptedBody);
            return;
        }
        Object u = isLoginUser(request);
        if (u == null) return;

        String userId = u.toString();
        Optional<UserSecret> list = userSecretRepository.findById(userId);
        if (!list.isPresent()) {
            return;
        }
        UserSecret userSecret = list.get();
        ThreadLocalUtil.put(CUR_REQUEST_USER_PUBLIC_KEY, userSecret.getThirdPublicKey());
        String encryptedBody = request.getReader().lines().reduce("", String::concat);
        log.info("请求加密数据 = {}", encryptedBody);
        String decryptedBody = decrypt(encryptedBody);
        log.info("请求解密参数 = {}", decryptedBody);

        request.setBody(decryptedBody);
    }

    @Nullable
    private Object isLoginUser(EncryptionRequestWrapper request) {
        String baseToken = request.getHeader("Authorization");
        // 请求内容解密逻辑
        // TODO 兼容一段时间，如果没有登录直接处理
        if (StringUtils.isEmpty(baseToken) || redisUtils == null) {
            return null;
        }

        Base64.getDecoder().decode(baseToken);
        String token = new String(Base64.getDecoder().decode(baseToken), StandardCharsets.UTF_8);
        // 将解密后的内容重新设置到请求体中
        String tokenKey = USER_TOKEN_KEY + token;
        Object u = redisUtils.get(tokenKey);
        if (u == null) {
            return null;
        }
        redisUtils.expire(tokenKey, 7*24, TimeUnit.HOURS);
        return u;
    }

    private Map<String, String> getMap(String text) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 将JSON字符串转换为Map
            Map<String, String> map = objectMapper.readValue(text, Map.class);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decrypt(String encryptedBody) {
        // 这里实现你的解密逻辑
        return RSAUtil.decode(encryptedBody, DM_PRIVATE_KEY);
    }

    private String encrypt(String plainText) {
        // 这里实现你的加密逻辑
        return RSAUtil.encrypt(plainText, ThreadLocalUtil.get(CUR_REQUEST_USER_PUBLIC_KEY)); // 示例返回原字符串
    }
}
