package com.example.vvpservice.chinasouthernpower;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.gateway.IotControlService;
import com.example.gateway.model.PointInfo;
import com.example.gateway.model.PointInfo;
import com.example.vvpcommom.Enum.ModuleNameEnum;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpcommom.HttpUtil;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.chinasouthernpower.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoHouseholdsServiceImpl implements INoHouseholdsService {
    private static final Logger logger = LoggerFactory.getLogger(NoHouseholdsServiceImpl.class);

    @Autowired
    private IotTsKvRepository iotTsKvRepository;

    @Autowired
    private IotTsKvLastRepository iotTsKvLastRepository;

    @Resource
    private SysParamRepository sysParamRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    /**
     * 判断对象中属性值是否全为空
     */
    public static void set(Object target, String fieldName, Object value) {
        try {
            for (Field f : target.getClass().getDeclaredFields()) {
                ReflectionUtils.makeAccessible(f);
                if (f.getName().equals(fieldName)) {
                    if (f.getType() == Double.class) {
                        f.set(target, new BigDecimal((String) value).doubleValue());
                    }

                    if (f.getType() == Integer.class) {
                        f.set(target, new BigDecimal((String) value).intValue());
                    }

                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Error setting field {} on object {}", fieldName, target.getClass().getSimpleName(), e);
        }
    }

    @Override
    public boolean noHouseholdsDeviceIsOnline(String resourceId) {
        Node byNoHouseholds = nodeRepository.findByNoHouseholds(resourceId);
        if (byNoHouseholds != null) {
            List<Device> de = deviceRepository.findAllByNode_NodeIdAndSystemType_SystemId(byNoHouseholds.getNodeId(), "nengyuanzongbiao");
            if (de.isEmpty()) {
                return false;
            }
            return de.get(0).getOnline();
        }
        return false;
    }

    @Override
    public Data lastData(String resourceId) {
        Data data = null;

        Node byNoHouseholds = nodeRepository.findByNoHouseholds(resourceId);
        if (byNoHouseholds != null) {
            List<IotTsKvLast> il = iotTsKvLastRepository.findAllByNodeId(byNoHouseholds.getNodeId());
            if (!il.isEmpty()) {
                data = new Data();
                RData rData = new RData();
                il.forEach(e -> {
                    if (e.getPointDesc().equals("load")) {
                        rData.setP(new BigDecimal(e.getPointValue()).doubleValue());
                    } else if (e.getPointDesc().equals("energy")) {
                        rData.setC(new BigDecimal(e.getPointValue()).doubleValue());
                    } else {
                        set(rData, e.getPointDesc(), e.getPointValue());
                    }
                });
                data.setRd(rData);
                data.setTs(il.get(0).getTs());
            }

        }
        return data;
    }

    @Override
    public List<Data> findHistoryData(String resourceId, Date start, Date end) {
        List<Data> data = new ArrayList<>();

        Node byNoHouseholds = nodeRepository.findByNoHouseholds(resourceId);
        if (byNoHouseholds != null) {
            List<IotTsKv> datas = iotTsKvRepository.findAllByNodeIdAndSystemIdAndTsBetweenOrderByTsAsc(byNoHouseholds.getNodeId(),
                    "nengyuanzongbiao", start, end);

            Map<Date, List<IotTsKv>> collect = datas.stream().collect(Collectors.groupingBy((px) -> TimeUtil.strDDToDate(TimeUtil.dateFormat(px.getTs()), "yyyy-MM-dd HH:mm")));

            collect.keySet().forEach(l -> {
                Data d = new Data();
                RData rData = new RData();
                d.setTs(l);
                collect.get(l).forEach(e -> {
                    if (e.getPointDesc().equals("load")) {
                        rData.setP(new BigDecimal(e.getPointValue()).doubleValue());
                    } else if (e.getPointDesc().equals("energy")) {
                        rData.setC(new BigDecimal(e.getPointValue()).doubleValue());
                    } else {
                        set(rData, e.getPointDesc(), e.getPointValue());
                    }
                });
                d.setRd(rData);
                data.add(d);

            });
        }
        return data;
    }

    /**
     * @param noHouseholds         户号
     * @param responsibleStartTime 响应开始时间
     * @param responsibleEndTime   响应结束时间
     * @return 冷机设备可响应功率
     * 判定发响应任务日期的前一天 对应的设备运行状态。所有开状态的 可调设备功率和。
     */
    @Override
    public double findResponsiblePowerByNoHouseholds(String noHouseholds, Date responsibleStartTime, Date responsibleEndTime) {
        double responsiblePower = 0d;
        if (nodeRepository.findByNoHouseholds(noHouseholds) != null
                && responsibleStartTime.after(responsibleEndTime) == false) {
            List<Device> devices = deviceRepository.findAllOnlineDeviceByNode_NoHouseholds(noHouseholds);
            if (devices != null && devices.size() > 0) {
                Date s = TimeUtil.dateAddDay(responsibleStartTime, -1);
                Date e = TimeUtil.dateAddDay(responsibleEndTime, -1);
                for (Device device : devices) {
                    if (device != null) {
                        String nodeId = device.getNode().getNodeId();
                        double deviceRatedPower = device.getDeviceRatedPower();
                        String deviceSn = device.getDeviceSn();
                        int count = 0;
                        List<IotTsKv> iotTsKvList = iotTsKvRepository.findAllDeviceRunStatus(nodeId, deviceSn, s, e);
                        if (iotTsKvList != null && iotTsKvList.size() > 0) {
                            count = iotTsKvList.size();
                        }
                        int on_count = 0;
                        List<IotTsKv> iotTsKvListOn = iotTsKvRepository.findAllDeviceStatusStatus_On(nodeId, deviceSn, s, e);
                        if (iotTsKvListOn != null && iotTsKvListOn.size() > 0) {
                            on_count = iotTsKvListOn.size();
                        }
                        if (count != 0) {
                            double offSize = (double) on_count / count;
                            double r_d_Power = offSize * deviceRatedPower;
                            responsiblePower += r_d_Power;
                        }
                    }
                }
            }
        }

        //测试写死 return responsiblePower;
        return 200d;
    }
    /**
     * @param noHouseholds 户号
     * @return 冷机设备当前信息集合（冷机设备编号，冷机设备额定功率,冷机设备当前出水温度）
     */
    @Override
    public List<DeviceInfo> findNoHouseholdsDeviceInfo(String noHouseholds) {
        List<DeviceInfo> deviceInfos = new ArrayList<>();
        if (nodeRepository.findByNoHouseholds(noHouseholds) != null) {
            List<Device> devices = deviceRepository.findAllOnlineDeviceByNode_NoHouseholds(noHouseholds);
            if (devices != null && devices.size() > 0) {
                for (Device device : devices) {
                    if (device != null) {
                        String nodeId = device.getNode().getNodeId();
                        double deviceRatedPower = device.getDeviceRatedPower();
                        String deviceSn = device.getDeviceSn();
                        List<DevicePoint> devicePointList = device.getDevicePointList();
                        if (devicePointList != null && devicePointList.size() > 0) {
                            for (DevicePoint devicePoint : devicePointList) {
                                if (devicePoint != null) {
                                    IotTsKvLast iotTsKvLast = iotTsKvLastRepository.findAllByNodeIdAndDeviceSnAndPointDesc(nodeId, deviceSn, "host_running_status");
                                    if (iotTsKvLast != null && "On".equals(iotTsKvLast.getPointValue())) {
                                        iotTsKvLast = iotTsKvLastRepository.findAllByNodeIdAndDeviceSnAndPointDesc(nodeId, deviceSn, "freezing_water_outlet_temperature");
                                        if (iotTsKvLast != null) {
                                            float deviceNowTemperature = Float.parseFloat(iotTsKvLast.getPointValue());
                                            iotTsKvLast = iotTsKvLastRepository.findAllByNodeIdAndDeviceSnAndPointDesc(nodeId, deviceSn, "freezing_water_outlet_temperature_set");
                                            if (iotTsKvLast != null) {
                                                float chilledWaterOutletTemperatureSetting = Float.parseFloat(iotTsKvLast.getPointValue());
                                                iotTsKvLast = iotTsKvLastRepository.findAllByNodeIdAndDeviceSnAndPointDesc(nodeId, deviceSn, "freezing_water_inlet_temperature");
                                                if (iotTsKvLast != null) {
                                                    DeviceInfo deviceInfo = new DeviceInfo();
                                                    deviceInfo.setDeviceSn(deviceSn);
                                                    deviceInfo.setOutletTemperature(deviceNowTemperature);
                                                    deviceInfo.setDeviceRatedPower(deviceRatedPower);
                                                    deviceInfo.setOutletTemperatureSetting(chilledWaterOutletTemperatureSetting);
                                                    deviceInfo.setInletTemperature(Float.parseFloat(iotTsKvLast.getPointValue()));
                                                    deviceInfos.add(deviceInfo);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //临时测试
                        DeviceInfo deviceInfo = new DeviceInfo();
                        deviceInfo.setDeviceSn(deviceSn);
                        deviceInfo.setOutletTemperature(6);
                        deviceInfo.setDeviceRatedPower(deviceRatedPower);
                        deviceInfo.setOutletTemperatureSetting(6);
                        deviceInfo.setInletTemperature(6);
                        deviceInfos.add(deviceInfo);
                    }
                }
            }
        }
        return deviceInfos;
    }

}
