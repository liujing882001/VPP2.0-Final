package com.example.vvpservice.demand.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author maoyating
 * @description 需求响应任务
 */
@Entity
@Getter
@Setter
public class DemandResponseInvitationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应任务id -- 需求唯一标识
     */
    private String demandId;

    /**
     * 需求时段 ["05:00-15:00","18:00-23:00"]
     */
    private String[] demandTime;

    /**
     * 响应日期
     */
    private String demandDate;

    /**
     * 响应类型(1-削峰响应 2-填谷响应)
     */
    private String demandType;

    /**
     * 负荷需求--响应负荷，单位（kW）
     */
    private Double demandValue;

    /**
     * 价格--响应补贴（元/kWh）
     */
    private Double demandPrice;

    /**
     * 电表户号
     */
    private String meterAccountNumber;

    /**
     * 响应结果 1 中标 2未中标
     */
    private String invitationResult;

    public DemandResponseInvitationResult() {

    }

}