package com.example.vvpweb.electricityTrading.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TradePowerModel {

	@ApiModelProperty("电力交易任务ID")
	private String id;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty("任务开始时间")
	private Date sTime;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty("任务结束时间")
	private Date eTime;

	@ApiModelProperty("任务类型")
	private String tradeType;

	@ApiModelProperty("站点")
	private String station;

	@ApiModelProperty("任务创建时间")
	private Date createTime;

	@ApiModelProperty("任务更新时间")
	private Date updateTime;

	@ApiModelProperty("任务状态")
	private Integer status;

	@ApiModelProperty("")
	private String loadNodes;

	@ApiModelProperty("")
	private String energyNodes;

	@ApiModelProperty("")
	private String pvNodes;
}
