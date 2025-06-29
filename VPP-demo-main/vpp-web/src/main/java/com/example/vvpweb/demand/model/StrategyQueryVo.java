package com.example.vvpweb.demand.model;


import com.alibaba.fastjson.JSONObject;
import com.example.vvpdomain.entity.DemandStrategy;
import com.example.vvpweb.demand.aigorithmmodel.InitVo;
import lombok.Data;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Data
public class StrategyQueryVo {

    private String id;
//    /**
//     * 任务id
//     */
//    private String respId;
    /**
     * 节点id
     */
    private String nodeId;
//    /**
//     * 系统id
//     */
//    private String systemId;
    /**
     * 时间点
     */
    private Date timePoint;
    /**
     * 策略内容
     */
    private String strategyContent;
//    /**
//     * 是否大模型生成
//     */
//    private boolean isLlm;
//    /**
//     * 策略使用状态
//     */
//    private Integer state;
//    /**
//     * 预测调节后负荷
//     */
//    private String forecastAdjustedLoad;
//    /**
//     * 预测调节负荷生成时间
//     */
//    private Date forecastTime;
    /**
     * 节点名称
     */
    private String nodeName;
//    /**
//     * 需求响应下发值
//     */
//    private String commandValue;
    /**
     * 户号
     */
    private String noHouseholds;
    /**
     * 设备名
     */
    private List<String> deviceName;
    /**
     * 确认
     */
    private Integer ensure;

    public StrategyQueryVo(DemandStrategy strategy,List<String> deviceName) {
        this.id = strategy.getId();
        this.nodeId = strategy.getNodeId();
        this.nodeName = strategy.getNodeName();
        this.noHouseholds = strategy.getNoHouseholds();
        this.timePoint = strategy.getTimePoint();
        this.deviceName = deviceName;
        this.ensure = strategy.getEnsure();
        if (strategy.getStrategyContent() != null ) {
            this.strategyContent = "出水温度"
                    + new DecimalFormat("#0.00").format(JSONObject.parseObject(strategy.getStrategyContent(), InitVo.class)
                    .getCh_water_outlet_temperature())
                    + "℃";
        } else {
            this.strategyContent = null;
        }

    }
}
