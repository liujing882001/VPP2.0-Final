package com.example.vvpdomain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zph
 * @description 节点字典数据表
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_dict_node")
public class SysDictNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id

    /**
     * 节点主键(=nodetypekey)
     */
    @Column(name = "node_type_id")
    private String nodeTypeId;

    /**
     * 节点类型名称
     */
    @Column(name = "node_type_name")
    private String nodeTypeName;

    /**
     * 归属类型 load ，pv ，storageEnergy
     */
    @Column(name = "node_post_type")
    private String nodePostType;


    /**
     * 节点类型所属描述 load 负荷，pv 光伏，storageEnergy
     */
    @Column(name = "node_post_type_desc")
    private String nodePostTypeDesc;

    /**
     * 系统内置（y是 n否）
     */
    @Column(name = "config_type")
    private String configType;


    /**
     * 序号
     */
    @Column(name = "node_order")
    private Integer nodeOrder;

    /**
     * created_time
     */
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public SysDictNode() {
    }

    public String getNodeTypeId() { return nodeTypeId; }
    public void setNodeTypeId(String nodeTypeId) { this.nodeTypeId = nodeTypeId; }
    public String getNodeTypeName() { return nodeTypeName; }
    public void setNodeTypeName(String nodeTypeName) { this.nodeTypeName = nodeTypeName; }
    public String getNodePostType() { return nodePostType; }
    public void setNodePostType(String nodePostType) { this.nodePostType = nodePostType; }
    public String getNodePostTypeDesc() { return nodePostTypeDesc; }
    public void setNodePostTypeDesc(String nodePostTypeDesc) { this.nodePostTypeDesc = nodePostTypeDesc; }
    public String getConfigType() { return configType; }
    public void setConfigType(String configType) { this.configType = configType; }
    public Integer getNodeOrder() { return nodeOrder; }
    public void setNodeOrder(Integer nodeOrder) { this.nodeOrder = nodeOrder; }
    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}