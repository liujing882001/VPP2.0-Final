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

    public int getTotalSize() { return totalSize; }
    public void setTotalSize(int totalSize) { this.totalSize = totalSize; }
    public List<DayInfoModel> getData() { return data; }
    public void setData(List<DayInfoModel> data) { this.data = data; }
}