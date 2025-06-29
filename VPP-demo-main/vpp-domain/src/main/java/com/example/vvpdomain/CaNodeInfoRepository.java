package com.example.vvpdomain;

import com.example.vvpdomain.entity.CaNodeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author maoyating
 * @description 碳模型-基本信息
 * @date 2022-08-09
 */
@Repository
public interface CaNodeInfoRepository extends JpaRepository<CaNodeInfo, String>,
        JpaSpecificationExecutor<CaNodeInfo> {

}