package com.example.vvpweb.iotdata.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AlarmModel implements Serializable {
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 点位名称
     */
    private String pointName;
    /**
     * 严重程度，等级 0 紧急1 重要2 次要3 提示
     */
    private Integer severity;

    /**
     * 报警状态0 已恢复 1报警中 2 处理中
     */
    private Integer status;

    /**
     * 报警附加信息
     */
    private String additionalInfo;

    /**
     * 报警开始时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTs;


}
