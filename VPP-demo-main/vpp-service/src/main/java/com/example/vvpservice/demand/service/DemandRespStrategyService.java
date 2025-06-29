package com.example.vvpservice.demand.service;


import com.example.vvpservice.demand.model.DemandRespStrategyModel;

import java.util.List;

public interface DemandRespStrategyService {

    boolean batchInsert(List<DemandRespStrategyModel> list);

}
