package com.example.vvpweb.systemmanagement.systemuser.UserLoginService;

/*
 * 短信验证登录服务实现类 - 当前已禁用
 * 原因：缺少vvpdomain依赖包，且当前项目中未使用此功能
 * 如需启用，请确保相关依赖包存在并取消注释
 */

/*
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.teaopenapi.models.Config;
import com.example.vvpcommom.*;
import com.example.vvpdomain.UseLogRepository;
import com.example.vvpdomain.UsersRepository;
import com.example.vvpdomain.entity.UseLog;
import com.example.vvpdomain.entity.UserLogin;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserLoginServiceImpl implements UserService {
    public static final String VERIFY_CODE = "login:verify_code:";
    public static final String ACCESS_KEY_ID = System.getenv("ALIYUN_ACCESS_KEY_ID");
    public static final String ACCESS_KEY_SECRET = System.getenv("ALIYUN_ACCESS_KEY_SECRET");
    public static final String END_POINT = "dysmsapi.aliyuncs.com";
    public static final String TEMPLATE_CODE = "SMS_296335865";
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UseLogRepository useLogRepository;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String getSmsCode(String phone) throws Exception {
        // 方法实现已注释...
        return null;
    }

    public String register(String phone, String smsCode, String ip) throws ParseException {
        // 方法实现已注释...
        return null;
    }

    public ResponseResult logOut(String phone, HttpServletRequest request) throws ParseException {
        // 方法实现已注释...
        return null;
    }

    public static String getSmsVerifyCode() {
        // 方法实现已注释...
        return null;
    }
}
*/
