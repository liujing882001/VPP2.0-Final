package com.example.vvpdomain;

import com.example.vvpdomain.entity.Node;
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
 * @description node
 * @date 2022-07-01
 */
@Repository
public interface NodeRepository extends JpaRepository<Node, String>, JpaSpecificationExecutor<Node> {


	Node findByNodeId(String nodeId);

	Node findByNodeName(String nodeName);

	List<Node> findAllByNodeName(String nodeName);

	List<Node> findAllByNodeIdIn(Collection<String> nodeIds);

	@Query(value = "SELECT\n" +
			"\tcount( node_type_id = 'louyu' OR NULL ) AS louyu,\n" +
			"\tcount( node_type_id = 'gongchang' OR NULL ) AS gongchang,\n" +
			"\tcount( node_post_type = 'storageEnergy' OR NULL ) AS chuneng,\n" +
			"\tcount( node_post_type = 'pv' OR NULL ) AS guangfu \n" +
			"FROM\n" +
			"\tnode where node_id in (:nodeIds);", nativeQuery = true)
	List<Object[]> findSumNodeType(@Param("nodeIds") Collection<String> nodeIds);

	List<Node> findAllByNodeIdIn(List<String> nodeIds);

	List<Node> findAllByNodeIdInAndNodePostType(List<String> nodeIds, String nodePostType);


	//获取节点类型 光伏 储能  负荷 NodePostTypeEnum
	List<Node> findAllByNodePostType(String nodePostType);

	Node findByNoHouseholds(String noHouseholds);

	Node findByNoHouseholdsAndNodeId(String noHouseholds, String nodeId);

	List<Node> findAllByNodeIdInAndNodeType_NodeTypeId(Collection<String> nodeIds, String nodeTypeId);

	List<Node> findAllByNodeIdInAndNodeNameContains(List<String> nodeIds, String nodeName);

	@Query(value = "SELECT node_type_id,\"count\"(1) as node_type_number from node  where node_id in (:nodeIds) and  node_type_id  IN (:nodeTypeIds) GROUP BY node_type_id", nativeQuery = true)
	List<Object[]> findCountNodeType(@Param("nodeIds") Collection<String> nodeIds, @Param("nodeTypeIds") Collection<String> nodeTypeIds);

	@Query(value = "SELECT node_id, node_name from node where node_id in (:nodeIds)", nativeQuery = true)
	List<Object[]> findNodeIdAndNodeName(@Param("nodeIds") Collection<String> nodeIds);
	@Query(value = "SELECT CONCAT(node_id, node_type_id) from node where node_post_type = :nodePostType", nativeQuery = true)
	List<String> findByNodeIdAndNodePostType(@Param("nodePostType") String nodePostType);
	List<Node> findAllBySystemIdsContains(String systemId);

	@Query(value = "UPDATE node set online= true , is_enabled =true WHERE node_id=:nodeId ", nativeQuery = true)
	@Transactional
	@Modifying
	void updateNodeStatus(@Param("nodeId") String nodeId);

	List<Node> findAllByNodeType_NodeTypeIdAndNodePostType(@Param("nodeTypeId")String nodeTypeId, @Param("nodePostType")String nodePostType);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM node WHERE node_id IN :ids", nativeQuery = true)
	void deleteAllByIdIn(@Param("ids") List<String> ids);

}