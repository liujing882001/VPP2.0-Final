package com.example.vvpdomain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "trade_power")
public class TradePower implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "s_time")
    private Date sTime;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "e_time")
    private Date eTime;

    @Column(name = "trade_type")
    private String tradeType;

    @Column(name = "station")
    private String station;

    @Column(name = "create_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private Date createTime;

    //申报状态
    @Column(name = "status")
    private Integer status;

    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Column(name = "load_nodes")
    private String loadNodes;

    @Column(name = "energy_nodes")
    private String energyNodes;

    @Column(name = "pv_nodes")
    private String pvNodes;

    @Column(name = "strategy")
    @JsonIgnore
    private String strategy;

    @Column(name = "operation")
    @JsonIgnore
    private String operation;

}
