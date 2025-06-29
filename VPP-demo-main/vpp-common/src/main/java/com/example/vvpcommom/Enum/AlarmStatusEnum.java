package com.example.vvpcommom.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Zhaoph
 * 报警状态
 */
@Getter
@AllArgsConstructor
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
}
