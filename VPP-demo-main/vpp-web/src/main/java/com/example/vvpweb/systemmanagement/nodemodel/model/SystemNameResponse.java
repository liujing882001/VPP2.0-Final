package com.example.vvpweb.systemmanagement.nodemodel.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
public class SystemNameResponse implements Serializable {

    /**
     * 节点下系统 id
     */
    private String id;
    /**
     * 节点下系统名称
     */
    private String systemName;

}
