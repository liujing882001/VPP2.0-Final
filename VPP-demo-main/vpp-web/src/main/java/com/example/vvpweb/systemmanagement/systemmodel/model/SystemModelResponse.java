package com.example.vvpweb.systemmanagement.systemmodel.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 * 系统类型
 */
@Data
public class SystemModelResponse implements Serializable {

    /**
     * 编码唯一
     */
    @ApiModelProperty(value = "编码唯一", name = "systemKey", required = true)
    private String systemKey;
    /**
     * 系统名称
     */
    @ApiModelProperty(value = "系统名称", name = "systemName", required = true)
    private String systemName;
    /**
     * 系统内置（y是 n否）
     */
    @ApiModelProperty(value = "系统内置（y是 n否）", name = "configType", required = true)
    private String configType;
}
