package com.example.vvpweb.systemmanagement.stationnode.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class AddSysNodeCommand {
    /**
     * 项目节点ID
     */
    @NotBlank(message = "项目节点id不能为空")
    private String nodeId;

    /**
     * 系统名称
     */
    @NotBlank(message = "系统名称不能为空")
    private String nodeName;

    /**
     * 系统节点类型id
     */
    @NotBlank(message = "系统节点类型id不能为空")
    private String nodeTypeId;
    /**
     * 系统节点类型
     */
    @NotBlank(message = "系统节点类型不能为空")
    private String nodeType;

    /**
     * 设备分类（系统类型）
     */
    @NotEmpty(message = "设备分类不能为空")
    private List<String> sysIds;
    private String noHouseholds;
    /**
     * 节点阶段 建设中、运营中
     */
    @NotBlank(message = "节点阶段")
    private String stationState;

    private String stationCategory;
}
