package com.example.vvpweb.demand.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author maoyating
 * @description 节点模型
 * @date 20240312
 */
@Data
public class DemandNodeRespModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 是否在线
     */
    private Boolean online;


    /**
     * 建设中/已完成，默认false 为建设中
     */
    private Boolean isEnabled;

    /**
     * 户号
     */
    private String noHouseholds;

    public DemandNodeRespModel() {
    }


}