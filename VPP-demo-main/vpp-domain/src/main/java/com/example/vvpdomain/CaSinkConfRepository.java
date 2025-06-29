package com.example.vvpdomain;

import com.example.vvpdomain.entity.CaSinkConf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author maoyating
 * @description 碳汇配置
 * @date 2022-08-09
 */
@Repository
public interface CaSinkConfRepository extends JpaRepository<CaSinkConf, String>,
        JpaSpecificationExecutor<CaSinkConf> {

}