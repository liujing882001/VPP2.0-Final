package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CreateRegistrationRequest implements Serializable {
    private static final long serialVersionUID = 3488123065515452343L;

    @ApiModelProperty("CreateRegistrationRequest")
    private String root;

    @ApiModelProperty("协议版本")
    private Integer version;

    @ApiModelProperty("请求ID,由请求方生成,同一个DN应保证其唯一性,以便与响应相对应")
    private String requestID;

    @ApiModelProperty("下位节点的ID")
    private String dnID;

    @ApiModelProperty("DN名称")
    private String dnName;

    @ApiModelProperty("注册ID,首次注册时为空,重新注册时使用上一次的ID")
    private String registrationID;

    @ApiModelProperty("是否只支持报告")
    private Boolean reportOnly;

    @ApiModelProperty("是否为pull模式")
    private Boolean pullMode;

    @ApiModelProperty("交换的报文是否签名,如果要签名,数据报文需要用DrPayload封装")
    private Boolean signature;

    @ApiModelProperty("传输方式,可以是REST或MQTT")
    private TransportType transport;

    @ApiModelProperty("服务接入点,REST传输方式时是请求URL,MQTT传输方式时是服务器地址")
    private String transportAddress;

    @ApiModelProperty("扩展备案信息")
    private List<KeyValue> otherInfo;
}
