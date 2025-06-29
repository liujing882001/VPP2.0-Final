package com.example.vvpweb.carbon.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maoyating
 */
@Data
public class CaSinkReq implements Serializable {

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

    @ApiModelProperty("类型（lvhuamianji-绿化面积;zhongzhishumu-种植树木 ）")
    private String cType;

    @ApiModelProperty("节点id")
    private String nodeId;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    @ApiModelProperty("开始日期(yyyy-MM)")
    @NotBlank(message = "开始日期不能为空")
    private Date startTime;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    @ApiModelProperty("结束日期(yyyy-MM)")
    @NotBlank(message = "结束日期不能为空")
    private Date endTime;

}
