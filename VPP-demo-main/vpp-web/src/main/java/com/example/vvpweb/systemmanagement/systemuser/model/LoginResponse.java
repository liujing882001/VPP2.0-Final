package com.example.vvpweb.systemmanagement.systemuser.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginResponse {
    @ApiModelProperty("登陆用户token")
    private String token;

    @ApiModelProperty("二维码图片路径")
    private String qrcodeUrl;

    @ApiModelProperty("sceneStr")
    private String sceneStr;

    @ApiModelProperty("ticket")
    private String ticket;
}
