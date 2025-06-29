package com.example.vvpdomain;

import com.example.vvpdomain.view.NodeInfoView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeInfoViewRepository extends JpaRepository<NodeInfoView, String>, JpaSpecificationExecutor<NodeInfoView> {

    List<NodeInfoView> findAllByNodePostType(String nodePostType);

    @Query(value = "SELECT * FROM node_info_view " +
            "WHERE (:nodeIds IS NULL OR :nodeIds = '' OR node_id IN :nodeIds) " +
            "AND node_post_type = :nodePostType", nativeQuery = true)
    List<NodeInfoView> findAllByNodeIdsAndNodePostType(@Param("nodeIds") List<String> nodeIds,
                                                       @Param("nodePostType") String nodePostType);

}
