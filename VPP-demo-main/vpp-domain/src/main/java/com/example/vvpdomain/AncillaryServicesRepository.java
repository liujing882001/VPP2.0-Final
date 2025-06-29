package com.example.vvpdomain;

import com.example.vvpdomain.entity.AncillaryServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author maoyating
 * @description 辅助任务
 * @date 2022-08-09
 */
@Repository
public interface AncillaryServicesRepository extends JpaRepository<AncillaryServices, String>,
        JpaSpecificationExecutor<AncillaryServices> {


}