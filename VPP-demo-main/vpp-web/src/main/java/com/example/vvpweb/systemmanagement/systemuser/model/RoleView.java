package com.example.vvpweb.systemmanagement.systemuser.model;


import lombok.Data;

@Data
public class RoleView {

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 角色名称
     */
    private String roleName;

    private String configType;

}
