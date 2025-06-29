package com.example.vvpweb.systemmanagement.systemuser.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
@ApiModel(value = "AuthChangePassword", description = "用户修改密码")
public class AuthChangePassword implements Serializable {

    @ApiModelProperty(value = "登陆ID", name = "userId", required = true)
    private String userId;

    @ApiModelProperty(value = "旧密码", name = "oldPassWord", required = true)
    private String oldPassWord;

    @ApiModelProperty(value = "新密码", name = "newPassWord", required = true)
    private String newPassWord;


}
