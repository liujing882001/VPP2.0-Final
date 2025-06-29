package com.example.vvpcommom;

/**
 * @author Zhaoph
 * 报警状态
 */

public enum AlarmStatusEnum {


    SUCCESS(0, "已恢复"),
    FAIL(1, "报警中"),
    LOGIN_ERROR(2, "处理中");

    /**
     * 报警状态
     */
    private Integer id;
    /**
     * 报警状态0 已恢复 1报警中 2 处理中
     */
    private String desc;

    AlarmStatusEnum(Integer id, String desc) {
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
