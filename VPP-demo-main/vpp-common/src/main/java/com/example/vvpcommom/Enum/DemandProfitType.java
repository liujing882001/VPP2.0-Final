package com.example.vvpcommom.Enum;

public enum DemandProfitType {
    USER_ALL(0, "全部"),
    CONSUMER(1, "电力用户"),
    LOAD_INTEGRATOR (2, "虚拟电厂运营商"),

    DATE_DAY (1, "日"),

    DATE_MONTH (2, "月"),

    DATE_YEAR (3, "年");

    private final int code;
    private final String name;

    DemandProfitType(int code, String name) {
        this.name = name;
        this.code = code;
    }

    public static DemandProfitType getProfitType(int code) {

        for (DemandProfitType e : DemandProfitType.values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}