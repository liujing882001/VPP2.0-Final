package com.example.kafka.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class DemandResponseDayInfoModel implements Serializable {
    private String id;
    private int totalSize;
    private List<DayInfoModel> data = new ArrayList<>();

}