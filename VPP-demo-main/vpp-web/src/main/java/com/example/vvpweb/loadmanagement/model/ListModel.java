package com.example.vvpweb.loadmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ListModel implements Serializable {

    private List<SubModel> subModelList;

    @ApiModelProperty(value = "开始时间 年-月-日", name = "startTime", required = true)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(value = "结束时间 年-月-日", name = "endTime", required = true)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endTime;
}
