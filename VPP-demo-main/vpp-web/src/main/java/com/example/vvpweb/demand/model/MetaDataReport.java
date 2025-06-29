package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MetaDataReport implements Serializable {
    private static final long serialVersionUID = 1422441173508930015L;

    @ApiModelProperty("报告创建的时间")
    private String createdDateTime;

    @ApiModelProperty("报告测点描述信息")
    private ReportDescription reportDescription;
}
