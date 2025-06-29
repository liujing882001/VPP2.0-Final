package com.example.vvpweb.device.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class DevicePageCommand {
    @NotNull(message = "nodeIds不能为空")
    private String nodeId;
    @Min(value = 1, message = "pageNum必须大于0")
    private Integer pageNum = 1;
    @Min(value = 1, message = "pageSize必须大于0")
    @Max(value = 100, message = "pageSize最大值为100")
    private Integer pageSize = 10;
}
