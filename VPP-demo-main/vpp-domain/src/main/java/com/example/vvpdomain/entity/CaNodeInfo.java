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
 * 碳模型-基本信息
 * add by maoyating
 */
@Entity
@Getter
@Setter
@Table(name = "ca_node_info")
@EntityListeners(AuditingEntityListener.class)
public class CaNodeInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 楼宇节点id
     * 表字段： ca_node_info.node_id
     */
    @Id
    @Column(name = "node_id")
    @ApiModelProperty("楼宇节点id")
    private String nodeId;

    /**
     * 楼宇建筑面积（平方米）
     * 表字段： ca_node_info.node_area
     */
    @Column(name = "node_area")
    @ApiModelProperty("楼宇建筑面积（平方米）")
    private Double nodeArea;

    /**
     * 楼宇建筑寿命（年）
     * 表字段： ca_node_info.life
     */
    @Column(name = "life")
    @ApiModelProperty("楼宇建筑寿命（年）")
    private Double life;

    /**
     * 楼宇已使用年限（年）
     * 表字段： ca_node_info.use_life
     */
    @Column(name = "use_life")
    @ApiModelProperty("楼宇已使用年限（年）")
    private Double useLife;

    /**
     * 楼宇绿化面积（平方米）
     * 表字段： ca_node_info.green_area
     */
    @Column(name = "green_area")
    @ApiModelProperty("楼宇绿化面积（平方米）")
    private Double greenArea;

    /**
     * 楼宇管辖区内树木数量(棵)
     * 表字段： ca_node_info.tree_num
     */
    @Column(name = "tree_num")
    @ApiModelProperty("楼宇管辖区内树木数量(棵)")
    private Long treeNum;

    /**
     * 每周工作时间开始时间(HH:mm)
     * 表字段： ca_node_info.start_time
     */
    @Column(name = "start_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm")
    @ApiModelProperty("每周工作时间开始时间(HH:mm)")
    private Date startTime;

    /**
     * 每周工作时间结束时间(HH:mm)
     * 表字段： ca_node_info.end_time
     */
    @Column(name = "end_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm")
    @ApiModelProperty("每周工作时间结束时间(HH:mm)")
    private Date endTime;

    /**
     * 每周工作哪几天
     * 表字段： ca_node_info.weekly
     */
    @ApiModelProperty("每周工作哪几天(用逗号分隔),例如周一,周三两天，就传1,3")
    @Column(name = "weekly")
    private String weekly;

    /**
     * 表字段： ca_node_info.created_time
     */
    @ApiModelProperty("创建时间(前端不用传)")
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 表字段： ca_node_info.update_time
     */
    @ApiModelProperty("修改时间(前端不用传)")
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public CaNodeInfo() {
    }
}