package com.example.vvpweb.electricitybill.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ElectricityBillRequest {

    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date date;

    private DateType type;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DateType getType() {
        return type;
    }

    public void setType(DateType type) {
        this.type = type;
    }

    public static enum DateType{
        YEAR,
        MONTH,
        DAY;
    }
}
