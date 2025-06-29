package com.example.vvpdomain;


import com.example.vvpdomain.entity.TradePower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TradePowerRepository extends JpaRepository<TradePower, String>, JpaSpecificationExecutor<TradePower> {


	@Query(value = "select count(*) from trade_power where trade_power.s_time > :sTime ;", nativeQuery = true)
	int getTaskCount(@Param("sTime") Date date);

}
