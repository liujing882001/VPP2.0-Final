package com.example.vvpweb.demand.model.cspg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class CSPGDemandPlanModel {

    @ApiModelProperty("系统时间戳，格式(yyyy-MM-dd HH:mm:ss)")
    private String systemTime;
    @ApiModelProperty("负荷聚合商唯一标识（统一社会信用代码，共 18 位），不缩略，全字段上传")
    private String creditCode;
    @ApiModelProperty("事件类型，填：“invitation”")
    private String eventType;
    @ApiModelProperty("邀约计划 ID")
    private String invitationId;
    @ApiModelProperty("邀约时间 (yyyy-MM-dd HH:mm:ss)")
    private String invitationTime;
    @ApiModelProperty("交易品种，精准响应削峰：RQXF； 精准响应填谷：RQTG；南网辅助服务：NWFZFW")
    private String exchangeType;
    @ApiModelProperty("响应执行开始时间(yyyy-MM-dd HH:mm:00)")
    private String startTime;
    @ApiModelProperty("响应执行结束时间(yyyy-MM-dd HH:mm:00)")
    private String endTime;
    @ApiModelProperty("页码，默认为 1")
    private int page;
    @ApiModelProperty("总页数，默认为第一页")
    private int totalPage;
    @ApiModelProperty("邀约回复截止时间，(yyyy-MM-dd HH:mm:ss)")
    private String replyTime;

    @ApiModelProperty("本次邀约市场需求曲线（MW）， 偏差量，只下发响应执行时段内的全网总需求量。负值表示削峰，正数表示填谷。")
    private Map<String, Double> mrLine;
    @ApiModelProperty("符合邀约调节的用户编号")
    //resourceId,    all：全部资源参与；    resourceId1,resourceId2,resourceId3…：指定资源参与
    private String inviteRange;
    @ApiModelProperty("resourceId 对应的基线负荷，供用户申报响应计划参考")
    private List<Map<String, Object>> baseLine;

}
