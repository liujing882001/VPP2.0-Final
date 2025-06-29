package com.example.vvpservice.energymodel.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ProfitRequest {
	@ApiModelProperty("节点id")
	private List<String> nodeId;

	@ApiModelProperty("系统id")
	private String systemId;

	@ApiModelProperty("开始日期,格式yyyy-MM-dd")
	@NotBlank(message = "开始日期不能为空")
	private String startDate;

	@ApiModelProperty("结束日期,格式yyyy-MM-dd")
	@NotBlank(message = "结束日期不能为空")
	private String endDate;

}
