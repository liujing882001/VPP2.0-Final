package com.example.vvpdomain;

import com.example.vvpdomain.entity.SysDictType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author zph
 * @description 系统类型字典数据表
 * @date 2022-07-01
 */
@Repository
public interface SysDictTypeRepository extends JpaRepository<SysDictType, String>, JpaSpecificationExecutor<SysDictType> {

    List<SysDictType> findAllBySystemIdIn(@Param("SystemIds") Collection<String> SystemKeys);


    List<SysDictType> findAllBySystemNameContains(String systemName);


    List<SysDictType> findAllBySystemNameIn(List<String> systemNames);

    SysDictType findBySystemName(String systemName);

}