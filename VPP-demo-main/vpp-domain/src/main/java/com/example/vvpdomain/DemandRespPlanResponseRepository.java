package com.example.vvpdomain;

import com.example.vvpdomain.entity.DemandRespPlanResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author maoyating
 * @description 每栋楼平均功率-南网
 * @date 2022-08-09
 */
@Repository
public interface DemandRespPlanResponseRepository extends JpaRepository<DemandRespPlanResponse, String>,
        JpaSpecificationExecutor<DemandRespPlanResponse> {


}