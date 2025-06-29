package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DemandRespTaskReq {

    /**
     * 响应任务id
     */
    @ApiModelProperty("响应任务id")
    private String respId;

    /**
     * 响应时段(开始)
     */
    @ApiModelProperty("响应开始时段,格式yyyy-MM-dd HH:mm")
    @NotBlank(message = "响应开始时段不能为空")
    private String rsTime;

    /**
     * 响应时段(结束)
     */
    @ApiModelProperty("响应结束时段,格式yyyy-MM-dd HH:mm")
    @NotBlank(message = "响应结束时段不能为空")
    private String reTime;

    /**
     * 响应日期
     */
    @ApiModelProperty("响应日期,格式yyyy-MM-dd")
    @NotBlank(message = "响应日期不能为空")
    private String rsDate;

    /**
     * 任务编码
     */
    @ApiModelProperty("任务编码")
    @NotNull(message = "任务编码不能为空")
    private Long taskCode;

    /**
     * 响应负荷，单位（kW）
     */
    @ApiModelProperty("响应负荷,单位（kW）")
    private Double respLoad;

    /**
     * 响应类型(1-削峰响应 2-填谷响应)
     */
    @ApiModelProperty("响应类型(1-削峰响应 2-填谷响应)")
    @NotNull(message = "响应类型不能为空")
    private Integer respType;

    /**
     * 响应补贴（元/kWh）
     */
    @ApiModelProperty("响应补贴（元/kWh）")
    private Double respSubsidy;

    /**
     * 响应级别(1-日前 2-小时 3-分钟 4-秒级)
     * 表字段： demand_resp_task.resp_level
     */
    @ApiModelProperty("响应级别(1-日前 2-小时 3-分钟 4-秒级)")
    @NotNull(message = "响应级别不能为空")
    private Integer respLevel;

    /**
     * 反馈截止时间
     */
    @ApiModelProperty("反馈截止时间,格式yyyy-MM-dd HH:mm:ss")
    private String feedbackTime;

    /**
     * 参与节点的户号
     */
    @ApiModelProperty("节点对应的户号(多个逗号隔开)")
    private String inviteRange;

    /**
     * 电网类型(1-国网 2-南网)
     */
//    @ApiModelProperty("电网类型(1-国网 2-南网)")
//    @NotNull(message = "电网类型不能为空")
//    private Integer powerGrid;

}