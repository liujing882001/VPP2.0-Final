package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 碳交易
 * add by maoyating
 */
@Entity
@Getter
@Setter
@Table(name = "ca_trade")
@EntityListeners(AuditingEntityListener.class)
public class CaTrade implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 表字段： ca_trade.trade_id
     */
    @Id
    @Column(name = "trade_id")
    @ApiModelProperty("交易id(录入交易时，不用填写)")
    private String tradeId;

    /**
     * 交易类型(1-碳交易 2-绿电交易 3-绿证交易)
     * 表字段： ca_trade.trade_type
     */
    @Column(name = "trade_type")
    @ApiModelProperty("交易类型(1-碳交易 2-绿电交易 3-绿证交易)")
    private Integer tradeType;

    /**
     * 碳配额公司/发电公司
     * 表字段： ca_trade.company
     */
    @Column(name = "company")
    @ApiModelProperty("碳配额公司/发电公司名称")
    private String company;

    /**
     * 绿电类型（1-光伏 2-风能）
     * 表字段： ca_trade.green_type
     */
    @Column(name = "green_type")
    @ApiModelProperty("绿电类型（1-光伏 2-风能） ")
    private Integer greenType;

    /**
     * 绿证类型（1-有补贴 2-无补贴）
     * 表字段： ca_trade.certificate_type
     */
    @Column(name = "certificate_type")
    @ApiModelProperty("绿证类型（1-有补贴 2-无补贴）")
    private Integer certificateType;

    /**
     * 交易量
     * 表字段： ca_trade.trading_volume
     */
    @Column(name = "trading_volume")
    @ApiModelProperty("交易量")
    private Double tradingVolume;

    /**
     * 交易金额(元)
     * 表字段： ca_trade.trade_amount
     */
    @Column(name = "trade_amount")
    @ApiModelProperty("交易金额(元)")
    private Double tradeAmount;

    /**
     * 交易时间（yyyy-MM-dd）
     * 表字段： ca_trade.trade_date
     */
    @ApiModelProperty("交易时间（yyyy-MM-dd）")
    @Column(name = "trade_date")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date tradeDate;

    /**
     * 表字段： ca_trade.created_time
     */
    @ApiModelProperty("创建时间")
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 表字段： ca_trade.update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 状态（0-删除 1-正常）
     * 表字段： ca_trade.t_status
     */
    @Column(name = "t_status")
    @ApiModelProperty("状态（0-删除 1-正常）")
    private Integer tStatus;

    /**
     * 节点id
     * 表字段： ca_trade.node_id
     */
    @Column(name = "node_id")
    @ApiModelProperty("节点id")
    private String nodeId;

    public CaTrade() {
    }
}