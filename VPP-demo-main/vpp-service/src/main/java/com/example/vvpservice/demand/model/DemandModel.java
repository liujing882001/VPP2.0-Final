package com.example.vvpservice.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandModel {

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date rsDate;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date rsTime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reTime;
    private String nodeId;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    // Manual setters to ensure compilation
    public void setRsDate(Date rsDate) { this.rsDate = rsDate; }
    public void setRsTime(Date rsTime) { this.rsTime = rsTime; }
    public void setReTime(Date reTime) { this.reTime = reTime; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    // Manual getters to ensure compilation
    public String getNodeId() { return nodeId; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
}
