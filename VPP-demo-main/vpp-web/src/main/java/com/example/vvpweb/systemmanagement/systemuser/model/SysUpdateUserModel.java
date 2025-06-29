package com.example.vvpweb.systemmanagement.systemuser.model;

import lombok.Data;

import java.util.List;

@Data
public class SysUpdateUserModel {

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
    private double shareRatio;


}
