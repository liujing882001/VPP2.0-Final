package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DemandBoardModel {

    @ApiModelProperty("节点总数")
    private Integer userNum;

    @ApiModelProperty("参与节点")
    private Integer partakeUser;

    @ApiModelProperty("参与率")
    private Float partakeRate;

    @ApiModelProperty("可调设备总数")
    private Integer deviceNum;

    @ApiModelProperty("在线设备数")
    private Integer onlineDeviceNum;

    @ApiModelProperty("可调总负荷（kW）")
    private Double adjustLoad;

    @ApiModelProperty("实时可调总负荷（kW）")
    private Double monitorLoad;

}
