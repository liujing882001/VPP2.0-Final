package com.example.vvpdomain;

import com.example.vvpdomain.entity.DemandRespTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author maoyating
 * @description 需求响应任务
 * @date 2022-08-09
 */
@Repository
public interface DemandRespTaskRepository extends JpaRepository<DemandRespTask, String>,
        JpaSpecificationExecutor<DemandRespTask> {

    @Query(value = "SELECT count(DISTINCT drsn.no_households) from demand_resp_task drt," +
            "demand_resp_strategy drs,demand_resp_strategy_no drsn WHERE " +
            " drt.resp_id=drs.resp_id and drs.s_id = drsn.s_id and drsn.drs_status=25" +
            " and drt.d_status=3 and drsn.node_id IN (:nodeIds) ", nativeQuery = true)
    int countUserNum(@Param("nodeIds") Collection<String> nodeIds);

    DemandRespTask findByRespId(String respId);

    /**
     * 获取一定时间内 根据不同的响应级别统计的总响应电量
     * @param startDate
     * @param endDate
     * @return
     */
//    @Query(value=" select resp_level,sum(declare_power) as declare_power from " +
//            "(select d.resp_level,round(cast((cast(round(cast(date_part('epoch', d.re_time::time - d.rs_time::time)/60/60 as numeric ),2)as numeric) * declare_load) as numeric),2) as declare_power " +
//            "from demand_resp_task d where d.d_status=3 and d.rs_date>=:startDate and d.rs_date<=:endDate) as d " +
//            "group by resp_level",nativeQuery = true)
//    List<Object[]> findActualPowerByDate(@Param("startDate")Date startDate,@Param("endDate")Date endDate);

    /**
     * 获取一定时间内 根据不同的响应级别统计的总响应电量
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(value = " select resp_level,round(cast(sum(actual_power) as numeric),2) as actual_power " +
            "from demand_resp_task d where d.d_status=3 and d.rs_date>=:startDate and d.rs_date<=:endDate " +
            "group by resp_level", nativeQuery = true)
    List<Object[]> findActualPowerByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 根据节点、起始截止日期、申报状态、任务执行状态 查询需求响应任务列表分页
     *
     * @param nodeIds   节点列表
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @param drsStatus 申报状态 状态（11-未申报 12-执行中未申报 15-已结束未申报 21-待出清已申报  22-出清成功已申报 23-出清失败已申报 24-执行中已申报 25-已结束已申报）
     * @param dStatus   任务执行状态 状态（0-删除 1-未开始 2-执行中 3-已完成）
     * @return
     */
    @Query(value = "select drt.resp_id,drt.task_code,drt.rs_time,drt.re_time,drt.rs_date,drt.resp_load,drt.resp_subsidy," +
            "drt.resp_type,drt.resp_level,sum(drsn.declare_load) declare_load," +
            "sum(drsn.profit) profit,sum(drsn.volume_profit) volume_profit" +
            " from demand_resp_task drt,demand_resp_strategy drs,demand_resp_strategy_no drsn " +
            "where drt.resp_id = drs.resp_id and drs.s_id = drsn.s_id and drt.d_status=:dStatus " +
            "and drt.rs_date >= :startDate and drt.rs_date <=:endDate " +
            "and drsn.drs_status = :drsStatus " +
            "and drsn.node_id in (:nodeIds) " +
            "group by drt.resp_id,drt.task_code,drt.rs_time,drt.re_time,drt.rs_date,drt.resp_load,drt.resp_subsidy," +
            "drt.resp_type,drt.resp_level" +
            " order by drt.rs_date desc, drt.rs_time desc " +
            "LIMIT :pageSize OFFSET :pageNum", nativeQuery = true)
    List<Object[]> findByRsDateAndNodeIdsPage(@Param("nodeIds") Collection<String> nodeIds,
                                              @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                              @Param("drsStatus") Integer drsStatus, @Param("dStatus") Integer dStatus,
                                              @Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);

    /**
     * 根据节点、起始截止日期、申报状态、任务执行状态 查询需求响应任务列表总数
     *
     * @param nodeIds   节点列表
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @param drsStatus 申报状态 状态（11-未申报 12-执行中未申报 15-已结束未申报 21-待出清已申报  22-出清成功已申报 23-出清失败已申报 24-执行中已申报 25-已结束已申报）
     * @param dStatus   任务执行状态 状态（0-删除 1-未开始 2-执行中 3-已完成）
     * @return
     */
    @Query(value = "select count(drt.resp_id) from (" +
            "select drt.resp_id from demand_resp_task drt,demand_resp_strategy drs,demand_resp_strategy_no drsn " +
            "where drt.resp_id = drs.resp_id and drs.s_id = drsn.s_id and drt.d_status=:dStatus " +
            "and drt.rs_date >= :startDate and drt.rs_date <=:endDate " +
            "and drsn.drs_status = :drsStatus " +
            "and drsn.node_id in (:nodeIds) " +
            "group by drt.resp_id) drt", nativeQuery = true)
    Integer countByRsDateAndNodeIds(@Param("nodeIds") Collection<String> nodeIds,
                                    @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                    @Param("drsStatus") Integer drsStatus, @Param("dStatus") Integer dStatus);


    /**
     * 根据日期查询过去的时间发生过响应时间的节点
     *
     * @return
     */
    @Query(value = "select dr.rs_date,dr.rs_time,dr.re_time,drsn.node_id from demand_resp_task dr," +
            "demand_resp_strategy drs,demand_resp_strategy_no drsn  " +
            " where dr.resp_id = drs.resp_id and drs.s_id = drsn.s_id and dr.d_status=3 and drsn.drs_status=25 " +
            " and dr.rs_date in (:rsDates) ", nativeQuery = true)
    List<Object[]> findByRsDates(@Param("rsDates") Collection<Date> rsDates);

    /**
     * 批量更新任务状态
     * @param respIds
     * @return
     */
    @Query(value = "update demand_resp_task set declare_status=:declareStatus,update_time=now() where resp_id in (:respIds) ", nativeQuery = true)
    @Modifying
    @Transactional
    int updateBatchStatus(@Param("respIds") String[] respIds,@Param("declareStatus")Integer declareStatus);

    /**
     * 更新响应任务的定时任务id
     * @param respId
     * @return
     */
    @Query(value = "update demand_resp_task set job_id=:jobId,update_time=now() where resp_id = :respId ", nativeQuery = true)
    @Modifying
    @Transactional
    int updateBatchJobId(@Param("respId") String respId,@Param("jobId")Long jobId);

    /**
     * 查询多个任务id的任务信息
     * @param respIds
     * @return
     */
    @Query(value = "select dr.* from demand_resp_task dr " +
            " where dr.resp_id in (:respIds) ", nativeQuery = true)
    List<DemandRespTask> findByRespIdList(@Param("respIds") String[] respIds);

    /**
     * 更新响应任务的状态
     * @param respId
     * @return
     */
    @Query(value = "update demand_resp_task set " +
            "d_status=:dStatus,update_time=now() where resp_id = :respId ", nativeQuery = true)
    @Modifying
    @Transactional
    int updateStatus(@Param("respId") String respId,
                     @Param("dStatus")Integer dStatus);

    /**
     * 更新已过期的响应任务
     * @return
     */
    @Query(value = "update demand_resp_task set d_status=0,update_time=now() where  (rs_time<now() and (( d_status=1 and declare_status !=3 ) or d_status=4)) or (re_time<now() and ( d_status in (1,4))) ", nativeQuery = true)
    @Modifying
    @Transactional
    int updateExpiredTasks();
}