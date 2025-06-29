package com.example.vvpweb.systemmanagement.systemuser;

import com.example.vvpcommom.*;
import com.example.vvpdomain.UserRepository;
import com.example.vvpdomain.entity.Menu;
import com.example.vvpdomain.entity.User;
import com.example.vvpservice.menu.service.IMenuService;
import com.example.vvpweb.systemmanagement.systemuser.model.AuthChangePassword;
import com.example.vvpweb.systemmanagement.systemuser.model.AuthLogin;
import com.example.vvpweb.systemmanagement.systemuser.model.AuthLoginResponse;
import com.example.vvpweb.systemmanagement.systemuser.model.CaptchaImgResponse;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.Base64; // 添加这一行

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.vvpdomain.constants.UserConstant.USER_TOKEN_KEY;

/**
 * 系统用户
 */
@RestController
@RequestMapping("/vpp")
@CrossOrigin
@Api(value = "用户登录类", tags = {"用户登录"})
public class SysLoginController {
    @Resource
    private Producer captchaProducer;
    @Resource
    private UserRepository userRepository;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private IMenuService menuService;
    @Value("${os.type}")
    private String osType;
    /**
     * 验证码
     */
    @ApiOperation("获取验证码")
    @RequestMapping(value = "/captcha", method = {RequestMethod.POST})
    @PassToken
    public ResponseResult<CaptchaImgResponse> captchaImage() {

        try {
            String sessionKey = UUID.randomUUID().toString();
            String codeText = captchaProducer.createText();

            BufferedImage image = captchaProducer.createImage(codeText);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);


            String base64Img = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());

            CaptchaImgResponse captchaImgResponse = new CaptchaImgResponse();
            captchaImgResponse.setCaptchaImg(base64Img);
            captchaImgResponse.setVerifyCode(sessionKey);

            // 保存到验证码到 redis 设置1分钟过期
            redisUtils.add(Constants.KAPTCHA_SESSION_KEY + "_" + sessionKey, codeText, 1, TimeUnit.MINUTES);
            return ResponseResult.success(captchaImgResponse);
        } catch (Exception ex) {
            return ResponseResult.error("获取验证码失败，服务发生异常！");
        }
    }

    @ApiOperation("用户登录")
    @RequestMapping(value = "/doLogin", method = {RequestMethod.POST})
    @PassToken
    public ResponseResult<AuthLoginResponse> doLogin(@RequestBody AuthLogin authLogin) {

        try {
            if (authLogin == null) {
                return ResponseResult.error("参数异常，请检查！");
            }
            if (StringUtils.isEmpty(authLogin.getUserName())) {
                return ResponseResult.error("登陆用户名不能为空！");
            }
            if (StringUtils.isEmpty(authLogin.getPassWord())) {
                return ResponseResult.error("密码不能为空！");
            }
            if (StringUtils.isEmpty(authLogin.getVerifyCodeText())) {
                return ResponseResult.error("输入验证码不能为空！");
            }
            if (StringUtils.isEmpty(authLogin.getVerifyCode())) {
                return ResponseResult.error("系统返回的verifyCode不能为空，请检查！");
            }
            String code = authLogin.getVerifyCodeText();
            String sessionKey = authLogin.getVerifyCode();

            String username = authLogin.getUserName();
            String passWord = authLogin.getPassWord();

            Object vCode = redisUtils.get(Constants.KAPTCHA_SESSION_KEY + "_" + sessionKey);

            if (StringUtils.isEmpty(sessionKey)
                    || StringUtils.isEmpty(code)
                    || null == vCode
                    || !code.equals(String.valueOf(vCode).replace("\"", ""))) {
                return ResponseResult.error("验证码错误，请刷新验证码！");
            }
            // 清除验证码，防止重用
            redisUtils.delete(Constants.KAPTCHA_SESSION_KEY + "_" + sessionKey);

            User user = userRepository.findUserByUserNameAndUserPassword(username, passWord);

            if (user != null) {

                String token = Md5TokenGenerator.generate(username, passWord);
                String userId = user.getUserId();
                String tokenKey = "TOKEN_EXPIRE_" + userId;
                String userKey = USER_TOKEN_KEY + token;

                AuthLoginResponse authLoginResponse = new AuthLoginResponse();
                authLoginResponse.setUserId(userId);
                authLoginResponse.setUsername(user.getUserName());
                authLoginResponse.setRefreshToken(token);
                authLoginResponse.setOsType(osType);
                if (user.getRole() != null) {
                    List<Menu> menuList = user.getRole().getMenuList();
                    if (menuList != null && menuList.size() > 0) {
                        List<Menu> menusF = menuList.stream()
                                .filter(m -> "F".equals(m.getMenuType()))
                                .collect(Collectors.toList());
                        if (menusF != null && menusF.size() > 0) {
                            Set<String> permissions = new HashSet<>();
                            menusF.stream().forEach(f -> {
                                permissions.add(f.getPerms());
                            });
                            authLoginResponse.setPermissions(permissions);
                        }
                    }
                }

                //如果是“记住我”，则Token有效期是7天，反之则是24个小时
                redisUtils.add(tokenKey, token, authLogin.isRemember() ? 7 * 24 : 24, TimeUnit.HOURS);
                redisUtils.add(userKey, userId, 7 * 24, TimeUnit.HOURS);
                return ResponseResult.success(authLoginResponse);
            }
        } catch (Exception ex) {
            return ResponseResult.error("登陆失败,请检查输入信息是否正确！");
        }
        return ResponseResult.error("登陆失败,请检查输入信息是否正确！");
    }


    @ApiOperation("用户退出")
    @RequestMapping(value = "/Logout", method = {RequestMethod.POST})
    @UserLoginToken
    public ResponseResult Logout(HttpServletRequest request) {
        String userId = request.getHeader("authorizationCode");
        String tokenKey = "TOKEN_EXPIRE_" + userId;
        //删除redis的token
        redisUtils.delete(tokenKey);
        return ResponseResult.success("退出成功");
    }


    @ApiOperation("获取用户菜单列表")
    @UserLoginToken
    @RequestMapping(value = "/getUserMenuList", method = {RequestMethod.GET})
    public ResponseResult getRouters(@RequestParam("userId") String userId) {
        String loginUserId = RequestHeaderContext.getInstance().getUserId();
        if(!loginUserId.equals(userId)){
            throw new PermsException("用户角色权限不完整，请联系管理员授权！");
        }
        List<Map<String, Object>> maps = menuService.getMenuListByUserId(userId);
        if (maps == null || maps.isEmpty()) {
            throw new PermsException("用户角色权限不完整，请联系管理员授权！");
        }
        return ResponseResult.success(maps);
    }

    @ApiOperation("获取copilot列表")
    @UserLoginToken
    @RequestMapping(value = "/getCopilotPermission", method = {RequestMethod.GET})
    public ResponseResult<List<Map<String, Object>>> getCopilotPermission() {
        String loginUserId = RequestHeaderContext.getInstance().getUserId();
        List<Map<String, Object>> maps = menuService.getCopilotPermission(loginUserId);
        return ResponseResult.success(maps);
    }



    @ApiOperation("修改密码")
    @UserLoginToken
    @RequestMapping(value = "/changePassword", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult changePassword(@RequestBody AuthChangePassword authChangePassword) throws IOException {

        if (authChangePassword == null) {
            return ResponseResult.error("参数异常，请检查！");
        }
        if (StringUtils.isEmpty(authChangePassword.getUserId())) {
            return ResponseResult.error("登陆用户id不能为空！");
        }
        if (StringUtils.isEmpty(authChangePassword.getNewPassWord())) {
            return ResponseResult.error("新密码不能为空！");
        }
        if (StringUtils.isEmpty(authChangePassword.getOldPassWord())) {
            return ResponseResult.error("旧密码不能为空！");
        }

        String userId = authChangePassword.getUserId();
        String newPassWord = authChangePassword.getNewPassWord();
        String oldPassWord = authChangePassword.getOldPassWord();


        if (oldPassWord.equals(newPassWord)) {
            return ResponseResult.error("用户新旧密码一致，请重新输入新密码!");
        }
        User user = userRepository.findById(userId).orElseGet(null);
        if (user != null) {
            user.setUserPassword(newPassWord);
            userRepository.save(user);
            return ResponseResult.success();
        }
        return ResponseResult.error("用户不存在或者密码错误");
    }
}
