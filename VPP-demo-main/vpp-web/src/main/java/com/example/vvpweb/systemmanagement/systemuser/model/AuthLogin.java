package com.example.vvpweb.systemmanagement.systemuser.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
@ApiModel(value = "authLogin", description = "系统登录")
public class AuthLogin implements Serializable {

    @NotBlank(message = "登陆用户名不能为空")
    @ApiModelProperty(value = "登陆用户名", name = "userName", required = true)
    private String userName;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", name = "passWord", required = true)
    private String passWord;

    @ApiModelProperty(value = "获取验证码，系统返回的verifyCode", name = "verifyCode", required = true)
    private String verifyCode;

    @NotBlank(message = "输入验证码不能为空")
    @ApiModelProperty(value = "界面输入验证码", name = "verifyCodeText", required = true)
    private String verifyCodeText;


    @ApiModelProperty(value = "记住我，默认false", name = "remember", required = true)
    private boolean remember;

}
