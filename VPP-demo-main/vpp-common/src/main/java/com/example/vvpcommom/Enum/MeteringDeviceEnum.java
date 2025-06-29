package com.example.vvpcommom.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Zhaoph
 * 计量设备  参数枚举
 */
@Getter
@AllArgsConstructor
public enum MeteringDeviceEnum {


    soh("soh", "电池的健康状态"),
    soc("soc", "电池的荷电状态"),
    in_energy("in_energy", "充电 总电量"),
    out_energy("out_energy", "放电 总电量"),
    load("load", "负载，功率"),
    energy("energy", "能耗，有功电能");

    private String id;
    private String name;
}
