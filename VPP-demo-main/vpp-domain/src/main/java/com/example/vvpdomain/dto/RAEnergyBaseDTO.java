package com.example.vvpdomain.dto;

import lombok.Data;


@Data
public class RAEnergyBaseDTO {
    private String nodeId;
    private Double storageEnergyCapacity;
    private Double maxChargePercent;
    private Double minDischargePercent;
    public RAEnergyBaseDTO() {
    }

    // 带参构造函数
    public RAEnergyBaseDTO(String nodeId, Double storageEnergyCapacity, Double maxChargePercent, Double minDischargePercent) {
        this.nodeId = nodeId;
        this.storageEnergyCapacity = storageEnergyCapacity;
        this.maxChargePercent = maxChargePercent;
        this.minDischargePercent = minDischargePercent;

    }
}
