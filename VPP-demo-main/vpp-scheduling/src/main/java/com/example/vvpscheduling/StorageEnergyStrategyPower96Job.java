package com.example.vvpscheduling;


import com.alibaba.fastjson.JSON;
import com.example.gateway.IotControlService;
import com.example.gateway.model.Every15MinuteModel;
import com.example.gateway.model.PointInfo;
import com.example.gateway.model.RPCModel;
import com.example.gateway.model.Strategy96Model;
import com.example.vvpcommom.Enum.ModuleNameEnum;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpscheduling.model.JobEnvironmentConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@EnableAsync
@Slf4j
public class StorageEnergyStrategyPower96Job {

    @Resource
    private IotControlService iotControlService;
    @Resource
    DevicePointRepository devicePointRepository;
    @Resource
    DeviceRepository deviceRepository;
    @Resource
    CfgStorageEnergyStrategyPower96LogRepository cfgStorageEnergyStrategyPower96LogRepository;
    @Resource
    private CfgStorageEnergyStrategyPower96Repository cfgStorageEnergyStrategyPower96Repository;
    @Resource
    private StationNodeRepository stationNodeRepository;
    @Resource
    private CfgStorageEnergyBaseInfoRepository baseInfoRepository;
    private static JobEnvironmentConfig config;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;

    @Autowired
    public StorageEnergyStrategyPower96Job(JobEnvironmentConfig environmentConfig, TaskScheduler taskScheduler) {
        config = environmentConfig;
        this.taskScheduler = taskScheduler;
    }

   @PostConstruct
    public void scheduleTask() {
        try {
            String cronExpression = config.getScheduledCron();
            if (StringUtils.isBlank(cronExpression)) {
                cronExpression = "0 45 23 * * ?";
                log.warn("Cron表达式为空，使用默认值: {}", cronExpression);
            }
            CronTrigger cronTrigger = new CronTrigger(cronExpression);
            scheduledFuture = taskScheduler.schedule(this::strategy96Issue, cronTrigger);
            log.info("使用cron表达式的计划任务: {}", cronExpression);
        } catch (Exception e) {
            log.error("调度任务时发生异常: ", e);
        }
    }
    @Async
    public void strategy96Issue() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date effectiveDate = sdf.parse(sdf.format(calendar.getTime()));
            List<CfgStorageEnergyStrategyPower96Log> logList = new ArrayList<>();
            List<String> list = stationNodeRepository.findStationIdsBySystemIdAndStationState("chuneng", "运营中");
            if (list.isEmpty()) {
                log.info("无运营中的储能");
            }
            List<CfgStorageEnergyStrategyPower96> strategyPower96List = cfgStorageEnergyStrategyPower96Repository
                    .findAllByNodeIds(list, "nengyuanzongbiao", effectiveDate);
            Map<String, List<CfgStorageEnergyStrategyPower96>> strategyPower96Map = strategyPower96List.stream()
                    .collect(Collectors.groupingBy(CfgStorageEnergyStrategyPower96::getNodeId));
            Map<String, Double> collect = baseInfoRepository.findAllByNodeIdIn(list).stream()
                    .collect(Collectors.toMap(CfgStorageEnergyBaseInfo::getNodeId, CfgStorageEnergyBaseInfo::getStorageEnergyLoad));
            List<String> issueList = new ArrayList<>();
            for (Map.Entry<String, List<CfgStorageEnergyStrategyPower96>> entry : strategyPower96Map.entrySet()) {
                String key = entry.getKey();
                double power;
                List<CfgStorageEnergyStrategyPower96> power96List = entry.getValue();

                if (collect.get(key) != null) {
                    power = collect.get(key);
                } else {
                    log.error("储能节点:{},储能配置基本信息未配置",key);
                    continue;

                }
                if (power96List.isEmpty() || power96List.size()!= 96) {
                    log.error("储能节点:{}充放电策略未完成配置",key);
                    continue;
                }
                List<Every15MinuteModel> every15MinuteModelList = new ArrayList<>();
                IntStream.range(0, power96List.size()).forEach(i -> {

                    CfgStorageEnergyStrategyPower96 strategyPower96 = power96List.get(i);
                    Every15MinuteModel every15MinuteModel = new Every15MinuteModel();
                    int strategyPower = (int) Math.round(strategyPower96.getPower());
                    int finalPower = Math.max(strategyPower, (int) power);
                    if (strategyPower96.getStrategy().contains("充")) {
                        every15MinuteModel.setStrategyType(-1);
                        every15MinuteModel.setStrategyLoad(finalPower);
                    } else if (strategyPower96.getStrategy().contains("放")) {
                        every15MinuteModel.setStrategyType(1);
                        every15MinuteModel.setStrategyLoad(finalPower);
                    } else {
                        every15MinuteModel.setStrategyType(0);
                        every15MinuteModel.setStrategyLoad(0);
                    }
                    every15MinuteModelList.add(every15MinuteModel);
                    logList.add(toDistLogInfo(now, strategyPower96));
                });
                //策略下发
                Strategy96Model strategy96Model = new Strategy96Model();
                Every15MinuteModel[] every15MinuteModels = every15MinuteModelList.toArray(new Every15MinuteModel[0]);
                strategy96Model.setCfStrategy(every15MinuteModels);
                strategy96Model.setImmediateEffectiveness(false);
                List<Device> allByNode_nodeIdAndSystemType_systemId = deviceRepository.findAllByNode_NodeIdAndSystemType_SystemId(key, "nengyuanzongbiao");
                List<String> deviceSns = allByNode_nodeIdAndSystemType_systemId.stream().map(Device::getDeviceSn).collect(Collectors.toList());
                List<DevicePoint> storage_strategy = devicePointRepository.findAllByDeviceSnInAndPointDesc(deviceSns, "storage_strategy");
                if (storage_strategy.size() != 1) {
                    log.error("储能节点没有录入控制点位，或者录入多个控制点位");
                }
                DevicePoint devicePoint = storage_strategy.get(0);
                Device device = devicePoint.getDevice();
                issueList.add(key);
                log.info("开始下发储能策略,设备序列号:{},设备下点位序列号:{},下发策略个数:{},下发策略:{}",
                        device.getDeviceSn(),devicePoint.getPointSn(),every15MinuteModels.length,JSON.toJSON(strategy96Model));
                newEnergyStorageChargingAndDischargingStrategyControl(device.getDeviceSn(), devicePoint.getPointSn(), strategy96Model);
            }
            log.info("完成策略下发的储能:{}",issueList);

            cfgStorageEnergyStrategyPower96Repository.updateDistributeStatusByNodeIds(issueList,effectiveDate);
            cfgStorageEnergyStrategyPower96LogRepository.saveAll(logList);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("覆盖策略失败: " + e.getMessage());
        }

    }
    private void newEnergyStorageChargingAndDischargingStrategyControl(String deviceSn, String pointSn, Strategy96Model strategy96Model) {

        if (StringUtils.isEmpty(deviceSn) || StringUtils.isEmpty(pointSn) || strategy96Model == null || strategy96Model.getCfStrategy() == null || strategy96Model.getCfStrategy().length != 96) {
            return;
        }

        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < strategy96Model.getCfStrategy().length; i++) {

            Every15MinuteModel cfStrategy = strategy96Model.getCfStrategy()[i];
            if (cfStrategy == null) {
                //打印错误
                return;
            }
            int strategyLoad = cfStrategy.getStrategyLoad();
            int strategyType = cfStrategy.getStrategyType();
            sb.append(strategyLoad * strategyType).append(",");
        }
        sb.append(strategy96Model.isImmediateEffectiveness() ? "1" : "0");

        List<PointInfo> pointInfoList = new ArrayList<>();
        pointInfoList.add(new PointInfo(pointSn, sb.toString()));
        List<RPCModel> models = new ArrayList<>();
        models.add(new RPCModel(deviceSn, pointInfoList, new Date()));

        iotControlService.CommonRPCRequestToDevice(ModuleNameEnum.storage_tactics.name(), models);
    }
    private CfgStorageEnergyStrategyPower96Log toDistLogInfo(Date date,CfgStorageEnergyStrategyPower96 power96) {
        CfgStorageEnergyStrategyPower96Log log = new CfgStorageEnergyStrategyPower96Log();
        log.setId("Dist" + UUID.randomUUID());
        log.setNodeId(power96.getNodeId());
        log.setSystemId(power96.getSystemId());
        log.setEffectiveDate(power96.getEffectiveDate());
        log.setTimeScope(power96.getTimeScope());
        log.setSTime(power96.getSTime());
        log.setETime(power96.getETime());
        log.setPower(power96.getPower());
        log.setStrategy(power96.getStrategy());
        log.setDistributeStatus(1);
        log.setStrategyType("下发策略");
        log.setCreateTime(date);
        return log;
    }
    private CfgStorageEnergyStrategyPower96Ai toDistAiInfo(CfgStorageEnergyStrategyPower96 power96) {
        CfgStorageEnergyStrategyPower96Ai ai = new CfgStorageEnergyStrategyPower96Ai();
        ai.setId(power96.getId());
        ai.setNodeId(power96.getNodeId());
        ai.setSystemId(power96.getSystemId());
        ai.setEffectiveDate(power96.getEffectiveDate());
        ai.setTimeScope(power96.getTimeScope());
        ai.setSTime(power96.getSTime());
        ai.setETime(power96.getETime());
        ai.setPower(power96.getPower());
        ai.setStrategy(power96.getStrategy());
        ai.setDistributeStatus(power96.getDistributeStatus());
        ai.setPolicyModel(power96.getPolicyModel());
        return ai;
    }
    private CfgStorageEnergyStrategyPower96 newDistInfo(CfgStorageEnergyStrategyPower96 power96) {
        CfgStorageEnergyStrategyPower96 newPower96 = new CfgStorageEnergyStrategyPower96();
        newPower96.setId(power96.getId());
        newPower96.setNodeId(power96.getNodeId());
        newPower96.setSystemId(power96.getSystemId());
        newPower96.setEffectiveDate(power96.getEffectiveDate());
        newPower96.setTimeScope(power96.getTimeScope());
        newPower96.setSTime(power96.getSTime());
        newPower96.setETime(power96.getETime());
        newPower96.setPower(power96.getPower());
        newPower96.setStrategy(power96.getStrategy());
        newPower96.setDistributeStatus(power96.getDistributeStatus());
        newPower96.setPolicyModel(power96.getPolicyModel());
        return newPower96;
    }
}
