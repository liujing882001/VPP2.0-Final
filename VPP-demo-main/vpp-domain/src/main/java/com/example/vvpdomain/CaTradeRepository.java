package com.example.vvpdomain;

import com.example.vvpdomain.entity.CaTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * @author maoyating
 * @description 碳交易
 * @date 2022-08-09
 */
@Repository
public interface CaTradeRepository extends JpaRepository<CaTrade, String>,
        JpaSpecificationExecutor<CaTrade> {

    @Query(value = "update ca_trade set t_status=0,update_time=now() where trade_id in (:tradeIds) ", nativeQuery = true)
    @Modifying
    @Transactional
    int updateBatchStatus(@Param("tradeIds") String[] tradeIds);

}