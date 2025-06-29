package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author maoyating
 */
@Data
public class DemandStrategyModel implements Serializable {

    /**
     * 每页大小
     */
    @ApiModelProperty("每页大小")
    private int pageSize;
    /**
     * 当前页为第几页 默认 1开始
     */
    @ApiModelProperty("第几页，默认从1开始")
    private int number;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /**
     * 响应任务id
     */
    @ApiModelProperty("响应任务id")
    private String respId;

    /**
     * 可调负荷运行策略id
     */
    @ApiModelProperty("可调负荷运行策略id")
    private String strategyId;

    @ApiModelProperty("额定负荷排序 1-升序 2-降序")
    private Integer deviceRatedPowerSort;

    @ApiModelProperty("策略状态排序 1-升序 2-降序")
    private Integer deviceStatusSort;

    /**
     * 策略id
     */
    @ApiModelProperty("策略id")
    private String sId;

    /**
     * 第三方平台id
     */
    @ApiModelProperty("第三方平台id")
    private String platformId;
}
