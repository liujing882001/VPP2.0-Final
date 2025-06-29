package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CancelRegistrationRequest implements Serializable {
    private static final long serialVersionUID = 2735924638199729298L;

    @ApiModelProperty("CancelRegistrationRequest")
    private String root;

    @ApiModelProperty("协议版本")
    private Integer version;

    @ApiModelProperty("请求ID,由请求方生成,同一个DN应保证其唯一性,以便与响应相对应")
    private String requestID;

    @ApiModelProperty("下位节点的ID")
    private String dnId;

    @ApiModelProperty("注册ID,首次注册时为空,重新注册时使用上一次的ID")
    private String registrationID;
}
