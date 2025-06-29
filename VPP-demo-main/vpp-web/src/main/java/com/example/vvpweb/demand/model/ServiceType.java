package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceType {
    Register("注册"),
    Event("事件"),
    Report("报告"),
    Opt("参与"),
    Poll("询问");

    private String desc;
}
