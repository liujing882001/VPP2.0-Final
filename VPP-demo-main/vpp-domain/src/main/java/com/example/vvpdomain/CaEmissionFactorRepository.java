package com.example.vvpdomain;

import com.example.vvpdomain.entity.CaEmissionFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author maoyating
 * @description 碳模型-碳排放因子
 * @date 2022-08-09
 */
@Repository
public interface CaEmissionFactorRepository extends JpaRepository<CaEmissionFactor, String>,
        JpaSpecificationExecutor<CaEmissionFactor> {

    @Query(value = " select COALESCE(sum(COALESCE(cs.discharge_value::::float,0)*COALESCE(cef.co2,0)),0) from ca_scope cs join ca_emission_factor cef on cs.scope_type=cef.scope_type and cs.discharge_entity = cef.emission_factor_num where cs.scope_type=:type :clause", nativeQuery = true)
    Object[] findCount(@Param("type") Integer type, @Param("clause") String clause);

    @Query(value = " select * from ca_emission_factor where emission_factor_name=:emission_factor_name and province=:province and s_status=1", nativeQuery = true)
    List<CaEmissionFactor> findByNameAndProvince(@Param("emission_factor_name") String emissionFactorName, @Param("province") String province);
}