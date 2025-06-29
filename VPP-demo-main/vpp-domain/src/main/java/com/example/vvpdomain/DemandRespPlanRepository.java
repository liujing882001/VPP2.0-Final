package com.example.vvpdomain;

import com.example.vvpdomain.entity.DemandRespPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author maoyating
 * @description 需求响应策略申报信息
 * @date 2022-08-09
 */
@Repository
public interface DemandRespPlanRepository extends JpaRepository<DemandRespPlan, String>,
        JpaSpecificationExecutor<DemandRespPlan> {


}