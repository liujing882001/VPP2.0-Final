package com.example.vvpdomain;

import com.example.vvpdomain.entity.SysDictNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description 节点字典数据表
 * @date 2022-07-01
 */
@Repository
public interface SysDictNodeRepository extends JpaRepository<SysDictNode, String>, JpaSpecificationExecutor<SysDictNode> {

    List<SysDictNode> findAllByOrderByNodeOrderAsc();

    @Query(value = "SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\tsys_dict_node \n" +
            "WHERE\n" +
            "\tnode_post_type = 'load' \n" +
            "\tAND node_type_id IN ( SELECT node_type_id FROM node ) \n" +
            "ORDER BY\n" +
            "\tnode_order", nativeQuery = true)
    List<SysDictNode> findAllLoadNodeMoreThanZero();
}