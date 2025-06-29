package com.example.vvpweb.systemmanagement.systemuser.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class DrSouthLogin implements Serializable {

    @NotBlank(message = "登陆用户ID不能为空")
    private String userId;

    @NotBlank(message = "密码不能为空")
    private String passWord;

}
