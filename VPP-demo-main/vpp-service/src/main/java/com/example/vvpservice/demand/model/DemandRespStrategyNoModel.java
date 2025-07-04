package com.example.vvpservice.demand.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author maoyating
 * @description 需求响应策略户号应对关系
 * @date 2022-08-09
 */
@Entity
@Getter
@Setter
public class DemandRespStrategyNoModel implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 策略id
     * 表字段： demand_resp_strategy_no.s_id
     */
    private String drsId;


    /**
     * 所属策略id
     */
    private String sId;

    /**
     * 户号
     */
    private String noHouseholds;

    /**
     * 状态（11-未申报 12-执行中未申报 15-已结束未申报 21-待出清已申报  22-出清成功已申报 23-出清失败已申报 24-执行中已申报 25-已结束已申报）
     */
    private Integer drsStatus;

    /**
     * 申报负荷
     */
    private Double declareLoad;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 是否第三方平台（1-是 2-否）
     */
    private Integer isPlatform;

    /**
     * 任务id
     */
    private String respId;

    // 手动添加缺失的getter/setter方法以确保编译通过
    public String getDrsId() { return drsId; }
    public void setDrsId(String drsId) { this.drsId = drsId; }
    public String getSId() { return sId; }
    public void setSId(String sId) { this.sId = sId; }
    public String getNoHouseholds() { return noHouseholds; }
    public void setNoHouseholds(String noHouseholds) { this.noHouseholds = noHouseholds; }
    public Integer getDrsStatus() { return drsStatus; }
    public void setDrsStatus(Integer drsStatus) { this.drsStatus = drsStatus; }
    public Double getDeclareLoad() { return declareLoad; }
    public void setDeclareLoad(Double declareLoad) { this.declareLoad = declareLoad; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public Integer getIsPlatform() { return isPlatform; }
    public void setIsPlatform(Integer isPlatform) { this.isPlatform = isPlatform; }
    public String getRespId() { return respId; }
    public void setRespId(String respId) { this.respId = respId; }
}