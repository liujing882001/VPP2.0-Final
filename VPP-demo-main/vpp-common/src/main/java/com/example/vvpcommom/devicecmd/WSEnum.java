package com.example.vvpcommom.devicecmd;

/**
 * 风速 一档 二挡  三挡
 */
public enum WSEnum {

    WS_AUTO(0, "自动"),
    WS_LOW(1, "一档"),
    WS_MEDIUM(2, "二挡"),
    WS_HIGH(3, " 三挡"),
    WS_MAX(4, "最大");

    /**
     * 风速
     */
    private Integer id;
    /**
     * 风速描述
     */
    private String desc;

    WSEnum(Integer id, String desc) {
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
