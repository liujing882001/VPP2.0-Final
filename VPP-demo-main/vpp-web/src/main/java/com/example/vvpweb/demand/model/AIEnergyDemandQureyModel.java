package com.example.vvpweb.demand.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author maoyating
 * @description 需求响应任务
 * @date 2024-03-07
 */
@Data
public class AIEnergyDemandQureyModel implements Serializable {

    /**
     * 任务状态(-1-全部 1-未开始 2-执行中 3-已完成 4-不参加)
     */
    @ApiModelProperty("任务状态（-1-全部 1-未开始 2-执行中 3-已完成 4-不参加）")
    private Integer status;

    /**
     * 响应类型(-1-全部 1-削峰响应 2-填谷响应)
     */
    @ApiModelProperty("响应类型（-1-全部 1-削峰响应 2-填谷响应）")
    private Integer respType;

}