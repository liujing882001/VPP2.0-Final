package com.example.vvpweb.runschedule.runstrategy;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.*;
import com.example.vvpcommom.devicecmd.AirConditioningDTO;
import com.example.vvpcommom.devicecmd.OtherConditioningDTO;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.tree.model.StructTreeResponse;
import com.example.vvpservice.tree.service.ITreeLabelService;
import com.example.vvpservice.usernode.service.IPageableService;
import com.example.vvpweb.runschedule.runstrategy.model.StrategyCal;
import com.example.vvpweb.runschedule.runstrategy.model.StrategyDetailModel;
import com.example.vvpweb.runschedule.runstrategy.model.StrategyViewModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/run_schedule/run_strategy")
@CrossOrigin
@Api(value = "运行调度-运行策略-可调负荷运行策略", tags = {"运行调度-运行策略-可调负荷运行策略"})
public class LoadRunStrategyController {

    @Autowired
    private IPageableService pageableService;

    @Autowired
    private CronUtils cronUtils;

    @Autowired
    private ITreeLabelService iTreeLabelService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ScheduleStrategyRepository scheduleStrategyRepository;

    @Autowired
    private ScheduleStrategyViewRepository scheduleStrategyViewRepository;

    @Autowired
    private ScheduleStrategyDeviceRepository scheduleStrategyDeviceRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;


    /**
     * 得到（权限下） 区域 -> 节点 -> 系统 -> 设备
     *
     * @return
     */
    @ApiOperation("获取权限下区域-节点-系统-设备")
    @UserLoginToken
    @RequestMapping(value = "areaDeviceView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> areaDeviceViewMatch(@RequestParam(value = "isResponse", required = false) boolean isResponse,
                                                                        @RequestParam(value = "strategyId", required = false) String strategyId) {

        if (isResponse) {
            return ResponseResult.success(iTreeLabelService.areaDeviceViewMatch(strategyId));

        } else {
            return ResponseResult.success(iTreeLabelService.areaDeviceView());
        }
    }


    /**
     * 可调负荷运行策略 - 策略查询
     */
    @ApiOperation("分页获取可调负荷运行策略")
    @UserLoginToken
    @RequestMapping(value = "strategyListByNamePageable", method = {RequestMethod.POST})
    public ResponseResult<PageModel> strategyListByName(@RequestParam("number") int number,
                                                        @RequestParam("pageSize") int pageSize,
                                                        @RequestParam(value = "strategyName", required = false) String strategyName) {
        List<StrategyViewModel> list = new ArrayList<>();
        Page<ScheduleStrategy> datas = pageableService.getStrategyLikeStrategyName(strategyName, number, pageSize);

        datas.getContent().forEach(e -> {
            StrategyViewModel sm = new StrategyViewModel();
            sm.setStrategyId(e.getStrategyId());
            sm.setStrategyName(e.getStrategyName());
            sm.setStrategyStatus(e.isStrategyStatus());
            sm.setDemandResponse(e.isDemandResponse());
            sm.setCreatedTime(e.getCreatedTime());
            sm.setRunStrategy(e.getRunStrategy());
            sm.setOwnerId(e.getUserId());
            sm.setStrategyType(e.getStrategyType());
            sm.setOwnerName(e.getUser().getUserName());
            List<ScheduleStrategyView> allByStrategyId = scheduleStrategyViewRepository.findAllByStrategyId(e.getStrategyId());

            allByStrategyId.forEach(l -> sm.setRatedPower(sm.getRatedPower() + l.getDeviceRatedPower()));

            list.add(sm);
        });
        PageModel pageModel = new PageModel();
        pageModel.setPageSize(pageSize);
        pageModel.setContent(list);
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);
    }


    /**
     * 可调负荷运行策略 - 策略查询
     */

    @UserLoginToken
    @RequestMapping(value = "strategyDetail", method = {RequestMethod.POST})
    public ResponseResult<StrategyDetailModel> strategyDetail(@RequestParam(value = "strategyId") String strategyId) {

        ScheduleStrategy scheduleStrategy = null;
        if (userService.isManger()) {
            scheduleStrategy = scheduleStrategyRepository.findByStrategyId(strategyId);
        } else {
            String userId = RequestHeaderContext.getInstance().getUserId();
            scheduleStrategy = scheduleStrategyRepository.findByUserIdAndStrategyId(userId, strategyId);
        }

        if (scheduleStrategy == null) {
            return ResponseResult.error("查询的策略不存在！");
        }

        StrategyDetailModel sm = new StrategyDetailModel();
        sm.setStrategyId(scheduleStrategy.getStrategyId());
        sm.setStrategyName(scheduleStrategy.getStrategyName());
        sm.setStrategyStatus(scheduleStrategy.isStrategyStatus());
        sm.setDemandResponse(scheduleStrategy.isDemandResponse());
        sm.setCreatedTime(scheduleStrategy.getCreatedTime());
        sm.setRunStrategy(scheduleStrategy.getRunStrategy());


        sm.setStrategyType(scheduleStrategy.getStrategyType());
        sm.setDeviceIdList(scheduleStrategy.getDeviceList().stream().map(Device::getDeviceId).collect(Collectors.toList()));

        int runStrategy = scheduleStrategy.getRunStrategy();
        if (runStrategy == 1) {
            CronUtils.CustomCronField customCronField = cronUtils.parseCon(scheduleStrategy.getCronExpression());
            if (customCronField != null) {
                List<Integer> hours = customCronField.getHours();
                List<Integer> minutes = customCronField.getMinutes();
                if (hours != null && hours.size() > 0 && minutes != null && minutes.size() > 0) {
                    StrategyDetailModel.CycleExe cycleExe = new StrategyDetailModel.CycleExe();
                    cycleExe.setCycleTimes(String.format("%02d", hours.get(0)) + ":" + String.format("%02d", minutes.get(0)));
                    cycleExe.setCycleWeeks(customCronField.getWeekdays());

                    sm.setCycleExe(cycleExe);
                }
            }
        } else {
            String s = CronUtils.onceExeTime(scheduleStrategy.getCronExpression());
            if (s != null) {
                StrategyDetailModel.OnceExe onceExe = new StrategyDetailModel.OnceExe();
                onceExe.setYmd(s.substring(0, 10));
                onceExe.setTimes(s.substring(11));

                sm.setOnceExe(onceExe);
            }

        }

        if (scheduleStrategy.getCmdExpression() != null) {
            if (scheduleStrategy.getStrategyType() == 1) {
                sm.setOtherConditioningDTO(JSONObject.parseObject(scheduleStrategy.getCmdExpression(), OtherConditioningDTO.class));
            }
            if (scheduleStrategy.getStrategyType() == 0) {
                sm.setAirConditioningDTO(JSONObject.parseObject(scheduleStrategy.getCmdExpression(), AirConditioningDTO.class));
            }
        }

        return ResponseResult.success(sm);
    }


    /**
     * 可调负荷运行策略 - 策略添加
     */

    @UserLoginToken
    @RequestMapping(value = "addStrategy", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult addStrategy(@RequestBody StrategyDetailModel strategyModel) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");
//        String userId = RequestHeaderContext.getInstance().getUserId();
        //查找sys_user表，判断用户电网类型
        User user = userRepository.findUserByUserId(userId);
        //如果电网字段为空的话，默认赋值为南网字段2
        if (user.getPowerGrid() == null) {
            user.setPowerGrid(2);
        }
        if (strategyModel == null) {
            return ResponseResult.error("参数异常，请检查！");
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(strategyModel.getStrategyName())) {
            return ResponseResult.error("可调负荷运行策略名称不能为空！");
        }
        if (strategyModel.getStrategyType() != 0 && strategyModel.getStrategyType() != 1) {
            return ResponseResult.error("策略类型有误！");
        }
        if (strategyModel.getDeviceIdList() == null || strategyModel.getDeviceIdList().size() == 0) {
            return ResponseResult.error("可调负荷运行策略控制设备数量为0，请修改！");
        }
//        String userId = RequestHeaderContext.getInstance().getUserId();
        String strategyName = strategyModel.getStrategyName();
        ScheduleStrategy byStrategyName = scheduleStrategyRepository.findByStrategyNameAndUserId(strategyName, userId);
        if (byStrategyName != null) {
            return ResponseResult.error("策略名称已经存在");
        }
        if (strategyModel.getDeviceIdList() == null || strategyModel.getDeviceIdList().size() == 0) {
            return ResponseResult.error("新建策略，已选择设备数量至少为1个！");
        }
        ScheduleStrategy scheduleStrategy = new ScheduleStrategy();
        String strategyId = IdGenerator.md5Id(strategyName);
        scheduleStrategy.setStrategyId(strategyId);
        if (strategyModel.isDemandResponse()) {
            List<ScheduleStrategy> allByUserIdAndDemandResponse = scheduleStrategyRepository.findAllByUserIdAndIsDemandResponse(RequestHeaderContext.getInstance().getUserId(), true);
            if (allByUserIdAndDemandResponse != null && !allByUserIdAndDemandResponse.isEmpty()) {
                return ResponseResult.error("用户只能有一个调度任务参加自动需求响应");
            }
        }
        //如果字段没填写的话默认为空
//        if (strategyModel.getPowerGrid() == null) {
//            strategyModel.setPowerGrid(2);
//        }
        scheduleStrategy.setDemandResponse(strategyModel.isDemandResponse());
        scheduleStrategy.setStrategyName(strategyModel.getStrategyName());
        scheduleStrategy.setStrategyStatus(true);
        scheduleStrategy.setRunStrategy(strategyModel.getRunStrategy());
        scheduleStrategy.setStrategyType(strategyModel.getStrategyType());
//        运行策略类型 0 一次性 或者 1 周期性
        if (strategyModel.getRunStrategy() == 0) {
            StrategyDetailModel.OnceExe onceExe = strategyModel.getOnceExe();
            String ymd = onceExe.getYmd();
            String times = onceExe.getTimes();
            if (ymd != null && times != null) {
                String[] ymdS = ymd.split("-");
                String[] ts = times.split(":");
                if (ymdS.length > 2 && ts.length > 1) {
                    scheduleStrategy.setCronExpression(cronUtils.buildCron(
                            String.valueOf(Integer.parseInt(ymdS[0])),
                            String.valueOf(Integer.parseInt(ymdS[1])),
                            String.valueOf(Integer.parseInt(ymdS[2])),
                            String.valueOf(Integer.parseInt(ts[0])),
                            String.valueOf(Integer.parseInt(ts[1]))));
                }else {
                    return ResponseResult.error("用户调度任务运行策略时间异常，日期-"+ymd +".时间-"+times);
                }
            }
        }
        if (strategyModel.getRunStrategy() == 1 && !strategyModel.isDemandResponse()) {
            StrategyDetailModel.CycleExe cycleExe = strategyModel.getCycleExe();
            String cycleTimes = cycleExe.getCycleTimes();
            List<Integer> cycleWeeks = cycleExe.getCycleWeeks();
            String[] split = cycleTimes.split(":");
            if (split.length > 1) {
                scheduleStrategy.setCronExpression(cronUtils.buildCron(Arrays.asList(Integer.parseInt(split[1])),
                        Arrays.asList(Integer.parseInt(split[0])),
                        cycleWeeks));
            }else {
                return ResponseResult.error("用户调度任务运行策略时间异常,星期-"+cycleWeeks+",时间-"+cycleTimes);
            }
        }

//        0 空调策略
//        1 其他策略(针对照明、基站充电桩等，可对设备的启动/停止进行控制)
        if (strategyModel.getStrategyType() == 0) {
            if (strategyModel.getAirConditioningDTO() != null) {
                scheduleStrategy.setCmdExpression(JSONObject.toJSONString(strategyModel.getAirConditioningDTO()));
            }
        } else {
            if (strategyModel.getOtherConditioningDTO() != null) {
                scheduleStrategy.setCmdExpression(JSONObject.toJSONString(strategyModel.getOtherConditioningDTO()));
            }
        }
        scheduleStrategy.setUserId(userId);
        scheduleStrategy.setPowerGrid(user.getPowerGrid());

        scheduleStrategyRepository.save(scheduleStrategy);

        List<String> deviceIdList = strategyModel.getDeviceIdList();
        List<ScheduleStrategyDevice> ssds = new ArrayList<>();

        //保存节点信息
        if (deviceIdList != null) {
            if (strategyModel.isDemandResponse()) {
                synchronized (this) {
                    List<String> nodeIds = scheduleStrategyViewRepository.findAllByIsDemandResponse(true)
                            .stream().map(ScheduleStrategyView::getNodeId).collect(Collectors.toList());
                    if (!nodeIds.isEmpty()) {
                        List<String> allByNode_nodeIdIn = deviceRepository.findAllByNode_NodeIdIn(nodeIds).stream()
                                .map(Device::getDeviceId).collect(Collectors.toList());
                        allByNode_nodeIdIn.retainAll(deviceIdList);
                        if (allByNode_nodeIdIn.size() > 0) {
                            return ResponseResult.error("所选节点已在其他的策略中选择参加了自动需求响应，不可重复参加");
                        }
                    }
                    deviceIdList.forEach(e -> {
                        ScheduleStrategyDevice ssd = new ScheduleStrategyDevice();
                        ssd.setId(IdGenerator.concatString(e, strategyId));
                        ssd.setDeviceId(e);
                        ssd.setStrategyId(strategyId);

                        ssds.add(ssd);
                    });
                    scheduleStrategyDeviceRepository.saveAll(ssds);
                }
            } else {
                deviceIdList.forEach(e -> {
                    ScheduleStrategyDevice ssd = new ScheduleStrategyDevice();
                    ssd.setId(IdGenerator.concatString(e, strategyId));
                    ssd.setDeviceId(e);
                    ssd.setStrategyId(strategyId);
                    ssds.add(ssd);
                });
                scheduleStrategyDeviceRepository.saveAll(ssds);
            }
        }
        return ResponseResult.success();
    }

    /**
     * 可调负荷运行策略 - 策略修改
     */
    @UserLoginToken
    @RequestMapping(value = "updateStrategy", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult updateStrategy(@RequestBody StrategyDetailModel strategyModel) {

        if (strategyModel == null) {
            return ResponseResult.error("参数异常，请检查！");
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(strategyModel.getStrategyId())) {
            return ResponseResult.error("可调负荷运行策略编号不能为空！");
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(strategyModel.getStrategyName())) {
            return ResponseResult.error("可调负荷运行策略名称不能为空！");
        }
        if (strategyModel.getStrategyType() != 0 && strategyModel.getStrategyType() != 1) {
            return ResponseResult.error("策略类型有误！");
        }
        if (strategyModel.getDeviceIdList() == null || strategyModel.getDeviceIdList().size() == 0) {
            return ResponseResult.error("可调负荷运行策略控制设备数量为0，请修改！");
        }


        String userId = RequestHeaderContext.getInstance().getUserId();
        String strategyName = strategyModel.getStrategyName();

        ScheduleStrategy oldStrategy = scheduleStrategyRepository.findByStrategyNameAndUserId(strategyName, userId);
        if (oldStrategy != null && !oldStrategy.getStrategyId().equals(strategyModel.getStrategyId())) {
            return ResponseResult.error("策略名称已经存在");
        }
        ScheduleStrategy scheduleStrategy = scheduleStrategyRepository.findByUserIdAndStrategyId(userId, strategyModel.getStrategyId());

        if (scheduleStrategy == null) {
            return ResponseResult.error("更新的策略不存在或者没有权限编辑！");
        }
        if (strategyModel.getDeviceIdList() == null || strategyModel.getDeviceIdList().size() == 0) {

            return ResponseResult.error("新建策略，已选择设备数量至少为1个！");
        }


        if (strategyModel.isDemandResponse()) {
            List<ScheduleStrategy> allByUserIdAndDemandResponse = scheduleStrategyRepository.findAllByUserIdAndIsDemandResponse(RequestHeaderContext.getInstance().getUserId(), true);
            if (allByUserIdAndDemandResponse != null && !allByUserIdAndDemandResponse.isEmpty()) {
                List<String> collect = allByUserIdAndDemandResponse.stream().map(ScheduleStrategy::getStrategyId).collect(Collectors.toList());
                if (!collect.contains(strategyModel.getStrategyId())) {
                    return ResponseResult.error("用户只能有一个调度任务参加自动需求响应");
                }

            }
        }
        scheduleStrategy.setDemandResponse(strategyModel.isDemandResponse());

        scheduleStrategy.setStrategyName(strategyModel.getStrategyName());
        scheduleStrategy.setStrategyStatus(strategyModel.isStrategyStatus());
        scheduleStrategy.setRunStrategy(strategyModel.getRunStrategy());
        scheduleStrategy.setStrategyType(strategyModel.getStrategyType());

//        运行策略类型 0 一次性 或者 1 周期性
        if (strategyModel.getRunStrategy() == 0) {
            StrategyDetailModel.OnceExe onceExe = strategyModel.getOnceExe();
            String ymd = onceExe.getYmd();
            String times = onceExe.getTimes();
            if (ymd != null && times != null) {
                String[] ymdS = ymd.split("-");
                String[] ts = times.split(":");
                if (ymdS.length > 2 && ts.length > 1) {
                    scheduleStrategy.setCronExpression(cronUtils.buildCron(
                            String.valueOf(Integer.parseInt(ymdS[0])),
                            String.valueOf(Integer.parseInt(ymdS[1])),
                            String.valueOf(Integer.parseInt(ymdS[2])),
                            String.valueOf(Integer.parseInt(ts[0])),
                            String.valueOf(Integer.parseInt(ts[1]))));
                }

            }
        } else {
            StrategyDetailModel.CycleExe cycleExe = strategyModel.getCycleExe();
            String cycleTimes = cycleExe.getCycleTimes();
            List<Integer> cycleWeeks = cycleExe.getCycleWeeks();
            String[] split = cycleTimes.split(":");
            if (split.length > 1) {
                scheduleStrategy.setCronExpression(cronUtils.buildCron(Arrays.asList(Integer.parseInt(split[1])),
                        Arrays.asList(Integer.parseInt(split[0])),
                        cycleWeeks));
            }

        }

        //       0 空调策略
//       1 其他策略(针对照明、基站充电桩等，可对设备的启动/停止进行控制)
        if (strategyModel.getStrategyType() == 0) {

            if (strategyModel.getAirConditioningDTO() != null) {
                scheduleStrategy.setCmdExpression(JSONObject.toJSONString(strategyModel.getAirConditioningDTO()));
            }

        } else {

            if (strategyModel.getOtherConditioningDTO() != null) {
                scheduleStrategy.setCmdExpression(JSONObject.toJSONString(strategyModel.getOtherConditioningDTO()));
            }
        }
        scheduleStrategy.setUserId(RequestHeaderContext.getInstance().getUserId());
        scheduleStrategyRepository.save(scheduleStrategy);

        scheduleStrategyDeviceRepository.deleteAllByStrategyId(strategyModel.getStrategyId());

        List<String> deviceIdList = strategyModel.getDeviceIdList();
        List<ScheduleStrategyDevice> ssds = new ArrayList<>();
        if (deviceIdList != null) {

            if (strategyModel.isDemandResponse()) {

                synchronized (this) {
                    List<String> nodeIds = scheduleStrategyViewRepository.findAllByIsDemandResponse(true)
                            .stream().filter(s -> !s.getStrategyId().equals(strategyModel.getStrategyId()))
                            .map(ScheduleStrategyView::getNodeId).collect(Collectors.toList());

                    if (!nodeIds.isEmpty()) {
                        List<String> allByNode_nodeIdIn = deviceRepository.findAllByNode_NodeIdIn(nodeIds).stream()
                                .map(Device::getDeviceId).collect(Collectors.toList());
                        allByNode_nodeIdIn.retainAll(deviceIdList);
                        if (allByNode_nodeIdIn.size() > 0) {
                            return ResponseResult.error("所选节点已在其他的策略中选择参加了自动需求响应，不可重复参加");
                        }

                    }

                    deviceIdList.forEach(e -> {
                        ScheduleStrategyDevice ssd = new ScheduleStrategyDevice();
                        ssd.setId(IdGenerator.concatString(e, strategyModel.getStrategyId()));
                        ssd.setDeviceId(e);
                        ssd.setStrategyId(strategyModel.getStrategyId());

                        ssds.add(ssd);
                    });
                    scheduleStrategyDeviceRepository.saveAll(ssds);

                }

            } else {
                deviceIdList.forEach(e -> {
                    ScheduleStrategyDevice ssd = new ScheduleStrategyDevice();
                    ssd.setId(IdGenerator.concatString(e, strategyModel.getStrategyId()));
                    ssd.setDeviceId(e);
                    ssd.setStrategyId(strategyModel.getStrategyId());

                    ssds.add(ssd);
                });
                scheduleStrategyDeviceRepository.saveAll(ssds);
            }
        }

        return ResponseResult.success();
    }


    /**
     * 可调负荷总览
     */
    @UserLoginToken
    @RequestMapping(value = "strategyCalculate", method = {RequestMethod.POST})
    public ResponseResult<StrategyCal> strategyCalculate(@RequestParam("strategyId") String strategyId) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(strategyId)) {
            return ResponseResult.error("可调负荷运行策略编号不能为空！");
        }
        StrategyCal response = new StrategyCal();
        ScheduleStrategy scheduleStrategy = null;
        if (userService.isManger()) {
            scheduleStrategy = scheduleStrategyRepository.findByStrategyId(strategyId);
        } else {
            String userId = RequestHeaderContext.getInstance().getUserId();
            scheduleStrategy = scheduleStrategyRepository.findByUserIdAndStrategyId(userId, strategyId);
        }

        if (scheduleStrategy == null) {
            return ResponseResult.error("策略不存在或者无权限查看该策略！");
        }

        List<ScheduleStrategyView> allByStrategyId = scheduleStrategyViewRepository.findAllByStrategyId(strategyId);

        response.setDeviceNumber(allByStrategyId.size());

        allByStrategyId.forEach(e -> response.setRatedPower(response.getRatedPower() + e.getDeviceRatedPower()));
        allByStrategyId.forEach(e -> response.setRealPower(response.getRealPower() + e.getDeviceRealPower()));

        List<String> collect = allByStrategyId.stream().map(ScheduleStrategyView::getNodeId).distinct().collect(Collectors.toList());

        response.setNodeNumber(collect.size());

        return ResponseResult.success(response);
    }


    /**
     * 可调设备总览
     */
    @UserLoginToken
    @RequestMapping(value = "strategyDeviceDetailAll", method = {RequestMethod.POST})
    public ResponseResult<PageModel> strategyDeviceDetailAll(@RequestParam("number") int number,
                                                             @RequestParam("pageSize") int pageSize,
                                                             @RequestParam("strategyId") String strategyId,
                                                             @RequestParam(value = "deviceName", required = false) String deviceName) {

        if (org.apache.commons.lang3.StringUtils.isEmpty(strategyId)) {
            return ResponseResult.error("可调负荷运行策略编号不能为空！");
        }
        ScheduleStrategy scheduleStrategy = null;
        if (userService.isManger()) {
            scheduleStrategy = scheduleStrategyRepository.findByStrategyId(strategyId);
        } else {
            String userId = RequestHeaderContext.getInstance().getUserId();
            scheduleStrategy = scheduleStrategyRepository.findByUserIdAndStrategyId(userId, strategyId);
        }
        if (scheduleStrategy == null) {
            return ResponseResult.error("策略不存在或者无权限查看该策略！");
        }
        Page<ScheduleStrategyView> datas = pageableService.getStrategyDeviceByStrategyId(strategyId, deviceName, number, pageSize);

        PageModel pageModel = new PageModel();
        pageModel.setPageSize(pageSize);
        pageModel.setContent(datas.getContent());
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);
    }


    /**
     * 可调设备总览
     */
    @UserLoginToken
    @RequestMapping(value = "strategyDeviceDetailOn", method = {RequestMethod.POST})
    public ResponseResult<PageModel> strategyDeviceDetailOn(@RequestParam("number") int number,
                                                            @RequestParam("pageSize") int pageSize,
                                                            @RequestParam("strategyId") String strategyId,
                                                            @RequestParam("online") boolean online,
                                                            @RequestParam(value = "deviceName", required = false) String deviceName) {

        if (org.apache.commons.lang3.StringUtils.isEmpty(strategyId)) {
            return ResponseResult.error("可调负荷运行策略编号不能为空！");
        }
        ScheduleStrategy scheduleStrategy = null;
        if (userService.isManger()) {
            scheduleStrategy = scheduleStrategyRepository.findByStrategyId(strategyId);
        } else {
            String userId = RequestHeaderContext.getInstance().getUserId();
            scheduleStrategy = scheduleStrategyRepository.findByUserIdAndStrategyId(userId, strategyId);
        }
        if (scheduleStrategy == null) {
            return ResponseResult.error("策略不存在或者无权限查看该策略！");
        }
        Page<ScheduleStrategyView> datas = pageableService.getStrategyDeviceByStrategyId(strategyId, deviceName, number, pageSize, online);

        PageModel pageModel = new PageModel();
        pageModel.setPageSize(pageSize);
        pageModel.setContent(datas.getContent());
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);
    }


}
