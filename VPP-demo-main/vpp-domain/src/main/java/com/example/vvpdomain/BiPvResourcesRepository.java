package com.example.vvpdomain;

import com.example.vvpdomain.entity.BiPvResources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

/**
 * @author zph
 * @description 光伏资源-电站列表信息
 * @date 2022-08-10
 */
@Repository
public interface BiPvResourcesRepository extends JpaRepository<BiPvResources, String>, JpaSpecificationExecutor<BiPvResources> {

    List<BiPvResources> findAllByNodeIdIn(Collection<String> nodeIds);

    @Query(value = "delete from bi_photovoltaic_resources where node_id= :nodeId ", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteAllByNodeId(@Param("nodeId") String nodeId);


    @Query(value = "DELETE FROM bi_photovoltaic_resources WHERE node_id NOT IN (SELECT node_id FROM node WHERE node_post_type = 'pv')", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteAllInvalidNode();
}