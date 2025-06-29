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
 * @description 系统数据字典表-用于存放系统的配置项,某些业务逻辑
 * 需要根据配置项的值来做出相应的处理，跨系统对接 共用识别标签
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_dict_data")
public class SysDictData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id

    /**
     * 参数主键
     */
    @Column(name = "model_id")
    private String modelId;

    /**
     * 参数名称
     */
    @Column(name = "model_name")
    private String modelName;

    /**
     * 参数键名
     */
    @Column(name = "model_key")
    private String modelKey;

    /**
     * 参数类型（1 设备 2 点位）
     */
    @Column(name = "model_key_type")
    private int modelKeyType;

    /**
     * 系统内置（y是 n否)
     */
    @Column(name = "config_type")
    private String configType;

    /**
     * 备注
     */
    @Column(name = "model_mark")
    private String modelMark;

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

    public SysDictData() {
    }

}