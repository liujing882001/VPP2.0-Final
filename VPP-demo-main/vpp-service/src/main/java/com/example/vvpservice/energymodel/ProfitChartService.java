package com.example.vvpservice.energymodel;
import java.util.Date;

import com.example.vvpservice.energymodel.model.ProfitRequest;
import com.example.vvpservice.energymodel.model.ProfitResponse;

import java.util.List;
import java.util.Map;

public interface ProfitChartService {

	List<ProfitResponse> getEnergyStorageProfitChart(ProfitRequest request, Map<String, String> energyNodeMap);

	List<ProfitResponse> getPvProfitChart(ProfitRequest request,Map<String, String> loadNodeMap);

	List<ProfitResponse> getProfitChartAll(Map<String, String> loadNodeMap,Map<String, String> energyNodeMap,List<String> pvNodeIdList, List<String> enNodeIdList,
	                                       String systemId, String startDate, String endDate);
}
