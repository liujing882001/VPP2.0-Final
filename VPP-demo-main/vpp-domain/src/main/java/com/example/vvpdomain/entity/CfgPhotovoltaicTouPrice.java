package com.example.vvpdomain.entity;

import com.example.vvpcommom.TimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zph
 * @description 光伏-分时电价
 * @date 2022-07-29
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cfg_photovoltaic_tou_price")
public class CfgPhotovoltaicTouPrice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 参数主键=键名类型父菜单id
     */
    @Column(name = "id")
    private String id;

    /**
     * 参数名称
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 参数键名
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * 生效时间 yyyy-MM
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
     * 开始时间 hh:mm
     */
    @Column(name = "s_time")
    private String sTime;

    /**
     * 结束时间 hh:mm
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

    public CfgPhotovoltaicTouPrice() {
    }

    public String getIdentify() {
        return getNodeId() + TimeUtil.toYmdStr(getEffectiveDate()) + getTimeFrame();
    }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public Date getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(Date effectiveDate) { this.effectiveDate = effectiveDate; }
    public String getTimeFrame() { return timeFrame; }
    public void setTimeFrame(String timeFrame) { this.timeFrame = timeFrame; }
    public BigDecimal getPriceHour() { return priceHour; }

    // 手动添加缺失的getter/setter方法以确保编译通过
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSystemId() { return systemId; }
    public void setSystemId(String systemId) { this.systemId = systemId; }
    public String getPriceTag() { return priceTag; }
    public void setPriceTag(String priceTag) { this.priceTag = priceTag; }
    public Integer getOrder() { return order; }
    public void setOrder(Integer order) { this.order = order; }
    public String getSTime() { return sTime; }
    public void setSTime(String sTime) { this.sTime = sTime; }
    public String getETime() { return eTime; }
    public void setETime(String eTime) { this.eTime = eTime; }
    public String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }
    public void setPriceHour(BigDecimal priceHour) { this.priceHour = priceHour; }
    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}