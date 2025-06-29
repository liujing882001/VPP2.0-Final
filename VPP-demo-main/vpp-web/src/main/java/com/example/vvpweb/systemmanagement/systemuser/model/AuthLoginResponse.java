package com.example.vvpweb.systemmanagement.systemuser.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Zhaoph
 */
@Data
public class AuthLoginResponse implements Serializable {

    @ApiModelProperty(value = "登陆用户id", name = "userId", required = true)
    private String userId;
    @ApiModelProperty(value = "登陆用户名", name = "username", required = true)
    private String username;
    @ApiModelProperty(value = "登陆用户token", name = "refreshToken", required = true)
    private String refreshToken;
    @ApiModelProperty(value = "系统类型(loadType 负荷类 resourcesType 资源类) ", name = "osType", required = true)
    private String osType;
    @ApiModelProperty(value = "登按钮权限", name = "permissions", required = true)
    private Set<String> permissions = new HashSet<>();
}
