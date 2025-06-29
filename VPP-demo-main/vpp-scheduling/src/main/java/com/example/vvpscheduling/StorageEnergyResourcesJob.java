package com.example.vvpscheduling;

import com.alibaba.fastjson.JSON;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.dto.FindPointValueAndTs;
import com.example.vvpdomain.entity.*;
import com.example.vvpdomain.view.StorageEnergyNodeInfoView;
import com.example.vvpscheduling.dto.SERJobDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
//import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 负荷管理-储能资源
 */
@Component("storageEnergyResourcesJob")
@EnableAsync
@Slf4j
public class StorageEnergyResourcesJob {
    private Map<String, SERJobDTO> serJobDTOMap = new HashMap<>();

//    private static final AtomicBoolean smsSent = new AtomicBoolean(false);
    private static Logger logger = LoggerFactory.getLogger(StorageEnergyResourcesJob.class);

    @Autowired
    private BiStorageEnergyResourcesHistoryRepository biStorageEnergyResourcesHistoryRepository;
    @Resource
    CfgStorageEnergyStrategyPower96Repository cfgStorageEnergyStrategyPower96Repository;
    @Resource
    BiStorageEnergyResourcesRepository biStorageEnergyResourcesRepository;
    @Resource
    BiStorageEnergyLogRepository biStorageEnergyLogRepository;
    @Resource
    StorageEnergyNodeInfoViewRepository storageEnergyNodeInfoViewRepository;
    @Resource
    private IotTsKvLastRepository iotTsKvLastRepository;
    @Resource
    PointModelMappingRepository pointModelMappingRepository;
    @Resource
    NodeRepository nodeRepository;
    @Resource
    DevicePointRepository devicePointRepository;

    @PostConstruct
    public void init() {
        List<String> energyNodes = nodeRepository.findByNodeIdAndNodePostType("storageEnergy");
        List<PointModelMapping> pointModelMappings = pointModelMappingRepository.findByKeyAndNodeIdNative("power",energyNodes);
        List<String> deviceSns = pointModelMappings.stream().map(PointModelMapping::getDeviceList).collect(Collectors.toList());
        List<DevicePoint> devicePoint = devicePointRepository.findAllByPointKey(deviceSns, "power");
        Map<String, DevicePoint> devicePointMap = devicePoint.stream()
                .collect(Collectors.toMap(
                        DevicePoint::getDeviceSn,
                        dP -> dP,
                        (existing, replacement) -> existing
                ));
        pointModelMappings.forEach(pm -> {
            String nodeId = pm.getStation().getStationId();
            String deviceSn = pm.getDeviceList();
            String pointSn = devicePointMap.get(deviceSn).getPointSn();
            String pointDesc = pm.getPointModel().getPointDesc();
            SERJobDTO serJobDTO = new SERJobDTO();
            serJobDTO.setNodeId(nodeId);
            serJobDTO.setDeviceSn(deviceSn);
            serJobDTO.setPointSn(pointSn);
            serJobDTO.setPointDesc(pointDesc);
            serJobDTOMap.put(nodeId,serJobDTO);
        });
        logger.info("serJobDTOMap个数:{}",serJobDTOMap.size());
    }

    public void addPoint(List<String> refreshNodes) {
        if (refreshNodes.isEmpty()) {
            return;
        }
        List<PointModelMapping> pointModelMappings = pointModelMappingRepository.findByKeyAndNodeIdNative("power",refreshNodes);
        if (pointModelMappings.isEmpty()){
            return;
        }
        List<String> deviceSns = pointModelMappings.stream().map(PointModelMapping::getDeviceList).collect(Collectors.toList());
        List<DevicePoint> devicePoint = devicePointRepository.findAllByPointKey(deviceSns, "power");
        Map<String, DevicePoint> devicePointMap = devicePoint.stream()
                .collect(Collectors.toMap(
                        DevicePoint::getDeviceSn,
                        dP -> dP,
                        (existing, replacement) -> existing
                ));
        pointModelMappings.forEach(pm -> {
            String nodeId = pm.getStation().getStationId();
            String deviceSn = pm.getDeviceList();
            String pointSn = devicePointMap.get(deviceSn).getPointSn();
            String pointDesc = pm.getPointModel().getPointDesc();
            SERJobDTO serJobDTO = new SERJobDTO();
            serJobDTO.setNodeId(nodeId);
            serJobDTO.setDeviceSn(deviceSn);
            serJobDTO.setPointSn(pointSn);
            serJobDTO.setPointDesc(pointDesc);
            serJobDTOMap.put(nodeId,serJobDTO);
        });
    }

    @Scheduled(initialDelay = 1000 * 15, fixedDelay = 120 * 1000)
    @Async
    public void initStorageEnergyStationInfo() {
        try {
            SimpleDateFormat fmt_ymd_hds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            fmt_ymd_hds.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            SimpleDateFormat fmt_ym = new SimpleDateFormat("yyyy-MM");
            fmt_ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
            fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            SimpleDateFormat fmt_h = new SimpleDateFormat("HH");
            fmt_h.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            biStorageEnergyResourcesRepository.deleteAllInvalidNode();
            List<String> refreshNodes = new ArrayList<>();
            List<StorageEnergyNodeInfoView> storageEnergyNodeInfoViews = storageEnergyNodeInfoViewRepository.findAll();
            if (storageEnergyNodeInfoViews != null && storageEnergyNodeInfoViews.size() > 0) {
                for (StorageEnergyNodeInfoView node : storageEnergyNodeInfoViews) {
                    try {
                        String nodeId = node.getNodeId();
                        String nodeName = node.getNodeName();
                        //在线状态
                        boolean online = node.getOnline();
                        //建设中 已完成，默认false 为建设中
                        boolean isEnabled = node.getIsEnabled();
                        //储能装机容量
                        double storage_capacity = node.getStorageEnergyCapacity();
                        //储能电站功率
                        double storage_load = node.getStorageEnergyLoad();
                        //电池状态监控设备编码u
                        String battery_status_device_sn = node.getBatteryStatusDeviceSn();

                        String siteStrategy="-";

                        Date ts = TimeUtil.getStartOfDay(new Date());
                        //soh soc  默认 *0.01 去掉百分号
                        double soh = 0;
                        double soc = 0;

                        FindPointValueAndTs lastNodeInfo = iotTsKvLastRepository.findPointValueAndTsByNodeIdAndDeviceSnAndPointDesc(nodeId, battery_status_device_sn, "soc");

                        if (lastNodeInfo != null) {
                            soc = Double.parseDouble(lastNodeInfo.getPointValue()) * 0.01;
                            ts = lastNodeInfo.getTs();
                        }
                        String lastValue = iotTsKvLastRepository.findPointValueByNodeIdAndDeviceSnAndPointDesc(nodeId, battery_status_device_sn, "soh");

                        if (lastValue != null && !lastValue.isEmpty()) {
                            soh = Double.parseDouble(lastValue) * 0.01;
                        }
                        Double actualLoad = null;
                        String actualStrategy = null;

                        if (serJobDTOMap.get(nodeId) != null) {
                            SERJobDTO serJobDTO = serJobDTOMap.get(nodeId);
                            String lastActualLoad = iotTsKvLastRepository.findLastValue(nodeId, serJobDTO.getDeviceSn(), serJobDTO.getPointSn(),serJobDTO.getPointDesc());
                            actualLoad = Double.valueOf(lastActualLoad);
                            if (actualLoad > 0) {
                                actualStrategy = "放电";
                            } else if(actualLoad < 0) {
                                actualStrategy = "充电";
                            } else {
                                actualStrategy = "待机";
                            }
                        } else {
                            refreshNodes.add(nodeId);
                        }

                        double in_load = 0;
                        double out_load = 0;
                        Date effective_date = fmt_ymd.parse(fmt_ym.format(ts) + "-"+ts.getDate());
                        int j = ts.getHours();
                        int m = ts.getMinutes();

                        String sTime = (m - m % 15 == 0) ? (String.format("%02d:00", j)) : (String.format("%02d:%02d", j, m - m % 15));
//                        String eTime = j < 10 ? ("0" + j + ":59") : (j + ":59");
//                        String time_frame = sTime + "-" + eTime;
                        String strategy = cfgStorageEnergyStrategyPower96Repository.findStrategyBySystemIdAndNodeIdeAndSTime(nodeId, "nengyuanzongbiao", effective_date, sTime);
                        if (strategy != null &&!strategy.isEmpty()) {
                            siteStrategy = strategy;
                            switch (strategy) {
                                case "充电":
                                    in_load = storage_load;
                                    break;
                                case "待机":
                                    if(soc == 0){
                                        in_load = storage_load;
                                        out_load = 0;
                                    }
                                    if((soc == soh)||(soc>soh)){
                                        in_load = 0;
                                        out_load = storage_load;
                                    }
                                    if(soc>0 && soc<soh){
                                        in_load = storage_load;
                                        out_load = storage_load;
                                    }

                                    break;
                                case "放电":
                                    out_load = storage_load;
                                    break;
                            }
                        }

                        if(isEnabled==false||online==false){
                            siteStrategy="-";
                        }

                        String id = nodeId;
                        BiStorageEnergyResources resources = new BiStorageEnergyResources();
                        resources.setId(id);
                        resources.setNodeId(nodeId);
                        resources.setNodeName(nodeName);
                        resources.setOnline(online);
                        resources.setIsEnabled(isEnabled);
                        resources.setCapacity(Double.parseDouble(String.format("%.2f", storage_capacity)));
                        resources.setLoad(Double.parseDouble(String.format("%.2f", storage_load)));

                        resources.setActualLoad(actualLoad);
                        resources.setActualStrategy(actualStrategy);

                        resources.setSoc(Double.parseDouble(String.format("%.4f", soc)));
                        resources.setSoh(Double.parseDouble(String.format("%.4f", soh)));

                        //可放容量= 单个电站的容量*（SOC）
                        resources.setOutCapacity(Double.parseDouble(String.format("%.2f", storage_capacity * soc)));
                        //可充容量= 单个电站的容量*（1-SOC）
                        resources.setInCapacity(Double.parseDouble(String.format("%.2f", storage_capacity * (1 - (soc)))));
                        //最大可充功率=单个电站电池处于“充电”状态时的电池功率
                        resources.setMaxInLoad(Double.parseDouble(String.format("%.2f", in_load)));
                        //最大可放功率=单个电站电池处于“放电”状态时的电池功率
                        resources.setMaxOutLoad(Double.parseDouble(String.format("%.2f", out_load)));

                        resources.setTs(ts.after(new Date()) ? new Date() : ts);

                        resources.setStrategy(siteStrategy);
                        biStorageEnergyResourcesRepository.save(resources);


                        BiStorageEnergyResourcesHistory biStorageEnergyResourcesHistory = new BiStorageEnergyResourcesHistory();
                        String alarmUuid = UUID.randomUUID().toString();
                        biStorageEnergyResourcesHistory.setAlarmUuid(alarmUuid);
                        BeanUtils.copyProperties(resources, biStorageEnergyResourcesHistory);
                        biStorageEnergyResourcesHistoryRepository.save(biStorageEnergyResourcesHistory);


                        biStorageEnergyLogRepository.save(new BiStorageEnergyLog(resources));
                    } catch (Exception ex) {
                        logger.error("处理节点信息时发生异常：{}", ex.getMessage(), ex);
                    }
                }
            }
            addPoint(refreshNodes);
        } catch (Exception ex) {
            logger.error("初始化储能电站信息时发生异常：{}", ex.getMessage(), ex);
        }
    }
}
