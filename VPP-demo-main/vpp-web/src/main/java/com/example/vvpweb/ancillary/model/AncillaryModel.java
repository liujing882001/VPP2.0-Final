package com.example.vvpweb.ancillary.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author maoyating
 */
@Data
public class AncillaryModel implements Serializable {

    /**
     * 每页大小
     */
    @ApiModelProperty("每页大小")
    private int pageSize;
    /**
     * 当前页为第几页 默认 1开始
     */
    @ApiModelProperty("第几页，默认从1开始")
    private int number;

    /**
     * 状态（1-未开始 2-执行中）
     */
    @ApiModelProperty("状态（1-未开始 2-执行中）,仅实时监测接口使用该字段")
    private Integer status;

    @ApiModelProperty("开始日期，仅历史查询接口使用该字段,yyyy-MM-dd")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty("结束日期，仅历史查询接口使用该字段,yyyy-MM-dd")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endDate;

}
