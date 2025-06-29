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


}
