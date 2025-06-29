package com.example.vvpdomain;

import com.example.vvpdomain.entity.ScheduleStrategyView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description 设备表
 * @date 2022-07-01
 */
@Repository
public interface ScheduleStrategyViewRepository extends JpaRepository<ScheduleStrategyView, String>, JpaSpecificationExecutor<ScheduleStrategyView> {

    List<ScheduleStrategyView> findAllByStrategyId(String strategyId);

    List<ScheduleStrategyView> findAllByIsDemandResponse(boolean isDemandResponse);
}
