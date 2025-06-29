package com.example.vvpweb.runschedule.runstrategy.model;

import lombok.Data;

import java.util.List;

@Data
public class StrategyModel {
    private String strategyId;

    private String strategyName;

    /**
     * 策略类型
     * 0 空调策略
     * 1 其他策略(针对照明、基站充电桩等，可对设备的启动/停止进行控制)
     */
    private int strategyType;

    private String onTimes;

    private List<Integer> onWeeks;

    private String offTimes;

    private List<Integer> offWeeks;

    private String demandResponse;

    private String nodeId;

    private String nodeName;

    private String systemId;

    private String systemName;

    private String status;

}
