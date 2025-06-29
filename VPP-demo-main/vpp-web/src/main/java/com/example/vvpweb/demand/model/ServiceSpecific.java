package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ServiceSpecific implements Serializable {
    private static final long serialVersionUID = 3582674604758230106L;

    @ApiModelProperty("服务名称")
    private ServiceType serviceName;

    @ApiModelProperty("用于扩展指定服务的信息")
    private List<KeyValue> keyValue;
}
