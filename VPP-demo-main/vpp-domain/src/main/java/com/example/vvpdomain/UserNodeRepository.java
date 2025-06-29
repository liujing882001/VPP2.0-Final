package com.example.vvpdomain;

import com.example.vvpdomain.entity.UserNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description 用户节点
 * @date 2022-07-21
 */
@Repository
public interface UserNodeRepository extends JpaRepository<UserNode, String>, JpaSpecificationExecutor<UserNode> {


    @Query(value = "delete from sys_user_node where user_id= :userId ", nativeQuery = true)
    @Modifying
    void deleteByUserId(@Param("userId") String userId);


    List<UserNode> findAllByUserId(String userId);

    @Query(value = "delete from sys_user_node where node_id= :nodeId ", nativeQuery = true)
    @Modifying
    void deleteByNodeId(@Param("nodeId") String userId);
}