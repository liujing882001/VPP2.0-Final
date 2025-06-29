package com.example.vvpdomain;

import com.example.vvpdomain.entity.DemandRespStrategyNo;
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
 * @description 需求响应策略申报信息
 * @date 2022-08-09
 */
@Repository
public interface DemandRespStrategyNoRepository extends JpaRepository<DemandRespStrategyNo, String>,
        JpaSpecificationExecutor<DemandRespStrategyNo> {

    @Query(value = "UPDATE demand_resp_strategy_no " +
            "SET drs_status = 21 ,winning_bid=3" +
            "WHERE drs_id in (:drsIds) ", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    @Transactional
    void updateStatus(@Param("drsIds") Collection<String> drsIds);

    @Query(value = " select count(no_households) as noNum,COALESCE(sum(declare_load), 0) as totalLoad from demand_resp_strategy_no " +
            " where s_id in (:sIds) ", nativeQuery = true)
    Object[] findStrategyCount(@Param("sIds") Collection<String> sIds);

    @Query(value = " select * from demand_resp_strategy_no where drs_id =:drsId ", nativeQuery = true)
    List<DemandRespStrategyNo> findByDrsId(@Param("drsId") String drsId);

    @Query(value = " select n.* from demand_resp_strategy_no n,demand_resp_strategy s " +
            "where n.s_id =s.s_id and s.resp_id=:respId ", nativeQuery = true)
    List<DemandRespStrategyNo> findNoListByRespId(@Param("respId") String respId);

    /**
     * 状态为25-已结束已申报 的户号相关信息
     *
     * @param respIds
     * @return
     */
    @Query(value = " select n.* from demand_resp_strategy_no n,demand_resp_strategy s " +
            "where n.s_id =s.s_id and n.drs_status=25 and s.resp_id in (:respIds) ", nativeQuery = true)
    List<DemandRespStrategyNo> findNoListByRespIds(@Param("respIds") Collection<String> respIds);


    /**
     * 查询特定状态 的户号相关信息
     *
     * @param respId
     * @return
     */
    @Query(value = " select n.* from demand_resp_strategy_no n,demand_resp_strategy s " +
            "where n.s_id =s.s_id and n.drs_status=:drsStatus and s.resp_id = :respId ", nativeQuery = true)
    List<DemandRespStrategyNo> findNoListByRespIdStatus(@Param("respId") String respId, @Param("drsStatus") int drsStatus);

    /**
     * 根据id查询户号详细信息
     *
     * @param drsIds
     * @return
     */
    @Query(value = " select n.* from demand_resp_strategy_no n " +
            "where  n.drs_id in (:drsIds) ", nativeQuery = true)
    List<DemandRespStrategyNo> findNoListByDrsIds(@Param("drsIds") Collection<String> drsIds);

    /**
     * 根据查询条件，统计不同响应类型的电量及收益
     *
     * @param nodeIds   节点列表
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @param drsStatus 申报状态 状态（11-未申报 12-执行中未申报 15-已结束未申报 21-待出清已申报  22-出清成功已申报 23-出清失败已申报 24-执行中已申报 25-已结束已申报）
     * @param dStatus   任务执行状态 状态（0-删除 1-未开始 2-执行中 3-已完成）
     * @return
     */
    @Query(value = "select drt.resp_type,sum(drsn.real_time_load) real_time_load,sum(drsn.declare_load) declare_load" +
            ",sum(drsn.profit) profit,sum(drsn.volume_profit) volume_profit " +
            "from demand_resp_task drt,demand_resp_strategy drs,demand_resp_strategy_no drsn " +
            "where drt.resp_id = drs.resp_id and drs.s_id = drsn.s_id and drt.d_status=:dStatus " +
            "and drt.rs_date >= :startDate and drt.rs_date <=:endDate " +
            "and drsn.drs_status = :drsStatus " +
            "and drsn.node_id in (:nodeIds) GROUP BY drt.resp_type", nativeQuery = true)
    List<Object[]> findBySumRsDateAndNodeIds(@Param("nodeIds") Collection<String> nodeIds,
                                             @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                             @Param("drsStatus") Integer drsStatus, @Param("dStatus") Integer dStatus);

    /**
     * 根据任务id跟节点ID查询详情列表
     *
     * @param respId
     * @param nodeIds
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Query(value = " select drsn.* from demand_resp_strategy drs,demand_resp_strategy_no drsn " +
            "where drs.s_id = drsn.s_id and drsn.drs_status=:drsStatus and drs.resp_id=:respId and drsn.node_id in (:nodeIds) " +
            " LIMIT :pageSize OFFSET :pageNum", nativeQuery = true)
    List<DemandRespStrategyNo> findByRespIdAndNodeIds(@Param("respId") String respId,
                                                      @Param("nodeIds") Collection<String> nodeIds,
                                                      @Param("drsStatus") Integer drsStatus,
                                                      @Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);

    @Query(value = " select drsn.* from demand_resp_strategy drs,demand_resp_strategy_no drsn " +
            "where drs.s_id = drsn.s_id and drs.resp_id=:respId and drsn.drs_status=:drsStatus and drsn.node_id in (:nodeIds) " +
            " LIMIT :pageSize OFFSET :pageNum", nativeQuery = true)
    List<DemandRespStrategyNo> findByRespIdAndNodeIdsAndDrsStatus(@Param("respId") String respId,
                                                      @Param("nodeIds") Collection<String> nodeIds, @Param("drsStatus") Integer drsStatus,
                                                      @Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);

    List<DemandRespStrategyNo> findByRespId(@Param("respId") String respId);
    /**
     * 根据任务id跟节点ID查询详情总数
     *
     * @param respId
     * @param nodeIds
     * @return
     */
    @Query(value = " select count(drsn.*) from demand_resp_strategy drs,demand_resp_strategy_no drsn " +
            "where drs.s_id = drsn.s_id and drs.resp_id=:respId and drsn.node_id in (:nodeIds) ", nativeQuery = true)
    Integer countRespIdAndNodeIds(@Param("respId") String respId,
                                  @Param("nodeIds") Collection<String> nodeIds);


    /**
     * 根据任务id、策略id,查询特定状态 的户号相关信息--第三方能源平台
     *
     * @param respId
     * @param platformId
     * @param drsStatus
     * @return
     */
    @Query(value = " select n.* from demand_resp_strategy_no n,demand_resp_strategy s " +
            "where n.s_id =s.s_id and n.drs_status=:drsStatus and s.resp_id = :respId " +
            "and s.strategy_id=:platformId", nativeQuery = true)
    List<DemandRespStrategyNo> findNoListByRespIdPlatformStatus(@Param("respId") String respId,
                                                                @Param("platformId") String platformId,
                                                                @Param("drsStatus") int drsStatus);

    /**
     * 批量更新中标状态
     *
     * @param drsIds
     * @param winningBid
     */
    @Query(value = "UPDATE demand_resp_strategy_no " +
            "SET winning_bid = :winningBid " +
            "WHERE drs_id in (:drsIds) ", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    @Transactional
    void updateWinningBid(@Param("drsIds") Collection<String> drsIds, @Param("winningBid") Integer winningBid);

    /**
     * 批量更新中标状态和节点状态
     *
     * @param drsIds
     * @param winningBid
     */
    @Query(value = "UPDATE demand_resp_strategy_no " +
            "SET winning_bid = :winningBid,drs_status = :drsStatus " +
            "WHERE drs_id in (:drsIds) ", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    @Transactional
    void updateWinningBidAndDrsStatus(@Param("drsIds") Collection<String> drsIds, @Param("winningBid") Integer winningBid, @Param("drsStatus") Integer drsStatus);

    /**
     * 查询中标/未发送南网的总条数
     * @param respId
     * @param winningBid
     * @return
     */
    @Query(value = " select count(n.*) from demand_resp_strategy_no n" +
            " where n.drs_status=21 and n.resp_id=:respId and n.winning_bid=:winningBid", nativeQuery = true)
    Long countNoListByRespIdBid(@Param("respId") String respId,@Param("winningBid") Integer winningBid);

    /**
     * 查询中标/未发送南网的信息(待出清已申报)
     * @param respId
     * @param winningBid
     * @return
     */
    @Query(value = " select n.* from demand_resp_strategy_no n" +
            " where n.drs_status=21 and n.resp_id=:respId and n.winning_bid=:winningBid" +
            " LIMIT :pageSize OFFSET :pageNum", nativeQuery = true)
    List<DemandRespStrategyNo> findNoListByRespIdBid(@Param("respId") String respId,
                                                     @Param("winningBid") Integer winningBid,
                                                     @Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);

    /**
     * 更新任务对应的价格
     *
     * @param respId
     * @param declarePrice
     */
    @Query(value = "UPDATE demand_resp_strategy_no " +
            " SET declare_price = :declarePrice " +
            " WHERE resp_id =:respId", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    @Transactional
    void updateDeclarePrice(@Param("respId") String respId, @Param("declarePrice") Double declarePrice);

    /**
     * 批量删除
     */
    @Query(value = "delete from demand_resp_strategy_no " +
            " WHERE resp_id =:respId and no_households in (:noHouseholds) ", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteByDrsIds(@Param("respId") String respId,@Param("noHouseholds") Collection<String> noHouseholds);

    /**
     * 更新任务对应的申报负荷
     *
     * @param respId
     * @param declareLoad
     */
    @Query(value = "UPDATE demand_resp_strategy_no " +
            " SET declare_load = :declareLoad " +
            " WHERE node_id = :nodeId " +
            "And resp_id = :respId " +
            "And drs_id like '%admin%'",
            nativeQuery = true)
    @Modifying(clearAutomatically = true)
    @Transactional
    void updateDeclareLoad(@Param("respId") String respId,
                           @Param("nodeId") String nodeId,
                           @Param("declareLoad") Double declareLoad);


    @Query(value = "UPDATE demand_resp_strategy_no " +
            " SET declare_load = :declareLoad " +
            " WHERE drs_id = :drsId ",
            nativeQuery = true)
    @Modifying(clearAutomatically = true)
    @Transactional
    void updateDeclareLoad1(@Param("drsId") String drsId,
                           @Param("declareLoad") Double declareLoad);

    /**
     * 根据统计日期，统计不同响应类型的电量及收益
     *
     * @param rsDate 统计日期
     * @param drsStatus 申报状态 状态（11-未申报 12-执行中未申报 15-已结束未申报 21-待出清已申报  22-出清成功已申报 23-出清失败已申报 24-执行中已申报 25-已结束已申报）
     * @param dStatus   任务执行状态 状态（0-删除 1-未开始 2-执行中 3-已完成）
     * @return
     */
    @Query(value = "select drsn.node_id,drt.resp_type,sum(drsn.real_time_load) real_time_load,sum(drsn.declare_load) declare_load" +
            ",sum(drsn.profit) profit,sum(drsn.volume_profit) volume_profit " +
            "from demand_resp_task drt,demand_resp_strategy drs,demand_resp_strategy_no drsn " +
            "where drt.resp_id = drs.resp_id and drs.s_id = drsn.s_id and drt.d_status=:dStatus " +
            "and drt.rs_date = :rsDate  " +
            "and drsn.drs_status = :drsStatus " +
            "GROUP BY drsn.node_id,drt.resp_type", nativeQuery = true)
    List<Object[]> findBySumRsDate(@Param("rsDate") Date rsDate,@Param("drsStatus") Integer drsStatus, @Param("dStatus") Integer dStatus);
}