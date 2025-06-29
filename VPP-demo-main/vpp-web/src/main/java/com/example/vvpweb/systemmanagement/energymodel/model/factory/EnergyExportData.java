package com.example.vvpweb.systemmanagement.energymodel.model.factory;

import com.alibaba.excel.annotation.ExcelProperty;
import com.example.vvpdomain.entity.BiStorageEnergyLog;
import com.example.vvpdomain.entity.CfgStorageEnergyStrategy;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class EnergyExportData {

    @ExcelProperty(value = "timeStamp", index = 0)
    private Date timeStamp;

    @ExcelProperty(value = "电价", index = 1)
    private BigDecimal price;

    @ExcelProperty(value = "属性", index = 2)
    private String property;

    @ExcelProperty(value = "SOC", index = 3)
    private Double soc;

    @ExcelProperty(value = "储能系统实时电量(kWh)", index = 4)
    private Double battery;

    public EnergyExportData() {
    }
    public EnergyExportData(BiStorageEnergyLog data, CfgStorageEnergyStrategy cfg) {
        this.timeStamp = data.getCreatedTime();
        this.soc = data.getSoc();
        this.battery = data.getOutCapacity();
        this.price = cfg.getPriceHour();
        this.property = cfg.getProperty();
    }
}
