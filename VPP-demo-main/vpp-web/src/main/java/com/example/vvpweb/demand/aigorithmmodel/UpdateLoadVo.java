package com.example.vvpweb.demand.aigorithmmodel;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateLoadVo {

    @ApiModelProperty(value = "id", required = false)
    private String resp_id;

    @ApiModelProperty(value = "任务id", required = false)
    private String node_id;

    @ApiModelProperty(value = "需求响应下发值", required = false)
    private String command_value;

    @ApiModelProperty(value = "节点id", required = false)
    private String node_name;

    @ApiModelProperty(value = "节点名称", required = false)
    private String opt_fx;

    @ApiModelProperty(value = "系统id", required = false)
    private String opt_x;
}
