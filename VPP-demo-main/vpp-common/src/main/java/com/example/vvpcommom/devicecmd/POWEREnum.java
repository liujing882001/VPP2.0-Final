package com.example.vvpcommom.devicecmd;

/**
 * 开关状态
 */
public enum POWEREnum {

    POWER_ON(0, "开"),
    POWER_OFF(1, "关");

    /**
     * 模式
     */
    private Integer id;
    /**
     * 开关状态描述
     */
    private String desc;

    POWEREnum(Integer id, String desc) {
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
