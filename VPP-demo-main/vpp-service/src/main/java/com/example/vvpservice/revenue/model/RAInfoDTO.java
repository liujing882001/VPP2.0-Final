package com.example.vvpservice.revenue.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RAInfoDTO {
    private String nodeId;
    private String time;
    private BigDecimal Dynamic;
    private BigDecimal Fixed;

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public BigDecimal getDynamic() { return Dynamic; }
    public void setDynamic(BigDecimal Dynamic) { this.Dynamic = Dynamic; }
    public BigDecimal getFixed() { return Fixed; }
    public void setFixed(BigDecimal Fixed) { this.Fixed = Fixed; }
}
