package com.example.vvpcommom.devicecmd;

/**
 * 温度  16-26℃
 */

public enum TEMPEnum {

    TEMP_16(16, "16℃"),
    TEMP_17(17, "17℃"),
    TEMP_18(18, "18℃"),
    TEMP_19(19, "19℃"),
    TEMP_20(20, "20℃"),
    TEMP_21(21, "21℃"),
    TEMP_22(22, "22℃"),
    TEMP_23(23, "23℃"),
    TEMP_24(24, "24℃"),
    TEMP_25(25, "25℃"),
    TEMP_26(26, "26℃");

    /**
     * 温度
     */
    private Integer id;
    /**
     * 温度描述
     */
    private String desc;

    TEMPEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }
}
