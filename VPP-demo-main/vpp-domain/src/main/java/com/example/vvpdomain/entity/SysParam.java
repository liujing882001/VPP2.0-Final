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
 * @description 系统参数表
 * @date 2023-04-21
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_param")
public class SysParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * id
     */
    @Column(name = "id")
    private String id;

    /**
     * 名字
     */
    @Column(name = "sys_param_name")
    private String sysParamName;

    /**
     * 保存值
     */
    @Column(name = "sys_param_value")
    private String sysParamValue;


    /**
     * UI界面显示内容
     */
    @Column(name = "sys_param_content")
    private String sysParamContent;

    /**
     * 参数字典
     */
    @Column(name = "sys_param_key")
    private int sysParamKey;

    /**
     * 系统参数字典key描述
     * 1：电网省公司需求响应地址
     * 2：IOT平台
     * 3：资源概览节点类型排序
     * 4：节点标准坪效设定
     * 5：三方智慧能源平台
     */
    @Column(name = "sys_param_key_desc")
    private String sysParamKeyDesc;

    /**
     *参数菜单状态（0正常 1停用）
     */
    @Column(name = "status")
    private String status;
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

    public SysParam() {
    }

    public String getSysParamValue() {
        return sysParamValue;
    }

    public void setSysParamValue(String sysParamValue) {
        this.sysParamValue = sysParamValue;
    }

}