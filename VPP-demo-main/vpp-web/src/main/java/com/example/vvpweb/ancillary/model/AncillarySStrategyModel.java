package com.example.vvpweb.ancillary.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author maoyating
 */
@Data
public class AncillarySStrategyModel implements Serializable {

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
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /**
     * 辅助服务id
     */
    @ApiModelProperty("辅助服务id")
    @NotBlank(message = "辅助服务id不能为空")
    private String asId;

}
