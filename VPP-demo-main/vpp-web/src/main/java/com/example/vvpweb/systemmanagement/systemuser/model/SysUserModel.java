package com.example.vvpweb.systemmanagement.systemuser.model;

import lombok.Data;

import java.util.List;

@Data
public class SysUserModel {

    private String userId;

    /**
     * 登录名称
     */
    private String userName;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 密码
     */
    private String userPassword;


    /**
     * 系统内置（Y是 N否）
     */
    private String configType;


    /**
     * 用户角色ID
     */
    private String roleId;

    /**
     * 用户角色名称
     */
    private String roleName;
    /**
     * 用户所属角色类型
     */
    private String roleTypeName;

    /**
     * 角色描述 角色权限key描述  1 系统管理员 2 普通管理员 3  电力用户  4 负荷集成商
     */
    private int roleKey;
    /**
     * 公司地址
     */
    private String address;

    /**
     * 公司业务
     */
    private String business;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 节点列表
     */
    private List<String> nodeIds;
    /**
     * 需求响应分成比例  例如分成80.6%，写入库为80.6
     */
    private String shareRatio;


}
