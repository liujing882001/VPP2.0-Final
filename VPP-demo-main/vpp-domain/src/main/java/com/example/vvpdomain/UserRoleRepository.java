package com.example.vvpdomain;

import com.example.vvpdomain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description 用户和角色关联对象
 * @date 2022-07-21
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String>, JpaSpecificationExecutor<UserRole> {

    @Query(value = "delete from sys_user_role where role_Id= :roleId ", nativeQuery = true)
    @Modifying
    void deleteByRoleId(@Param("roleId") String roleId);

    @Query(value = "delete from sys_user_role where user_id= :userId ", nativeQuery = true)
    @Modifying
    void deleteByUserId(@Param("userId") String userId);

    List<UserRole> findAllByUserId(String userId);
    UserRole findByUserId(String userId);


    List<UserRole> findAllByRoleId(String roleId);
}