package com.example.vvpdomain;

import com.example.vvpdomain.entity.AlarmLog;
import com.example.vvpdomain.entity.BiStorageEnergyResources;
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
 * @description 储能资源-电站列表信息
 * @date 2022-08-12
 */
@Repository
public interface BiStorageEnergyResourcesRepository extends JpaRepository<BiStorageEnergyResources, String>, JpaSpecificationExecutor<BiStorageEnergyResources> {



    @Query(value = "select e from BiStorageEnergyResources e where e.nodeId= ?1")
    BiStorageEnergyResources findByNodeId(@Param("nodeId") String nodeId);


    List<BiStorageEnergyResources> findAllByNodeIdIn(Collection<String> nodeIds);

    @Query(value = "delete from bi_storage_energy_resources where node_id= :nodeId ", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteAllByNodeId(@Param("nodeId") String nodeId);

    @Query(value = "DELETE FROM bi_storage_energy_resources WHERE node_id NOT IN (SELECT node_id FROM node WHERE node_post_type = 'storageEnergy')", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteAllInvalidNode();
}