package com.example.vvpdomain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author maoyating
 * @description 需求响应策略户号应对关系
 * @date 2022-08-09
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "demand_resp_strategy_no")
public class DemandRespStrategyNo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 策略id
     * 表字段： demand_resp_strategy_no.s_id
     */
    @Id
    @Column(name = "drs_id")
    @ApiModelProperty("策略id")
    private String drsId;

    /**
     * 所属策略id
     */
    @ManyToOne(targetEntity = DemandRespStrategy.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "s_id", referencedColumnName = "s_id")
    private DemandRespStrategy demandRespStrategy;

    /**
     * 户号
     */
    @Column(name = "no_households")
    @ApiModelProperty("户号")
    private String noHouseholds;

    /**
     * 状态（11-未申报 12-执行中未申报 15-已结束未申报 21-待出清已申报  22-出清成功已申报 23-出清失败已申报 24-执行中已申报 25-已结束已申报）
     */
    @Column(name = "drsStatus")
    @ApiModelProperty("状态（11-未申报 12-执行中未申报 15-已结束未申报 21-待出清已申报  22-出清成功已申报 23-出清失败已申报 24-执行中已申报 25-已结束已申报）")
    private Integer drsStatus;

    /**
     * 申报负荷
     */
    @Column(name = "declare_load")
    @ApiModelProperty("申报负荷")
    private Double declareLoad;

    /**
     * 节点名称
     */
    @Column(name = "node_name")
    @ApiModelProperty("节点名称")
    private String nodeName;

    /**
     * 节点id
     */
    @Column(name = "node_id")
    @ApiModelProperty("节点id")
    private String nodeId;

    /**
     * 实际响应率
     */
    @ApiModelProperty("实际响应率")
    @Column(name = "actual_ratio")
    private Double actualRatio;

    /**
     * 收益
     */
    @ApiModelProperty("收益")
    @Column(name = "profit")
    private Double profit;

    /**
     * 实时响应负荷电量
     */
    @ApiModelProperty("实时响应负荷电量")
    @Column(name = "real_time_load")
    private Double realTimeLoad;

    /**
     * 容量收益（元）--电网返回
     */
    @ApiModelProperty("容量收益（元）--电网返回")
    @Column(name = "volume_profit")
    private Double volumeProfit;

    /**
     * 是否中标（1-中标 2-未中标 3-未发送 4-已发送）
     */
    @Column(name = "winning_bid")
    @ApiModelProperty("是否中标（1-中标 2-未中标 3-未发送 4-已发送）")
    private Integer winningBid;

    /**
     * 是否第三方平台（1-是 2-否）
     */
    @Column(name = "is_platform")
    @ApiModelProperty("是否第三方平台（1-是 2-否）")
    private Integer isPlatform;

    /**
     * 申报价格
     */
    @ApiModelProperty("申报价格")
    @Column(name = "declare_price")
    private Double declarePrice;

    /**
     * 任务id
     */
    @Column(name = "resp_id")
    @ApiModelProperty("任务id")
    private String respId;

    // Manual getters and setters to ensure compilation
    public String getDrsId() { return drsId; }
    public void setDrsId(String drsId) { this.drsId = drsId; }
    public DemandRespStrategy getDemandRespStrategy() { return demandRespStrategy; }
    public void setDemandRespStrategy(DemandRespStrategy demandRespStrategy) { this.demandRespStrategy = demandRespStrategy; }
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
    public Double getDeclarePrice() { return declarePrice; }
    public void setDeclarePrice(Double declarePrice) { this.declarePrice = declarePrice; }
    public Integer getIsPlatform() { return isPlatform; }
    public void setIsPlatform(Integer isPlatform) { this.isPlatform = isPlatform; }
    public String getRespId() { return respId; }
    public void setRespId(String respId) { this.respId = respId; }
    public Integer getWinningBid() { return winningBid; }
    public void setWinningBid(Integer winningBid) { this.winningBid = winningBid; }
}