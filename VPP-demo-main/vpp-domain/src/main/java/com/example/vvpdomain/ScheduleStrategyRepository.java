package com.example.vvpdomain;

import com.example.vvpdomain.entity.ScheduleStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 运行策略-可调负荷运行策略
 */
@Repository
public interface ScheduleStrategyRepository extends JpaRepository<ScheduleStrategy, String>, JpaSpecificationExecutor<ScheduleStrategy> {

    ScheduleStrategy findByUserIdAndStrategyId(String userId, String strategyId);

    ScheduleStrategy findByStrategyId(String strategyId);

    ScheduleStrategy findByStrategyName(String strategyName);

    ScheduleStrategy findByStrategyNameAndUserId(String strategyName, String userId);

    List<ScheduleStrategy> findAllByUserIdAndIsDemandResponse(String userId, boolean isDemandResponse);

    List<ScheduleStrategy> findAllByStrategyIdIn(List<String> strategyIds);

    List<ScheduleStrategy> findAllByStrategyIdInAndUserId(List<String> strategyIds, String userId);

    List<ScheduleStrategy> findAllByUserId(String userId);

}
