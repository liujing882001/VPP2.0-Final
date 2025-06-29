package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Target implements Serializable {
    private static final long serialVersionUID = 3092535902338289437L;

    @ApiModelProperty("组ID")
    private List<String> groupID;

    @ApiModelProperty("资源ID")
    private List<String> resourceID;

    @ApiModelProperty("下位节点的ID")
    private List<String> dnID;

    @ApiModelProperty("参与方ID")
    private List<String> partyID;

    @ApiModelProperty("终端设备资产")
    private List<EndDeviceAsset> endDeviceAsset;

    @ApiModelProperty("表计资产")
    private List<MeterAsset> meterAsset;
}
