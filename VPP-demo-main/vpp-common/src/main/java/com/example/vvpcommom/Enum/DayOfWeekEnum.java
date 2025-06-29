package com.example.vvpcommom.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DayOfWeekEnum {
    //周几（1-周一 2-周二 3-周三 4-周四 5-周五 6-周六 7-周日）

    monday(1, "周一"),
    tuesday(2, "周二"),
    wednesday(3, "周三"),
    thursday(4, "周四"),
    friday(5, "周五"),
    saturday(6, "周六"),
    sunday(7, "周日");

    private Integer id;
    private String name;

    // 普通方法
    public static String getName(int id) {
        for (DayOfWeekEnum c : DayOfWeekEnum.values()) {
            if (c.getId() == id) {
                return c.name;
            }
        }
        return null;
    }
}
