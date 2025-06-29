package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author maoyating
 */
@Data
public class CaTradeModel {

    /**
     * 每页大小
     */
    @ApiModelProperty("每页大小")
    @NotNull(message = "每页大小不能为空")
    private int pageSize;
    /**
     * 当前页为第几页 默认 1开始
     */
    @ApiModelProperty("第几页，默认从1开始")
    @NotNull(message = "页码不能为空")
    private int number;

    @ApiModelProperty("交易类型(1-碳交易 2-绿电交易 3-绿证交易)")
    @NotNull(message = "交易类型不能为空")
    private Integer tradeType;

    @ApiModelProperty("节点Id")
    @NotNull(message = "节点不能为空")
    private String nodeId;

}
