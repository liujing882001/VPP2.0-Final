package com.example.vvpdomain;


import com.example.vvpdomain.entity.StationNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StationNodeRepository extends JpaRepository<StationNode, String>, JpaSpecificationExecutor<StationNode> {

    @Query(value = "SELECT * FROM station_node where (station_id = :nodeId or parent_id = :nodeId)",nativeQuery = true)
    List<StationNode> findAllInfoByStationId(@Param("nodeId") String nodeId);


    /**
     * 递归查询
     * 根据节点ID,递归查询该节点与他所有父节点ID
     */
    @Query(value = "WITH RECURSIVE node_hierarchy AS (" +
            "    SELECT " +
            "        station_id, " +
            "        parent_id " +
            "    FROM " +
            "        station_node" +
            "    WHERE " +
            "        station_id = :nodeId " +
            "    UNION ALL" +
            "    SELECT " +
            "        n.station_id, " +
            "        n.parent_id " +
            "    FROM " +
            "        station_node n" +
            "    INNER JOIN " +
            "        node_hierarchy nh" +
            "    ON " +
            "        n.station_id = nh.parent_id" +
            ")" +
            "SELECT * FROM node_hierarchy;", nativeQuery = true)
    List<Object[]> findNodeHierarchy(@Param("nodeId") String nodeId);


    /**
     * 递归查询
     * 根据节点ID,递归查询该节点与他所有子节点ID
     */
    @Query(value = "WITH RECURSIVE node_hierarchy AS (" +
            "    SELECT " +
            "        station_id, " +
            "        parent_id " +
            "    FROM " +
            "        station_node" +
            "    WHERE " +
            "        station_id = :nodeId " +
            "    UNION ALL" +
            "    SELECT " +
            "        n.station_id, " +
            "        n.parent_id " +
            "    FROM " +
            "        station_node n" +
            "    INNER JOIN " +
            "        node_hierarchy nh" +
            "    ON " +
            "        n.parent_id = nh.station_id" +
            ")" +
            "SELECT * FROM node_hierarchy;", nativeQuery = true)
    List<Object[]> findNodeHierarchyChild(@Param("nodeId") String nodeId);

    @Query(value = "WITH ParentNode AS (" +
            "    SELECT parent_id " +
            "    FROM station_node " +
            "    WHERE station_id = :stationId" +
            ") " +
            "SELECT sn.* " +
            "FROM station_node sn " +
            "JOIN ParentNode pn " +
            "    ON sn.station_id = pn.parent_id " +
            "    OR sn.parent_id = pn.parent_id",
            nativeQuery = true)
    List<StationNode> findNodesByStationId(@Param("stationId") String stationId);
    @Query(value = "SELECT station_id FROM station_node where parent_id = :nodeId and station_type = :stationType ",nativeQuery = true)
    List<String> allStationIdByParentIdAndStationType(@Param("nodeId") String nodeId,@Param("stationType") String stationType);
    @Query(value = "SELECT station_id FROM station_node where (station_id = :nodeId or parent_id = :nodeId)",nativeQuery = true)
    List<String> findAllInfoByStationIdToString(@Param("nodeId") String nodeId);
    @Query(value = "SELECT station_name FROM station_node where station_id in (:nodeIds) and station_state = '运营中' and station_category = '项目' ",nativeQuery = true)
    List<String> findAllNameByNodeIdToString(@Param("nodeIds") List<String> nodeIds);
    @Query(value = "SELECT * FROM station_node where station_id in (:nodeIds) and station_category = '项目' ",nativeQuery = true)
    List<StationNode> findAllByNodeIdsAndSc(@Param("nodeIds") List<String> nodeIds);
    @Query(value = "SELECT * FROM station_node where station_id in (:nodeIds) ",nativeQuery = true)
    List<StationNode> findAllByNodeIds(@Param("nodeIds") List<String> nodeIds);
    @Query(value = "SELECT station_id FROM station_node where station_id in (:nodeIds) and parent_id = '' ",nativeQuery = true)
    List<String> listActSNForUser(@Param("nodeIds") List<String> nodeIds);
    @Query(value = "SELECT * FROM station_node where station_id = :nodeId",nativeQuery = true)
    StationNode findByStationId(@Param("nodeId") String nodeId);

    List<StationNode> findAllByStationCategory(String stationCategory);

    List<StationNode> findAllByStationIdIn(List<String> stationIds);

    @Query(value = "SELECT sn FROM StationNode sn where sn.stationState = '运营中'")
    List<StationNode> findAllNodeInOperation();

    @Query("SELECT s FROM StationNode s WHERE s.stationCategory = '项目' AND s.stationState = '运营中'")
    List<StationNode> findProjectStationsInOperation();
    @Query("SELECT s.stationId FROM StationNode s WHERE s.stationCategory = '项目' AND s.stationState = '运营中'")
    List<String> findAllStationId();
    @Query("SELECT s FROM StationNode s WHERE s.stationTypeId = :stationTypeId AND s.stationState = '运营中'")
    List<StationNode> findAllByStationTypeIdAndStationState(@Param("stationTypeId") String stationTypeId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM station_node WHERE station_id = :nodeId ", nativeQuery = true)
    void deleteByStationId(@Param("nodeId") String nodeId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM station_node WHERE (station_id = :nodeId or parent_id = :nodeId)", nativeQuery = true)
    void deleteByStationIdAndParentId(@Param("nodeId") String nodeId);

    @Query("SELECT DISTINCT sn.stationId FROM StationNode sn " +
            "JOIN Node n ON sn.stationId = n.nodeId " +
            "WHERE n.nodeId IN :nodeIds " +
            "AND sn.stationState = '运营中' " +
            "AND n.nodePostType = :nodePostType")
    List<String> findAllByNodeIdsAndStationType(@Param("nodeIds") List<String> nodeIds, @Param("nodePostType") String nodePostType);

    @Query("SELECT DISTINCT sn.stationId FROM StationNode sn " +
            "JOIN Node n ON sn.stationId = n.nodeId " +
            "WHERE n.nodeId IN :nodeIds " +
            "AND sn.stationState = '运营中' " +
            "AND n.nodeType.nodeTypeId = :nodeTypeId " +
            "AND n.nodePostType = :nodePostType")
    List<String> findAllByNodeIdsAndStationType(@Param("nodeIds") List<String> nodeIds, @Param("nodeTypeId") String nodeTypeId, @Param("nodePostType") String nodePostType);


    List<StationNode>findAllByParentIdAndStationType(String parentId,String stationType);

    List<StationNode>findAllByParentId(String parentId);

    StationNode findByStationName(String stationName);
    @Query(value = "SELECT * FROM station_node where parent_id in (:parentIds) ", nativeQuery = true)
    List<StationNode> findAllByParents(@Param("parentIds") List<String> parentIds);
    @Query(value = "SELECT * FROM station_node where station_id = :parentId", nativeQuery = true)
    StationNode findByStationIdAlarm(@Param("parentId")String parentId);

    @Query("SELECT s.stationId FROM StationNode s WHERE s.systemIds LIKE %:systemId% AND s.stationState = :stationState")
    List<String> findStationIdsBySystemIdAndStationState(@Param("systemId") String systemId, @Param("stationState") String stationState);

    List<StationNode> findAllByStationType(String StationType);

    @Query(value = "WITH RECURSIVE tree AS (" +
            "    SELECT station_id, parent_id, station_name " +
            "    FROM station_node " +
            "    WHERE station_id = :stationId " +
            "    UNION ALL " +
            "    SELECT sn.station_id, sn.parent_id, sn.station_name " +
            "    FROM station_node sn " +
            "    INNER JOIN tree t ON sn.parent_id = t.station_id" +
            "), " +
            "parent_tree AS (" +
            "    SELECT station_id, parent_id, station_name " +
            "    FROM station_node " +
            "    WHERE station_id = :stationId " +
            "    UNION ALL " +
            "    SELECT sn.station_id, sn.parent_id, sn.station_name " +
            "    FROM station_node sn " +
            "    INNER JOIN parent_tree pt ON sn.station_id = pt.parent_id" +
            ") " +
            "SELECT * FROM tree " +
            "UNION " +
            "SELECT * FROM parent_tree", nativeQuery = true)
    List<Object[]> findTreeByStationId(@Param("stationId") String stationId);

    @Query(value = "WITH RECURSIVE tree AS (" +
            "    SELECT " +
            "        station_id," +
            "        parent_id," +
            "        station_name" +
            "    FROM " +
            "        station_node" +
            "    WHERE " +
            "        station_id = :stationId " +
            "    UNION ALL" +
            "    SELECT " +
            "        sn.station_id," +
            "        sn.parent_id," +
            "        sn.station_name" +
            "    FROM " +
            "        station_node sn" +
            "    INNER JOIN " +
            "        tree t" +
            "    ON " +
            "        sn.parent_id = t.station_id " +
            ")" +
            "SELECT * FROM tree", nativeQuery = true)
    List<Object[]> findSubTreeByStationId(@Param("stationId") String stationId);
    @Query(value = "WITH RECURSIVE tree AS (" +
            "    SELECT " +
            "        station_id," +
            "        station_type_id," +
            "        system_ids" +
            "    FROM " +
            "        station_node" +
            "    WHERE " +
            "        station_id IN :stationIds " +
            "    UNION ALL" +
            "    SELECT " +
            "        sn.station_id," +
            "        sn.station_type_id," +
            "        sn.system_ids" +
            "    FROM " +
            "        station_node sn" +
            "    INNER JOIN " +
            "        tree t" +
            "    ON " +
            "        sn.parent_id = t.station_id " +
            ")" +
            "SELECT t.station_id, t.station_type_id, t.system_ids FROM tree t", nativeQuery = true)
    List<Object[]> findSubTreeByStationIds(@Param("stationIds") List<String> stationIds);

    @Query(value = "WITH RECURSIVE tree AS (" +
            "    SELECT " +
            "        station_id," +
            "        system_ids" +
            "    FROM " +
            "        station_node" +
            "    WHERE " +
            "        station_id = :stationId " +
            "    UNION ALL" +
            "    SELECT " +
            "        sn.station_id," +
            "        sn.system_ids" +
            "    FROM " +
            "        station_node sn" +
            "    INNER JOIN " +
            "        tree t" +
            "    ON " +
            "        sn.parent_id = t.station_id " +
            ")" +
            "SELECT t.station_id FROM tree t WHERE t.system_ids LIKE %:keyword%", nativeQuery = true)
    List<String> findSubEnergyIdsByStationIds(@Param("stationId") String stationId, @Param("keyword") String keyword);
    @Query(value = "WITH RECURSIVE tree AS (" +
            "    SELECT " +
            "        station_id," +
            "        system_ids" +
            "    FROM " +
            "        station_node" +
            "    WHERE " +
            "        station_id = :stationId " +
            "    UNION ALL" +
            "    SELECT " +
            "        sn.station_id," +
            "        sn.system_ids" +
            "    FROM " +
            "        station_node sn" +
            "    INNER JOIN " +
            "        tree t" +
            "    ON " +
            "        sn.parent_id = t.station_id " +
            ")" +
            "SELECT t.station_id FROM tree t WHERE t.system_ids NOT LIKE %:keyword%", nativeQuery = true)
    List<String> findSubLoadIdsByStationIds(@Param("stationId") String stationId, @Param("keyword") String keyword);

    @Query(value = "SELECT sn.station_id, sn.parent_id, sn.station_name, COUNT(d.device_id) AS device_count " +
            "FROM station_node sn " +
            "LEFT JOIN device d ON sn.station_id = d.node_id " +
            "WHERE sn.station_id IN (:stationIds) " +
            "GROUP BY sn.station_id, sn.parent_id, sn.station_name", nativeQuery = true)
    List<Object[]> findTreeByRole(@Param("stationIds") List<String> stationIds);

    @Query(value = "WITH RECURSIVE parent_tree AS (" +
            "    SELECT * " +
            "    FROM station_node " +
            "    WHERE station_id = :stationId " +
            "    UNION ALL " +
            "    SELECT sn.* " +
            "    FROM station_node sn " +
            "    INNER JOIN parent_tree pt ON sn.station_id = pt.parent_id" +
            ") " +
            "SELECT * " +
            "FROM parent_tree " +
            "WHERE parent_id = '' " +
            "LIMIT 1",
            nativeQuery = true)
    StationNode findTopByStationId(@Param("stationId") String stationId);

}
