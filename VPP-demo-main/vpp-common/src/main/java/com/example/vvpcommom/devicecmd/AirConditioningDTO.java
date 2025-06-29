package com.example.vvpcommom.devicecmd;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 空调设置不同的分组，并对分组空调设备进行统一的启停、温度、模式、风速等设置
 */
@Data
public class AirConditioningDTO implements Serializable {

    /**
     * 开关状态 开启 关闭
     */
    @ApiModelProperty(value = "开关状态 开启 关闭", name = "power", required = true)
    private POWEREnum power;
    /**
     * 温度  16-26℃
     */
    @ApiModelProperty(value = "温度", name = "temp", required = true)
    private TEMPEnum temp;
    /**
     * 模式 制冷  制热
     */
    @ApiModelProperty(value = " 模式 制冷  制热", name = "mode", required = true)
    private MODEEnum mode;
    /**
     * 风速 一档 二挡  三挡
     */
    @ApiModelProperty(value = "风速 一档 二挡  三挡", name = "windSpeed", required = true)
    private WSEnum windSpeed;

}
