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

}
