package com.example.vvpweb.flexibleresourcemanagement;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.EntityUtils;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.tunableload.ITunableLoadService;
import com.example.vvpservice.tunableload.model.RTLoadModel;
import com.example.vvpservice.tunableload.model.RTLoadMonthModel;
import com.example.vvpweb.flexibleresourcemanagement.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zph
 * @description 灵活性资源管理
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/flexible_resource_management/main")
@CrossOrigin
@Api(value = "灵活性资源管理", tags = {"灵活性资源管理"})
public class MainController {

    @Resource
    ITunableLoadService iTunableLoadService;
    @Resource
    NodeRepository nodeRepository;
    @Resource
    DeviceRepository deviceRepository;
    @Autowired
    private IUserService userService;
    @Resource
    private SysDictTypeRepository sysDictTypeRepository;
    @Resource
    private SysDictNodeRepository sysDictNodeRepository;
    @Resource
    private SysParamRepository sysParamRepository;
    @Resource
    private IotTsKvLastRepository iotTsKvLastRepository;

    @Resource
    private DeviceInfoViewRepository deviceInfoViewRepository;

    @ApiOperation("登录用户下,新版节点类型 数量统计")
    @UserLoginToken
    @RequestMapping(value = "getNodeTypeNumberCount", method = {RequestMethod.POST})
    public ResponseResult<NodeTypeNumberResponse> getNodeTypeNumberCountByIndex(@Param("index") int index) {
        try {
            if (index < 0 || index > 3) {

                return ResponseResult.success(null);
            }
            String nodeTypeId = "";
            String nodeTypeName = "-";
            int number = 0;

            SysParam sysParam = sysParamRepository.findSysParamBySysParamKey(SysParamEnum.ResourceOverviewNodeTypeOrder.getId());
            if (sysParam != null && StringUtils.isNotEmpty(sysParam.getSysParamValue())) {

                List<String> nodeTypeIds = JSON.parseArray(sysParam.getSysParamValue(), String.class);
                if (nodeTypeIds != null && nodeTypeIds.size() > 0 && nodeTypeIds.size() > index) {
                    try {
                        nodeTypeId = nodeTypeIds.get(index);
                    } catch (Exception ex) {
                    }
                }
            }

            if (StringUtils.isEmpty(nodeTypeId)) {
                return ResponseResult.success(null);
            }
            SysDictNode sysDictNode = sysDictNodeRepository.findById(nodeTypeId).orElse(null);
            if (sysDictNode != null) {
                nodeTypeName = sysDictNode.getNodeTypeName();
            }
            List<Node> nodes = nodeRepository.findAllByNodeIdInAndNodeType_NodeTypeId(userService.getAllowNodeIds(), nodeTypeId);
            if (nodes != null && nodes.size() > 0) {

                number = nodes.size();
            }
            NodeTypeNumberResponse response = new NodeTypeNumberResponse();
            response.setOrder(index);
            response.setName(nodeTypeName);
            response.setNumber(number);
            return ResponseResult.success(response);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }

    @ApiOperation("登录用户下,楼宇/工厂/基站/IDC 数量统计 v2.0版本适用 新版本 已弃用")
    @UserLoginToken
    @RequestMapping(value = "getNodeTypeNumberCount2", method = {RequestMethod.POST})
    public ResponseResult<LoadMonthModelResponse2> getNodeTypeNumberCount2() {
        try {
            List<String> idList = userService.getAllowNodeIds();
            if (idList != null && idList.size() > 0) {

                List<Object[]> objects = nodeRepository.findSumNodeType(idList);
                if (objects != null && objects.size() > 0) {

                    List<LoadMonthModelResponse2> models = EntityUtils.castEntity(objects, LoadMonthModelResponse2.class, new LoadMonthModelResponse2());
                    if (models != null && models.size() > 0) {
                        return ResponseResult.success(models.get(0));
                    }
                }
            }
            return ResponseResult.success(null);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }

    @ApiOperation("实时可调符合 近1天")
    @UserLoginToken
    @RequestMapping(value = "getNearlyADayList", method = {RequestMethod.POST})
    public ResponseResult<List<LoadModelResponse>> getNearlyADayList() {
        SimpleDateFormat fmt_ymd_hds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 国内时区是GMT+8
        fmt_ymd_hds.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        List<LoadModelResponse> loadModels = new ArrayList<>();
        try {
            List<String> ids = userService.getAllowLoadNodeIds();

            if (ids != null && ids.size() > 0) {
                List<RTLoadModel> models = iTunableLoadService.getNearlyADayList(ids);
                if (models != null && models.size() > 0) {

                    for (RTLoadModel p : models) {
                        try {
                            LoadModelResponse loadModel = new LoadModelResponse();
                            loadModel.setTs(fmt_ymd_hds.parse(p.getTs()));
                            loadModel.setValue(p.getValue());
                            loadModels.add(loadModel);
                        } catch (Exception e) {
                        }
                    }
                }
            }

            return ResponseResult.success(loadModels);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }

    @UserLoginToken
    @ApiOperation("实时可调符合 近7天")
    @RequestMapping(value = "getNearlySevenDaysList", method = {RequestMethod.POST})
    public ResponseResult<List<LoadModelResponse>> getNearlySevenDaysList() {
        SimpleDateFormat fmt_ymd_hds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 国内时区是GMT+8
        fmt_ymd_hds.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        List<LoadModelResponse> loadModels = new ArrayList<>();
        try {
            List<String> ids = userService.getAllowLoadNodeIds();

            if (ids != null && ids.size() > 0) {
                List<RTLoadModel> models = iTunableLoadService.getNearlySevenDaysList(ids);
                if (models != null && models.size() > 0) {

                    for (RTLoadModel p : models) {
                        try {
                            LoadModelResponse loadModel = new LoadModelResponse();
                            loadModel.setTs(fmt_ymd_hds.parse(p.getTs()));
                            loadModel.setValue(p.getValue());
                            loadModels.add(loadModel);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            return ResponseResult.success(loadModels);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }

    }

    @UserLoginToken
    @ApiOperation("实时可调符合 近一个月")
    @RequestMapping(value = "getNearlyAMonthList", method = {RequestMethod.POST})
    public ResponseResult<List<LoadMonthModelResponse>> getNearlyAMonthList() {

        SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
        // 国内时区是GMT+8
        fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        List<LoadMonthModelResponse> loadModels = new ArrayList<>();

        try {
            List<String> ids = userService.getAllowLoadNodeIds();

            if (ids != null && ids.size() > 0) {
                List<RTLoadMonthModel> models = iTunableLoadService.getNearlyAMonthList(ids);
                if (models != null && models.size() > 0) {

                    for (RTLoadMonthModel p : models) {
                        try {
                            LoadMonthModelResponse loadMonthModel = new LoadMonthModelResponse();
                            loadMonthModel.setTs(fmt_ymd.parse(p.getTs()));
                            loadMonthModel.setValue(p.getValue());

                            loadModels.add(loadMonthModel);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            return ResponseResult.success(loadModels);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }

    @UserLoginToken
    @ApiOperation("实时可调符合 开始 年-月 到  年-月 检索")
    @RequestMapping(value = "getAutoMonthList", method = {RequestMethod.POST})
    public ResponseResult<List<LoadMonthModelResponse>> getAutoMonthList(@RequestBody AutoMonthLoadModel autoMonthLoadModel) {

        if (autoMonthLoadModel == null || autoMonthLoadModel.getTs_e() == null || autoMonthLoadModel.getTs_s() == null) {
            return ResponseResult.error("参数为空，请重新输入!");
        }
        SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
        // 国内时区是GMT+8
        fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        if (autoMonthLoadModel.getTs_s().after(autoMonthLoadModel.getTs_e())) {

            return ResponseResult.error("开始时间不能大于结束时间!");
        }

        List<LoadMonthModelResponse> loadModels = new ArrayList<>();
        try {
            List<String> ids = new ArrayList<>();

            List<Node> list = nodeRepository.findAllByNodeIdIn(userService.getAllowNodeIds());
            if (list != null && list.size() > 0) {
                list.stream().forEach(p -> {
                    ids.add(p.getNodeId());
                });
            }
            if (ids != null && ids.size() > 0) {
                Date ts_s = fmt_ymd.parse(fmt_ymd.format(autoMonthLoadModel.getTs_s()));
                Date ts_e = fmt_ymd.parse(fmt_ymd.format(autoMonthLoadModel.getTs_e()));
                if (ts_s.getTime() > ts_e.getTime()) {

                    return ResponseResult.error("开始时间要小于结束时间，请修改！");
                }
                List<RTLoadMonthModel> models = iTunableLoadService.getAutoMonthList(ts_s, ts_e, ids);
                if (models != null && models.size() > 0) {

                    for (RTLoadMonthModel p : models) {
                        try {
                            LoadMonthModelResponse loadMonthModel = new LoadMonthModelResponse();
                            loadMonthModel.setTs(fmt_ymd.parse(p.getTs()));
                            loadMonthModel.setValue(p.getValue());

                            loadModels.add(loadMonthModel);
                        } catch (Exception e) {
                        }
                    }
                }
            }

            return ResponseResult.success(loadModels);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }

    @UserLoginToken
    @ApiOperation("可调负荷")
    @RequestMapping(value = "getLoadCount", method = {RequestMethod.POST})
    public ResponseResult<LoadResponse> getLoadCount() {

        try {
            double loadNumber = (double) 0;
            double totalLoad = (double) 0;
            double jieRuLoad = (double) 0;
            //实时可调负荷 除能源总表为 设备负荷之和
            double load = (double) 0;

            List<String> nodeIds = userService.getAllowLoadNodeIds();
            if (nodeIds != null && nodeIds.size() > 0) {

                loadNumber = nodeIds.size();

                try {
                    totalLoad = deviceRepository.findAllLoadByNodeIds(nodeIds);
                } catch (Exception ex) {
                }
                try {
                    List<Object[]> loadList = iotTsKvLastRepository.findJieRuLoadByNodeIdsNew(nodeIds);
                    for (Object[] objects : loadList) {
                        jieRuLoad += Double.parseDouble((String) objects[1]);
                    }
                } catch (Exception ex) {
                }
                try {
                    load = iTunableLoadService.findNowLoad(nodeIds);
                } catch (Exception ex) {
                }
            }

            LoadResponse response = new LoadResponse();
            response.setLoad(load);
            response.setTotalLoad(totalLoad);
            response.setJieRuLoad(jieRuLoad);
            response.setLoadNumber(loadNumber);
            return ResponseResult.success(response);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }



    @UserLoginToken
    @ApiOperation("可调负荷-负荷性质分类")
    @RequestMapping(value = "getLoadNatureClassification", method = {RequestMethod.POST})
    public ResponseResult<List<LoadNatureResponse>> getLoadNatureClassification() {
        try {
            List<LoadNatureResponse> list = new ArrayList<>();
            double adjustableLoad = (double) 0;
            double transferableLoad = (double) 0;
            double interruptibleLoad = (double) 0;
            double otherLoad = (double) 0;
            double totalLoad = (double) 0;

            List<String> nodeIds = userService.getAllowLoadNodeIds();
            if (nodeIds != null && nodeIds.size() > 0) {

//                try {
//                    //总可调负荷 除能源总表外 设备额定功率之和
//                    totalLoad = deviceRepository.findAllLoadByNodeIds(nodeIds);
//                } catch (Exception ex) {
//                }
//                LoadNatureResponse total = new LoadNatureResponse();
//                total.setName("总可调负荷");
//                total.setValue(totalLoad);
//                list.add(total);
                try {
                    //可中断负荷
                    interruptibleLoad = deviceRepository.findAllLoadByNodeIdsAndLoadProper(nodeIds, "interruptible_load");
                } catch (Exception ex) {
                }
                LoadNatureResponse inter = new LoadNatureResponse();
                inter.setName("可中断负荷");
                inter.setValue(Double.parseDouble(String.format("%.2f", interruptibleLoad)));
                list.add(inter);

                try {
                    adjustableLoad = deviceRepository.findAllLoadByNodeIdsAndLoadProper(nodeIds, "adjustable_load");
                } catch (Exception ex) {
                }
                LoadNatureResponse adjust = new LoadNatureResponse();
                adjust.setName("可调节负荷");
                adjust.setValue(Double.parseDouble(String.format("%.2f", adjustableLoad)));
                list.add(adjust);

                try {
                    //总可调负荷 除能源总表外 设备额定功率之和
                    transferableLoad = deviceRepository.findAllLoadByNodeIdsAndLoadProper(nodeIds, "transferable_load");
                } catch (Exception ex) {
                }
                LoadNatureResponse transfer = new LoadNatureResponse();
                transfer.setName("可转移负荷");
                transfer.setValue(Double.parseDouble(String.format("%.2f", transferableLoad)));
                list.add(transfer);

                try {
                    //总可调负荷 除能源总表外 设备额定功率之和
                    otherLoad = deviceRepository.findAllLoadByNodeIdsAndLoadProper(nodeIds, "other_loads");
                } catch (Exception ex) {
                }
                LoadNatureResponse other = new LoadNatureResponse();
                other.setName("其他负荷");
                other.setValue(Double.parseDouble(String.format("%.2f", otherLoad)));
                list.add(other);
            }
            return ResponseResult.success(list);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }

    /**
     * 负荷类型：-       ，
     * 空调（air_conditioning），
     * 充电桩（charging_piles），
     * 照明（lighting），
     * 其它（others）
     */
    @UserLoginToken
    @ApiOperation("可调负荷-负荷类型分类")
    @RequestMapping(value = "getLoadTypeClassification", method = {RequestMethod.POST})
    public ResponseResult<List<LoadTypeResponse>> getLoadTypeClassification() {
        try {
            List<LoadTypeResponse> list = new ArrayList<>();
            double air_conditioning_value=0d;
            double lighting_value=0d;
            double charging_piles_value=0d;
            double others_value=0d;
            //实时可调负荷 除能源总表外 设备负荷之和
            double load_value = 0d;

            List<String> nodeIds = userService.getAllowLoadNodeIds();
            List<Device> allByNode_nodeIdIn = deviceRepository.findAllByNode_NodeIdIn(nodeIds);
            List<String> deviceSns = allByNode_nodeIdIn.stream().map(Device::getDeviceSn).collect(Collectors.toList());


            LoadTypeResponse air_conditioning = new LoadTypeResponse();
            air_conditioning.setName("空调");

            try {
                //总可调负荷 除能源总表外 设备额定功率之和
                air_conditioning_value =  deviceInfoViewRepository.findDeviceLoadByDeviceSnAndLoadType(deviceSns, "air_conditioning");
            } catch (Exception ex) {
            }
            air_conditioning.setValue(Double.parseDouble(String.format("%.2f", air_conditioning_value)));
            list.add(air_conditioning);

            LoadTypeResponse lighting = new LoadTypeResponse();
            lighting.setName("照明");
            try {
                //总可调负荷 除能源总表外 设备额定功率之和
                lighting_value =   deviceInfoViewRepository.findDeviceLoadByDeviceSnAndLoadType(deviceSns, "lighting");
            } catch (Exception ex) {
            }
            lighting.setValue(Double.parseDouble(String.format("%.2f", lighting_value)));
            list.add(lighting);

            LoadTypeResponse charging_piles = new LoadTypeResponse();
            charging_piles.setName("充电桩");
            try {
                //总可调负荷 除能源总表外 设备额定功率之和
                charging_piles_value =   deviceInfoViewRepository.findDeviceLoadByDeviceSnAndLoadType(deviceSns, "charging_piles");
            } catch (Exception ex) {
            }
            charging_piles.setValue(Double.parseDouble(String.format("%.2f", charging_piles_value)));
            list.add(charging_piles);


            LoadTypeResponse others = new LoadTypeResponse();
            others.setName("其他负荷");
            try {
                //总可调负荷 除能源总表外 设备额定功率之和
                others_value =   deviceInfoViewRepository.findDeviceLoadByDeviceSnAndLoadType(deviceSns, "others");
            } catch (Exception ex) {
            }
            others.setValue(Double.parseDouble(String.format("%.2f", others_value)));
            list.add(others);

//
//            try {
//                //实时可调负荷 除能源总表外 设备负荷之和
//                load_value = iTunableLoadService.findNowLoad(nodeIds);
//            } catch (Exception ex) {
//            }
//
//            LoadTypeResponse load = new LoadTypeResponse();
//            load.setName("实时可调负荷");
//            load.setValue(load_value);
//            list.add(load);

            return ResponseResult.success(list);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }

    @UserLoginToken
    @ApiOperation("商业综合体  v2.0版本适用 新版本 已弃用")
    @RequestMapping(value = "getBuildCount", method = {RequestMethod.POST})
    public ResponseResult<Map<String, Object>> getBuildCount() {

        try {
            int louYuNumber = 0;
            Double totalLoad = (double) 0;
            Double jieRuLoad = (double) 0;
            Double load = (double) 0;
            Map<String, Object> childLoadMap = new HashMap();
            Map<String, Object> map = new HashMap();

            List<String> nodeIds = new ArrayList<>();

            List<Node> nodes = nodeRepository.findAllByNodeIdInAndNodeType_NodeTypeId(userService.getAllowNodeIds(), "louyu");
            if (nodes != null && nodes.size() > 0) {

                louYuNumber = nodes.size();

                nodes.stream().forEach(c -> {
                    nodeIds.add(c.getNodeId());
                });

                nodes.stream().forEach(c -> {
                    List<String> systemIdList = JSONObject.parseArray(c.getSystemIds(), String.class);
                    if (systemIdList != null && systemIdList.size() > 0) {
                        for (String systemId : systemIdList) {
                            try {
                                if ("nengyuanzongbiao".equals(systemId) == false) {
                                    SysDictType sysDictType = sysDictTypeRepository.findById(systemId).orElse(null);
                                    if (!childLoadMap.containsKey(sysDictType.getSystemName())) {
                                        childLoadMap.put(sysDictType.getSystemName(), 0);
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }
                    }
                });
            }

            if (nodeIds != null && nodeIds.size() > 0) {


                try {
                    totalLoad = deviceRepository.findAllLoadByNodeIds(nodeIds);
                } catch (Exception ex) {
                }


                try {
                    jieRuLoad = iotTsKvLastRepository.findJieRuLoadByNodeIds(nodeIds);
                } catch (Exception ex) {
                }


                //实时可调负荷 除能源总表为 设备负荷之和
                try {
                    load = iTunableLoadService.findNowLoad(nodeIds);
                } catch (Exception ex) {
                }


                List<Object[]> objects = deviceRepository.findSystemTotalPowerGroupBySystemIdAndNodeItems(nodeIds);
                if (objects != null && objects.size() > 0) {
                    List<BuildFactoryModel> models = EntityUtils.castEntity(objects, BuildFactoryModel.class, new BuildFactoryModel());
                    if (models != null && models.size() > 0) {
                        models.stream().forEach(model -> {
                            SysDictType sysDictType = sysDictTypeRepository.findById(model.getSystemId()).orElse(null);
                            if (sysDictType != null) {
                                childLoadMap.put(sysDictType.getSystemName(), model.getLoad());
                            }
                        });
                    }
                }
            }

            map.put("loadRatio", JSONArray.toJSONString(childLoadMap));
            map.put("louYuNumber", louYuNumber);
            map.put("totalLoad", totalLoad);
            map.put("load", load);
            map.put("jieRuLoad", jieRuLoad);

            return ResponseResult.success(map);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }

    @UserLoginToken
    @ApiOperation("工厂园区  v2.0版本适用 新版本 已弃用")
    @RequestMapping(value = "getFactoryCount", method = {RequestMethod.POST})
    public ResponseResult<Map<String, Object>> getFactoryCount() {
        try {
            //总功率
            Double totalLoad = (double) 0;
            Double load = (double) 0;
            Double jieRuLoad = (double) 0;
            int gongChangNumber = 0;
            Map<String, Object> childLoadMap = new HashMap();
            Map<String, Object> map = new HashMap();

            List<String> nodeIds = new ArrayList<>();

            List<Node> nodes = nodeRepository.findAllByNodeIdInAndNodeType_NodeTypeId(userService.getAllowNodeIds(), "gongchang");
            if (nodes != null && nodes.size() > 0) {

                gongChangNumber = nodes.size();

                nodes.stream().forEach(c -> {
                    nodeIds.add(c.getNodeId());
                });
                nodes.stream().forEach(c -> {
                    List<String> systemIdList = JSONObject.parseArray(c.getSystemIds(), String.class);
                    if (systemIdList != null && systemIdList.size() > 0) {
                        if (systemIdList != null && systemIdList.size() > 0) {
                            for (String systemId : systemIdList) {
                                try {
                                    if ("nengyuanzongbiao".equals(systemId) == false) {
                                        SysDictType sysDictType = sysDictTypeRepository.findById(systemId).orElse(null);
                                        if (!childLoadMap.containsKey(sysDictType.getSystemName())) {
                                            childLoadMap.put(sysDictType.getSystemName(), 0);
                                        }
                                    }
                                } catch (Exception ex) {
                                }
                            }
                        }
                    }
                });
            }

            if (nodeIds != null && nodeIds.size() > 0) {

                //可调负荷：负荷节点所有系统中设备的额定功率之和
                try {
                    totalLoad = deviceRepository.findAllLoadByNodeIds(nodeIds);
                } catch (Exception ex) {
                }

                //接入负荷：负荷节点的能源总表对应的负荷
                try {
                    jieRuLoad = iotTsKvLastRepository.findJieRuLoadByNodeIds(nodeIds);
                } catch (Exception ex) {
                }

                //实时可调负荷 除能源总表为 设备负荷之和
                try {
                    load = iTunableLoadService.findNowLoad(nodeIds);
                } catch (Exception ex) {
                }

                List<Object[]> objects = deviceRepository.findSystemTotalPowerGroupBySystemIdAndNodeItems(nodeIds);
                if (objects != null && objects.size() > 0) {
                    List<BuildFactoryModel> models = EntityUtils.castEntity(objects, BuildFactoryModel.class, new BuildFactoryModel());
                    if (models != null && models.size() > 0) {
                        models.stream().forEach(model -> {
                            SysDictType sysDictType = sysDictTypeRepository.findById(model.getSystemId()).orElse(null);
                            if (sysDictType != null) {
                                childLoadMap.put(sysDictType.getSystemName(), model.getLoad());
                            }
                        });
                    }
                }
            }

            map.put("loadRatio", JSONArray.toJSONString(childLoadMap));
            map.put("gongChangNumber", gongChangNumber);
            map.put("totalLoad", totalLoad);
            map.put("load", load);
            map.put("jieRuLoad", jieRuLoad);
            return ResponseResult.success(map);

        } catch (Exception e) {
            return ResponseResult.success(null);
        }

    }


    @UserLoginToken
    @ApiOperation("资源概览节点信息")
    @RequestMapping(value = "getCountChartByIndex", method = {RequestMethod.POST})
    public ResponseResult<Map<String, Object>> getCountChartByIndex(@Param("index") int index) {

        try {
            if (index < 0 || index > 3) {

                return ResponseResult.success(null);
            }
            String nodeTypeId = "";
            String nodeTypeName = "-";
            int number = 0;
            Double totalLoad = (double) 0;
            Double jieRuLoad = (double) 0;
            Double load = (double) 0;

            SysParam sysParam = sysParamRepository.findSysParamBySysParamKey(SysParamEnum.ResourceOverviewNodeTypeOrder.getId());
            if (sysParam != null && StringUtils.isNotEmpty(sysParam.getSysParamValue())) {

                List<String> nodeTypeIds = JSON.parseArray(sysParam.getSysParamValue(), String.class);
                if (nodeTypeIds != null && nodeTypeIds.size() > 0 && nodeTypeIds.size() > index) {
                    try {
                        nodeTypeId = nodeTypeIds.get(index);
                    } catch (Exception ex) {
                    }
                }
            }

            if (StringUtils.isEmpty(nodeTypeId)) {
                return ResponseResult.success(null);
            }
            SysDictNode sysDictNode = sysDictNodeRepository.findById(nodeTypeId).orElse(null);
            if (sysDictNode != null) {
                nodeTypeName = sysDictNode.getNodeTypeName();
            }
            Map<String, Object> childLoadMap = new HashMap();
            List<String> nodeIds = new ArrayList<>();
            List<Node> nodes = nodeRepository.findAllByNodeIdInAndNodeType_NodeTypeId(userService.getAllowNodeIds(), nodeTypeId);
            if (nodes != null && nodes.size() > 0) {

                number = nodes.size();
                nodes.stream().forEach(c -> {
                    nodeIds.add(c.getNodeId());
                });

                nodes.stream().forEach(c -> {
                    List<String> systemIdList = JSONObject.parseArray(c.getSystemIds(), String.class);
                    if (systemIdList != null && systemIdList.size() > 0) {
                        for (String systemId : systemIdList) {
                            try {
                                if ("nengyuanzongbiao".equals(systemId) == false) {
                                    SysDictType sysDictType = sysDictTypeRepository.findById(systemId).orElse(null);
                                    if (!childLoadMap.containsKey(sysDictType.getSystemName())) {
                                        childLoadMap.put(sysDictType.getSystemName(), 0);
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }
                    }
                });
            }


            Map<String, Object> map = new HashMap();

            if (nodeIds != null && nodeIds.size() > 0) {


                try {
                    totalLoad = deviceRepository.findAllLoadByNodeIds(nodeIds);
                } catch (Exception ex) {
                }


                try {
                    jieRuLoad = iotTsKvLastRepository.findJieRuLoadByNodeIds(nodeIds);
                } catch (Exception ex) {
                }


                //实时可调负荷 除能源总表为 设备负荷之和
                try {
                    load = iTunableLoadService.findNowLoad(nodeIds);
                } catch (Exception ex) {
                }


                List<Object[]> objects = deviceRepository.findSystemTotalPowerGroupBySystemIdAndNodeItems(nodeIds);
                if (objects != null && objects.size() > 0) {
                    List<BuildFactoryModel> models = EntityUtils.castEntity(objects, BuildFactoryModel.class, new BuildFactoryModel());
                    if (models != null && models.size() > 0) {
                        models.stream().forEach(model -> {
                            SysDictType sysDictType = sysDictTypeRepository.findById(model.getSystemId()).orElse(null);
                            if (sysDictType != null) {
                                childLoadMap.put(sysDictType.getSystemName(), Double.parseDouble(String.format("%.2f", model.getLoad())));
                            }
                        });
                    }
                }
            }

            map.put("loadRatio", JSONArray.toJSONString(childLoadMap));
            map.put("number", number);
            map.put("totalLoad", Double.parseDouble(String.format("%.2f", totalLoad)));
            map.put("load", Double.parseDouble(String.format("%.2f", load)));
            map.put("jieRuLoad", Double.parseDouble(String.format("%.2f", jieRuLoad)));
            map.put("name", nodeTypeName);

            return ResponseResult.success(map);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }


    @UserLoginToken
    @ApiOperation("场景分类-第1步 统计、显示所有接入节点大于0的nodeTypeId列表")
    @RequestMapping(value = "getNodeTypeIdItems", method = {RequestMethod.POST})
    public ResponseResult<List<String>> getNodeTypeIdItems() {
        try {
            List<String> nodeTypeIds = new ArrayList<>();
            List<SysDictNode> list = sysDictNodeRepository.findAllLoadNodeMoreThanZero();
            if (list != null && list.size() > 0) {
                for (SysDictNode dictNode : list) {
                    if (dictNode.getConfigType().equals("Y")) {
                        nodeTypeIds.add(dictNode.getNodeTypeId());
                    }
                }
            }
            return ResponseResult.success(nodeTypeIds);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }

    @UserLoginToken
    @ApiOperation("场景分类-第2步根据节点类型分类")
    @RequestMapping(value = "getSceneClassification", method = {RequestMethod.GET})
    public ResponseResult<Map<String, Object>> sceneClassification(@RequestParam("nodeTypeId") String nodeTypeId) {
        try {

            String nodeTypeName = "-";
            int number = 0;
            Double totalLoad = (double) 0;
            Double jieRuLoad = (double) 0;
            Double load = (double) 0;


            if (StringUtils.isEmpty(nodeTypeId)) {
                return ResponseResult.success(null);
            }
            SysDictNode sysDictNode = sysDictNodeRepository.findById(nodeTypeId).orElse(null);
            if (sysDictNode != null) {
                nodeTypeName = sysDictNode.getNodeTypeName();
            }
            Map<String, Object> childLoadMap = new HashMap();
            List<String> nodeIds = new ArrayList<>();
            List<Node> nodes = nodeRepository.findAllByNodeIdInAndNodeType_NodeTypeId(userService.getAllowNodeIds(), nodeTypeId);
            if (nodes != null && nodes.size() > 0) {

                number = nodes.size();
                nodes.stream().forEach(c -> {
                    nodeIds.add(c.getNodeId());
                });

                nodes.stream().forEach(c -> {
                    List<String> systemIdList = JSONObject.parseArray(c.getSystemIds(), String.class);
                    if (systemIdList != null && systemIdList.size() > 0) {
                        for (String systemId : systemIdList) {
                            try {
                                if ("nengyuanzongbiao".equals(systemId) == false) {
                                    SysDictType sysDictType = sysDictTypeRepository.findById(systemId).orElse(null);
                                    if (!childLoadMap.containsKey(sysDictType.getSystemName())) {
                                        childLoadMap.put(sysDictType.getSystemName(), 0);
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }
                    }
                });
            }


            Map<String, Object> map = new HashMap();

            if (nodeIds != null && nodeIds.size() > 0) {


                try {
                    totalLoad = deviceRepository.findAllLoadByNodeIds(nodeIds);
                } catch (Exception ex) {
                }


                try {
                    List<Object[]> loadList = iotTsKvLastRepository.findJieRuLoadByNodeIdsNew(nodeIds);
                    for (Object[] objects : loadList) {
                        jieRuLoad += Double.parseDouble((String) objects[1]);
                    }
                } catch (Exception ex) {
                }


                //实时可调负荷 除能源总表为 设备负荷之和
                try {
                    load = iTunableLoadService.findNowLoad(nodeIds);
                } catch (Exception ex) {
                }


                List<Object[]> objects = deviceRepository.findSystemTotalPowerGroupBySystemIdAndNodeItems(nodeIds);
                if (objects != null && objects.size() > 0) {
                    List<BuildFactoryModel> models = EntityUtils.castEntity(objects, BuildFactoryModel.class, new BuildFactoryModel());
                    if (models != null && models.size() > 0) {
                        models.stream().forEach(model -> {
                            SysDictType sysDictType = sysDictTypeRepository.findById(model.getSystemId()).orElse(null);
                            if (sysDictType != null) {
                                childLoadMap.put(sysDictType.getSystemName(), Double.parseDouble(String.format("%.2f", model.getLoad())));
                            }
                        });
                    }
                }
            }

            map.put("loadRatio", JSONArray.toJSONString(childLoadMap));
            map.put("number", number);
            map.put("totalLoad", Double.parseDouble(String.format("%.2f", totalLoad)));
            map.put("load", Double.parseDouble(String.format("%.2f", load)));
            map.put("jieRuLoad", Double.parseDouble(String.format("%.2f", jieRuLoad)));
            map.put("name", nodeTypeName);

            return ResponseResult.success(map);
        } catch (Exception e) {
            return ResponseResult.success(null);
        }
    }

}
