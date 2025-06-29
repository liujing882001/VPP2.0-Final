package com.example.vvpweb.ancillary.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author maoyating
 * @description 辅助服务
 * @date 2022-08-09
 */
@Data
public class AncillaryServicesReq {
    /**
     * 辅助任务id
     */
    private String asId;

    /**
     * 辅助时段(开始)
     */
    @ApiModelProperty("辅助开始时段,格式HH:mm")
    @NotBlank(message = "辅助开始时段不能为空")
    private String assTime;

    /**
     * 辅助时段(结束)
     */
    @ApiModelProperty("辅助结束时段,格式HH:mm")
    @NotBlank(message = "辅助结束时段不能为空")
    private String aseTime;

    /**
     * 辅助日期
     */
    @ApiModelProperty("辅助日期,格式yyyy-MM-dd")
    @NotBlank(message = "辅助日期不能为空")
    private String assDate;

    /**
     * 任务编码
     */
    @ApiModelProperty("任务编码")
    @NotNull(message = "任务编码不能为空")
    private Long taskCode;

    /**
     * 辅助规模，单位（kW）
     * 表字段： ancillary_services.as_load
     */
    @ApiModelProperty("辅助规模，单位（kW）")
    @NotNull(message = "辅助规模不能为空")
    private Double asLoad;

    /**
     * 辅助类型( 1-调峰、2-调频、3-备用)
     */
    @ApiModelProperty("辅助类型( 1-调峰、2-调频、3-备用)")
    @NotNull(message = "辅助类型不能为空")
    private Integer asType;

    /**
     * 辅助补贴（元/kWh）
     * 表字段： ancillary_services.as_subsidy
     */
    @ApiModelProperty("辅助补贴（元/kWh）")
    @NotNull(message = "辅助补贴不能为空")
    private Double asSubsidy;


}