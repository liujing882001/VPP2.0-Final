package com.example.vvpweb.systemmanagement.systemuser.model;

import lombok.Data;


/**
 * 角色表 sys_role
 */
@Data
public class SysRole {
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 角色名称
     */
    private String roleName;

    private String roleLabel;

}
