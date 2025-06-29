package com.example.vvpdomain.entity;

import com.example.vvpcommom.TimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author zph
 * @description 充放电比例
 * @date 2022-07-26
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cfg_storage_energy_strategy")
public class CfgStorageEnergyStrategy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 参数主键=键名类型父菜单id
     */
    @Column(name = "id")
    private String id;

    /**
     * 节点id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 经度
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * 纬度
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * 系统id
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * 生效时间 yyyy-MM-dd
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    @Column(name = "effective_date")
    private Date effectiveDate;

    /**
     * 价格表标签
     */
    @Column(name = "price_tag")
    private String priceTag;

    /**
     * 序号 yyyymm
     */
    @Column(name = "`order`")
    private Integer order;

    /**
     * 时间范围 00:00-00:59
     */
    @Column(name = "time_frame")
    private String timeFrame;

    /**
     * 开始时间 mm:ss
     */
    @Column(name = "s_time")
    private String sTime;

    /**
     * 结束时间 mm:ss
     */
    @Column(name = "e_time")
    private String eTime;

    /**
     * 属性 尖，峰，平，谷
     */
    @Column(name = "property")
    private String property;

    /**
     * 每小时价格
     */
    @Column(name = "price_hour")
    private BigDecimal priceHour;

    /**
     * 充放电策略
     */
    @Column(name = "strategy")
    private String strategy;


    /**
     * 充放电策略预测
     */
    @Column(name = "strategy_forecasting")
    private String strategyForecasting;

    /**
     * 充放电小时数
     */
    @Column(name = "strategy_hour")
    private Integer strategyHour;

    /**
     * 倍率
     */
    @Column(name = "multiplying_power")
    private Double multiplyingPower;

    /**
     * created_time
     */
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public CfgStorageEnergyStrategy() {
    }

    public String getIdentify() {
        return getNodeId() + TimeUtil.toYmdStr(getEffectiveDate()) + getTimeFrame();
    }

    public BigDecimal calPriceHour() {
        if (getStrategy() == null) {
            return BigDecimal.ZERO;
        }
        if (getStrategy().contains("充")) {
            return getPriceHour().negate();
        } else if (getStrategy().contains("放")) {
            return getPriceHour();
        } else {
            return BigDecimal.ZERO;
        }
    }
}