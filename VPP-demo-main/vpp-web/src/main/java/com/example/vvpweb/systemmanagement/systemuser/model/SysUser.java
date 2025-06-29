package com.example.vvpweb.systemmanagement.systemuser.model;

import lombok.Data;

/**
 * 用户对象 sys_user
 */
@Data
public class SysUser {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色对象
     */
    private SysRole role;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 岗位组
     */
    private Long postId;
}
