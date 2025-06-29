package com.example.vvpservice.exceloutput.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class BiStorageEnergyResourcesExport {

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
     * 电站容量
     */
    @ExcelProperty(value = "电站容量")
    private Double capacity;

    /**
     * 电站功率
     */
    @ExcelProperty(value = "电站功率")
    private Double load;

    /**
     * soc
     */
    @ExcelProperty(value = "SOC")
    private Double soc;

    /**
     * soh
     */
    @ExcelProperty(value = "SOH")
    private Double soh;

    /**
     * 当前可充容量kwh
     */
    @ExcelProperty(value = "当前可充容量")
    private Double inEnergy;

    /**
     * 当前可放容量kwh
     */
    @ExcelProperty(value = "当前可放容量")
    private Double outEnergy;

    /**
     * 最大可充功率kw
     */
    @ExcelProperty(value = "最大可充功率")
    private Double maxInLoad;

    /**
     * 最大可放功率kw
     */
    @ExcelProperty(value = "最大可放功率")
    private Double maxOutLoad;

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

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Double getLoad() {
        return load;
    }

    public void setLoad(Double load) {
        this.load = load;
    }

    public Double getSoc() {
        return soc;
    }

    public void setSoc(Double soc) {
        this.soc = soc;
    }

    public Double getSoh() {
        return soh;
    }

    public void setSoh(Double soh) {
        this.soh = soh;
    }

    public Double getInEnergy() {
        return inEnergy;
    }

    public void setInEnergy(Double inEnergy) {
        this.inEnergy = inEnergy;
    }

    public Double getOutEnergy() {
        return outEnergy;
    }

    public void setOutEnergy(Double outEnergy) {
        this.outEnergy = outEnergy;
    }

    public Double getMaxInLoad() {
        return maxInLoad;
    }

    public void setMaxInLoad(Double maxInLoad) {
        this.maxInLoad = maxInLoad;
    }

    public Double getMaxOutLoad() {
        return maxOutLoad;
    }

    public void setMaxOutLoad(Double maxOutLoad) {
        this.maxOutLoad = maxOutLoad;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
