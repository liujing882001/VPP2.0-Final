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

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public Double getStorageEnergyCapacity() { return storageEnergyCapacity; }
    public void setStorageEnergyCapacity(Double storageEnergyCapacity) { this.storageEnergyCapacity = storageEnergyCapacity; }
    public Double getMaxChargePercent() { return maxChargePercent; }
    public void setMaxChargePercent(Double maxChargePercent) { this.maxChargePercent = maxChargePercent; }
    public Double getMinDischargePercent() { return minDischargePercent; }
    public void setMinDischargePercent(Double minDischargePercent) { this.minDischargePercent = minDischargePercent; }
}
