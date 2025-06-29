package com.example.vvpweb.systemmanagement.energymodel.model;

import com.example.vvpweb.demand.model.CopilotResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CopilotBlockResponseNew {
    @ApiModelProperty("字段名称")
    private String name;
    private String nodeId;
    private String suffix;
    private String type;
    @ApiModelProperty("数据")
    private List<CopilotResponse> dataList;
    @ApiModelProperty("是否展示")
    private Boolean show;
    public CopilotBlockResponseNew(String name,String nodeId,String suffix,String type, Boolean show, List<CopilotResponse> dataList) {
        this.name = name;
        this.nodeId = nodeId;
        this.suffix = suffix;
        this.type = type;
        this.dataList = dataList;
        this.show = show;
    }

}
