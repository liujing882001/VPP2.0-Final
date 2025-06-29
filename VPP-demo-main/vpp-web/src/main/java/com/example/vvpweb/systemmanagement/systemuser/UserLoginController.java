//package com.example.vvpweb.systemmanagement.systemuser;
//
//import com.example.vvpcommom.ResponseResult;
//import com.example.vvpcommom.StringUtils;
//import com.example.vvpweb.systemmanagement.systemuser.UserLoginService.UserService;
//import com.example.vvpweb.systemmanagement.systemuser.model.LoginResponse;
//import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.text.ParseException;
//import java.util.Date;
//
//@RestController
//@RequestMapping("/user")
//@CrossOrigin
//@Api(value = "登录", tags = {"登录"})
//@Slf4j
//public class UserLoginController {
//
//    @Autowired
//    private UserService userService;
//
//    /**
//     * 获取短信验证码
//     */
//    @PostMapping("/getSmsCode")
//    public ResponseResult getSmsVerifyCode(@RequestParam("phone") String phone) throws Exception {
//        if (StringUtils.isEmpty(phone)) {
//            return ResponseResult.error("手机号不能为空");
//        }
//        String smsCode = userService.getSmsCode(phone);
//        if (smsCode.equals("操作过于频繁，请一分钟之后再次点击发送")) {
//            return ResponseResult.error("操作过于频繁，请一分钟之后再次点击发送");
//        } else {
//            return ResponseResult.success();
//        }
//    }
//
//    /**
//     * 注册
//     * @param phone
//     * @param smsCode
//     * @param request
//     * @return
//     * @throws ParseException
//     */
//    @PostMapping("/register")
//    public ResponseResult<LoginResponse> register(@RequestParam("phone") String phone, @RequestParam("smsCode") String smsCode, HttpServletRequest request) throws ParseException {
//
//        if (StringUtils.isEmpty(phone)) {
//            return ResponseResult.error("手机号不能为空");
//        }
//        if (StringUtils.isEmpty(smsCode)) {
//            return ResponseResult.error("验证码不能为空");
//        }
//
//        LoginResponse loginResponse = new LoginResponse();
//        String ip = request.getRemoteAddr();
//        String token = userService.register(phone, smsCode, ip);
//        if (token.equals("用户不存在")) {
//            return ResponseResult.error("用户不存在");
//        }
//        if (token.equals("短信验证码不存在或已过期")) {
//            return ResponseResult.error("短信验证码不存在或已过期");
//        }
//        if (token.equals("短信验证码错误")) {
//            return ResponseResult.error("短信验证码错误");
//        }
//        loginResponse.setToken(token);
//        return ResponseResult.success(loginResponse);
//    }
//
//    @PostMapping("/logOut")
//    public ResponseResult Logout(@RequestParam("phone") String phone, HttpServletRequest request) throws ParseException {
//        return userService.logOut(phone, request);
//    }
//}
