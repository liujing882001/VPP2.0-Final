package com.example.vvpweb.loadmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AiPageModel implements Serializable {

    @ApiModelProperty("节点id")
    private String nodeId;

    @ApiModelProperty("系统id")
    private String systemId;

    @ApiModelProperty("开始时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date startTs;

    @ApiModelProperty("结束时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endTs;

    @ApiModelProperty("每页大小")
    private int pageSize;

    @ApiModelProperty("当前页为第几页 默认 1开始")
    private int number;
}
