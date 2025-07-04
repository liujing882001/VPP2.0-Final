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
 * @description user
 * @date 2022-07-21
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 用户id
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 登录名称
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 用户邮箱
     */
    @Column(name = "user_email")
    private String userEmail;

    /**
     * 密码
     */
    @Column(name = "user_password")
    private String userPassword;

    /**
     * 系统内置（y是 n否）
     */
    @Column(name = "config_type")
    private String configType;


    /**
     * 签约 需求响应分成比例  例如分成80.6%，写入库为80.6
     */
    @Column(name = "share_ratio")
    private double shareRatio;


    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Role role;
    /**
     * 用户下策略
     */
    @OneToMany(mappedBy = "user")
    private List<ScheduleStrategy> scheduleStrategyList;
    /**
     * 公司地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 公司业务
     */
    @Column(name = "business")
    private String business;

    /**
     * 联系人
     */
    @Column(name = "contact")
    private String contact;

    /**
     * 手机号
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 电网类型
     */
    @Column(name = "power_grid")
    private Integer powerGrid;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_node", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "node_id"))
    private List<Node> nodeList;


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

    /**
     * 普通管理员 虚拟电厂运营商 电力用户
     */
    @Column(name = "role_type_name")
    private String roleTypeName;


    public User() {
    }

    public Role getRole() { return role; }
    public List<Node> getNodeList() { return nodeList; }

}