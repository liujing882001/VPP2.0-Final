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
import java.util.Date;

/**
 * @author zph
 * @description 节点系统 24点 用电数据  非累计
 * @date 2022-08-19
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "iot_ts_kv_metering_device_96")
public class IotTsKvMeteringDevice96 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    /**
     * province_region_id 省所在区域id
     */
    @Column(name = "province_region_id")
    private String provinceRegionId;

    /**
     * city_region_id 市所在区域id
     */
    @Column(name = "city_region_id")
    private String cityRegionId;


    /**
     * county_region_id 县/区所在区域id
     */
    @Column(name = "county_region_id")
    private String countyRegionId;
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
     * node_post_type
     * 节点归属类型 load 负荷，pv 光伏，storageEnergy 储能
     */
    @Column(name = "node_post_type")
    private String nodePostType;

    /**
     * 系统id
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * 设备序列号
     */
    @Column(name = "device_sn")
    private String deviceSn;

    /**
     * 设备属性唯一码
     */
    @Column(name = "point_sn")
    private String pointSn;


    /**
     * 设备类型 计量设备 metering_device
     */
    @Column(name = "config_key")
    private String configKey;

    /**
     * 采集数值=(采集数累计值-基数)
     */
    @Column(name = "h_total_use")
    private Double hTotalUse;

    /**
     * 切片时段特殊计算的采集数值
     */
    @Column(name = "col_value")
    private Double colValue;

    /**
     * 切片时段平均值
     */
    @Column(name = "avg_value")
    private Double avgValue;

    /**
     * 切片时段最小值
     */
    @Column(name = "min_value")
    private Double minValue;

    /**
     * 切片时段最大值
     */
    @Column(name = "max_value")
    private Double maxValue;

    /**
     * 切片时段最先值
     */
    @Column(name = "first_value")
    private Double firstValue;

    /**
     * 切片时段最后值
     */
    @Column(name = "last_value")
    private Double lastValue;

    /**
     * 采集数的累计数值只对标签为energy 有意义 用电量（千瓦时）
     */
    @Column(name = "total_power_energy")
    private Double totalPowerEnergy;


    /**
     * 采集电表数的累计数值 基数
     */
    @Column(name = "total_power_energy_base")
    private Double totalPowerEnergyBase;

    /**
     * 标签 参考模型参数表
     */
    @Column(name = "point_desc")
    private String pointDesc;

    /**
     * 数据点单位 电压V  电流 I等等
     */
    @Column(name = "point_unit")
    private String pointUnit;


    /**
     * 统计日期（年-月-日）
     */
    @Column(name = "count_date")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date countDate;


    /**
     * hh:mm (00:15)
     */
    @Column(name = "count_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm:ss")
    private Date countTime;


    /**
     * yyy-mm-dd hh:mm:ss
     */
    @Column(name = "count_data_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date countDataTime;

    /**
     * 时间范围 例如00:00-00:15点
     */
    @Column(name = "time_scope")
    private String timeScope;

    /**
     * ts 消息时间戳 毫秒数转为 年月日 时分秒 毫秒 YYY-MM-dd HH:mm:ss.S
     */
    @Column(name = "ts")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss.S")
    private Date ts;

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


    public IotTsKvMeteringDevice96() {
    }

    public String getIdentify() {
        return getNodeId() + TimeUtil.toYmdStr(getCountDate());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProvinceRegionId() { return provinceRegionId; }
    public void setProvinceRegionId(String provinceRegionId) { this.provinceRegionId = provinceRegionId; }
    public String getCityRegionId() { return cityRegionId; }
    public void setCityRegionId(String cityRegionId) { this.cityRegionId = cityRegionId; }
    public String getCountyRegionId() { return countyRegionId; }
    public void setCountyRegionId(String countyRegionId) { this.countyRegionId = countyRegionId; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public String getNodePostType() { return nodePostType; }
    public void setNodePostType(String nodePostType) { this.nodePostType = nodePostType; }
    public String getSystemId() { return systemId; }
    public void setSystemId(String systemId) { this.systemId = systemId; }
    public String getDeviceSn() { return deviceSn; }
    public void setDeviceSn(String deviceSn) { this.deviceSn = deviceSn; }
    public String getPointSn() { return pointSn; }
    public void setPointSn(String pointSn) { this.pointSn = pointSn; }
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }
    public Double getHTotalUse() { return hTotalUse; }
    public void setHTotalUse(Double hTotalUse) { this.hTotalUse = hTotalUse; }
    public Double getColValue() { return colValue; }
    public void setColValue(Double colValue) { this.colValue = colValue; }
    public Double getAvgValue() { return avgValue; }
    public void setAvgValue(Double avgValue) { this.avgValue = avgValue; }
    public Double getMinValue() { return minValue; }
    public void setMinValue(Double minValue) { this.minValue = minValue; }
    public Double getMaxValue() { return maxValue; }
    public void setMaxValue(Double maxValue) { this.maxValue = maxValue; }
    public Double getFirstValue() { return firstValue; }
    public void setFirstValue(Double firstValue) { this.firstValue = firstValue; }
    public Double getLastValue() { return lastValue; }
    public void setLastValue(Double lastValue) { this.lastValue = lastValue; }
    public Double getTotalPowerEnergy() { return totalPowerEnergy; }
    public void setTotalPowerEnergy(Double totalPowerEnergy) { this.totalPowerEnergy = totalPowerEnergy; }
    public Double getTotalPowerEnergyBase() { return totalPowerEnergyBase; }
    public void setTotalPowerEnergyBase(Double totalPowerEnergyBase) { this.totalPowerEnergyBase = totalPowerEnergyBase; }
    public String getPointDesc() { return pointDesc; }
    public void setPointDesc(String pointDesc) { this.pointDesc = pointDesc; }
    public String getPointUnit() { return pointUnit; }
    public void setPointUnit(String pointUnit) { this.pointUnit = pointUnit; }
    public Date getCountDate() { return countDate; }
    public void setCountDate(Date countDate) { this.countDate = countDate; }
    public Date getCountTime() { return countTime; }
    public void setCountTime(Date countTime) { this.countTime = countTime; }
    public Date getCountDataTime() { return countDataTime; }
    public String getTimeScope() { return timeScope; }

}
