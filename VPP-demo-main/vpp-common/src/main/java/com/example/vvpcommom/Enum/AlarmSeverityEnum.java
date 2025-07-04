package com.example.vvpcommom.Enum;

/**
 * @author Zhaoph
 * 严重程度
 */
public enum AlarmSeverityEnum {

        SUCCESS(0, "紧急"),
        FAIL(1, "重要"),
        LOGIN_ERROR(2, "次要"),
        UNKNOWN_ERROR(3, "提示"),
        OTHER(4, "其它");

/*
    FAIL(1, "故障"),
    LOGIN_ERROR(2, "警告"),
    UNKNOWN_ERROR(3, "提示"),
    ;
*/

    /**
     * 严重程度
     */
    private Integer id;
    /**
     * 严重程度，1 故障 2 警告 3 提示
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
