package com.example.vvpdomain;

import com.example.vvpdomain.entity.CaScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author maoyating
 * @description 碳模型-范围
 * @date 2022-08-09
 */
@Repository
public interface CaScopeRepository extends JpaRepository<CaScope, String>,
        JpaSpecificationExecutor<CaScope> {

}