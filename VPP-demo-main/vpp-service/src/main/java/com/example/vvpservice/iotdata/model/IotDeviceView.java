package com.example.vvpservice.iotdata.model;


import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

public class IotDeviceView {

    private String deviceId;

    /**
     * 设备序列号
     */
    private String deviceSn;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 设备品牌
     */
    private String deviceBrand;

    /**
     * 备注
     */
    private String deviceLabel;

    /**
     * 所属节点
     */
    private String nodeName;

    /**
     * 所属系统ID
     */
    private String systemId;

    /**
     * 所属系统
     */
    private String systemName;

    /**
     * 设备类型:计量或者非计量设备
     * other 是 非计量设备
     */
    private String configKey;

    /**
     * 额定功率
     */
    private Double deviceRatedPower;
    /**
     * 是否在线
     */
    private Boolean online;
    /**
     * 负荷类型：-       ，
     * 空调（air_conditioning），
     * 充电桩（charging_piles），
     * 照明（lighting），
     * 其它（others）
     */
    private String loadType;
    /**
     符合性质：    -    ，
     可调节负荷(adjustable_load)，
     可转移负荷(transferable_load)，
     可中断负荷(interruptible_load)，
     其它负荷(other_loads)
     */
    private String loadProperties;

    /**
     * 是否在线
     */
    private Boolean mecOnline;

    private String mecName;

    private List<IotDevicePointView> pointViewList = new ArrayList<>();

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Double getDeviceRatedPower() {
        return deviceRatedPower;
    }

    public void setDeviceRatedPower(Double deviceRatedPower) {
        this.deviceRatedPower = deviceRatedPower;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getDeviceLabel() {
        return deviceLabel;
    }

    public void setDeviceLabel(String deviceLabel) {
        this.deviceLabel = deviceLabel;
    }

    public List<IotDevicePointView> getPointViewList() {
        return pointViewList;
    }

    public void setPointViewList(List<IotDevicePointView> pointViewList) {
        this.pointViewList = pointViewList;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getLoadProperties() {
        return loadProperties;
    }

    public void setLoadProperties(String loadProperties) {
        this.loadProperties = loadProperties;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public Boolean getMecOnline() {
        return mecOnline;
    }

    public void setMecOnline(Boolean mecOnline) {
        this.mecOnline = mecOnline;
    }

    public String getMecName() {
        return mecName;
    }

    public void setMecName(String mecName) {
        this.mecName = mecName;
    }

    public static class IotDevicePointView {
        private String pointId;

        private String pointSn;

        private String pointName;

        private String pointDesc;
        private Boolean online;

        public Boolean getOnline() {
            return online;
        }

        public void setOnline(Boolean online) {
            this.online = online;
        }

        public String getPointId() {
            return pointId;
        }

        public void setPointId(String pointId) {
            this.pointId = pointId;
        }

        public String getPointSn() {
            return pointSn;
        }

        public void setPointSn(String pointSn) {
            this.pointSn = pointSn;
        }

        public String getPointName() {
            return pointName;
        }

        public void setPointName(String pointName) {
            this.pointName = pointName;
        }

        public String getPointDesc() {
            return pointDesc;
        }

        public void setPointDesc(String pointDesc) {
            this.pointDesc = pointDesc;
        }
    }
}
