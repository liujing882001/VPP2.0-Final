package com.example.vvpweb.systemmanagement.systemmodel.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Zhaoph
 * 系统模型
 */
@Data
public class SystemModel implements Serializable {

    @ApiModelProperty(value = "系统名称列表", name = "systemName", required = true)
    private List<String> systemName;
}
