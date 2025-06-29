package com.example.vvpweb.profitmanagement.model;

public enum ProfitType {
    ALL_PROFIT(0, "全部"),
    CHANGE_PROFIT(1, "削峰填谷"),
    PV_PROFIT(2, "光伏收益");

    private final int code;
    private final String name;

    ProfitType(int code, String name) {
        this.name = name;
        this.code = code;
    }

    public static ProfitType getProfitType(int code) {

        for (ProfitType e : ProfitType.values()) {
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