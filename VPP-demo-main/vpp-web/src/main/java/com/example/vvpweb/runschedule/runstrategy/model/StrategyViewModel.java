package com.example.vvpweb.runschedule.runstrategy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.Date;


@Data
public class StrategyViewModel {
    private String strategyId;

    private String strategyName;

    /**
     * 是否参与自动需求响应
     */
    @ApiParam(name = "isDemandResponse", value = "是否参与自动需求响应")
    private boolean isDemandResponse;
    /**
     * 策略启停状态
     */
    @ApiParam(name = "isStrategyStatus", value = "策略启停状态")
    private boolean isStrategyStatus;


    /**
     * 运行策略类型 0 一次性 或者 1 周期性
     */
    @ApiParam(name = "runStrategy", value = "运行策略类型 0 一次性 或者 1 周期性")
    private int runStrategy;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 额定功率
     */
    private double ratedPower;

    /**
     * 所有者id
     */
    private String ownerId;


    /**
     * 所有者名称
     */
    private String ownerName;

    /**
     * 策略类型
     * 0 空调策略
     * 1 其他策略(针对照明、基站充电桩等，可对设备的启动/停止进行控制)
     */
    private int strategyType;

}
