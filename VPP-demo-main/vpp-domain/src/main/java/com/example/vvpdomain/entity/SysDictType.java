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
 * @description 系统类型字典数据表
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_dict_type")
public class SysDictType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id

    /**
     * 字典主键
     */
    @Column(name = "system_id")
    private String systemId;
    /**
     * 系统类型名称
     */
    @Column(name = "system_name")
    private String systemName;

    /**
     * 系统内置（y是 n否）
     */
    @Column(name = "config_type")
    private String configType;


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

    public SysDictType() {
    }

    public String getSystemName() { return systemName; }
    public String getSystemId() { return systemId; }

}