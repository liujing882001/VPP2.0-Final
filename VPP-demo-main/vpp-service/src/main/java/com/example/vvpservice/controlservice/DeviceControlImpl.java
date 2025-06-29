package com.example.vvpservice.controlservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.gateway.IotControlService;
import com.example.gateway.model.PointInfo;
import com.example.gateway.model.RPCModel;
import com.example.vvpcommom.Enum.ModuleNameEnum;
import com.example.vvpcommom.RedisUtils;
import com.example.vvpcommom.devicecmd.AirConditioningDTO;
import com.example.vvpcommom.devicecmd.OtherConditioningDTO;
import com.example.vvpcommom.devicecmd.POWEREnum;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.chinasouthernpower.INoHouseholdsService;
import com.example.vvpservice.chinasouthernpower.model.DeviceInfo;
import com.example.vvpservice.chinasouthernpower.model.DeviceInfoModel;
import com.example.vvpservice.chinasouthernpower.model.DeviceSetInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("deviceControl")
public class DeviceControlImpl implements IDeviceControl {
    private static final Logger logger = LoggerFactory.getLogger(DeviceControlImpl.class);
    @Resource
    DemandStrategyRepository demandStrategyRepository;
    @Resource
    private ScheduleStrategyRepository scheduleStrategyRepository;
    @Resource
    private RedisUtils redisUtils;
    @Autowired
    private INoHouseholdsService noHouseholdsService;
    @Resource
    private DemandRespStrategyNoRepository noRepository;
    @Resource
    private IotControlService iotControlService;
    @Resource
    private DeviceRepository deviceRepository;
    /*
     * 任务调度反射
     * 发送设备控制命令消息 控制空调和灯光设备等
     * @param strategyId 策略ID
     */

    public boolean sendDeviceControlCommandMessage(String strategyId) {

        try {
            logger.info("可调负荷调度任务进入..." + strategyId);
            ScheduleStrategy scheduleStrategy = scheduleStrategyRepository.findByStrategyId(strategyId);

            if (scheduleStrategy != null && scheduleStrategy.getDeviceList() != null && scheduleStrategy.getDeviceList().size() > 0) {

                String cmdExpression = scheduleStrategy.getCmdExpression();

                List<Device> deviceList = scheduleStrategy.getDeviceList();

                if (!StringUtils.isEmpty(cmdExpression) && deviceList != null && deviceList.size() > 0) {

                    Map<String, List<Device>> groupByMecId = deviceList
                            .stream()
                            .collect(Collectors.groupingBy((px) -> px.getMecId()));

                    if (groupByMecId != null && groupByMecId.size() > 0) {

                        boolean isAirConditioning = scheduleStrategy.getStrategyType() == 0;
                        //反控开关设备
                        if (!isAirConditioning) {
                            OtherConditioningDTO otherConditioningDTO = JSON.parseObject(cmdExpression, OtherConditioningDTO.class);
                            if (otherConditioningDTO != null) {
                                for (String mecId : groupByMecId.keySet()) {

                                    groupByMecId.get(mecId).stream().forEach(device -> {
                                        DevicePoint power = device.getDevicePointList()
                                                .stream()
                                                .findFirst()
                                                .filter(c -> c.getPointDesc().equals("switch"))
                                                .orElse(null);
                                        if (power != null) {

                                            List<PointInfo> pointInfoList = new ArrayList<>();
                                            pointInfoList.add(new PointInfo(power.getPointSn(), otherConditioningDTO.getPower().name()));

                                            List<RPCModel> models = new ArrayList<>();
                                            models.add(new RPCModel(device.getDeviceSn(), pointInfoList, new Date()));
                                            //IOT照明 充电桩开关控制下发
                                            iotControlService.CommonRPCRequestToDevice(ModuleNameEnum.tactics.name(), models);
                                        }
                                    });
                                }
                            }
                        }
                        //反控空调设备
                        if (isAirConditioning) {
                            AirConditioningDTO airConditioningDTO = JSON.parseObject(cmdExpression, AirConditioningDTO.class);
                            if (airConditioningDTO != null) {
                                for (String mecId : groupByMecId.keySet()) {


                                    groupByMecId.get(mecId).stream().forEach(device -> {
                                        List<DevicePoint> pointList = device.getDevicePointList();
                                        if (pointList != null && pointList.size() > 0) {

                                            DevicePoint power = pointList.stream().filter(c -> c.getPointDesc().equals("switch")).findFirst().orElse(null);
                                            DevicePoint windSpeed = pointList.stream().filter(c -> c.getPointDesc().equals("wind_speed")).findFirst().orElse(null);
                                            DevicePoint model = pointList.stream().filter(c -> c.getPointDesc().equals("model")).findFirst().orElse(null);
                                            DevicePoint temperature = pointList.stream().filter(c -> c.getPointDesc().equals("temperature")).findFirst().orElse(null);

                                            List<PointInfo> pointInfoList = new ArrayList<>();
                                            if (power != null) {
                                                pointInfoList.add(new PointInfo(power.getPointSn(), airConditioningDTO.getPower().name()));
                                            }
                                            if (windSpeed != null) {

                                                pointInfoList.add(new PointInfo(windSpeed.getPointSn(), airConditioningDTO.getWindSpeed().name()));
                                            }
                                            if (model != null) {

                                                pointInfoList.add(new PointInfo(model.getPointSn(), airConditioningDTO.getMode().name()));
                                            }
                                            if (temperature != null) {

                                                pointInfoList.add(new PointInfo(temperature.getPointSn(), airConditioningDTO.getTemp().name()));
                                            }

                                            List<RPCModel> models = new ArrayList<>();
                                            models.add(new RPCModel(device.getDeviceSn(), pointInfoList, new Date()));
                                            //IOT空调控制下发
                                            iotControlService.CommonRPCRequestToDevice(ModuleNameEnum.tactics.name(), models);
                                        }
                                    });
                                }
                            }
                        }

                    }
                }
            }
        } catch (Exception ex) {
        }
        return true;
    }

    /**
     * 反控设备--需求响应用
     *
     * @param strategyIds 策略id
     * @param respType    响应类型 1：削峰 2：填谷
     * @param dStatus     任务状态 2：任务执行中 3：任务结束
     * @param respId      响应任务id
     * @return
     */
    @Override
//    @Async
    public void sendDeviceControlCommandMessageDemand(List<String> strategyIds, int respType, int dStatus, String respId) {
        try {
        for (String strategyId : strategyIds) {
            if (strategyId.equals("zhinengtuijian")) {
                //若是智能推荐，目前只能调整冷机出水温度，
                if (dStatus == 2) {
                    logger.info("下发智能iot指令开始：" + respId);
                    List<DemandRespStrategyNo> noList = noRepository.findNoListByRespId(respId).stream().filter(v -> v.getWinningBid() != null).collect(Collectors.toList());
                    Map<String, DemandStrategy> map = demandStrategyRepository
                            .findByRespIdAndState(respId, Collections.singletonList(1)).stream()
                            .collect(Collectors.toMap(DemandStrategy::getNoHouseholds, v -> v));
                    if (noList != null && noList.size() > 0) {
                        //记录未调节温度前的设备信息，待响应时间结束后，从redis中获取下来
                        List<DeviceInfoModel> deviceList = new ArrayList<>();
                        for (DemandRespStrategyNo n : noList) {
                            List<DeviceInfoModel> deviceList2 = new ArrayList<>();

                            //判断该户号是否已中标
                            if (n.getWinningBid() == 1) {
                                //在线设备
                                List<DeviceInfo> deviceInfoList = noHouseholdsService.findNoHouseholdsDeviceInfo(n.getNoHouseholds());
                                if (deviceInfoList != null && deviceInfoList.size() > 0) {
                                    List<DeviceSetInfo> deviceSetInfoList = new ArrayList<>();
                                    //该户号需要调节的总负荷
                                    double declareLoad = n.getDeclareLoad();

                                    //根据节点的申报额定功率，进行占比分配
                                    Double declare = deviceInfoList.stream().mapToDouble(DeviceInfo::getDeviceRatedPower).sum();
                                    double remaining = declareLoad;
                                    for (DeviceInfo s : deviceInfoList) {
                                        double ratio = (s.getDeviceRatedPower() / declare);
                                        double updateQuantity = (int) (ratio * declareLoad);

                                        DeviceInfoModel model = new DeviceInfoModel();
                                        model.setDeviceSn(s.getDeviceSn());
                                        model.setDeviceNowTemperature(s.getOutletTemperature());
                                        model.setDeviceRatedPower(s.getDeviceRatedPower());
                                        model.setActualDeclare(updateQuantity);
                                        model.setNoHouseholds(n.getNoHouseholds());

                                        remaining -= updateQuantity;

                                        deviceList.add(model);
                                        deviceList2.add(model);
                                    }
                                    // 检查是否所有申报量之和与总申报量相等
                                    if (remaining != 0) {
                                        // 如果不相等，则调整最后一个用户的申报量，使其与总申报量相等
                                        deviceList.get(deviceList.size() - 1).setActualDeclare(remaining);
                                    }
                                    //目前根据节点也就是户号调节，不能根据每个设备进行调节，全部设备都调为一个温度
                                    DemandStrategy demandStrategy = map.get(n.getNoHouseholds());
//                                    //未确定推荐策略的不下发暂时不用了，先注释
//                                    if (demandStrategy.getEnsure() == null || demandStrategy.getEnsure() != 1) {
//                                        continue;
//                                    }
                                    if (demandStrategy.getStrategyContent() == null || JSONObject.parseObject(demandStrategy.getStrategyContent()).getDouble("ch_water_outlet_temperature") == null) {
                                        continue;
                                    }
                                    Double deviceSetTemperature = JSONObject.parseObject(demandStrategy.getStrategyContent()).getDouble("ch_water_outlet_temperature");
                                    if (deviceSetTemperature == null) {
                                        continue;
                                    }
                                    deviceList2.forEach(d -> {
                                        DeviceSetInfo deviceSetInfo = new DeviceSetInfo();
                                        deviceSetInfo.setDeviceSn(d.getDeviceSn());
                                        deviceSetInfo.setDeviceSetTemperature(Math.round(deviceSetTemperature));

                                        deviceSetInfoList.add(deviceSetInfo);
                                    });
                                    //IOT温度控制下发
                                    demandResponseCommand(n.getNoHouseholds(), deviceSetInfoList);
                                    logger.info("下发智能推荐iot指令：" + n.getNoHouseholds() + "," + JSON.toJSONString(deviceSetInfoList));
                                    deviceSetInfoList.clear();
                                }

                            }
                        }
                        //将设置温度前的设备信息，放入redis里
                        redisUtils.add("ChinaSouthernPowerGrid_" + respId, JSON.toJSON(deviceList).toString());
                    }
                } else if (dStatus == 3) {
                    logger.info("下发智能结束iot指令开始：" + respId);
                    //将设备温度恢复之前的设置,暂时写死
                    String value = String.valueOf(redisUtils.get("ChinaSouthernPowerGrid_" + respId));
                    logger.info(respId + "之前的温度设置：" + value);
                    if (StringUtils.isNotBlank(value)) {
                        List<DeviceInfoModel> list =
                                JSONObject.parseArray(value, DeviceInfoModel.class);
                        if (list != null && list.size() > 0) {
                            Map<String, List<DeviceInfoModel>> deviceMap = list.stream().collect(Collectors.groupingBy(DeviceInfoModel::getNoHouseholds));
                            for (String noHouseholds : deviceMap.keySet()) {
                                List<DeviceInfoModel> deviceList = deviceMap.get(noHouseholds);
                                List<DeviceSetInfo> setInfoList = new ArrayList<>();
                                deviceList.forEach(d -> {
                                    DeviceSetInfo deviceSetInfo = new DeviceSetInfo();
                                    deviceSetInfo.setDeviceSn(d.getDeviceSn());
                                    deviceSetInfo.setDeviceSetTemperature(Math.round(d.getDeviceNowTemperature()));

                                    setInfoList.add(deviceSetInfo);
                                });
                                //IOT温度控制下发
                                demandResponseCommand(noHouseholds, setInfoList);
                                logger.info("下发智能推荐iot指令恢复设置：" + noHouseholds + "," + setInfoList.toString());
                                setInfoList.clear();
                                deviceList.clear();
                            }
                        }
                    }
                    //删除键
                    redisUtils.delete("ChinaSouthernPowerGrid_" + respId);
                }
            }
            else if (!strategyId.equals("getInvitation")) {
                //非南网的策略，直接走正常可调负荷的运行策略
                logger.info("下发普通策略iot指令开始：" + strategyId);

                ScheduleStrategy scheduleStrategy = scheduleStrategyRepository.findByStrategyId(strategyId);

                if (scheduleStrategy != null && scheduleStrategy.getDeviceList() != null && scheduleStrategy.getDeviceList().size() > 0) {
                    String cmdExpression = scheduleStrategy.getCmdExpression();
                    List<Device> deviceList = scheduleStrategy.getDeviceList();

                    if (!StringUtils.isEmpty(cmdExpression) && deviceList != null && deviceList.size() > 0) {
                        Map<String, List<Device>> groupByMecId = deviceList.stream().collect(Collectors.groupingBy((px) -> px.getMecId()));

                        if (groupByMecId != null && groupByMecId.size() > 0) {
                            boolean isAirConditioning = scheduleStrategy.getStrategyType() == 0;
                            //反控开关设备
                            if (!isAirConditioning) {
                                OtherConditioningDTO otherConditioningDTO = JSON.parseObject(cmdExpression, OtherConditioningDTO.class);
                                if (otherConditioningDTO != null) {
                                    for (String mecId : groupByMecId.keySet()) {

                                        groupByMecId.get(mecId).forEach(device -> {
                                            DevicePoint power = device.getDevicePointList()
                                                    .stream()
                                                    .findFirst()
                                                    .filter(c -> c.getPointDesc().equals("switch")).orElse(null);

                                            List<PointInfo> pointInfoList = new ArrayList<>();
                                            //1、手动需求响应
                                            //削峰、填谷需求响应，响应开始时，直接执行策略里配置的内容（执行时，与策略的开关状态无关）;
                                            // 响应结束时，设备策略不做处理
                                            //2、自动需求响应
                                            //当为削峰需求响应时，响应开始时，如果有参与自动需求响应的策略且状态为为开启，则自动关闭配置了自动需求响应策略中的设备；
                                            // 响应结束时，如果有参与自动需求响应的策略且状态为为开启，自动开启配置了自动需求响应策略中的设备。
                                            //当为填谷需求响应时，响应开始，如果有参与自动需求响应的策略且状态为为开启，自动开启配置了自动需求响应策略中的设备；
                                            // 响应结束，如果有参与自动需求响应的策略且状态为为开启，自动关闭配置了自动需求响应策略中的设备。
                                            if (power != null) {
                                                if (scheduleStrategy.isDemandResponse() && scheduleStrategy.isStrategyStatus()) {
                                                    if (dStatus == 2) {
                                                        //直接查到所有设备，respType == 1 进行关闭
                                                        //直接查到所有设备，respType != 1 进行开启
                                                        pointInfoList.add(new PointInfo(power.getPointSn(), respType == 1 ? POWEREnum.POWER_OFF.toString() : POWEREnum.POWER_ON.toString()));
                                                    } else {
                                                        //直接查到所有设备，respType == 1 进行开启
                                                        //直接查到所有设备，respType != 1 进行关闭
                                                        pointInfoList.add(new PointInfo(power.getPointSn(), respType == 1 ? POWEREnum.POWER_ON.toString() : POWEREnum.POWER_OFF.toString()));
                                                    }
                                                } else {
                                                    if (dStatus == 2) {
                                                        pointInfoList.add(new PointInfo(power.getPointSn(), otherConditioningDTO.getPower().name()));
                                                    }
                                                }
                                            }

                                            if (pointInfoList != null && pointInfoList.size() > 0) {
                                                List<RPCModel> models = new ArrayList<>();
                                                models.add(new RPCModel(device.getDeviceSn(), pointInfoList, new Date()));
                                                //IOT照明 充电桩开关控制下发
                                                iotControlService.CommonRPCRequestToDevice(ModuleNameEnum.tactics.name(), models);
                                            }
                                        });
                                    }
                                }
                            }
                            //反控空调设备
                            if (isAirConditioning) {
                                AirConditioningDTO airConditioningDTO = JSON.parseObject(cmdExpression, AirConditioningDTO.class);
                                if (airConditioningDTO != null) {
                                    for (String mecId : groupByMecId.keySet()) {

                                        groupByMecId.get(mecId).stream().forEach(device -> {
                                            DevicePoint power = device.getDevicePointList().stream().filter(c -> c.getPointDesc().equals("switch")).findFirst().orElse(null);
                                            DevicePoint windSpeed = device.getDevicePointList().stream().filter(c -> c.getPointDesc().equals("wind_speed")).findFirst().orElse(null);
                                            DevicePoint model = device.getDevicePointList().stream().filter(c -> c.getPointDesc().equals("model")).findFirst().orElse(null);
                                            DevicePoint temperature = device.getDevicePointList().stream().filter(c -> c.getPointDesc().equals("temperature")).findFirst().orElse(null);

                                            List<PointInfo> pointInfoList = new ArrayList<>();

                                            //1、手动需求响应
                                            //削峰、填谷需求响应，响应开始时，直接执行策略里配置的内容（执行时，与策略的开关状态无关）;
                                            // 响应结束时，设备策略不做处理
                                            //2、自动需求响应
                                            //当为削峰需求响应时，响应开始时，如果有参与自动需求响应的策略且状态为为开启，则自动关闭配置了自动需求响应策略中的设备；
                                            // 响应结束时，如果有参与自动需求响应的策略且状态为为开启，自动开启配置了自动需求响应策略中的设备。
                                            //当为填谷需求响应时，响应开始，如果有参与自动需求响应的策略且状态为为开启，自动开启配置了自动需求响应策略中的设备；
                                            // 响应结束，如果有参与自动需求响应的策略且状态为为开启，自动关闭配置了自动需求响应策略中的设备。
                                            if (scheduleStrategy.isDemandResponse() && scheduleStrategy.isStrategyStatus()) {
                                                if (dStatus == 2) {
                                                    if (power != null) {
                                                        //直接查到所有设备，等于1 进行关闭
                                                        //直接查到所有设备，不等于1 进行开启
                                                        pointInfoList.add(new PointInfo(power.getPointSn(), respType == 1 ? POWEREnum.POWER_OFF.toString() : POWEREnum.POWER_ON.toString()));
                                                    }

                                                } else {

                                                    if (power != null) {
                                                        //直接查到所有设备，等于1 进行开启
                                                        //直接查到所有设备，不等于1 进行关闭
                                                        pointInfoList.add(new PointInfo(power.getPointSn(), respType == 1 ? POWEREnum.POWER_ON.toString() : POWEREnum.POWER_OFF.toString()));
                                                    }
                                                }
                                            } else {
                                                if (dStatus == 2) {
                                                    if (power != null) {
                                                        pointInfoList.add(new PointInfo(power.getPointSn(), airConditioningDTO.getPower().name()));
                                                    }
                                                    if (windSpeed != null) {
                                                        pointInfoList.add(new PointInfo(windSpeed.getPointSn(), airConditioningDTO.getWindSpeed().name()));
                                                    }
                                                    if (model != null) {
                                                        pointInfoList.add(new PointInfo(model.getPointSn(), airConditioningDTO.getMode().name()));
                                                    }
                                                    if (temperature != null) {
                                                        pointInfoList.add(new PointInfo(temperature.getPointSn(), airConditioningDTO.getTemp().name()));
                                                    }
                                                }
                                            }

                                            if (pointInfoList != null && pointInfoList.size() > 0) {

                                                List<RPCModel> models = new ArrayList<>();
                                                models.add(new RPCModel(device.getDeviceSn(), pointInfoList, new Date()));
                                                //IOT空调控制下发
                                                iotControlService.CommonRPCRequestToDevice(ModuleNameEnum.tactics.name(), models);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                //若是南网的需求响应任务，直接查询户号设置冷机的出水温度
                if (dStatus == 2) {
                    logger.info("下发联想iot指令开始：" + respId);
                    List<DemandRespStrategyNo> noList = noRepository.findNoListByRespId(respId).stream().filter(v -> v.getWinningBid() != null).collect(Collectors.toList());
                    if (noList != null && noList.size() > 0) {
                        //记录未调节温度前的设备信息，待响应时间结束后，从redis中获取下来
                        List<DeviceInfoModel> deviceList = new ArrayList<>();
                        for (DemandRespStrategyNo n : noList) {
                            //判断该户号是否已中标
                            if (n.getWinningBid() == 1) {
                                //在线设备
                                List<DeviceInfo> deviceInfoList = noHouseholdsService.findNoHouseholdsDeviceInfo(n.getNoHouseholds());

                                if (deviceInfoList != null && deviceInfoList.size() > 0) {
                                    List<DeviceSetInfo> deviceSetInfoList = new ArrayList<>();
                                    //该户号需要调节的总负荷
                                    double declareLoad = n.getDeclareLoad();

                                    //根据节点的申报额定功率，进行占比分配
                                    Double declare = deviceInfoList.stream().mapToDouble(DeviceInfo::getDeviceRatedPower).sum();
                                    double remaining = declareLoad;
                                    for (DeviceInfo s : deviceInfoList) {
                                        double ratio = (s.getDeviceRatedPower() / declare);
                                        double updateQuantity = (int) (ratio * declareLoad);

                                        DeviceInfoModel model = new DeviceInfoModel();
                                        model.setDeviceSn(s.getDeviceSn());
                                        model.setDeviceNowTemperature(s.getOutletTemperature());
                                        model.setDeviceRatedPower(s.getDeviceRatedPower());
                                        model.setActualDeclare(updateQuantity);
                                        model.setNoHouseholds(n.getNoHouseholds());

                                        remaining -= updateQuantity;

                                        deviceList.add(model);
                                    }
                                    // 检查是否所有申报量之和与总申报量相等
                                    if (remaining != 0) {
                                        // 如果不相等，则调整最后一个用户的申报量，使其与总申报量相等
                                        deviceList.get(deviceList.size() - 1).setActualDeclare(remaining);
                                    }
                                    deviceList.forEach(d -> {
                                        double targetOutTemp = returnTargetOutTemp(respType, 0, d.getDeviceNowTemperature(),
                                                d.getDeviceRatedPower(), d.getActualDeclare());
                                        DeviceSetInfo deviceSetInfo = new DeviceSetInfo();
                                        deviceSetInfo.setDeviceSn(d.getDeviceSn());
                                        deviceSetInfo.setDeviceSetTemperature(Math.round(targetOutTemp));

                                        deviceSetInfoList.add(deviceSetInfo);
                                    });

                                    //IOT温度控制下发
                                    demandResponseCommand(n.getNoHouseholds(), deviceSetInfoList);
                                    logger.info("下发iot指令：" + n.getNoHouseholds() + "," + JSON.toJSONString(deviceInfoList));
                                }

                            }
                        }

                        //将设置温度前的设备信息，放入redis里
                        redisUtils.add("ChinaSouthernPowerGrid_" + respId, JSON.toJSON(deviceList).toString());
                    }
                } else if (dStatus == 3) {
                    logger.info("下发结束联想iot指令开始：" + respId);
                    //将设备温度恢复之前的设置
                    String value = String.valueOf(redisUtils.get("ChinaSouthernPowerGrid_" + respId));
                    logger.info(respId + "之前的温度设置：" + value);
                    if (StringUtils.isNotBlank(value)) {
                        List<DeviceInfoModel> list =
                                JSONObject.parseArray(value, DeviceInfoModel.class);
                        if (list != null && list.size() > 0) {
                            Map<String, List<DeviceInfoModel>> deviceMap = list.stream().collect(Collectors.groupingBy(DeviceInfoModel::getNoHouseholds));

                            for (String noHouseholds : deviceMap.keySet()) {
                                List<DeviceInfoModel> deviceList = deviceMap.get(noHouseholds);
                                List<DeviceSetInfo> setInfoList = new ArrayList<>();
                                deviceList.forEach(d -> {
                                    DeviceSetInfo deviceSetInfo = new DeviceSetInfo();
                                    deviceSetInfo.setDeviceSn(d.getDeviceSn());
                                    deviceSetInfo.setDeviceSetTemperature(Math.round(d.getDeviceNowTemperature()));

                                    setInfoList.add(deviceSetInfo);
                                });
                                //IOT温度控制下发
                                demandResponseCommand(noHouseholds, setInfoList);
                                logger.info("下发iot指令恢复设置：" + noHouseholds + "," + setInfoList.toString());
                            }
                        }
                    }
                    //删除键
                    redisUtils.delete("ChinaSouthernPowerGrid_" + respId);
                }

            }

        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 南网水温调节算法
     *
     * @param DPType         1 削减峰 2 填谷
     * @param setInTemp      冷机进水温度 ，一般精确到小数点后 1 位
     * @param setOutTemp     冷机现在设定的出水温度，，一般精确到小数点后 1 位
     * @param chillerStdLoad 额定功率 XXXKW
     * @param declareLoad    调节的负荷
     * @return setTargetOutTemp 冷机要调节的目标出水温度，一般精确到小数点后 1 位
     */
    double returnTargetOutTemp(int DPType, double setInTemp, double setOutTemp, double chillerStdLoad, double declareLoad) {
        double targetOutTemp = 0.0;
        //当削峰响应时 —— 调高出水温度,降低负荷（setTargetOutTemp- setOutTemp）* 10% *  ChillerStdLoad
        //declareLoad/(chillerStdLoad*0.1)+setOutTemp;
        if (DPType == 1) {
            targetOutTemp = declareLoad / (chillerStdLoad * 0.1) + setOutTemp;
        } else if (DPType == 2) {
            //当填谷响应时 —— 调低出水温度,提高负荷（setOutTemp-setTargetOutTemp ）* 10% *  ChillerStdLoad
            //setOutTemp-declareLoad/(chillerStdLoad*0.1)
            targetOutTemp = setOutTemp - declareLoad / (chillerStdLoad * 0.1);
        }
        return targetOutTemp;

    }

    /**
     * IOT下发控制冷机温度
     *
     * @param noHouseholds      户号
     * @param deviceSetInfoList 该户号下调控 设备的编号及对应的设置温度
     */
    void demandResponseCommand(String noHouseholds, List<DeviceSetInfo> deviceSetInfoList) {

        if (deviceSetInfoList == null || deviceSetInfoList.size() == 0) {
            return;
        }
        for (DeviceSetInfo info : deviceSetInfoList) {
            Device device = deviceRepository.findByDeviceSn(info.getDeviceSn());
            if (device != null) {
                String deviceSn = device.getDeviceSn();
                String temperature = String.valueOf(info.getDeviceSetTemperature());
                String pointSn = getPointSn(deviceSn, "freezing_water_outlet_temperature_set");
                if (StringUtils.isEmpty(deviceSn) == false
                        && StringUtils.isEmpty(pointSn) == false
                        && StringUtils.isEmpty(temperature) == false) {

                    List<PointInfo> pointInfoList = new ArrayList<>();
                    pointInfoList.add(new PointInfo(pointSn, temperature));
                    List<RPCModel> models = new ArrayList<>();
                    models.add(new RPCModel(device.getDeviceSn(), pointInfoList, new Date()));

                    iotControlService.CommonRPCRequestToDevice(ModuleNameEnum.demand_response_task.name(),models);
                }
            }
        }
    }


    String getPointSn(String deviceSn, String pointDesc) {
        Device device = deviceRepository.findByDeviceSn(deviceSn);
        if (device != null) {
            Optional<DevicePoint> devicePoint = device.getDevicePointList()
                    .stream()
                    .filter(c -> c.getPointDesc().equals(pointDesc))
                    .findFirst();
            if (devicePoint.isPresent()) {
                return devicePoint.get().getPointSn();
            }
        }
        return null;
    }
}
