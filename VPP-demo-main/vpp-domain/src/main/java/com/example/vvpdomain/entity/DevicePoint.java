package com.example.vvpdomain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zph
 * @description 设备点位信息表
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "device_point")
public class DevicePoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id

    /**
     * 设备下点位id=devicesn+"_"+pointsn
     */
    @Column(name = "point_id")
    private String pointId;

    /**
     * 设备序列号
     * 一对多，多的一方必须维护关系，即不能指定mapped=""
     *
     * @ManyToOne注解"一对多"关系中'多'方的实体类属性(该属性是单个对象)， targetEntity注解关联的实体类类型
     */
    @ManyToOne(targetEntity = Device.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE)
    @JoinColumn(name = "device_id", referencedColumnName = "device_id")
    private Device device;

    /**
     * 设备序列号 全码
     */
    @Column(name = "device_sn")
    private String deviceSn;

    /**
     * 设备参数键名
     */
    @Column(name = "device_config_key")
    private String deviceConfigKey;

    /**
     * 设备模型唯一标识符
     */
    @Column(name = "point_key")
    private String pointKey;

    /**
     * 设备下点位序列号
     */
    @Column(name = "point_sn")
    private String pointSn;

    /**
     * 点位名称
     */
    @Column(name = "point_name")
    private String pointName;

    /**
     * 点位对应设备下点位参数键名
     */
    @Column(name = "point_desc")
    private String pointDesc;

    /**
     * 点位对应设备下点位数据单位
     */
    @Column(name = "point_unit")
    private String pointUnit;

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

    public DevicePoint() {
    }

    @Override
    public String toString() {
        return String.format("DevicePoint [id=%s, pointName=%s, device detail=%s]", pointId, pointName, device.getDeviceId());
    }

    public String getPointId() { return pointId; }
    public String getPointSn() { return pointSn; }
    public String getPointDesc() { return pointDesc; }
    public String getPointName() { return pointName; }
    public String getPointUnit() { return pointUnit; }
    public Device getDevice() { return device; }
    public String getPointKey() { return pointKey; }
    public String getDeviceSn() { return deviceSn; }

    public void setPointId(String pointId) { this.pointId = pointId; }
    public void setDevice(Device device) { this.device = device; }
    public void setDeviceSn(String deviceSn) { this.deviceSn = deviceSn; }
    public void setDeviceConfigKey(String deviceConfigKey) { this.deviceConfigKey = deviceConfigKey; }
    public void setPointKey(String pointKey) { this.pointKey = pointKey; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
    public void setPointSn(String pointSn) { this.pointSn = pointSn; }
    public void setPointName(String pointName) { this.pointName = pointName; }
    public void setPointUnit(String pointUnit) { this.pointUnit = pointUnit; }
    public void setPointDesc(String pointDesc) { this.pointDesc = pointDesc; }
}