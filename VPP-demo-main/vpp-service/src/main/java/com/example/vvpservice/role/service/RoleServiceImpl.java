package com.example.vvpservice.role.service;

import com.example.vvpcommom.IdGenerator;
import com.example.vvpdomain.RoleMenuRepository;
import com.example.vvpdomain.RoleRepository;
import com.example.vvpdomain.entity.Role;
import com.example.vvpdomain.entity.RoleMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements IRoleService {

    private static Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMenuRepository roleMenuRepository;


    @Override
    @Transactional
    public boolean addRole(Role role) {
        Role byRoleName = roleRepository.findByRoleName(role.getRoleName());
        if (byRoleName != null) {
            throw new IllegalArgumentException("角色名称已经存在，请修改");
        }
        roleRepository.save(role);

        return true;
    }


    public boolean CheckNameIsExists(String roleName) {
        Role byRoleName = roleRepository.findByRoleName(roleName);
        if (byRoleName != null) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void updateRoleName(String roleId, String roleName, String roleLabel) {

        Optional<Role> byId = roleRepository.findById(roleId);
        if (!byId.isPresent()) {
            throw new IllegalArgumentException("角色ID不存在");
        }
        Role role = byId.get();
        role.setRoleName(roleName);
        role.setRoleLabel(roleLabel);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void updateRoleInfoMenu(String roleId, List<String> menuIds) {
        roleMenuRepository.deleteAllByRoleId(roleId);
        menuIds.forEach(e -> {
            RoleMenu rm = new RoleMenu();
            rm.setId(IdGenerator.concatString(roleId, e));
            rm.setRoleId(roleId);
            rm.setMenuId(e);
            roleMenuRepository.save(rm);
        });
    }

    @Override
    @Transactional
    public void deleteRole(String roleId) {
        Optional<Role> byId = roleRepository.findById(roleId);
        if (byId.isPresent()) {
            Role role = byId.get();
            if ("Y".equals(role.getConfigType())) {
                throw new IllegalArgumentException("角色为内置角色，不能删除");
            }
            roleMenuRepository.deleteAllByRoleId(roleId);

            roleRepository.delete(role);
        }
    }

    @Override
    public List<Role> sysUserRoles() {
        /**
         * roleKey角色权限字符串 1 系统管理员 2 普通管理员 3 电力用户 4 负荷集成商
         */
        ;
        List<String> roleKeys = Arrays.asList("1", "2");
        List<Role> all = roleRepository.findAllByRoleKeyIn(roleKeys);
        return all;
    }

    @Override
    public List<Role> allUserRoles() {
        List<Role> all = roleRepository.findAll();
        return all;
    }

    @Override
    public List<Role> allRoleListByName(String roleName) {
        return roleRepository.findAllByRoleNameContains(roleName);
    }
}
