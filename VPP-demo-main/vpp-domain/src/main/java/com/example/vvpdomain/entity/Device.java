package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zph
 * @description 设备表
 * @date 2022-07-01
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "device")
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * devicesn
     */
    @Column(name = "device_id")
    private String deviceId;

    /**
     * 设备序列号
     */
    @Column(name = "device_sn")
    private String deviceSn;

    /**
     * 设备名称
     */
    @Column(name = "device_name")
    private String deviceName;

    /**
     * 设备型号
     */
    @Column(name = "device_model")
    private String deviceModel;

    /**
     * 设备协议类型
     */
    @Column(name = "device_protocol")
    private String deviceProtocol;

    /**
     * 网关id
     */
    @Column(name = "mec_id")
    private String mecId;
    /**
     * 设备品牌
     */
    @Column(name = "device_brand")
    private String deviceBrand;

    /**
     * 备注
     */
    @Column(name = "device_label")
    private String deviceLabel;
    /**
     * 额定功率
     */
    @Column(name = "device_rated_power")
    private double deviceRatedPower;
    /**
     * 是否在线
     */
    @Column(name = "online")
    private Boolean online;


    /**
     * 所属设备点位id
     *
     * @OneToMany注解"一对多"关系中'一'方的实体类属性(该属性是一个集合对象)， targetEntity注解关联的实体类类型，mappedBy注解另一方实体类中本实体类的属性名称
     * 多方的加载方式改为立刻加载,而一对多中的一方改为懒加载即可.
     */
    @OneToMany(mappedBy = "device",
            targetEntity = DevicePoint.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    private List<DevicePoint> devicePointList;


    /**
     * 所属节点id
     */
    @ManyToOne(targetEntity = Node.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE)
    @JoinColumn(name = "node_id", referencedColumnName = "node_id")
    private Node node;

    /**
     * 所属系统标签id
     */
    @OneToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "system_id", referencedColumnName = "system_id")
    private SysDictType systemType;


    /**
     * 参数键名
     */
    @Column(name = "config_key")
    private String configKey;


    /**
     * 负荷类型：-       ，
     * 空调（air_conditioning），
     * 充电桩（charging_piles），
     * 照明（lighting），
     * 其它（others）
     */
    @Column(name = "load_type")
    private String loadType;

    /**
     符合性质：    -    ，
     可调节负荷(adjustable_load)，
     可转移负荷(transferable_load)，
     可中断负荷(interruptible_load)，
     其它负荷(other_loads)
     */
    @Column(name = "load_properties")
    private String loadProperties;


    @Column(name = "mec_online")
    private Boolean mecOnline;

    @Column(name = "mec_name")
    private String mecName;

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

    public Device() {
    }

    @Override
    public String toString() {
        return String.format("Device [id=%s, deviceName=%s, Device detail=%s]", deviceId, deviceName, node.getNodeId());
    }

    public String getDeviceBrand() { return deviceBrand; }
    public String getDeviceModel() { return deviceModel; }
    public List<DevicePoint> getDevicePointList() { return devicePointList; }
    public void setDeviceBrand(String deviceBrand) { this.deviceBrand = deviceBrand; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }
    public void setDevicePointList(List<DevicePoint> devicePointList) { this.devicePointList = devicePointList; }
    public void setLoadType(String loadType) { this.loadType = loadType; }
    public void setLoadProperties(String loadProperties) { this.loadProperties = loadProperties; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }
    public void setDeviceLabel(String deviceLabel) { this.deviceLabel = deviceLabel; }
    public void setDeviceRatedPower(double deviceRatedPower) { this.deviceRatedPower = deviceRatedPower; }
    public void setOnline(Boolean online) { this.online = online; }
    public void setNode(Node node) { this.node = node; }
    public void setSystemType(SysDictType systemType) { this.systemType = systemType; }
    public void setMecOnline(Boolean mecOnline) { this.mecOnline = mecOnline; }
    public void setMecName(String mecName) { this.mecName = mecName; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public void setDeviceSn(String deviceSn) { this.deviceSn = deviceSn; }
    public String getDeviceProtocol() { return deviceProtocol; }
    public String getMecId() { return mecId; }
    public String getDeviceName() { return deviceName; }
    public String getDeviceSn() { return deviceSn; }
    public String getConfigKey() { return configKey; }
    public String getDeviceLabel() { return deviceLabel; }
    public double getDeviceRatedPower() { return deviceRatedPower; }
    public Boolean getOnline() { return online; }
    public Node getNode() { return node; }
    public SysDictType getSystemType() { return systemType; }
    public String getLoadType() { return loadType; }
    public String getLoadProperties() { return loadProperties; }
    public Boolean getMecOnline() { return mecOnline; }
    public String getMecName() { return mecName; }
    public Date getCreatedTime() { return createdTime; }
    public Date getUpdateTime() { return updateTime; }
    public String getDeviceId() { return deviceId; }
    public void setMecId(String mecId) { this.mecId = mecId; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public void setDeviceProtocol(String deviceProtocol) { this.deviceProtocol = deviceProtocol; }
}