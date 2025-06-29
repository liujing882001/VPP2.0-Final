package com.example.vvpdomain;

import com.example.vvpdomain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description 角色信息表
 * @date 2022-07-21
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    Role findByRoleName(String roleName);

    List<Role> findAllByRoleKeyIn(List<String> roleKeys);

    List<Role> findAllByRoleNameContains(String roleName);

    Role findByRoleKeyAndConfigType(String roleKey, String configType);
}