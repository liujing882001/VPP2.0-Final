package com.example.vvpservice.role.service;


import com.example.vvpdomain.entity.Role;

import java.util.List;

public interface IRoleService {


    boolean addRole(Role role);

    void updateRoleName(String roleId, String roleName, String roleLabel);

    void updateRoleInfoMenu(String roleId, List<String> menuIds);

    boolean CheckNameIsExists(String roleName);

    void deleteRole(String roleId);

    List<Role> sysUserRoles();

    List<Role> allUserRoles();

    List<Role> allRoleListByName(String roleName);
}
