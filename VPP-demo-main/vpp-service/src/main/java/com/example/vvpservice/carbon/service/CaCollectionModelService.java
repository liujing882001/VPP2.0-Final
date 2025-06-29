package com.example.vvpservice.carbon.service;


import com.example.vvpdomain.entity.CaCollectionModel;

import java.util.List;

public interface CaCollectionModelService {

    Double getEmissionDataCountDay(String nodeId, Integer scopeType, String startTime, String endTime);

    Double getEmissionDataCountYM(String nodeId, Integer scopeType, String startTime, String endTime);

    List<Object[]> getDisplacementAnalysis(String nodeId, Integer scopeType, String startTime, String endTime);

    Double getTradeDataCount(String nodeId, Integer tradeType, Integer greenType, String startTime, String endTime);

    List<CaCollectionModel> getCaCollectionModelList(String nodeId, Integer scopeType);

    Object[] getSinkConfCount(String nodeId, String cType, String emissionFactorName, String startTime, String endTime);

    //碳报告统计范围1
    List<Object[]> getReportCount(String nodeId, Integer scopeType, Integer dischargeType, String year, String clause);

    List<Object[]> getCaFactorList(String nodeId, String[] factorNames);
}
