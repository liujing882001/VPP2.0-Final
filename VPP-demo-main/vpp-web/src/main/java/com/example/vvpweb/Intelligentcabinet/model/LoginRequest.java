package com.example.vvpweb.Intelligentcabinet.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LoginRequest implements Serializable {

    @ApiModelProperty("用户名")
    private String giccName;

    @ApiModelProperty("密码")
    private String giccPwd;

}
