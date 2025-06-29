package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReportDescription implements Serializable {
    private static final long serialVersionUID = 7208647827988725809L;

    @ApiModelProperty("测点ID")
    private Integer rID;

    @ApiModelProperty("读数类型")
    private ReadingType readingType;

    @ApiModelProperty("度量单位")
    private MetricDescription metric;

    @ApiModelProperty("仅使用endDeviceAsset")
    private EndDeviceAsset reportSubject;

    @ApiModelProperty("仅使用resourceID、meterAsset")
    private Target reportDataSource;

    @ApiModelProperty("采样周期")
    private SamplingRate samplingRate;
}
