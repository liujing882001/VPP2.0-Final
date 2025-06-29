package com.example.vvpservice.demand.service;
import java.util.Date;

import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.DemandRespTask;
import com.example.vvpdomain.entity.SysJob;
import com.example.vvpservice.ai.service.AiLoadForecastingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
public class DemandRespTaskServiceImpl implements DemandRespTaskService {

    private static Logger logger = LoggerFactory.getLogger(DemandRespTaskServiceImpl.class);

    @Resource
    AiLoadRepository aiLoadRepository;
    @Resource
    private DemandCalendarRepository calendarRepository;
    @Resource
    private DemandRespTaskRepository taskRepository;
    @Resource
    AiLoadForecastingService aiLoadForecastingService;
    @Resource
    private NodeRepository nodeRepository;
    @Resource
    private DemandRespTaskRepository demandRespTaskRepository;


    @Override
    public void batchSysJob(String[] respId) {

    }
}
