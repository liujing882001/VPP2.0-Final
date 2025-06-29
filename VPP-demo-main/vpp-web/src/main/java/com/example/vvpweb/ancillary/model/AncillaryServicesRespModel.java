package com.example.vvpweb.ancillary.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author maoyating
 * @description 辅助服务
 * @date 2022-08-09
 */
@Data
public class AncillaryServicesRespModel {
    /**
     * 辅助任务id
     * 表字段： ancillary_services.as_id
     */
    private String asId;

    /**
     * 辅助时段(开始)
     * 表字段： ancillary_services.ass_time
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm:ss")
    private Date assTime;

    /**
     * 辅助时段(结束)
     * 表字段： ancillary_services.ase_time
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm:ss")
    private Date aseTime;

    /**
     * 辅助日期
     * 表字段： ancillary_services.ass_date
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date assDate;

    /**
     * 任务编码
     * 表字段： ancillary_services.task_code
     */
    private Long taskCode;

    /**
     * 辅助规模，单位（kW）
     * 表字段： ancillary_services.as_load
     */
    @ApiModelProperty("辅助规模，单位（kW）")
    private Double asLoad;

    /**
     * 辅助类型( 1-调峰、2-调频、3-备用)
     * 表字段： ancillary_services.as_type
     */
    @ApiModelProperty("辅助类型( 1-调峰、2-调频、3-备用)")
    private Integer asType;

    /**
     * 辅助补贴（元/kWh）
     * 表字段： ancillary_services.as_subsidy
     */
    @ApiModelProperty("辅助补贴（元/kWh）")
    private Double asSubsidy;

    /**
     * 状态（0-删除 1-未开始 2-执行中 3-已完成）
     * 表字段： ancillary_services.a_status
     */
    private Integer aStatus;

    /**
     * 表字段： ancillary_services.create_by
     */
    private String createBy;

    /**
     * 表字段： ancillary_services.create_time
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 表字段： ancillary_services.update_by
     */
    private String updateBy;

    /**
     * 表字段： ancillary_services.update_time
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 定时任务id
     * 表字段： ancillary_services.job_id
     */
    private Long jobId;

    /**
     * 预估收益
     * 表字段： ancillary_services.profit
     */
    @ApiModelProperty("预估收益")
    private Double profit;

    /**
     * 辅助服务负荷（kW）
     *
     * @Transient 代表表中不存在的字段
     */
    @ApiModelProperty("辅助服务负荷（kW）|总调节负荷")
    private Double actualLoad;

    /**
     * 总调节电量（kWh）
     */
    @ApiModelProperty("总调节电量（kWh）")
    private Double actualPower;

}