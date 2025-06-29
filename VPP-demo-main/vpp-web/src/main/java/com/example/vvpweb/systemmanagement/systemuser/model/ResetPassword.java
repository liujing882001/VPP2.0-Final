package com.example.vvpweb.systemmanagement.systemuser.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
@ApiModel(value = "ResetPassword", description = "重置用户密码")
public class ResetPassword implements Serializable {

    @ApiModelProperty(value = "用户ID", name = "userId", required = true)
    private String userId;

    @ApiModelProperty(value = "新密码", name = "newPassWord", required = true)
    private String newPassWord;


}
