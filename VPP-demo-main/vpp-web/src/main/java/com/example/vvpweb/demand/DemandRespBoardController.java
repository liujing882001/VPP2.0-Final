package com.example.vvpweb.demand;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.*;
import com.example.vvpcommom.Enum.DemandProfitType;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpscheduling.DemandProfitDataJob;
import com.example.vvpscheduling.controller.BaseExcelController;
import com.example.vvpscheduling.model.DemandProfitRevertModel;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.tunableload.ITunableLoadService;
import com.example.vvpservice.tunableload.model.RTLoadModel;
import com.example.vvpweb.demand.model.*;
import com.example.vvpweb.flexibleresourcemanagement.model.KWNodeModel;
import com.example.vvpweb.loadmanagement.model.AiLoadModelResponse;
import com.example.vvpweb.loadmanagement.model.AiModel;
import com.example.vvpweb.smartscreen.model.ElectricityLoadModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jsoup.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author maoyating
 * @description 需求响应-响应看板
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/demand_resp/resp_board")
@CrossOrigin
@Api(value = "需求响应-响应看板", tags = {"需求响应-响应看板"})
public class DemandRespBoardController extends BaseExcelController {


    private static Logger logger = LoggerFactory.getLogger(DemandRespBoardController.class);
    @Resource
    ITunableLoadService iTunableLoadService;
    @Autowired
    private DemandRespTaskRepository demandRespTaskRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private IotTsKvMeteringDevice96Repository device96Repository;
    @Autowired
    private DemandRespStrategyNoRepository noRepository;
    @Autowired
    private IUserService userService;
    @Resource
    private IotTsKvLastRepository iotTsKvLastRepository;
    @Resource
    private NodeRepository nodeRepository;
    @Autowired
    private UserRepository userRepository;
    @Resource
    private DemandProfitRepository profitRepository;
    @Resource
    private IExcelOutPutService iExcelOutPutService;

    @ApiOperation("查询已完成需求响应任务")
    @UserLoginToken
    @RequestMapping(value = "/getTaskAndStrategyList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getTaskAndStrategyList(@RequestBody DemandModel model) {
        try {
            PageModel pageModel = new PageModel();
            //得到该用户的节点列表
            List<String> nodeIds = userService.getAllowLoadNodeIds();
            if (nodeIds != null && nodeIds.size() > 0) {

                //当前页为第几页 默认 1开始
                int pageSize = model.getPageSize();
                int pageNum = (model.getNumber() - 1) * pageSize;//pg 从0开始

                List<Object[]> datas = demandRespTaskRepository.findByRsDateAndNodeIdsPage(nodeIds,
                        model.getStartDate(), model.getEndDate(), 25, 3, pageSize, pageNum);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
                sdfTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                if (datas != null && datas.size() > 0) {
                    List<DemandBoardCancelModel> newList = new ArrayList<>();
                    datas.forEach(d -> {
                        DemandBoardCancelModel demand = new DemandBoardCancelModel();
                        demand.setTaskCode(Long.valueOf(d[1].toString()));
                        try {
                            String rsTime = sdfTime.format(d[2]);
                            Date rs = sdfTime.parse(rsTime);
                            demand.setRsTime(rs);
                            String reTime = sdfTime.format(d[3]);
                            Date re = sdfTime.parse(reTime);
                            demand.setReTime(re);
                            String rsDateStr = sdf.format(d[4]);
                            Date rsDate = sdf.parse(rsDateStr);
                            demand.setRsDate(rsDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (d[5] != null) {
                            demand.setRespLoad(Double.valueOf(d[5].toString()));
                        }
                        if (d[6] != null) {
                            demand.setRespSubsidy(Double.valueOf(d[6].toString()));
                        }
                        demand.setRespType(Integer.valueOf(d[7].toString()));
                        demand.setRespLevel(Integer.valueOf(d[8].toString()));
                        if (d[8] != null) {
                            demand.setDeclareLoad(Double.valueOf(d[9].toString()));
                        }
                        if (d[11] != null) {
                            demand.setProfit(Double.valueOf(d[11].toString()));
                        } else {
                            if (d[10] != null) {
                                demand.setProfit(Double.valueOf(d[10].toString()));
                            }
                        }
                        newList.add(demand);

                    });
                    pageModel.setContent(newList);
                } else {
                    pageModel.setContent(datas);
                }
                //总数
                Integer count = demandRespTaskRepository.countByRsDateAndNodeIds(nodeIds,
                        model.getStartDate(), model.getEndDate(), 25, 3);
                //封装到pageUtil
                pageModel.setTotalElements(count);
                return ResponseResult.success(pageModel);
            }
            pageModel.setTotalElements(0);
            return ResponseResult.success(pageModel);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("查询有误！");
        }
    }

    @ApiOperation("查询可调负荷信息")
    @UserLoginToken
    @RequestMapping(value = "/getAdjustLoad", method = {RequestMethod.POST})
    public ResponseResult<DemandBoardModel> getAdjustLoad() {
        DemandBoardModel boardModel = new DemandBoardModel();
        try {
            List<String> nodeIds = userService.getAllowLoadNodeIds();
            if (nodeIds != null && nodeIds.size() > 0) {
                //总可调负荷
                Double totalLoad = (double) 0;
                List<Object[]> kw_objects = deviceRepository.findAllKWByNodeIds(nodeIds);
                if (kw_objects != null && kw_objects.size() > 0) {

                    List<KWNodeModel> kwNodeModels = EntityUtils.castEntity(kw_objects, KWNodeModel.class, new KWNodeModel());
                    if (kwNodeModels != null && kwNodeModels.size() > 0) {
                        totalLoad = kwNodeModels.stream().mapToDouble(c -> c.getDevice_rated_power()).sum();
                    }
                }

                //实时可调负荷 除能源总表为 设备负荷之和
                Double load = (double) 0;
                List<RTLoadModel> loadLast = iTunableLoadService.getNearlyADayList(nodeIds);
                if (loadLast != null && loadLast.size() > 0) {
                    load = loadLast.get(loadLast.size() - 1).getValue();
                }

                //查询非计量设备的设备列表 //统计非计量设备（other）的设备数
                Specification<Device> spec = new Specification<Device>() {
                    @Override
                    public Predicate toPredicate(Root<Device> root,
                                                 CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(cb.in(root.get("node").get("nodeId")).value(nodeIds));
                        predicates.add(cb.notEqual(root.get("systemType").get("systemId"), "nengyuanzongbiao"));//查询非"能源系统"
                        predicates.add(cb.equal(root.get("configKey"),"other"));//查询非计量设备
                        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                };
                List<Device> deviceList = deviceRepository.findAll(spec);

                int onlineDeviceNum = 0;//在线设备数
                if (deviceList != null && deviceList.size() > 0) {
                    Map<String, String> nodeMap = new HashMap<>();
                    for (Device device : deviceList) {
                        nodeMap.put(device.getNode().getNodeId(), "");
                        if (device.getOnline()) {
                            onlineDeviceNum += 1;
                        }
                    }
                }

                //均在用户可访问的节点条件下！！！
                //节点总数：统计所有可调负荷节点的数量
                boardModel.setUserNum(nodeIds.size());//用户总数
                //参与节点：统计申报过需求响应的节点数量（唯一值，统一用户参与多次，只算一次）
                boardModel.setPartakeUser(demandRespTaskRepository.countUserNum(nodeIds));//响应用户数量
                // 参与率： 参与用户/用户总数
                boardModel.setPartakeRate((float) (boardModel.getPartakeUser() * 100) / boardModel.getUserNum());
                //可调设备总数： 可调负荷节点（非储能、光伏节点）系统下，对应的设备数量
                boardModel.setDeviceNum(deviceList.size());//设备数量
                //在线设备数：可调负荷节点（非储能、光伏节点）系统下，所有在线的设备数量
                boardModel.setOnlineDeviceNum(onlineDeviceNum);//在线设备数
                //总可调负荷：同“资源概览”中的“总可调负荷”
                boardModel.setAdjustLoad(totalLoad);
                //实时可调负荷：同“资源概览”中的“实时可调负荷”
                boardModel.setMonitorLoad(load);

            } else {
                //均在用户可访问的节点条件下！！！
                //节点总数：统计所有可调负荷节点的数量
                boardModel.setUserNum(0);//用户总数
                //参与节点：统计申报过需求响应的节点数量（唯一值，统一用户参与多次，只算一次）
                boardModel.setPartakeUser(0);//响应用户数量
                // 参与率： 参与用户/用户总数
                boardModel.setPartakeRate(0.0f);
                //可调设备总数： 可调负荷节点（非储能、光伏节点）系统下，对应的设备数量
                boardModel.setDeviceNum(0);//设备数量
                //在线设备数：可调负荷节点（非储能、光伏节点）系统下，所有在线的设备数量
                boardModel.setOnlineDeviceNum(0);//在线设备数
                //总可调负荷：同“资源概览”中的“总可调负荷”
                boardModel.setAdjustLoad(0.0);
                //实时可调负荷：同“资源概览”中的“实时可调负荷”
                boardModel.setMonitorLoad(0.0);
            }
            return ResponseResult.success(boardModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseResult.success(boardModel);
    }

    @ApiOperation("需求响应统计信息")
    @UserLoginToken
    @RequestMapping(value = "/getDemandStatistics", method = {RequestMethod.POST})
    public ResponseResult<DemandStatisticsModel> getDemandStatistics(@RequestBody DemandModel model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");
        DemandStatisticsModel sModel = new DemandStatisticsModel();
        try {
            //得到该用户的节点列表
            List<String> nodeIds = userService.getAllowLoadNodeIds();
            if (nodeIds != null && nodeIds.size() > 0) {
                List<Object[]> list = noRepository.findBySumRsDateAndNodeIds(nodeIds,
                        model.getStartDate(), model.getEndDate(), 25, 3);
                //申报响应任务数量
                int count = demandRespTaskRepository.countByRsDateAndNodeIds(nodeIds,
                        model.getStartDate(), model.getEndDate(), 25, 3);

                if (list != null && list.size() > 0) {
                    //得到统计信息
                    List<DemandBoardRevertModel> datas = EntityUtils.castEntity(list, DemandBoardRevertModel.class, new DemandBoardRevertModel());

                    double totalCutPower = 0.00;//削峰电量
                    double totalFillPower = 0.00;//填谷电量
                    double totalDeclareLoad = 0.00;//总申报负荷

                    double totalCutProfit = 0.00;//削峰总收益
                    double totalFillProfit = 0.00;//填谷总收益
                    double totalVolumeProfit = 0.00;//容量收益

                    for (DemandBoardRevertModel d : datas) {
                        if (d.getResp_type() == 1) {
                            totalCutPower += d.getReal_time_load() != null ? d.getReal_time_load() : 0.00;
                            //统计削峰需求响应任务的总收益，总收益为电网返回的总收益，如果电网没有返回，这里为预估收益
                            if (d.getVolume_profit() != null) {
                                totalCutProfit += d.getVolume_profit();
                            } else if (d.getProfit() != null) {
                                totalCutProfit += d.getProfit();
                            }
                        } else if (d.getResp_type() == 2) {
                            totalFillPower += d.getReal_time_load() != null ? d.getReal_time_load() : 0.00;
                            //统计填谷需求响应任务的总收益，总收益为电网返回的总收益，如果电网没有返回，这里为预估收益
                            if (d.getVolume_profit() != null) {
                                totalFillProfit += d.getVolume_profit();
                            } else if (d.getProfit() != null) {
                                totalFillProfit += d.getProfit();
                            }
                        }
                        totalDeclareLoad += d.getDeclare_load() != null ? d.getDeclare_load() : 0.00;
                        //统计容量补贴总收益，总收益为电网返回的总收益，如果电网没有返回，这里为空
                        totalVolumeProfit += d.getVolume_profit() != null ? d.getVolume_profit() : 0.00;

                    }
                    //	申报次数（户次）： 统计参与的需求响应申报的次数总和
                    sModel.setDemandNum(count);
                    //削峰电量
                    sModel.setTotalCutPower(totalCutPower);
                    //填谷电量
                    sModel.setTotalFillPower(totalFillPower);

                    //“平均申报负荷（kW/次）“= ∑▒需求响应申报负荷 / 需求响应申报数
                    if (sModel.getDemandNum() != 0) {
                        sModel.setAvgDeclareLoad(totalDeclareLoad / sModel.getDemandNum());
                    } else {
                        sModel.setAvgDeclareLoad(0.00);
                    }

                    //总收益
                    sModel.setTotalProfit(totalCutProfit + totalFillProfit + totalVolumeProfit);
                    //削峰总收益
                    sModel.setTotalCutProfit(totalCutProfit);
                    //填谷总收益
                    sModel.setTotalFillProfit(totalFillProfit);
                    //容量收益
                    sModel.setTotalVolumeProfit(totalVolumeProfit);

                    //用户收益= ∑_(n=1)^n▒〖每户需求响应总收益*每户分成比例〗
                    //按照用户签约的比例收益分成 用户管理里电力用户有签约
                    User user = userRepository.getOne(userId);
                    double userProfit = 0.00;
                    if (user != null) {
                        userProfit = sModel.getTotalProfit() * user.getShareRatio();
                    }
                    sModel.setUserProfit(userProfit / 100); //签约分成比例是*100存放数据库，因此这里的收入要除以100
                    //平台收益=∑_(n=1)^n▒〖每户需求响应总收益*平台分成比例〗
                    //平台收益=需求响应收益 - 用户收益
                    //平台收益 = 虚拟电厂运营商的收益 ，如果管理员自己配置，其实也是在替“虚拟电厂运营商”做操作，收益就属于平台（或虚拟电厂运营商）
                    sModel.setPlatformProfit(sModel.getTotalProfit() - sModel.getUserProfit());

                    //户均收益（元/户）= “总收益“/ 参与需求响应的节点数
                    sModel.setAvgProfit(sModel.getTotalProfit() / nodeIds.size());
                    //户均用户收益（元/户）=“按照分成比例用户获得的总收益“ / 参与需求响应的节点数
                    sModel.setAvgUserProfit(sModel.getUserProfit() / nodeIds.size());
                    //户均平台收益（元/户）=“按照分成比例平台获得的总收益“ / 参与需求响应的节点数
                    sModel.setAvgPlatformProfit(sModel.getPlatformProfit() / nodeIds.size());

                    //实际负荷响应率（%）=平均实际响应负荷（kW/次） /平均申报响应负荷（kW/次）
//                    if (sModel.getAvgDeclareLoad() != 0) {
//                        sModel.setActualComplianceRate((sModel.getAvgActualLoad() * 100) / sModel.getAvgDeclareLoad());
//                    } else {
//                        sModel.setActualComplianceRate(0.00);
//                    }
                }
            } else {
                //参与需求响应次数=需求响应任务的次数
                sModel.setDemandNum(0);
                //削峰电量
                sModel.setTotalCutPower(0.00);
                //填谷电量
                sModel.setTotalFillPower(0.00);
                //平均申报负荷=总申报负荷/需求响应次数
                sModel.setAvgDeclareLoad(0.00);
                //平均实际响应负荷（kW/次）=实时响应负荷总和/参与需求响应次数
                sModel.setAvgActualLoad(0.00);
                //实际负荷响应率（%）=平均实际响应负荷（kW/次） /平均申报响应负荷（kW/次）
                sModel.setActualComplianceRate(0.0);
                //总收益
                sModel.setTotalProfit(0.0);
                //削峰总收益
                sModel.setTotalCutProfit(0.0);
                //填谷总收益
                sModel.setTotalFillProfit(0.0);
                //容量收益
                sModel.setTotalVolumeProfit(0.0);
                //用户收益= ∑_(n=1)^n▒〖每户需求响应总收益*每户分成比例〗
                sModel.setUserProfit(0.0);
                //平台收益=∑_(n=1)^n▒〖每户需求响应总收益*平台分成比例〗
                sModel.setPlatformProfit(0.0);
                //户均收益（元/户）= “总收益“/ ”参与用户“
                sModel.setAvgProfit(0.0);
                //户均用户收益（元/户）=“按照分成比例用户获得的总收益“ /”参与用户
                sModel.setAvgUserProfit(0.0);
                //户均平台收益（元/户）=“按照分成比例平台获得的总收益“ /”参与用户“
                sModel.setAvgPlatformProfit(0.0);
            }

            return ResponseResult.success(sModel);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.success();
        }

    }

    @ApiOperation("查询首页响应信息")
    @UserLoginToken
    @RequestMapping(value = "/getHomePage", method = {RequestMethod.POST})
    public ResponseResult<Map> getHomePage() {
        //当年日前、小时、分钟、秒级响应均为执行需求响应的削峰和填谷的电量统计（kWh）
        Date date = new Date();
        //当年第一天
        Date firstDate = TimeUtil.getYearFirstDay(Integer.parseInt(TimeUtil.getThisYear()));
        List<Object[]> list = demandRespTaskRepository.findActualPowerByDate(firstDate, date);
        Map<Integer, String> respMap = new HashMap<>();
        if (list != null && list.size() > 0) {
            list.forEach(l -> {
                respMap.put(Integer.valueOf(l[0].toString()), l[1].toString());
            });
        }
        return ResponseResult.success(respMap);
    }

    @ApiOperation("响应收益-具体收益信息")
    @UserLoginToken
    @RequestMapping(value = "/getDemandIncome", method = {RequestMethod.POST})
    public ResponseResult getDemandIncome(@RequestBody DemandProfitModel model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");
        Map<String,Object> respMap = new HashMap<>();
        Integer num = 0;
        try {
            if(!StringUtil.isBlank(model.getNodeId())){
                //得到该用户的节点列表
                List<String> nodeIds = Arrays.asList(model.getNodeId().split(","));
                //当前页为第几页 默认 1开始
                int pageSize = model.getPageSize();
                int pageNum = (model.getNumber() - 1) * pageSize;//pg 从0开始

                List<Object[]> list = new ArrayList<>();

                //用户收益= ∑_(n=1)^n▒〖每户需求响应总收益*每户分成比例〗
                //按照用户签约的比例收益分成 用户管理里电力用户有签约
                User user = userRepository.getOne(userId);
                double shareRatio = 0.00;
                if (user != null) {
                    shareRatio = user.getShareRatio()/100.0; //签约分成比例是*100存放数据库，因此这里的收入要除以100
                }
                if(model.getUserType()==DemandProfitType.LOAD_INTEGRATOR.getCode()){
                    shareRatio=(100.0-user.getShareRatio())/100.0;
                }
                //如果日期类型为 日
                if(model.getDateType()==DemandProfitType.DATE_DAY.getCode()){
                    if(model.getUserType()== DemandProfitType.USER_ALL.getCode()){
                        //判断不同的用户类型，乘以不同的系数
                        list = profitRepository.findByRsDateNodeIds(
                                TimeUtil.strDDToDate(model.getStartDate(),"yyyy-MM-dd"),
                                TimeUtil.strDDToDate(model.getEndDate(),"yyyy-MM-dd"), nodeIds,pageSize,pageNum);
                    }else{
                        //判断不同的用户类型，乘以不同的系数
                        list = profitRepository.findByRsDateNodeIdsRatio(
                                TimeUtil.strDDToDate(model.getStartDate(), "yyyy-MM-dd"),
                                TimeUtil.strDDToDate(model.getEndDate(), "yyyy-MM-dd"), nodeIds, shareRatio, pageSize, pageNum);
                    }
                    //得到总条数
                    num =profitRepository.countByRsDateNodeIds(TimeUtil.strDDToDate(model.getStartDate(),"yyyy-MM-dd"),
                            TimeUtil.strDDToDate(model.getEndDate(),"yyyy-MM-dd"), nodeIds);
                }else if(model.getDateType()==DemandProfitType.DATE_MONTH.getCode()){//月
                    if(model.getUserType()== DemandProfitType.USER_ALL.getCode()){
                        //判断不同的用户类型，乘以不同的系数
                        list = profitRepository.findByRsDateNodeIdsMonth(
                                Integer.parseInt(model.getStartDate()),
                                Integer.parseInt(model.getEndDate()), nodeIds,pageSize,pageNum);
                    }else{
                        //判断不同的用户类型，乘以不同的系数
                        list = profitRepository.findByRsDateNodeIdsRatioMonth(
                                Integer.parseInt(model.getStartDate()),
                                Integer.parseInt(model.getEndDate()), nodeIds, shareRatio, pageSize, pageNum);
                    }
                    //得到总条数
                    num =profitRepository.countByRsDateNodeIdsMonth(Integer.parseInt(model.getStartDate()),
                            Integer.parseInt(model.getEndDate()), nodeIds);
                }else if(model.getDateType()==DemandProfitType.DATE_YEAR.getCode()){//年
                    if(model.getUserType()== DemandProfitType.USER_ALL.getCode()){
                        list = profitRepository.findByRsDateNodeIdsYear(
                                Integer.parseInt(model.getStartDate()),
                                Integer.parseInt(model.getEndDate()), nodeIds,pageSize,pageNum);
                    }else{
                        //判断不同的用户类型，乘以不同的系数
                        list = profitRepository.findByRsDateNodeIdsRatioYear(
                                Integer.parseInt(model.getStartDate()),
                                Integer.parseInt(model.getEndDate()), nodeIds, shareRatio, pageSize, pageNum);
                    }
                    //得到总条数
                    num =profitRepository.countByRsDateNodeIdsYear(Integer.parseInt(model.getStartDate()),
                            Integer.parseInt(model.getEndDate()), nodeIds);
                }

                List<String> nodeIdList = new ArrayList<>(nodeIds);
                if (list.size() < nodeIdList.size()) {
                    for (Object[] object : list) {
                        nodeIdList.remove(object[0].toString());
                    }
                    List<Object[]> objects = nodeRepository.findNodeIdAndNodeName(nodeIdList);
                    list.addAll(objects);
                }

                if (list != null && list.size() > 0) {
                    //日期json串 例如 [{"profitDate": "2024-03-18", "totalProfit": 55}, {"profitDate": "2024-03-17", "totalProfit": 23}]
                    List<Object[]> reslist = ListUtils.Pager(5, model.getNumber(), list);
                    respMap.put("list",reslist);
                    respMap.put("count",list.size());

                }else{
                    List<Object[]> objects = nodeRepository.findNodeIdAndNodeName(nodeIds);
                    list.addAll(objects);
                    respMap.put("list",list);
                    respMap.put("count",0);
                }
                return ResponseResult.success(respMap);
            }else{
                return ResponseResult.error("节点信息不能为空");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.success();
        }
    }

    @ApiOperation("响应收益-具体收益信息导出")
    @UserLoginToken
    @RequestMapping(value = "/getDemandIncomeExport", method = {RequestMethod.POST})
    public void getDemandIncomeExport(HttpServletResponse response, @RequestBody DemandProfitModel model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        List<Map<String, Object>> mapList = new ArrayList<>();//最终返回格式

        Integer num = 0;
        try {
            if(!StringUtil.isBlank(model.getNodeId())) {
                //若为导出
                if (!StringUtil.isBlank(model.getIsExport()) && model.getIsExport().equals("export")) {
                    //得到该用户的节点列表
                    List<String> nodeIds = Arrays.asList(model.getNodeId().split(","));

                    List<Object[]> list = new ArrayList<>();

                    //用户收益= ∑_(n=1)^n▒〖每户需求响应总收益*每户分成比例〗
                    //按照用户签约的比例收益分成 用户管理里电力用户有签约
                    User user = userRepository.getOne(userId);
                    double shareRatio = 0.00;
                    if (user != null) {
                        shareRatio = user.getShareRatio() / 100.0; //签约分成比例是*100存放数据库，因此这里的收入要除以100
                    }
                    if (model.getUserType() == DemandProfitType.LOAD_INTEGRATOR.getCode()) {
                        shareRatio = (100.0 - user.getShareRatio()) / 100.0;
                    }
                    Map<String, Object> total = new HashMap<>();
                    total.put("节点", "合计");
                    total.put("合计", 0.00d);

                    //如果日期类型为 日
                    if (model.getDateType() == DemandProfitType.DATE_DAY.getCode()) {
                        //判断不同的用户类型，乘以不同的系数
                        list = profitRepository.findByRsDateNodeIdsExport(
                                TimeUtil.strDDToDate(model.getStartDate(), "yyyy-MM-dd"),
                                TimeUtil.strDDToDate(model.getEndDate(), "yyyy-MM-dd"), nodeIds);
                        //得到统计信息
                        List<DemandProfitDayModel> datas = EntityUtils.castEntity(list, DemandProfitDayModel.class, new DemandProfitDayModel());
                        //获得所有的日期
                        List<Date> dates = TimeUtil.truncateToSplitDayContainEndDate(TimeUtil.strDDToDate(model.getStartDate(), "yyyy-MM-dd"),
                                TimeUtil.strDDToDate(model.getEndDate(), "yyyy-MM-dd"));

                        Map<String,List<DemandProfitDayModel>> dayMap = datas.stream().collect(Collectors.groupingBy(DemandProfitDayModel::getNode_id));

                        for(String nodeId:dayMap.keySet()){
                            List<DemandProfitDayModel> dayList = dayMap.get(nodeId);

                            Map<String, Object> mo = new LinkedHashMap<>();//用linkedHashMap，是为了先进先出
                            mo.put("节点", dayList.get(0).getNode_name());

                            Map<Date,DemandProfitDayModel> dateMap = dayList.stream().collect(Collectors.toMap(DemandProfitDayModel::getProfit_date,d->d));

                            BigDecimal totalProfits = BigDecimal.ZERO;

                            //遍历日期  以防缺失某个日期
                            for(Date d:dates){
                                DemandProfitDayModel dayModel = dateMap.get(d);
                                String dateStr = TimeUtil.toYmdStr(d);
                                double profits = 0.0d;

                                if(dayModel!=null){
                                    if (model.getUserType() == DemandProfitType.USER_ALL.getCode()) {
                                        profits=dayModel.getProfits();
                                    } else {
                                        profits=dayModel.getProfits()*shareRatio;
                                    }
                                }
                                BigDecimal bg = new BigDecimal(profits);

                                double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                                totalProfits=totalProfits.add(bg);
                                mo.put(dateStr, f1);
                            }
                            mo.put("合计", totalProfits.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                            mapList.add(mo);
                        }


                    } else if (model.getDateType() == DemandProfitType.DATE_MONTH.getCode()) {//月
                        Integer start = Integer.parseInt(model.getStartDate());
                        Integer end = Integer.parseInt(model.getEndDate());

                        list = profitRepository.findByRsDateNodeIdsMonthExport(start,end, nodeIds);
                        //得到统计信息
                        List<DemandProfitMonthModel> datas = EntityUtils.castEntity(list, DemandProfitMonthModel.class, new DemandProfitMonthModel());

                        //得到区间月份集合
                        List<Integer> monthList = new ArrayList<>();
                        for (int i = start; i <= end; i++) {
                            monthList.add(i);
                        }
                        Map<String,List<DemandProfitMonthModel>> monthMap = datas.stream().collect(Collectors.groupingBy(DemandProfitMonthModel::getNode_id));
                        for(String nodeId:monthMap.keySet()){
                            List<DemandProfitMonthModel> dayList = monthMap.get(nodeId);

                            Map<String, Object> mo = new LinkedHashMap<>();//用linkedHashMap，是为了先进先出
                            mo.put("节点", dayList.get(0).getNode_name());

                            Map<Integer,DemandProfitMonthModel> dateMap = dayList.stream().collect(Collectors.toMap(DemandProfitMonthModel::getProfit_year_month,d->d));

                            BigDecimal totalProfits = BigDecimal.ZERO;

                            //遍历月份  以防缺失某个月份
                            for(Integer d:monthList){
                                DemandProfitMonthModel dayModel = dateMap.get(d);
                                double profits = 0.0d;

                                if(dayModel!=null){
                                    if (model.getUserType() == DemandProfitType.USER_ALL.getCode()) {
                                        profits=dayModel.getProfits();
                                    } else {
                                        profits=dayModel.getProfits()*shareRatio;
                                    }
                                }
                                BigDecimal bg = new BigDecimal(profits);

                                double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                                totalProfits=totalProfits.add(bg);
                                String str = String.valueOf(d);
                                StringBuilder sb = new StringBuilder(str);
                                sb.insert(4, '-');
                                mo.put(sb.toString(), f1);
                            }
                            mo.put("合计", totalProfits.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                            mapList.add(mo);
                        }

                    } else if (model.getDateType() == DemandProfitType.DATE_YEAR.getCode()) {//年
                        Integer start = Integer.parseInt(model.getStartDate());
                        Integer end = Integer.parseInt(model.getEndDate());

                        list = profitRepository.findByRsDateNodeIdsYearExport(
                                start, end, nodeIds);
                        //得到统计信息
                        List<DemandProfitYearModel> datas = EntityUtils.castEntity(list, DemandProfitYearModel.class, new DemandProfitYearModel());

                        //得到区间年份集合
                        List<Integer> yearList = new ArrayList<>();

                        for (int i = start; i <= end; i++) {
                            yearList.add(i);
                        }

                        Map<String,List<DemandProfitYearModel>> yearMap = datas.stream().collect(Collectors.groupingBy(DemandProfitYearModel::getNode_id));

                        for(String nodeId:yearMap.keySet()){
                            List<DemandProfitYearModel> dayList = yearMap.get(nodeId);

                            Map<String, Object> mo = new LinkedHashMap<>();//用linkedHashMap，是为了先进先出
                            mo.put("节点", dayList.get(0).getNode_name());

                            Map<Integer,DemandProfitYearModel> dateMap = dayList.stream().collect(Collectors.toMap(DemandProfitYearModel::getProfit_year,d->d));

                            BigDecimal totalProfits = BigDecimal.ZERO;

                            //遍历年份  以防缺失某个年份
                            for(Integer d:yearList){
                                DemandProfitYearModel dayModel = dateMap.get(d);
                                double profits = 0.0d;

                                if(dayModel!=null){
                                    if (model.getUserType() == DemandProfitType.USER_ALL.getCode()) {
                                        profits=dayModel.getProfits();
                                    } else {
                                        profits=dayModel.getProfits()*shareRatio;
                                    }
                                }
                                BigDecimal bg = new BigDecimal(profits);

                                double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                                totalProfits=totalProfits.add(bg);
                                mo.put(String.valueOf(d), f1);
                            }
                            mo.put("合计", totalProfits.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                            mapList.add(mo);
                        }

                    }
                  //  mapList.add(total);

                    Map<String, Object> totalCnt = new HashMap<>();

                    Set<String> keys = mapList.get(0).keySet();
                    for (String key : keys) {
                        List<Object> cntList = new ArrayList<>();
                        if (key.equals("节点")) {
                            totalCnt.put("节点","合计");
                        } else {
                            for (int i = 0; i < mapList.size(); i++) {
                                cntList.add(mapList.get(i).get(key));
                                System.out.println(mapList.get(i).get(key).toString());
                            }
                            int sum = 0;
                            for (Object j:cntList) {
                                BigDecimal temp = BigDecimal.valueOf(Double.valueOf(j.toString()));
                                temp = temp.multiply(BigDecimal.valueOf(100));
                                double k = temp.doubleValue();
                                sum += k;
                            }
                            double res = divideBy100(sum);
                            totalCnt.put(key,res);
                        }
                    }
                    mapList.add(totalCnt);

                    exec(response, mapList, iExcelOutPutService);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static double divideBy100(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


}