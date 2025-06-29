package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class StorageEnergyStrategyModel implements Serializable {

    @ApiModelProperty(value = "节点id", required = false)
    private String nodeId;
    /**
     * 系统id
     */
    @ApiModelProperty(value = "系统id", required = false)

    private String systemId;

    /**
     * 时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8")
    @ApiModelProperty(value = "时间", required = false)
    private Date ts;

    /**
     * 每页大小
     */
    @ApiModelProperty(value = "每页大小", required = false)
    private int pageSize;
    /**
     * 当前页为第几页 默认 1开始
     */
    @ApiModelProperty(value = "当前页为第几页", required = false)
    private int number;

    @ApiModelProperty(value = "时间级别", required = false)
    private String timeLevel;
    // 假设这是一个接收日期的 setter 方法
    public void setTs(String dateString) {
        if (dateString != null) {
            try {
                // 创建 SimpleDateFormat 对象来解析日期字符串
                SimpleDateFormat sdf;
                if (dateString.length() == 7) { // yyyy-MM
                    sdf = new SimpleDateFormat("yyyy-MM");
                } else { // yyyy-MM-dd
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                }
                // 解析日期字符串并赋值给 ts
                this.ts = sdf.parse(dateString);
            } catch (ParseException e) {
                // 如果解析日期字符串失败，可以根据需要进行处理
                e.printStackTrace();
            }
        } else {
            this.ts = null; // 如果传入的日期字符串为空，将 ts 设为 null
        }
    }
}
