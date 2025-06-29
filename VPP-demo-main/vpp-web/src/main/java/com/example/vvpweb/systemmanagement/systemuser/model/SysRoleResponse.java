package com.example.vvpweb.systemmanagement.systemuser.model;

import lombok.Data;

import java.io.Serializable;


/**
 * 角色表 sys_role
 */
@Data
public class SysRoleResponse implements Serializable {

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限key 1 , 2 , 3 , 4
     */
    private int roleKey;

}
