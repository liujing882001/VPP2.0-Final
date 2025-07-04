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

    // 手动添加getter和setter方法以确保编译通过
    public String getSId() {
        return sId;
    }

    public void setSId(String sId) {
        this.sId = sId;
    }

    public String getRespId() {
        return respId;
    }

    public void setRespId(String respId) {
        this.respId = respId;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
}