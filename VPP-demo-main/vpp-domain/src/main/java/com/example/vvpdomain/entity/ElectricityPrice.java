package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Electricity Price Entity
 * Represents electricity pricing information for different time periods
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "electricity_price")
public class ElectricityPrice implements Serializable {
    private String sTime;
    private String eTime;
    
    public String getSTime() { return sTime; }
    public void setSTime(String sTime) { this.sTime = sTime; }
    public String getETime() { return eTime; }
    public void setETime(String eTime) { this.eTime = eTime; }

    
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    /**
     * Node identifier
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * Date when the price becomes effective
     */
    @Column(name = "effective_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime effectiveDate;

    /**
     * Time frame description (e.g., "Peak", "Off-Peak")
     */
    @Column(name = "time_frame")
    private String timeFrame;

    /**
     * Start time of the price period
     */
    @Column(name = "s_time")
    private LocalTime startTime;

    /**
     * End time of the price period
     */
    @Column(name = "e_time")
    private LocalTime endTime;

    /**
     * Price property/type
     */
    @Column(name = "property")
    private String property;

    /**
     * Electricity price value
     */
    @Column(name = "price", precision = 10, scale = 4)
    private BigDecimal price;

    /**
     * Strategy value
     */
    @Column(name = "strategy", precision = 10, scale = 4)
    private BigDecimal strategy;

    /**
     * Record creation timestamp
     */
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * Record last update timestamp
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * Type of date (e.g., "Weekday", "Weekend", "Holiday")
     */
    @Column(name = "date_type")
    private String dateType;

    /**
     * Price usage type
     */
    @Column(name = "price_use")
    private String priceUse;

    /**
     * Default constructor
     */
    public ElectricityPrice() {
    }

    public ElectricityPrice(ElectricityPrice price) {
        this.id = price.getId();
        this.nodeId = price.getNodeId();
        this.effectiveDate = price.getEffectiveDate();
        this.timeFrame = price.getTimeFrame();
        this.startTime = price.getStartTime();
        this.endTime = price.getEndTime();
        this.property = price.getProperty();
        this.price = price.getPrice();
        this.createdTime = price.getCreatedTime();
        this.updateTime = price.getUpdateTime();
        this.dateType = price.getDateType();
        this.priceUse = price.getPriceUse();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public LocalDateTime getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDateTime effectiveDate) { this.effectiveDate = effectiveDate; }
    public String getTimeFrame() { return timeFrame; }
    public void setTimeFrame(String timeFrame) { this.timeFrame = timeFrame; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getStrategy() { return strategy; }
    public void setStrategy(BigDecimal strategy) { this.strategy = strategy; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public String getDateType() { return dateType; }
    public void setDateType(String dateType) { this.dateType = dateType; }
    public String getPriceUse() { return priceUse; }
    public void setPriceUse(String priceUse) { this.priceUse = priceUse; }
}
