package com.example.vvpdomain;


import com.example.vvpdomain.entity.DemandLoadRegulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author maoyating
 * @description 负荷调节
 * @date 2024-03-11
 */
@Repository
public interface DemandLoadRegulationRepository extends JpaRepository<DemandLoadRegulation, String>,
        JpaSpecificationExecutor<DemandLoadRegulation> {
}
