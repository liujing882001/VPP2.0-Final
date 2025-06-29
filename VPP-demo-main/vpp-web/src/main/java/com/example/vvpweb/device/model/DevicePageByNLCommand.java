package com.example.vvpweb.device.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class DevicePageByNLCommand {
    @NotNull(message = "nodeIds不能为空")
    @Size(min = 1, message = "nodeIds不能为空且必须至少包含一个元素")
    private List<String> nodeIds;

    @Min(value = 1, message = "pageNum必须大于0")
    private Integer pageNum = 1;

    @Min(value = 1, message = "pageSize必须大于0")
    @Max(value = 100, message = "pageSize最大值为100")
    private Integer pageSize = 10;
}
