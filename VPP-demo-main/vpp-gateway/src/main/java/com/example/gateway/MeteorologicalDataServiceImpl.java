package com.example.gateway;

import com.alibaba.fastjson.JSONObject;
import com.example.gateway.forest.ApplicationError;
import com.example.gateway.forest.TqRequest;
import com.example.gateway.forest.TqService;
import com.example.gateway.model.MeteorologicalData;
import com.example.gateway.model.MeteorologicalDataResponse;
import com.example.gateway.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.util.*;

/**
 * 气象数据服务.
 */
@Service("meteorologicalDataService")
public class MeteorologicalDataServiceImpl implements MeteorologicalDataService {

   @Autowired
   private TqService tqService;

    @Override
    public MeteorologicalDataResponse getMeteorologicalData(double longitude, double latitude
            , Date startDate
            , Date endDate) {

        MeteorologicalDataResponse res = new MeteorologicalDataResponse();

        TqRequest request = new TqRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setLat(latitude);
        request.setLon(longitude);

        Object retObject = tqService.tqInfo(request, ApplicationError.create());
        Object data = JsonUtils.getValueByKey(retObject, "data");
        if(data!=null){
            List<MeteorologicalData> meteorologicalData = JSONObject.parseArray(JSONObject.toJSONString(data), MeteorologicalData.class);
            res.setData(meteorologicalData);
        }
        return res;

    }
}
