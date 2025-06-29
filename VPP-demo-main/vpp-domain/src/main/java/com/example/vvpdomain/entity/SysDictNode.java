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

}