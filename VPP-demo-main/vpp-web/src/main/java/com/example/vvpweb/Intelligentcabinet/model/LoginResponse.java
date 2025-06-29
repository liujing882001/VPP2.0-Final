package com.example.vvpweb.Intelligentcabinet.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginResponse implements Serializable {

    @ApiModelProperty("登陆用户id")
    private String userId;

    @ApiModelProperty("登陆用户名")
    private String username;

    @ApiModelProperty("登陆用户token")
    private String token;

}
