package com.example.vvpweb.systemmanagement.nodemodel.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
public class NodeNameResponse implements Serializable {

    /**
     * 节点id
     */
    private String id;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 节点类型(load 负荷 pv 光伏 storageEnergy 储能)
     */
    private String nodePostType;

}
