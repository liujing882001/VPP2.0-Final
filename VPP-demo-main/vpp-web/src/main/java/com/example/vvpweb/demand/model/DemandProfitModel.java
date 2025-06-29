package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author maoyating
 */
@Data
public class DemandProfitModel implements Serializable {

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

    @ApiModelProperty("节点id,多个用逗号隔开")
    private String nodeId;

    @ApiModelProperty("用户类型 0-全部 1-电力用户 2-虚拟电厂运营")
    private Integer userType;

    @ApiModelProperty("开始日期 若日期类型为日，格式为yyyy-MM-dd格式;若为月，格式为yyyyMM;若为年,格式为yyyy")
    private String startDate;

    @ApiModelProperty("结束日期 同开始日期格式")
    private String endDate;

    @ApiModelProperty("日期类型 1-日 2-月 3-年")
    private Integer dateType;

    @ApiModelProperty("是否导出 导出时填’export‘")
    private String isExport;

}
