package com.example.vvpweb.systemmanagement.systemmodel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 * 更新系统模型
 */
@Data
@ApiModel(value = "UpdateSystemModel", description = "更新系统模型")
public class UpdateSystemModel implements Serializable {
    /**
     * 编码唯一
     */
    @ApiModelProperty(value = "编码唯一", name = "systemKey", required = true)
    private String systemKey;
    /**
     * 新系统名称
     */
    @ApiModelProperty(value = "新系统名称", name = "newSystemName", required = true)
    private String newSystemName;
}
