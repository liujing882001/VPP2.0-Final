package com.example.vvpservice.exceloutput.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class BiPvResourcesExport {

    /**
     * 电站状态
     */
    @ExcelProperty(value = "电站状态")
    private Boolean online;

    /**
     * 电站名称
     */
    @ExcelProperty(value = "电站名称")
    private String nodeName;

    /**
     * 当日发电量
     */
    @ExcelProperty(value = "当日发电量")
    private Double nowEnergy;

    /**
     * 累计发电量
     */
    @ExcelProperty(value = "累计发电量")
    private Double energy;

    /**
     * 实际发电功率
     */
    @ExcelProperty(value = "实际发电功率")
    private Double load;

    /**
     *
     */
    @ExcelProperty(value = "装机容量")
    private Double capacity;

    /**
     * 消息时间戳 毫秒数转为 年月日 时分秒
     */
    @ExcelProperty(value = "消息时间戳")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ts;

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Double getNowEnergy() {
        return nowEnergy;
    }

    public void setNowEnergy(Double nowEnergy) {
        this.nowEnergy = nowEnergy;
    }

    public Double getEnergy() {
        return energy;
    }

    public void setEnergy(Double energy) {
        this.energy = energy;
    }

    public Double getLoad() {
        return load;
    }

    public void setLoad(Double load) {
        this.load = load;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
