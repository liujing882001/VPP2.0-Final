package com.example.vvpdomain;

import com.example.vvpdomain.entity.DemandRespPlanPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @author maoyating
 * @description 价格、总功率-南网
 * @date 2022-12-09
 */
@Repository
public interface DemandRespPlanPriceRepository extends JpaRepository<DemandRespPlanPrice, String>,
        JpaSpecificationExecutor<DemandRespPlanPrice> {

    @Query(value = "SELECT * from demand_resp_plan_price WHERE " +
            " invitation_id=:respId ", nativeQuery = true)
    List<DemandRespPlanPrice> findByInvitationIdList(@Param("respId") String respId);

    @Query(value = "SELECT * from demand_resp_plan_price WHERE " +
            " invitation_id=:respId and response_time>=:startDate and response_time<=:endDate " +
            " order by response_time asc"+
            " LIMIT :pageSize OFFSET :pageNum", nativeQuery = true)
    List<DemandRespPlanPrice> findByRespIdDateList(@Param("respId") String respId,
                                                     @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                                     @Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);

    @Query(value = "SELECT count(*) from demand_resp_plan_price WHERE " +
            " invitation_id=:respId and response_time>=:startDate and response_time<=:endDate ", nativeQuery = true)
    int countByRespIdDate(@Param("respId") String respId,@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 更新任务对应的价格
     */
    @Query(value = "UPDATE demand_resp_plan_price " +
            "SET price = :price " +
            "WHERE invitation_id =:invitationId", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    @Transactional
    void updatePrice(@Param("invitationId") String invitationId, @Param("price") Double price);

}