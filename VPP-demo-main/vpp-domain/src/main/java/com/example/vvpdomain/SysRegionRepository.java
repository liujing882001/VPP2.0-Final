package com.example.vvpdomain;

import com.example.vvpdomain.entity.SysRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description 系统类型字典数据表
 * @date 2022-07-01
 */
@Repository
public interface SysRegionRepository extends JpaRepository<SysRegion, String>, JpaSpecificationExecutor<SysRegion> {

    List<SysRegion> findAllByRegionLevel(String regionLevel);

    List<SysRegion> findAllByRegionLevelAndRegionParentId(String regionLevel, String regionId);

    List<SysRegion> findAllByRegionIdIn(List<String> regions);

}