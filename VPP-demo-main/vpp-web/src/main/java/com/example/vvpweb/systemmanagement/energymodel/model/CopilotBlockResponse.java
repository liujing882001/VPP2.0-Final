package com.example.vvpweb.systemmanagement.energymodel.model;

import com.example.vvpweb.demand.model.CopilotResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CopilotBlockResponse {
    @ApiModelProperty("字段名称")
    private String name;

    @ApiModelProperty("数据")
    private List<CopilotResponse> dataList;
    @ApiModelProperty("是否展示")
    private Boolean show;

    private String type;
    public CopilotBlockResponse(String name,Boolean show, List<CopilotResponse> dataList) {
        this.name = name;
        this.dataList = dataList;
        this.show = show;
    }

}
