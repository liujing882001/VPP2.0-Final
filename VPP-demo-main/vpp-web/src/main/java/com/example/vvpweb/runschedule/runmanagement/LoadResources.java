package com.example.vvpweb.runschedule.runmanagement;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.EntityUtils;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.Node;
import com.example.vvpdomain.entity.SysDictType;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.flexibleresourcemanagement.model.BuildFactoryModel;
import com.example.vvpweb.runschedule.runmanagement.model.AlarmInfoModel;
import com.example.vvpweb.runschedule.runmanagement.model.KWNodeModel;
import com.example.vvpweb.runschedule.runmanagement.model.LastSERInfoModel;
import com.example.vvpweb.runschedule.runmanagement.model.VNModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/runSchedule/runManagement")
@CrossOrigin
@Api(value = "运行管理-可调负荷", tags = {"运行管理-可调负荷"})
public class LoadResources {

    private static Logger logger = LoggerFactory.getLogger(LoadResources.class);
    @Resource
    IotTsKvMeteringDevice96Repository device96Repository;
    @Resource
    DeviceRepository deviceRepository;
    @Resource
    SysDictTypeRepository sysDictTypeRepository;
    @Resource
    NodeRepository nodeRepository;
    @Resource
    private AlarmLogRepository alarmRepository;
    @Autowired
    private IUserService userService;

    @ApiOperation("可调负荷-今天")
    @UserLoginToken
    @RequestMapping(value = "loadListByNow", method = {RequestMethod.POST})
    public ResponseResult<Map<String, Object>> loadListByNow(@RequestParam(value = "nodeId") String nodeId) {
        try {
            Map<String, Object> map = new HashMap();
            List<String> ids = userService.getAllowLoadNodeIds();
            if (ids == null || !ids.contains(nodeId)) {
                map.put("alarmRatio", null);
                map.put("alarmNumber", 0);
                map.put("load", 0);
                map.put("totalLoad", 0);
                return ResponseResult.success(map);
            }

            SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
            // 国内时区是GMT+8
            fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Date dt = new Date();
            //总功率
            Double totalLoad = (double) 0;
            //实时功率
            Double load = (double) 0;


            List<Object[]> kw_objects = deviceRepository.findAllKWByNodeId(nodeId);
            if (kw_objects != null && kw_objects.size() > 0) {

                List<KWNodeModel> kwNodeModels = EntityUtils.castEntity(kw_objects, KWNodeModel.class, new KWNodeModel());
                if (kwNodeModels != null && kwNodeModels.size() > 0) {
                    totalLoad = kwNodeModels.stream().mapToDouble(c -> c.getDevice_rated_power()).sum();
                }
            }

            List<Object[]> LastNodeInfo = device96Repository.findLastNodeInfo(nodeId);
            if (LastNodeInfo != null && LastNodeInfo.size() > 0) {
                List<LastSERInfoModel> model = EntityUtils.castEntity(LastNodeInfo, LastSERInfoModel.class, new LastSERInfoModel());
                if (model != null && model.size() > 0) {
                    String now = fmt_ymd.format(dt.getTime());
                    String ts = fmt_ymd.format(model.get(0).getCount_data_time());
                    if (ts.equals(now)) {
                        load = model.get(0).getLoad();
                    }
                }
            }
            Map<String, Object> childLoadMap = new HashMap();

            Node node = nodeRepository.findById(nodeId).orElse(null);
            if (node != null) {
                List<String> systemIdList = JSONObject.parseArray(node.getSystemIds(), String.class);
                if (systemIdList != null && systemIdList.size() > 0) {
                    for (String systemId : systemIdList) {
                        if (!systemId.equals("nengyuanzongbiao")) {
                            try {
                                SysDictType sysDictType = sysDictTypeRepository.findById(systemId).orElse(null);
                                if (!childLoadMap.containsKey(sysDictType.getSystemName())) {
                                    childLoadMap.put(sysDictType.getSystemName(), 0);
                                }
                            } catch (Exception ex) {
                            }
                        }
                    }
                }
            }

            //List<Object[]> load_objects = device96Repository.findLoadSystemRatio(nodeId, fmt_ymd.format(dt));
            List<Object[]> load_objects = deviceRepository.findSystemTotalPowerGroupBySystemIdAndNodeItems(Arrays.asList(nodeId));
            if (load_objects != null && load_objects.size() > 0) {
                List<BuildFactoryModel> loadInfoModels = EntityUtils.castEntity(load_objects, BuildFactoryModel.class, new BuildFactoryModel());
                if (loadInfoModels != null && loadInfoModels.size() > 0) {

                    loadInfoModels.stream().forEach(alarmInfoModel -> {
                        SysDictType sysDictType = sysDictTypeRepository.findById(alarmInfoModel.getSystemId()).orElse(null);
                        if (sysDictType != null) {
                            childLoadMap.put(sysDictType.getSystemName(), alarmInfoModel.getLoad());
                        }
                    });
                }
            }

            map.put("loadRatio", JSONArray.toJSONString(childLoadMap));

            AtomicInteger alarmNumber = new AtomicInteger();
            List<VNModel> childAlarmRatioMap = new ArrayList<>();

            List<Object[]> objects = alarmRepository.getAlarmLogInfoByYMD(nodeId, fmt_ymd.format(dt));
            if (objects != null && objects.size() > 0) {
                List<AlarmInfoModel> alarmInfoModels = EntityUtils.castEntity(objects, AlarmInfoModel.class, new AlarmInfoModel());
                if (alarmInfoModels != null && alarmInfoModels.size() > 0) {

                    alarmInfoModels.stream().forEach(alarmInfoModel -> {

                        VNModel vnModel = new VNModel();
                        vnModel.setName(alarmInfoModel.getSystem_name());
                        vnModel.setValue(alarmInfoModel.getCount().toString());
                        childAlarmRatioMap.add(vnModel);

                        alarmNumber.addAndGet(alarmInfoModel.getCount().intValue());
                    });
                }
            }

            map.put("alarmRatio", JSONArray.toJSONString(childAlarmRatioMap));
            map.put("alarmNumber", alarmNumber);
            map.put("load", Double.parseDouble(String.format("%.4f", load)));
            map.put("totalLoad", Double.parseDouble(String.format("%.4f", totalLoad)));
            return ResponseResult.success(map);
        } catch (Exception e) {
            logger.error("loadListByNow 获取可调负荷失败!", e.getMessage());
            return ResponseResult.error("loadListByNow 获取可调负荷失败!" + e.getMessage());
        }

    }
}
