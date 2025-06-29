package com.example.vvpweb.systemmanagement.stationnode.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProperty {
    private String property;
    private BigDecimal price;
}
