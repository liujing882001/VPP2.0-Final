package com.example.vvpweb.presalesmodule.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PriceInfo {
    private String month;
    private List<PriceData> prices = new ArrayList<>();
}
