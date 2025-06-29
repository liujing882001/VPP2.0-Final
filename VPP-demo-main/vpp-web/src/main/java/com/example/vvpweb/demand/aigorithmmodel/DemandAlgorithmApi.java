package com.example.vvpweb.demand.aigorithmmodel;

import com.example.vvpcommom.HttpUtil;
import com.example.vvpdomain.IotTsKvRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Arrays;


public class DemandAlgorithmApi {

    public String ptimizationProblem(String json) {
        try {


            return HttpUtil.okHttpPost("url", String.valueOf(json));
        } catch (Exception e) {
            return "";
        }
    }
}
