package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Data
public class ReportSpecifier implements Serializable {
    private static final long serialVersionUID = -5466983228430354518L;

    @ApiModelProperty("报告类型")
    private ReportType reportType;

    @ApiModelProperty("开始发送报告的时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDateTime;

    @ApiModelProperty("取消报告的时间,为空表示报告一直不取消")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDateTime;

    @ApiModelProperty("报告循环周期")
    private Duration backDuration;

    @ApiModelProperty("曲线数据之间的采样间隔")
    private Duration period;

    @ApiModelProperty("报告包含的rID列")
    private List<Integer> points;

}
