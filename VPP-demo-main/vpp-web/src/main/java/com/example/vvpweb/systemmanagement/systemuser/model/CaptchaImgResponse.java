package com.example.vvpweb.systemmanagement.systemuser.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
public class CaptchaImgResponse implements Serializable {
    @ApiModelProperty(value = "图片唯一对应标识", name = "verifyCode", required = true)
    private String verifyCode;
    @ApiModelProperty(value = "base64图片", name = "captchaImg", required = true)
    private String captchaImg;
}
