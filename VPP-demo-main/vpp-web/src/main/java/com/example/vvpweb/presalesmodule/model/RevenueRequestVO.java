package com.example.vvpweb.presalesmodule.model;

import com.example.vvpservice.revenue.ExcelData;
import lombok.Data;

import java.util.List;

@Data
public class RevenueRequestVO {
    private String projectId;
    private Double designCapacity;
    private Double designPower;
    private Double usableDepth;
    private Double systemEfficiency;
    private ElectricityQueryYearlyResponse electricity;
    private List<ExcelData> powerList;
}
