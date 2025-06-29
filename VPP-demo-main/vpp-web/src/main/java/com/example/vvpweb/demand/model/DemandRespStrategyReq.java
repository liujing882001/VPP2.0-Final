package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author maoyating
 * @description 需求响应策略
 * @date 2022-08-09
 */
@Data
public class DemandRespStrategyReq {

    /**
     * 策略id
     */
    @ApiModelProperty("策略id")
    private String sId;

    /**
     * 节点id
     */
    @ApiModelProperty("节点id")
    private String nodeId;

    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String nodeName;

    /**
     * 系统id
     */
    @ApiModelProperty("系统id")
    private String systemId;

    /**
     * 所属系统名称
     */
    @ApiModelProperty("系统名称")
    private String systemName;

    /**
     * 设备ID，设备序列号，唯一码
     */
    @ApiModelProperty("设备ID")
    private String deviceId;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /**
     * 额定负荷(kW)
     */
    @ApiModelProperty("额定负荷(kW)")
    private Double deviceRatedPower;

    /**
     * 实时负荷(kW)
     */
    @ApiModelProperty("实时负荷(kW)")
    private Double actualLoad;

    /**
     * 策略状态1-开启 2-关闭
     */
    @ApiModelProperty("策略状态1-开启 2-关闭")
    private Integer sStatus;

    /**
     * 响应任务id
     */
    @ApiModelProperty("响应任务id")
    private String respId;

    /**
     * 户号
     */
    @ApiModelProperty("户号")
    private String noHouseholds;

    /**
     * 可调负荷运行策略id
     */
    @ApiModelProperty("可调负荷运行策略id")
    private String strategyId;
}