package com.example.vvpservice.nodeprofit.service;


import com.example.vvpdomain.entity.CfgPhotovoltaicTouPrice;
import com.example.vvpdomain.entity.CfgStorageEnergyStrategy;
import com.example.vvpservice.nodeep.model.ElectricityInfoModel;
import com.example.vvpservice.nodeprofit.model.BillNodeProfit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface INodeProfitService {


	/**
	 * 得到节点下的负荷集成商
	 *
	 * @param nodeId
	 * @return
	 */
	String getLoadIntegrator(String nodeId);

	/**
	 * 得到节点下的电力用户
	 *
	 * @param nodeId
	 * @return
	 */
	String getConsumer(String nodeId);


	List<String> getPvNodeIdList();

	List<String> getStoreEnergyNodeIdList();

	List<String> getChargingPileNodeIdList();

	BillNodeProfit getBillNodeProfit(String nodeId, Date start, Date end);

	void doCalculateChargingPileProfit(String nodeId, Map<String, ElectricityInfoModel> timeInfo, LocalDateTime nowTime, LocalDate firstDayOfMonth);

	void doCalculatePvProfit(String nodeId, Map<String, BigDecimal> priceMap, Map<LocalTime, String> propertyMap,
	                         Date date);

	void doCalculateStorageEnergyProfit(String nodeId, Map<String, BigDecimal> priceMap, Map<LocalTime, String> propertyMap,
	                                    Date date);
}
