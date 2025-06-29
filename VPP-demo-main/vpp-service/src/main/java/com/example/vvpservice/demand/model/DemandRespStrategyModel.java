package com.example.vvpservice.demand.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author maoyating
 * @description 需求响应策略
 * @date 2022-08-09
 */
@Entity
@Getter
@Setter
public class DemandRespStrategyModel implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 策略id
     * 表字段： demand_resp_strategy.s_id
     */
    private String sId;


    /**
     * 响应任务id
     * 表字段： demand_resp_strategy.resp_id
     */
    private String respId;

    /**
     * 所属策略id
     */
    private String strategyId;

    /**
     * 操作者
     */
    private String createBy;

}