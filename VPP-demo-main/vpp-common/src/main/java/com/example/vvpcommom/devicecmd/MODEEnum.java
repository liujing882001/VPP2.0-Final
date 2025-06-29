package com.example.vvpcommom.devicecmd;

/**
 * 模式 制冷  制热
 */
public enum MODEEnum {

    MODE_COOL(0, "制冷"),
    MODE_DRY(1, "制热");

    /**
     * 模式
     */
    private Integer id;
    /**
     * 模式描述 0 制冷 1制热
     */
    private String desc;

    MODEEnum(Integer id, String desc) {
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
