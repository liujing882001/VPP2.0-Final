package com.example.vvpdomain;

import com.example.vvpdomain.entity.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description role_menu
 * @date 2022-07-21
 */
@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenu, String>, JpaSpecificationExecutor<RoleMenu> {

    List<RoleMenu> findAllByRoleId(String roleId);

    @Query(value = "delete from sys_role_menu where role_id= :roleId ", nativeQuery = true)
    @Modifying
    void deleteAllByRoleId(@Param("roleId") String roleId);
}