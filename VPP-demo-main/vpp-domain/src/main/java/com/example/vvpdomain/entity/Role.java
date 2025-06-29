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
import java.util.List;

/**
 * @author zph
 * @description 角色信息表
 * @date 2022-07-21
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 角色id
     */
    @Column(name = "role_id")
    private String roleId;

    /**
     * 角色名称
     */
    @Column(name = "role_name")
    private String roleName;

    /**
     * 角色权限key 1 ， 2 ， 3，  4 , 5
     */
    @Column(name = "role_key")
    private int roleKey;

    /**
     * 角色描述 角色权限key描述  1 系统管理员 2 普通管理员 3  电力用户  4 负荷集成商
     */
    @Column(name = "role_key_desc")
    private String roleKeyDesc;

    /**
     * 备注
     */
    @Column(name = "role_label")
    private String roleLabel;

    /**
     * 系统内置（y是 n否）
     */
    @Column(name = "config_type")
    private String configType;


    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "sys_role_menu", joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id"))
    private List<Menu> menuList;
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

    public Role() {
    }

}