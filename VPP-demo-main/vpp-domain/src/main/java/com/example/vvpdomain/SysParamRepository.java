package com.example.vvpdomain;

import com.example.vvpdomain.entity.SysParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author zph
 * @description 系统参数
 * @date 2022-07-24
 */
@Repository
public interface SysParamRepository extends JpaRepository<SysParam, String>, JpaSpecificationExecutor<SysParam> {

    SysParam findSysParamBySysParamKey(int sysParamKey);
}