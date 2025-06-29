package com.example.vvpcommom.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalendarTypeEnum {
    //1-工作日 2-非工作日 3-删除计算日 4-元旦 5-春节 6-清明节 7-劳动节 8-端午节 9-中秋节 10-国庆节

    weekday(1, "工作日"),
    nonWorkingDay(2, "非工作日"),
    delete(3, "删除计算日"),
    newYear(4, "元旦"),
    springFestival(5, "春节"),
    tombSweeping(6, "清明节"),
    laborDay(7, "劳动节"),
    dragonBoatFestival(8, "端午节"),
    midAutumnFestival(9, "中秋节"),
    nationalDay(10, "国庆节");


    private Integer id;
    private String name;


    // 普通方法
    public static String getName(int id) {
        for (CalendarTypeEnum c : CalendarTypeEnum.values()) {
            if (c.getId() == id) {
                return c.name;
            }
        }
        return null;
    }
}
