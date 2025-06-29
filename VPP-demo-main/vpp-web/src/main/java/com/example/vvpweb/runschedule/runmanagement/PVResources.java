package com.example.vvpweb.runschedule.runmanagement;

import com.example.vvpcommom.EntityUtils;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.AlarmLogRepository;
import com.example.vvpdomain.CfgPhotovoltaicBaseInfoRepository;
import com.example.vvpdomain.IotTsKvLastRepository;
import com.example.vvpdomain.IotTsKvMeteringDevice96Repository;
import com.example.vvpdomain.entity.CfgPhotovoltaicBaseInfo;
import com.example.vvpdomain.entity.IotTsKvLast;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.runschedule.runmanagement.model.AlarmModel;
import com.example.vvpweb.runschedule.runmanagement.model.LastSERInfoModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "运行管理-光伏资源", tags = {"运行管理-光伏资源"})
public class PVResources {
    private static Logger logger = LoggerFactory.getLogger(PVResources.class);
    @Resource
    IotTsKvMeteringDevice96Repository device96Repository;
    @Resource
    private AlarmLogRepository alarmRepository;
    @Autowired
    private IUserService userService;
    @Resource
    private CfgPhotovoltaicBaseInfoRepository cfgPhotovoltaicBaseInfoRepository;
    @Resource
    private IotTsKvLastRepository iotTsKvLastRepository;

    @ApiOperation("光伏资源-今天")
    @UserLoginToken
    @RequestMapping(value = "photovoltaicListNow", method = {RequestMethod.POST})
    public ResponseResult<Map<String, Object>> photovoltaicListNow(@RequestParam(value = "nodeId") String nodeId) {
        try {
            Map<String, Object> map = new HashMap();
            List<String> ids = userService.getAllowPvNodeIds();
            if (ids == null || !ids.contains(nodeId)) {
                map.put("alarmNumber", 0);
                map.put("energy", 0);
                map.put("pvCapacity", 0);
                map.put("pvLoad", 0);
                map.put("AccumulatedStartupTime", 0);
                return ResponseResult.success(map);
            }
            SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
            // 国内时区是GMT+8
            fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            Date dt = new Date();
            //装机容量
            double pv_capacity = 0;
            //实时功率
            double pv_load = 0;
            //发电量
            double pv_energy = 0;
            //满发小时数
            double accumulated_startup_time = 0;


            CfgPhotovoltaicBaseInfo baseInfo = cfgPhotovoltaicBaseInfoRepository.findCfgPhotovoltaicBaseInfoByNodeId(nodeId);
            if (baseInfo != null) {
                pv_capacity = baseInfo.getPhotovoltaicInstalledCapacity();
            }
            List<IotTsKvLast> lastNodeInfo = iotTsKvLastRepository.findAllByNodeIdAndPointDesc(nodeId, "accumulated_startup_time");
            if (lastNodeInfo != null && lastNodeInfo.size() > 0 && lastNodeInfo.get(0) != null) {
                String now = fmt_ymd.format(dt.getTime());
                String ts = fmt_ymd.format(lastNodeInfo.get(0).getTs());
                if (ts.equals(now)) {
                    accumulated_startup_time = Double.parseDouble(lastNodeInfo.get(0).getPointValue());
                }
            }
            List<Object[]> LastNodeInfo = device96Repository.findLastNodeInfo(nodeId);
            if (LastNodeInfo != null && LastNodeInfo.size() > 0) {
                List<LastSERInfoModel> model = EntityUtils.castEntity(LastNodeInfo, LastSERInfoModel.class, new LastSERInfoModel());
                if (model != null && model.size() > 0) {
                    String now = fmt_ymd.format(dt.getTime());
                    String ts = fmt_ymd.format(model.get(0).getCount_data_time());
                    if (ts.equals(now)) {
                        pv_load = model.get(0).getLoad();
                        pv_energy = model.get(0).getEnergy();
                    }
                }
            }


            Date ymd = fmt_ymd.parse(fmt_ymd.format(dt));
            List<Object[]> objects = alarmRepository.getAlarmLogByYMD(nodeId, fmt_ymd.format(ymd));
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
            map.put("energy", Double.parseDouble(String.format("%.4f", pv_energy)));
            map.put("pvCapacity", Double.parseDouble(String.format("%.4f", pv_capacity)));
            map.put("pvLoad", Double.parseDouble(String.format("%.4f", pv_load)));
            map.put("AccumulatedStartupTime", accumulated_startup_time);
            return ResponseResult.success(map);
        } catch (Exception e) {
            logger.error("photovoltaicListNow 获取光伏资源失败!", e.getMessage());
            return ResponseResult.error("photovoltaicListNow 获取光伏资源失败!" + e.getMessage());
        }

    }

}
