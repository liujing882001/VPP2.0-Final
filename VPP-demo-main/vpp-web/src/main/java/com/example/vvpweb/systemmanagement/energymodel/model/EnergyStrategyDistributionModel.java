package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class EnergyStrategyDistributionModel {
    String nodeId;
    String systemId;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    String strategy;
    double power;
    int index;
    public EnergyStrategyDistributionModel() {}
    public EnergyStrategyDistributionModel(String nodeId, String systemId, String startDate, String endDate, String startTime, String endTime, String strategy, double power,int index) {
        this.nodeId = nodeId;
        this.systemId = systemId;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.startDateTime = LocalDateTime.parse(startDate + " " + startTime, dateFormatter);
        this.endDateTime = LocalDateTime.parse(endDate + " " + endTime, dateFormatter);
        this.strategy = strategy;
        this.power = power;
        this.index = index;
    }

    public EnergyStrategyDistributionModel(StrategyDistributionModel model) {
        this.nodeId = model.getNodeId();
        this.systemId = model.getSystemId();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.startDateTime = LocalDateTime.parse(model.getStartDate() + " " + model.getStartTime(), dateFormatter);
        this.endDateTime = LocalDateTime.parse(model.getEndDate() + " " + model.getEndTime(), dateFormatter);
        this.strategy = model.getStrategy();
        this.power = model.getPower();
        this.index = model.getIndex();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnergyStrategyDistributionModel that = (EnergyStrategyDistributionModel) o;
        return index == that.index &&
                Double.compare(that.power, power) == 0 &&
                Objects.equals(nodeId, that.nodeId) &&
                Objects.equals(systemId, that.systemId) &&
                Objects.equals(startDateTime, that.startDateTime) &&
                Objects.equals(endDateTime, that.endDateTime) &&
                Objects.equals(strategy, that.strategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, systemId, startDateTime, endDateTime, strategy, power, index);
    }

}
