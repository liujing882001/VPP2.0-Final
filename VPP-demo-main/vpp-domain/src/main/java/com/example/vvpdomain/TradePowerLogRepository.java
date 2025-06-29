package com.example.vvpdomain;


import com.example.vvpdomain.entity.TradePowerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TradePowerLogRepository extends JpaRepository<TradePowerLog, String>, JpaSpecificationExecutor<TradePowerLog> {

}
