package com.example.vvpweb.profitmanagement.model;

public enum ProfitUserType {
    LOAD_INTEGRATOR("负荷集成商"),
    CONSUMER("电力用户"),
    USER_ALL("全部");

    private String name;

    private ProfitUserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}