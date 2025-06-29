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
 * @description 菜单权限对象
 * @date 2022-07-21
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 菜单id
     */
    @Column(name = "menu_id")
    private String menuId;

    /**
     * 菜单名称
     */
    @Column(name = "menu_name")
    private String menuName;

    /**
     * 菜单英文名称
     */
    @Column(name = "menu_name_en")
    private String menuNameEn;

    /**
     * 父菜单id
     */
    @Column(name = "parent_id")
    private String parentId;

    /**
     * 显示顺序
     */
    @Column(name = "order_num")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @Column(name = "path")
    private String path;


    /**
     * 组件路径
     */
    @Column(name = "component")
    private String component;


    /**
     * 菜单类型（m目录 c菜单 f按钮 j跳转）
     */
    @Column(name = "menu_type")
    private String menuType;


    /**
     * 权限字符
     */
    @Column(name = "perms")
    private String perms;


    /**
     * icon
     */
    @Column(name = "icon")
    private String icon;


    /**
     * loadType          负荷类
     * resourcesType     资源类
     * common            共同
     */
    @Column(name = "os_type")
    private String osType;

    /**
     *菜单状态（0正常 1停用）
     */
    @Column(name = "status")
    private String status;
    /**
     *是否为外链（0是 1否）
     */
    @Column(name = "is_frame")
    private int isFrame;
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

    @Column(name = "icon_link")
    private String iconLink;

    public Menu() {
    }

}