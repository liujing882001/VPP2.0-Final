package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UnitSymbol {
    A("用安培表示的电流"),
    V("用伏特表示的电压"),
    W("用瓦特表示的有功功率");

    private String desc;
}
