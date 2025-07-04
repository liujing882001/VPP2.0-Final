package com.example.vvpservice.iotdata.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IotDevicePointDataView {
    @ApiModelProperty(value = "设备名称", name = "deviceName", required = true)
    private String deviceName;
    @ApiModelProperty(value = "节点名称", name = "nodeName", required = true)
    private String nodeName;
    @ApiModelProperty(value = "系统名称", name = "systemName", required = true)
    private String systemName;
    @ApiModelProperty(value = "点位名称", name = "pointName", required = true)
    private String pointName;
    @ApiModelProperty(value = "点位值", name = "pointValue", required = true)
    private String pointValue;
    @ApiModelProperty(value = "点位编码", name = "pointSn", required = true)
    private String pointSn;
    @ApiModelProperty(value = "时间", name = "ts", required = true)
    private String ts;
    @ApiModelProperty(value = "数据点单位", name = "pointUnit", required = true)
    private String pointUnit;

    @ApiModelProperty(value = "参数键名", name = "pointUnit", required = true)
    private String pointDesc;

    @ApiModelProperty(value = "省", name = "provinceRegionName", required = true)
    private String provinceRegionName;
    @ApiModelProperty(value = "市", name = "cityRegionName", required = true)
    private String cityRegionName;
    @ApiModelProperty(value = "县/区", name = "countyRegionName", required = true)
    private String countyRegionName;
    @ApiModelProperty(value = "设备是否在线", name = "online", required = true)
    private Boolean online;

    /**
     * 负荷类型：-       ，
     * 空调（air_conditioning），
     * 充电桩（charging_piles），
     * 照明（lighting），
     * 其它（others）
     */
    @ApiModelProperty(value = "负荷类型", name = "countyRegionName", required = true)
    private String loadType;
    /**
     符合性质：    -    ，
     可调节负荷(adjustable_load)，
     可转移负荷(transferable_load)，
     可中断负荷(interruptible_load)，
     其它负荷(other_loads)
     */
    @ApiModelProperty(value = "符合性质", name = "countyRegionName", required = true)
    private String loadProperties;

    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public void setPointName(String pointName) { this.pointName = pointName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }
    public void setPointSn(String pointSn) { this.pointSn = pointSn; }
    public void setPointUnit(String pointUnit) { this.pointUnit = pointUnit; }
    public void setProvinceRegionName(String provinceRegionName) { this.provinceRegionName = provinceRegionName; }
    public void setCountyRegionName(String countyRegionName) { this.countyRegionName = countyRegionName; }
    public void setCityRegionName(String cityRegionName) { this.cityRegionName = cityRegionName; }
    public void setPointDesc(String pointDesc) { this.pointDesc = pointDesc; }
    public void setLoadType(String loadType) { this.loadType = loadType; }
    public void setLoadProperties(String loadProperties) { this.loadProperties = loadProperties; }
}
