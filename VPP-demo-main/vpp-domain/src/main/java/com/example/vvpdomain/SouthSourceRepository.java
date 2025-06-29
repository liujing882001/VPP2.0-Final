package com.example.vvpdomain;


import com.example.vvpdomain.entity.SouthSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SouthSourceRepository extends JpaRepository<SouthSource, String>, JpaSpecificationExecutor<SouthSource> {

    @Query(value = "SELECT * FROM station_node where resource_id = :resourceId",nativeQuery = true)
    List<SouthSource> findAllInfoByResourceId(@Param("resourceId") String resourceId);
}
