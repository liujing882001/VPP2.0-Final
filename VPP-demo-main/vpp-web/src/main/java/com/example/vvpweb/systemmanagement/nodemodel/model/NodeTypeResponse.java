package com.example.vvpweb.systemmanagement.nodemodel.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
public class NodeTypeResponse implements Serializable {

    /**
     * 节点类型key
     */
    private String nodeTypeKey;

    /**
     * 节点类型id
     */
    private String nodeTypeId;
    /**
     * 节点类型名称
     */
    private String nodeTypeName;

    /**
     * 系统内置（y是 n否）
     */
    private String configType;
}
