package com.example.vvpweb.systemmanagement.systemuser.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 角色表 sys_role
 */
@Data
public class SysRoleListResponse implements Serializable {

    private String orderNum;
    /**
     * 角色ID
     */
    private String roleId;

    private String roleName;

    /**
     * 角色权限key 1 , 2 , 3 , 4
     */
    private int roleKey;

    /**
     * 角色权限key描述 1 系统管理员 2 普通管理员 3 电力用户 4 负荷集成商
     */
    private String roleKeyDesc;

    private String roleLabel;


    /**
     * 系统内置（y是 n否）
     */
    private String configType;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

}
