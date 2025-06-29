package com.example.vvpcommom;

/**
 * @author Zhaoph
 * 严重程度
 */
public enum AlarmSeverityEnum {

    SUCCESS(0, "紧急"),
    FAIL(1, "重要"),
    LOGIN_ERROR(2, "次要"),
    UNKNOWN_ERROR(3, "提示");

    /**
     * 严重程度
     */
    private Integer id;
    /**
     * 严重程度，等级 0 紧急 1 重要  2 次要  3 提示
     */
    private String desc;

    AlarmSeverityEnum(Integer id, String desc) {
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
