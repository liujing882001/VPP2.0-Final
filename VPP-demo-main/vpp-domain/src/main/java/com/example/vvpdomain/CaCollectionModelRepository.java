package com.example.vvpdomain;

import com.example.vvpdomain.entity.CaCollectionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author maoyating
 * @description 碳模型-碳排放因子
 * @date 2022-08-09
 */
@Repository
public interface CaCollectionModelRepository extends JpaRepository<CaCollectionModel, String>,
        JpaSpecificationExecutor<CaCollectionModel> {

//    @Query(value=" select COALESCE(sum(COALESCE(cs.discharge_value::::float,0)*COALESCE(cef.co2,0)),0) from ca_scope cs join ca_emission_factor cef on cs.scope_type=cef.scope_type and cs.discharge_entity = cef.emission_factor_num where cs.scope_type=:type :clause",nativeQuery = true)
//    Object[] findCount(@Param("type") Integer type,@Param("clause") String clause);

}