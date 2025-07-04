package com.example.vvpservice.iotdata.model;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false) // 设置 chain = false，避免用户导入有问题
@ExcelIgnoreUnannotated
public class IotDevice implements Serializable {

    @ExcelProperty(value = "deviceId")
    private String deviceId;
    @ExcelProperty(value = "deviceName")
    private String deviceName;
    @ExcelProperty(value = "propId")
    private String propId;
    @ExcelProperty(value = "propName")
    private String propName;
    @ExcelProperty(value = "model")
    private String model;
    @ExcelProperty(value = "brand")
    private String brand;
    @ExcelProperty(value = "location")
    private String location;
    @ExcelProperty(value = "propUnit")
    private String propUnit;
    @ExcelProperty(value = "propKey")
    private String propKey;
    @ExcelProperty(value = "propValue")
    private String propValue;
    @ExcelProperty(value = "deviceType")
    private String deviceProtocol;
    @ExcelProperty(value = "mecId")
    private String mecId;

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getPropId() { return propId; }
    public void setPropId(String propId) { this.propId = propId; }
    public String getPropName() { return propName; }
    public void setPropName(String propName) { this.propName = propName; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getPropUnit() { return propUnit; }
    public void setPropUnit(String propUnit) { this.propUnit = propUnit; }
    public String getPropKey() { return propKey; }
    public void setPropKey(String propKey) { this.propKey = propKey; }
    public String getPropValue() { return propValue; }
    public void setPropValue(String propValue) { this.propValue = propValue; }
    public String getDeviceProtocol() { return deviceProtocol; }
    public void setDeviceProtocol(String deviceProtocol) { this.deviceProtocol = deviceProtocol; }
    public String getMecId() { return mecId; }
    public void setMecId(String mecId) { this.mecId = mecId; }

}
