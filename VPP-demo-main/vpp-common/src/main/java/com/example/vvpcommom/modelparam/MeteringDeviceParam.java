package com.example.vvpcommom.modelparam;

import lombok.Data;

import java.io.Serializable;

@Data
public class MeteringDeviceParam implements Serializable {
    /**
     * 电池的健康状态
     */
    public static String soh = "soh";
    /**
     * 电池的荷电状态
     */
    public static String soc = "soc";
    /**
     * 充电 总电量
     */
    public static String in_energy = "in_energy";
    /**
     * 放电 总电量
     */
    public static String out_energy = "out_energy";
    /**
     * 能耗，有功电能
     */
    public static String energy = "energy";
    /**
     * 负载，功率
     */
    public static String load = "load";
}
