package com.example.vvpweb.flexibleresourcemanagement.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "GisModel", description = "GIS地图")
public class GisModel implements Serializable {

    @ApiModelProperty(value = "节点id", name = "nodeId", required = true)
    String nodeId;
    @ApiModelProperty(value = "节点名称", name = "nodeName", required = true)
    String nodeName;
    @ApiModelProperty(value = "节点类型", name = "nodePostType", required = true)
    String nodePostType;
    @ApiModelProperty(value = "显示内容", name = "content", required = true)
    String content;
    @ApiModelProperty(value = "经度", name = "longitude", required = true)
    private double longitude;
    @ApiModelProperty(value = "纬度", name = "latitude", required = true)
    private double latitude;
    @ApiModelProperty(value = "类型", name = "stationCategory", required = true)
    private String stationCategory;
    @ApiModelProperty(value = "规划中、建设中、运营中、已关闭", name = "stationState", required = true)
    private String stationState;
    private Double loadKeTiao;
    private Double loadJieRu;


}
