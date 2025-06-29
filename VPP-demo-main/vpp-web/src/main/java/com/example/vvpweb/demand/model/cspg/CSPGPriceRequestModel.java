package com.example.vvpweb.demand.model.cspg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CSPGPriceRequestModel {

    @ApiModelProperty("系统时间戳，格式(yyyy-MM-dd HH:mm:ss)")
    private String systemTime;
    @ApiModelProperty("负荷聚合商唯一标识（统一社会信用代码，共 18 位）")
    private String creditCode;
    @ApiModelProperty("事件类型，填：“responsePlan”")
    private String invitationId;
    @ApiModelProperty("邀约计划 ID")
    private int page;
    @ApiModelProperty("页码，默认为第一页")
    private int pageSize;
    @ApiModelProperty("每页显示条数,默认 100 个资源")
    private String eventType;
    @ApiModelProperty("首次为 null,后续为最后一次收到的答复时间;")
    private String lastReplyTime;
}
