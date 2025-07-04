package com.example.vvpcommom.devicecmd;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 其他策略：针对照明、基站充电桩等，可对设备的启动/停止进行控制
 */
@Data
public class OtherConditioningDTO implements Serializable {

    /**
     * 开关状态
     */
    @ApiModelProperty(value = "开关状态 开启 关闭", name = "power", required = true)
    private POWEREnum power;

    // Manual getter to ensure compilation
    public POWEREnum getPower() { return power; }
}
