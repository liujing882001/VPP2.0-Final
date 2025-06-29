package com.example.vvpdomain;

import com.example.vvpdomain.entity.ScheduleStrategyDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 运行策略-可调负荷运行策略设备中间表  多对的
 */
@Repository
public interface ScheduleStrategyDeviceRepository extends JpaRepository<ScheduleStrategyDevice, String>, JpaSpecificationExecutor<ScheduleStrategyDevice> {

    /**
     * 根据序列号联表查出序列号集合
     *
     * @param strategyId
     * @return
     */
    @Query(value = "select d.device_sn from device d,schedule_strategy_device ssd " +
            "where d.device_id=ssd.device_id and ssd.strategy_id=:strategyId", nativeQuery = true)
    List<String> findDeviceSnByStrategyId(@Param("strategyId") String strategyId);


    @Query(value = "delete from schedule_strategy_device where strategy_Id= :strategyId ", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteAllByStrategyId(@Param("strategyId") String strategyId);

    List<ScheduleStrategyDevice> findAllByDeviceId(String deviceId);
}
