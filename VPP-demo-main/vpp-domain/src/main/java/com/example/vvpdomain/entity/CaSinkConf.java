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
 * 碳汇
 * add by maoyating
 */
@Entity
@Getter
@Setter
@Table(name = "ca_sink_conf")
@EntityListeners(AuditingEntityListener.class)
public class CaSinkConf implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 表字段： ca_sink_conf.c_id
     */
    @Id
    @Column(name = "c_id")
    @ApiModelProperty("code")
    private String cId;

    /**
     * 属性数量(平方米/棵)
     * 表字段： ca_sink_conf.attr_num
     */
    @Column(name = "attr_num")
    @ApiModelProperty("属性数量(平方米/棵)")
    private Integer attrNum;

    /**
     * 类型（lvhuamianji-绿化面积;zhongzhishumu-种植树木 ）
     * 表字段： ca_sink_conf.c_type
     */
    @Column(name = "c_type")
    @ApiModelProperty("类型（lvhuamianji-绿化面积;zhongzhishumu-种植树木 ）")
    private String cType;

    /**
     * 节点id
     * 表字段： ca_sink_conf.node_id
     */
    @Column(name = "node_id")
    @ApiModelProperty("节点id")
    private String nodeId;

    /**
     * 表字段： ca_sink_conf.created_time
     */
    @ApiModelProperty("创建时间")
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 表字段： ca_sink_conf.update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 添加日期
     * 表字段： ca_sink_conf.add_time
     */
    @ApiModelProperty("添加日期 yyyy-MM-dd")
    @Column(name = "add_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date addTime;

    public CaSinkConf() {
    }
}