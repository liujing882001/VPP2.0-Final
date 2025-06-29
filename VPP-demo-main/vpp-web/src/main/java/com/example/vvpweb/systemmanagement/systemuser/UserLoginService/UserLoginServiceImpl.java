//package com.example.vvpweb.systemmanagement.systemuser.UserLoginService;
//
//import com.aliyun.dysmsapi20170525.Client;
//import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
//import com.aliyun.teaopenapi.models.Config;
//import com.example.vvpcommom.*;
//import com.example.vvpdomain.UseLogRepository;
//import com.example.vvpdomain.UsersRepository;
//import com.example.vvpdomain.entity.UseLog;
//import com.example.vvpdomain.entity.UserLogin;
//import com.google.gson.Gson;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class UserLoginServiceImpl implements UserService {
//    public static final String VERIFY_CODE = "login:verify_code:";
//    public static final String ACCESS_KEY_ID = System.getenv("ALIYUN_ACCESS_KEY_ID");
//    public static final String ACCESS_KEY_SECRET = System.getenv("ALIYUN_ACCESS_KEY_SECRET");
//    public static final String END_POINT = "dysmsapi.aliyuncs.com";
//    public static final String TEMPLATE_CODE = "SMS_296335865";
//    @Autowired
//    private RedisTemplate<String,String> redisTemplate;
//    @Autowired
//    private UsersRepository usersRepository;
//    @Autowired
//    private UseLogRepository useLogRepository;
//    @Autowired
//    private RedisUtils redisUtils;
//
//    @Override
//    public String getSmsCode(String phone) throws Exception {
//        //生成验证码
//        String smsVerifyCode = getSmsVerifyCode();
//        String smsCodeKey = VERIFY_CODE + phone;
//
//        //查找redis
//        String existedSmsCode = redisTemplate.opsForValue().get(smsCodeKey);
//        //如果验证码已经存在
//        if (!StringUtils.isEmpty(existedSmsCode)) {
//            //验证码在expireTime秒后过期
//            Long expireTime = redisTemplate.opsForValue().getOperations().getExpire(smsCodeKey);
//            long lastTime = 60 * 3 - expireTime;
//            //三分钟内验证码有效，1分钟到3分钟之间，用户可以继续输入验证码，也可以重新获取验证码，新的验证码将覆盖旧的
//            if(lastTime > 60 && expireTime >0){
//                //调用第三方平台发短信，只有短信发送成功了，才能将短信验证码保存到redis
//                Config config = new Config()
//                        //这里修改为我们上面生成自己的AccessKey ID
//                        .setAccessKeyId(ACCESS_KEY_ID)
//                        //这里修改为我们上面生成自己的AccessKey Secret
//                        .setAccessKeySecret(ACCESS_KEY_SECRET);
//                // 访问的域名
//                config.endpoint = END_POINT;
//                com.aliyun.dysmsapi20170525.Client client = new Client(config);
//                SendSmsRequest sendSmsRequest = new SendSmsRequest()
//                        .setSignName("达卯智能")//短信签名
//                        .setTemplateCode(TEMPLATE_CODE)//短信模板
//                        .setPhoneNumbers(phone)//这里填写接受短信的手机号码
//                        .setTemplateParam("{\"code\":\""+smsVerifyCode+"\"}");//验证码
//                // 复制代码运行请自行打印 API 的返回值
//                client.sendSms(sendSmsRequest);
//                redisTemplate.opsForValue().set(smsCodeKey, smsVerifyCode, 60 * 3, TimeUnit.SECONDS);
//            }
//            //一分钟之内不得多次获取验证码
//            if(lastTime < 60){
//                throw new RuntimeException("操作过于频繁，请一分钟之后再次点击发送");
//            }
//        }else {
//            Config config = new Config()
//                    //这里修改为我们上面生成自己的AccessKey ID
//                    .setAccessKeyId(ACCESS_KEY_ID)
//                    //这里修改为我们上面生成自己的AccessKey Secret
//                    .setAccessKeySecret(ACCESS_KEY_SECRET);
//            // 访问的域名
//            config.endpoint = END_POINT;
//            com.aliyun.dysmsapi20170525.Client client = new Client(config);
//            SendSmsRequest sendSmsRequest = new SendSmsRequest()
//                    .setSignName("达卯智能")//短信签名
//                    .setTemplateCode(TEMPLATE_CODE)//短信模板
//                    .setPhoneNumbers(phone)//这里填写接受短信的手机号码
//                    .setTemplateParam("{\"code\":\""+smsVerifyCode+"\"}");//验证码
//            // 复制代码运行请自行打印 API 的返回值
//            client.sendSms(sendSmsRequest);
//            redisTemplate.opsForValue().set(smsCodeKey, smsVerifyCode, 60 * 3, TimeUnit.SECONDS);
//        }
//        return smsVerifyCode;
//    }
//
//    public String register(String phone, String smsCode, String ip) throws ParseException {
//
//        //1,先查验证码
//        String smsCodeKey = VERIFY_CODE + phone;
//        String verifyCode = redisTemplate.opsForValue().get(smsCodeKey);
//        if (StringUtils.isEmpty(verifyCode)) {
//            return "短信验证码不存在或已过期";
//        }
//        if (!smsCode.equals(verifyCode)) {
//            return "短信验证码错误";
//        }
//        //清理验证码
//        if (redisTemplate.hasKey(smsCodeKey)) {
//            redisTemplate.delete(smsCodeKey);
//        }
//        //2,再判断是否第一次登录
//        UserLogin loginUser = usersRepository.findUsersByPhone(phone);
//
//        if (loginUser == null) {
//            UserLogin user = new UserLogin();
//            user.setPhone(phone);
//
//            user.setUserId(String.valueOf(System.currentTimeMillis()));
//            user.setRegisterType("手机验证码");
//
//            String defaultPassword = "123456";
//            String md5Pwd = IdGenerator.md5Id(defaultPassword);
//            user.setPassword(md5Pwd);
//
//            Date day = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            String time = sdf.format(day);
//            user.setCreateTime(sdf.parse(time));
//            usersRepository.save(user);
//        }
//
//        //3,修改登录日志数据表
//        UseLog useLog = new UseLog();
//        useLog.setUseLogId(String.valueOf(System.currentTimeMillis()));
//        useLog.setUserPhone(phone);
//        Date loginDay = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        String loginTime = sdf.format(loginDay);
//        useLog.setLoginTime(sdf.parse(loginTime));
//        useLog.setLoginIp(ip);
//        useLogRepository.save(useLog);
//
//        String token = Md5TokenGenerator.generate(phone, verifyCode);
//        String tokenKey = "TOKEN_EXPIRE_" + phone;
//        //如果是"记住我"，则Token有效期是7天，反之则是24个小时
//        redisUtils.add(tokenKey, token,  24, TimeUnit.HOURS);
//        return token;
//    }
//
//    public ResponseResult logOut(String phone, HttpServletRequest request) throws ParseException {
//        String token = request.getHeader("authorizationCode");
//        String tokenKey = "TOKEN_EXPIRE_" + phone;
//        //删除redis的token
//        redisUtils.delete(tokenKey);
//        //String ip = request.getRemoteAddr();
//        //根据手机号查找日志表，增加登出时间
//        Date logoutDay = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        String logoutTime = sdf.format(logoutDay);
//        Date date = sdf.parse(logoutTime);
//        useLogRepository.updateLogoutTime(phone, date);
//
//        return ResponseResult.success("退出成功");
//    }
//
//    public static String getSmsVerifyCode() {
//        Random random = new Random();
//        String code = "";
//        for (int i = 0; i < 6; i++) {
//            int rand = random.nextInt(10);
//            code += rand;
//        }
//        return code;
//    }
//}
