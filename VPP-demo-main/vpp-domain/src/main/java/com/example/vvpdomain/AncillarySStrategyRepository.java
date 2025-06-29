package com.example.vvpdomain;

import com.example.vvpdomain.entity.AncillarySStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * @author maoyating
 * @description 辅助服务策略
 * @date 2022-08-09
 */
@Repository
public interface AncillarySStrategyRepository extends JpaRepository<AncillarySStrategy, String>,
        JpaSpecificationExecutor<AncillarySStrategy> {

    @Query(value = "delete from ancillary_s_strategy where as_id= :asId ", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteAsId(@Param("asId") String asId);
}