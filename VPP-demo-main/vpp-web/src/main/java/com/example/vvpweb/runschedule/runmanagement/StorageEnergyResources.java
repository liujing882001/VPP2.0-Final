package com.example.vvpweb.runschedule.runmanagement;

import com.example.vvpcommom.EntityUtils;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.AlarmLogRepository;
import com.example.vvpdomain.CfgStorageEnergyBaseInfoRepository;
import com.example.vvpdomain.IotTsKvLastRepository;
import com.example.vvpdomain.IotTsKvMeteringDevice96Repository;
import com.example.vvpdomain.entity.CfgStorageEnergyBaseInfo;
import com.example.vvpdomain.entity.IotTsKvLast;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.runschedule.runmanagement.model.AlarmModel;
import com.example.vvpweb.runschedule.runmanagement.model.StorageEnergyChartModel;
import com.example.vvpweb.runschedule.runmanagement.model.StorageEnergyResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/runSchedule/runManagement")
@CrossOrigin
@Api(value = "运行管理-分布式储能实时运行状态", tags = {"运行管理-分布式储能实时运行状态"})
public class StorageEnergyResources {

    private static Logger logger = LoggerFactory.getLogger(StorageEnergyResources.class);
    @Resource
    IotTsKvMeteringDevice96Repository device96Repository;
    @Resource
    CfgStorageEnergyBaseInfoRepository cfgStorageEnergyBaseInfoRepository;
    @Resource
    private AlarmLogRepository alarmRepository;
    @Autowired
    private IUserService userService;
    @Resource
    private IotTsKvLastRepository iotTsKvLastRepository;

    @ApiOperation("分布式储能实时运行状态-今日")
    @UserLoginToken
    @RequestMapping(value = "storageEnergyListNow", method = {RequestMethod.POST})
    public ResponseResult<Map<String, Object>> storageEnergyListNow(@RequestParam(value = "nodeId") String nodeId) {
        try {
            Map<String, Object> map = new HashMap();

            List<String> ids = userService.getAllowStorageEnergyNodeIds();
            if (ids == null || !ids.contains(nodeId)) {
                map.put("alarmNumber", 0);
                map.put("inEnergy", 0);
                map.put("outEnergy", 0);
                map.put("storageCapacity", 0);
                map.put("storageLoad", 0);
                map.put("soh", 0);
                map.put("soc", 0);
                return ResponseResult.success(map);
            }

            SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
            // 国内时区是GMT+8
            fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            SimpleDateFormat fmt_ym = new SimpleDateFormat("yyyy-MM");
            // 国内时区是GMT+8
            fmt_ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));


            Date dt = new Date();
            String charging_device_sn = "";
            String discharging_device_sn = "";
            //电池状态监控设备编码
            String battery_status_device_sn = "";

            double storage_capacity = 0;
            double storage_load = 0;
            double soh = 0;
            double soc = 0;
            double in_energy = 0;
            double out_energy = 0;

            CfgStorageEnergyBaseInfo baseInfo = cfgStorageEnergyBaseInfoRepository.findCfgStorageEnergyBaseInfoByNodeId(nodeId);
            if (baseInfo != null) {
                storage_capacity = baseInfo.getStorageEnergyCapacity();
                storage_load = baseInfo.getStorageEnergyLoad();
                charging_device_sn = baseInfo.getChargingDeviceSn();
                discharging_device_sn = baseInfo.getDischargingDeviceSn();
                battery_status_device_sn = baseInfo.getBatteryStatusDeviceSn();
            }

            IotTsKvLast lastNodeInfo = iotTsKvLastRepository.findAllByNodeIdAndDeviceSnAndPointDesc(nodeId, battery_status_device_sn, "soc");
            if (lastNodeInfo != null) {
                String now = fmt_ymd.format(dt.getTime());
                String ts = fmt_ymd.format(lastNodeInfo.getTs());
                if (ts.equals(now)) {
                    soc = Double.parseDouble(lastNodeInfo.getPointValue()) * 0.01;
                }
            }
            lastNodeInfo = iotTsKvLastRepository.findAllByNodeIdAndDeviceSnAndPointDesc(nodeId, battery_status_device_sn, "soh");
            if (lastNodeInfo != null) {
                String now = fmt_ymd.format(dt.getTime());
                String ts = fmt_ymd.format(lastNodeInfo.getTs());
                if (ts.equals(now)) {
                    soh = Double.parseDouble(lastNodeInfo.getPointValue()) * 0.01;
                }
            }


            List<Object[]> charging_objects = device96Repository.findInOutEnergyEGroupYearMonth(nodeId, charging_device_sn, fmt_ym.format(dt));
            if (charging_objects != null && charging_objects.size() > 0) {
                List<StorageEnergyChartModel> chartModels = EntityUtils.castEntity(charging_objects, StorageEnergyChartModel.class, new StorageEnergyChartModel());
                if (chartModels != null && chartModels.size() > 0) {

                    in_energy = chartModels.stream().mapToDouble(p -> p.getH_total_use()).sum();
                }
            }

            List<Object[]> discharging_objects = device96Repository.findInOutEnergyEGroupYearMonth(nodeId, discharging_device_sn, fmt_ym.format(dt));
            if (discharging_objects != null && discharging_objects.size() > 0) {
                List<StorageEnergyChartModel> chartModels = EntityUtils.castEntity(discharging_objects, StorageEnergyChartModel.class, new StorageEnergyChartModel());
                if (chartModels != null && chartModels.size() > 0) {
                    out_energy = chartModels.stream().mapToDouble(p -> p.getH_total_use()).sum();
                }
            }


            List<Object[]> objects = alarmRepository.getAlarmLogByYMD(nodeId, fmt_ymd.format(dt));
            if (objects != null && objects.size() > 0) {
                List<AlarmModel> alarmModels = EntityUtils.castEntity(objects, AlarmModel.class, new AlarmModel());
                if (alarmModels != null && alarmModels.size() > 0) {
                    alarmModels.stream().forEach(alarmModel -> {
                        switch (String.valueOf(alarmModel.getSeverity())) {
                            //严重程度，等级 0 紧急1 重要2 次要3 提示
                            case "0":
                                map.put("紧急", alarmModel.getCount());
                                break;
                            case "1":
                                map.put("重要", alarmModel.getCount());
                                break;
                            case "2":
                                map.put("次要", alarmModel.getCount());
                                break;
                            case "3":
                                map.put("提示", alarmModel.getCount());
                                break;
                        }
                    });
                }
            }

            map.put("alarmNumber", objects != null ? objects.size() : 0);
            map.put("inEnergy", Double.parseDouble(String.format("%.4f", in_energy)));
            map.put("outEnergy", Double.parseDouble(String.format("%.4f", out_energy)));
            map.put("storageCapacity", Double.parseDouble(String.format("%.4f", storage_capacity)));
            map.put("storageLoad", Double.parseDouble(String.format("%.4f", storage_load)));
            map.put("soh", soh <= 0 ? 0 : Double.parseDouble(String.format("%.4f", soh)));
            map.put("soc", soc <= 0 ? 0 : Double.parseDouble(String.format("%.4f", soc)));
            return ResponseResult.success(map);
        } catch (Exception e) {
            logger.error("storageEnergyListNow 分布式储能实时运行状态失败!", e.getMessage());
            return ResponseResult.error("storageEnergyListNow 分布式储能实时运行状态失败!" + e.getMessage());
        }

    }


    @ApiOperation("分布式储能实时运行状态-年 e-chart")
    @UserLoginToken
    @RequestMapping(value = "storageEnergyListChart", method = {RequestMethod.POST})
    public ResponseResult<StorageEnergyResponse> storageEnergyListChart(@RequestParam(value = "nodeId") String nodeId) {
        try {
            List<String> ids = userService.getAllowStorageEnergyNodeIds();
            if (ids == null || !ids.contains(nodeId)) {
                return ResponseResult.success(null);
            }
            SimpleDateFormat fmt_ym = new SimpleDateFormat("yyyy-MM");
            // 国内时区是GMT+8
            fmt_ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Date now_dt = new Date();
            Date last_dt = TimeUtil.dateAddMonths(now_dt, -11);

            String charging_device_sn = null;
            String discharging_device_sn = null;

            CfgStorageEnergyBaseInfo baseInfo = cfgStorageEnergyBaseInfoRepository.findCfgStorageEnergyBaseInfoByNodeId(nodeId);
            if (baseInfo != null) {
                charging_device_sn = baseInfo.getChargingDeviceSn();
                discharging_device_sn = baseInfo.getDischargingDeviceSn();
            }
            if (StringUtils.isNotEmpty(charging_device_sn) && StringUtils.isNotEmpty(discharging_device_sn)) {

                List<String> y = new ArrayList<>();
                List<Double> x_in = new ArrayList<>();
                List<Double> x_out = new ArrayList<>();

                Map<String, Double> charging = new TreeMap<>();
                Map<String, Double> discharging = new TreeMap<>();

                List<Date> dts = TimeUtil.truncateToSplitMonth(last_dt, now_dt);
                if (dts != null && dts.size() > 0) {
                    dts.stream().forEach(dt -> {
                        String dt_ym = fmt_ym.format(dt);
                        y.add(dt_ym);
                        charging.put(dt_ym, (double) 0);
                        discharging.put(dt_ym, (double) 0);
                    });
                }

                StorageEnergyResponse response = new StorageEnergyResponse();

                List<Object[]> charging_objects = device96Repository.findInOutEnergyGroupYearMonth(nodeId, charging_device_sn, fmt_ym.format(last_dt));
                if (charging_objects != null && charging_objects.size() > 0) {
                    List<StorageEnergyChartModel> chartModels = EntityUtils.castEntity(charging_objects, StorageEnergyChartModel.class, new StorageEnergyChartModel());
                    if (chartModels != null && chartModels.size() > 0) {
                        for (StorageEnergyChartModel chartModel : chartModels) {
                            try {
                                String dt_ym = fmt_ym.format(fmt_ym.parse(chartModel.getTs()));
                                Double h_total_use = chartModel.getH_total_use();
                                if (charging.containsKey(dt_ym)) {
                                    charging.put(dt_ym, h_total_use);
                                }
                            } catch (Exception ex) {
                            }
                        }
                    }
                }

                List<Object[]> discharging_objects = device96Repository.findInOutEnergyGroupYearMonth(nodeId, discharging_device_sn, fmt_ym.format(last_dt));
                if (discharging_objects != null && discharging_objects.size() > 0) {
                    List<StorageEnergyChartModel> chartModels = EntityUtils.castEntity(discharging_objects, StorageEnergyChartModel.class, new StorageEnergyChartModel());
                    if (chartModels != null && chartModels.size() > 0) {
                        for (StorageEnergyChartModel chartModel : chartModels) {
                            try {
                                String dt_ym = fmt_ym.format(fmt_ym.parse(chartModel.getTs()));
                                Double h_total_use = chartModel.getH_total_use();
                                if (discharging.containsKey(dt_ym)) {
                                    discharging.put(dt_ym, h_total_use);
                                }
                            } catch (Exception ex) {
                            }
                        }
                    }
                }

                for (Map.Entry<String, Double> entry : charging.entrySet()) {
                    String mapKey = entry.getKey();
                    Double mapValue = entry.getValue();
                    System.out.println(mapKey + ":" + mapValue);
                    x_in.add(Double.parseDouble(String.format("%.4f", mapValue)));
                }

                for (Map.Entry<String, Double> entry : discharging.entrySet()) {
                    String mapKey = entry.getKey();
                    Double mapValue = entry.getValue();
                    System.out.println(mapKey + ":" + mapValue);
                    x_out.add(Double.parseDouble(String.format("%.4f", mapValue)));
                }

                response.setX_in(x_in);
                response.setX_out(x_out);
                response.setY(y);
                return ResponseResult.success(response);
            }
        } catch (Exception e) {
            logger.error("storageEnergyListChart 布式储能实时运行状态-年 e-chart失败!", e.getMessage());
            return ResponseResult.error("storageEnergyListChart 布式储能实时运行状态-年 e-chart失败!" + e.getMessage());
        }
        return ResponseResult.success(null);
    }
}
