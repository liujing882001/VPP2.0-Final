package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UnitMultiplier {
    m(-3,"千分之一"),
    c(-2,"百分之一"),
    k(-2,"千");

    private Integer value;
    private String desc;
}
