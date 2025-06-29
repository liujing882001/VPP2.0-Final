package com.example.vvpscheduling;

import com.example.vvpdomain.AiLoadRepository;
import com.example.vvpdomain.DemandCalendarRepository;
import com.example.vvpdomain.DemandRespTaskRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpservice.ai.service.AiLoadForecastingService;
import com.example.vvpservice.demand.service.DemandCalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * AI 变电站相关算法的识别-基线负荷-新
 * add by maoyating
 */
@Component("aiLoadBaseLineDataDemand")
@EnableAsync
public class AILoadBaseLineDataDemand {

    private static final String valueDefault = "-";
    private static Logger LOGGER = LoggerFactory.getLogger(AILoadBaseLineDataDemand.class);
    @Autowired
    private DemandCalendarService demandCalendarService;

    @Scheduled(cron = "0 1 0 * * ?") // 每天凌晨00:01:00执行任务
//@Scheduled(cron = "0 46 13 * * ?") // 每天凌晨00:01:00执行任务
    @Async
    public void loadBaseLineDemandJob() {
        try {
            LOGGER.info("基线负荷的算法开始");
            Date date = new Date();
            demandCalendarService.generateBaseline(date);
            LOGGER.info("基线负荷的算法结束");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
