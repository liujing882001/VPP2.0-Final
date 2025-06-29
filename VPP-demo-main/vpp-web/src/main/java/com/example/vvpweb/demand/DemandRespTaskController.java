package com.example.vvpweb.demand;
import com.alibaba.fastjson.JSON;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.Instant;
import java.time.ZoneId;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpcommom.*;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpscheduling.service.ISysJobService;
import com.example.vvpscheduling.util.constant.ScheduleConstants;
import com.example.vvpservice.demand.model.DemandRespStrategyModel;
import com.example.vvpservice.demand.model.DemandRespStrategyNoModel;
import com.example.vvpservice.demand.model.DemandResponseInvitation;
import com.example.vvpservice.demand.model.DemandResponseInvitationResult;
import com.example.vvpservice.demand.service.DemandRespStrategyNoService;
import com.example.vvpservice.demand.service.DemandRespStrategyService;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.demand.aigorithmmodel.DemandAlgorithmVo;
import com.example.vvpweb.demand.aigorithmmodel.EnumExample;
import com.example.vvpweb.demand.aigorithmmodel.InitVo;
import com.example.vvpweb.demand.model.*;
import com.example.vvpweb.systemmanagement.systemparamer.model.SmartEnergyModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.Resource;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
/**
 * @author maoyating
 * @description 需求响应-响应任务
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/demand_resp/resp_task")
@CrossOrigin
@Api(value = "需求响应-响应任务", tags = {"需求响应-响应任务"})
public class DemandRespTaskController {
    private static Logger logger = LoggerFactory.getLogger(DemandRespTaskController.class);
    @Autowired
    private DemandRespTaskRepository demandRespTaskRepository;
    @Autowired
    private DemandRespStrategyRepository respStrategyRepository;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private ScheduleStrategyRepository scheduleStrategyRepository;
    @Autowired
    private ISysJobService sysJobService;
    @Autowired
    private SysJobRepository sysJobRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private DemandRespStrategyNoRepository noRepository;
    @Autowired
    private ScheduleStrategyDeviceRepository scheduleStrategyDeviceRepository;
    @Resource
    private AiLoadRepository aiLoadRepository;
    @Resource
    private SysParamRepository sysParamRepository;
    @Autowired
    private DemandRespPlanPriceRepository planPriceRepository;
    @Resource
    private DemandRespPlanPriceRepository priceRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${server.port}")
    private int port;
    @Resource
    private DemandRespStrategyService respStrategyService;
    @Resource
    private DemandRespStrategyNoService noService;
    @Resource
    private DemandRespPlanRepository planRepository;
    @Resource
    private IotTsKvRepository iotTsKvRepository;
    @Resource
    DemandStrategyRepository demandStrategyRepository;
    /**
     * 自动获取任务
     */
    @ApiOperation("自动获取任务")
    @UserLoginToken
    @RequestMapping(value = "/getTask", method = {RequestMethod.GET})
    public ResponseResult getTask() {
        //TODO 从哪获取？？
        return ResponseResult.success();
    }
    /**
     * 接收厦门国网传的内容
     */
    @ApiOperation("接收厦门国网传的内容")
    @RequestMapping(value = "/receiverMqttTask", method = {RequestMethod.POST})
    public ResponseResult receiverMqttTask(@RequestBody String json) {
        logger.info(json);
        try {
            if (StringUtils.isNotEmpty(json)) {
                DemandTaskMqttModel taskMqttModel = JSON.parseObject(json, DemandTaskMqttModel.class);
                DemandRespTask dr = new DemandRespTask();
                String[] rsTimeStr = taskMqttModel.getStartTime().split(" ");
                String[] reTimeStr = taskMqttModel.getEndTime().split(" ");
                dr.setRespId(taskMqttModel.getCmdCode());//存指令编码
                dr.setRsTime(TimeUtil.strDDToDate(taskMqttModel.getStartTime(), "yyyy-MM-dd HH:mm:00"));
                dr.setReTime(TimeUtil.strDDToDate(taskMqttModel.getEndTime(), "yyyy-MM-dd HH:mm:00"));
                dr.setRsDate(TimeUtil.strDDToDate(rsTimeStr[0], "yyyy-MM-dd"));
                dr.setTaskCode(TimeUtil.startEndDateToStr(dr.getRsDate(),dr.getRsTime(),dr.getReTime()));
                if(taskMqttModel.getControls()!=null ){
                    List<ControlsMqttModel> controls = JSON.parseArray(taskMqttModel.getControls().toString()
                            , ControlsMqttModel.class);
                    String respLoad = controls.get(0).getControlValue();
                    if(StringUtils.isNotEmpty(respLoad)){
                        dr.setRespLoad(Double.valueOf(respLoad));
                    }
                }
                dr.setRespType(Integer.valueOf(taskMqttModel.getAdjustType()));
                dr.setRespLevel(Integer.valueOf(taskMqttModel.getAdjustCode().trim()));//响应级别
                //已超时的数据，直接设置为0-删除状态   ，未超时的数据才能正常处理
                if(taskMqttModel.getFlag()==0){
                    dr.setDStatus(1);
                    Specification<ScheduleStrategy> spec = new Specification<ScheduleStrategy>() {
                        @Override
                        public Predicate toPredicate(Root<ScheduleStrategy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                            List<Predicate> predicates = new ArrayList<>();
                            predicates.add(cb.equal(root.get("isDemandResponse"), true));//查询自动参加需求响应
                            predicates.add(cb.equal(root.get("isStrategyStatus"), true));//查询策略状态为开启状态
                            criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                            return criteriaQuery.getRestriction();
                        }
                    };
                    //根据userId，查找创建的策略
                    List<ScheduleStrategy> strategyList = scheduleStrategyRepository.findAll(spec);
                    //申报负荷表
                    List<DemandRespStrategyNo> strategyNoList = new ArrayList<>();
                    //找到自动参加需求响应的策略信息
                    List<DemandRespStrategy> sList = new ArrayList<>();
                    if (strategyList != null && strategyList.size() > 0) {
                        strategyList.forEach(d -> {
                            DemandRespStrategy strategy = new DemandRespStrategy();
                            strategy.setRespTask(dr);
                            strategy.setScheduleStrategy(d);
                            strategy.setSId("admin" + dr.getRespId() + d.getStrategyId());
                            strategy.setCreateBy(d.getUserId());
                            sList.add(strategy);
                            //选择的运行策略，各节点的设备的负荷值
                            Map<Node, List<Device>> nodeListMap = d.getDeviceList().stream().
                                    collect(Collectors.groupingBy(Device::getNode));
                            for (Node node : nodeListMap.keySet()) {
                                double declareLoad = 0.00;//申报负荷
                                DemandRespStrategyNo strategyNo = new DemandRespStrategyNo();
                                strategyNo.setDrsId(strategy.getSId() + node.getNoHouseholds());//申报负荷id
                                strategyNo.setNoHouseholds(node.getNoHouseholds());//户号
                                if (d.isStrategyStatus()) {
                                    declareLoad = nodeListMap.get(node).stream().mapToDouble(Device::getDeviceRatedPower).sum();
                                }
                                strategyNo.setDeclareLoad(declareLoad);//申报负荷
                                strategyNo.setDemandRespStrategy(strategy);
                                strategyNo.setDrsStatus(21);//已申报
                                strategyNo.setNodeName(node.getNodeName());//节点名称
                                strategyNo.setNodeId(node.getNodeId());//节点id
                                strategyNo.setIsPlatform(2);//非第三方平台
                                strategyNo.setRespId(dr.getRespId());
                                strategyNoList.add(strategyNo);
                            }
                        });
                    }
                    //策略信息入库
                    if (sList != null && sList.size() > 0) {
                        respStrategyRepository.saveAll(sList);
                    }
                    //自动申报负荷信息入库
                    if (strategyNoList != null && strategyNoList.size() > 0) {
                        noRepository.saveAll(strategyNoList);
                    }
                    SysJob sysJob = new SysJob();
                    sysJob.setJobName("需求响应任务" + dr.getTaskCode());
                    sysJob.setJobGroup("需求响应");
                    SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                    dateSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
                    timeSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    String[] rsTimeArr = timeSdf.format(dr.getRsTime()).split(":");
                    String[] rsDateArr = dateSdf.format(dr.getRsDate()).split("-");
                    sysJob.setCronExpression("0 " + Integer.valueOf(rsTimeArr[1]).intValue() + " "
                            + Integer.valueOf(rsTimeArr[0]).intValue() + " "
                            + Integer.valueOf(rsDateArr[2]).intValue() + " " +
                            Integer.valueOf(rsDateArr[1]).intValue() + " ? " + Integer.valueOf(rsDateArr[0]).intValue());
                    sysJob.setInvokeTarget("demandRespTask.initRespTask('" + dr.getRespId() + "')");
                    sysJob.setMisfirePolicy("3");//计划执行错误策略（1立即执行 2执行一次 3放弃执行）
                    sysJob.setConcurrent("1");//0允许 1禁止
                    sysJob.setCreateBy("admin");
                    sysJob = sysJobService.insertJobDemand(sysJob);
                    sysJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());//状态（0正常 1暂停）
                    sysJobService.changeStatus(sysJob);
                    dr.setJobId(sysJob.getJobId());
                    dr.setCreateBy("admin");//保存创建者
                }else{
                    dr.setDStatus(0);
                    dr.setCreateBy("admin_delete");//保存创建者
                }
                demandRespTaskRepository.save(dr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("mqtt需求响应任务异常 +" + e.getMessage());
        }
        return ResponseResult.success();
    }
    @ApiOperation("查询任务列表")
    @UserLoginToken
    @RequestMapping(value = "/getTaskList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getTaskList(@RequestBody DemandModel model) {
        try {
            //更新已过期的响应任务
            demandRespTaskRepository.updateExpiredTasks();
            List<Integer> statusList = new ArrayList<>();
            statusList.add(1);
            statusList.add(4);
            Specification<DemandRespTask> spec = new Specification<DemandRespTask>() {
                @Override
                public Predicate toPredicate(Root<DemandRespTask> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.in(root.get("dStatus")).value(statusList));//查询状态非 删除的数据
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    Order order = cb.desc(root.get("taskCode"));
                    //纯日期不好排序，用任务编号来判断即可
                    if (model.getRsTimeSort() != null) {
                        if (model.getRsTimeSort() == 1) {
                            order = cb.asc(root.get("taskCode"));
                        } else {
                            order = cb.desc(root.get("taskCode"));
                        }
                    }
                    if (model.getFeedbackTimeSort() != null) {
                        if (model.getFeedbackTimeSort() == 1) {
                            order = cb.asc(root.get("feedbackTime"));
                        } else {
                            order = cb.desc(root.get("feedbackTime"));
                        }
                    }
                    criteriaQuery.orderBy(order); //
                    return criteriaQuery.getRestriction();
                }
            };
            //当前页为第几页 默认 1开始
            int page = model.getNumber();
            int size = model.getPageSize();
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<DemandRespTask> datas = demandRespTaskRepository.findAll(spec, pageable);
            PageModel pageModel = new PageModel();
            //封装到pageUtil
            if (datas.getContent() != null && datas.getContent().size() > 0) {
                List<DemandRespTaskRespModel> list = new ArrayList<>();
                datas.getContent().forEach(d -> {
                    DemandRespTaskRespModel newModel = new DemandRespTaskRespModel();
                    BeanUtils.copyProperties(d, newModel);
                    list.add(newModel);
                });
                pageModel.setContent(list);
            } else {
                pageModel.setContent(datas.getContent());
            }
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);
            return ResponseResult.success(pageModel);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.error("查询为空");
        }
    }
    @ApiOperation("录入任务")
    @UserLoginToken
    @RequestMapping(value = "/addTask", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult addTask(@RequestBody @Valid DemandRespTaskReq respTaskReq) {
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
        //判断是否是南网
//        if (respTaskReq.getInviteRange() != null) {
        if (user.getPowerGrid() == 2) {
            if (!TimeUtil.isLegalDate(respTaskReq.getRsDate().length(), respTaskReq.getRsDate(), "yyyy-MM-dd")) {
                return ResponseResult.error("响应日期格式不正确");
            }
            if (!TimeUtil.isLegalDate(respTaskReq.getRsTime().length(), respTaskReq.getRsTime(), "yyyy-MM-dd HH:mm")) {
                return ResponseResult.error("响应开始时段格式不正确");
            }
            if (!TimeUtil.isLegalDate(respTaskReq.getReTime().length(), respTaskReq.getReTime(), "yyyy-MM-dd HH:mm")) {
                return ResponseResult.error("响应结束时段格式不正确");
            }
            DemandRespTask dr = new DemandRespTask();
            Date date = new Date();
            Date rsdate = TimeUtil.strDDToDate(respTaskReq.getRsTime()+ ":00","yyyy-MM-dd HH:mm:00");
            if (rsdate.before(date)) {
                return ResponseResult.error("响应开始时段不能小于当前时间");
            }
            dr.setRespId(date.getTime() + "");
            dr.setRsTime(rsdate);
            dr.setReTime(TimeUtil.strDDToDate(respTaskReq.getReTime()+":00", "yyyy-MM-dd HH:mm:00"));
            dr.setRsDate(TimeUtil.strDDToDate(respTaskReq.getRsDate(), "yyyy-MM-dd"));
            dr.setTaskCode(respTaskReq.getTaskCode());
            dr.setRespLoad(respTaskReq.getRespLoad());
            dr.setRespType(respTaskReq.getRespType());
            dr.setRespSubsidy(respTaskReq.getRespSubsidy());
            dr.setPowerGrid(user.getPowerGrid());
            dr.setDStatus(1);
            dr.setRespLevel(respTaskReq.getRespLevel());//响应级别
            if (StringUtils.isNotEmpty(respTaskReq.getFeedbackTime())) {//反馈日期
                dr.setFeedbackTime(TimeUtil.strToDateFormatYMDHMS(respTaskReq.getFeedbackTime()));
            }
            dr.setCreateBy(userId);//保存创建者
            //判断该用户角色key类型为 1(系统管理员) 2(普通管理员)
            boolean role;
            if (user.getRole().getRoleId().equals("1")) {
                role = true;
            } else {
                role = false;
            }
            Specification<ScheduleStrategy> spec = new Specification<ScheduleStrategy>() {
                @Override
                public Predicate toPredicate(Root<ScheduleStrategy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
//                    if (!role) {
//                        predicates.add(cb.equal(root.get("userId"), userId));
//                    }
                    predicates.add(cb.equal(root.get("userId"), userId));
                    predicates.add(cb.equal(root.get("isDemandResponse"), true));//查询自动参加需求响应
                    predicates.add(cb.equal(root.get("isStrategyStatus"), true));//查询策略状态为开启状态
                    if (user.getPowerGrid() == 1) {
                        predicates.add(cb.equal(root.get("powerGrid"), user.getPowerGrid()));//查询对应电网的策略
                    }
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    return criteriaQuery.getRestriction();
                }
            };
            //根据userId，查找创建的策略
            List<ScheduleStrategy> strategyList = scheduleStrategyRepository.findAll(spec);
            //申报负荷表
            List<DemandRespStrategyNo> strategyNoList = new ArrayList<>();
            //找到自动参加需求响应的策略信息
            List<DemandRespStrategy> sList = new ArrayList<>();
            if (strategyList != null && strategyList.size() > 0) {
                strategyList.forEach(d -> {
                    DemandRespStrategy strategy = new DemandRespStrategy();
                    strategy.setRespTask(dr);
                    strategy.setScheduleStrategy(d);
                    strategy.setSId(userId + dr.getRespId() + d.getStrategyId());
                    strategy.setCreateBy(d.getUserId());
                    sList.add(strategy);
                    logger.info("选择的运行策略，各节点的设备的负荷值");
                    //选择的运行策略，各节点的设备的负荷值
                    Map<Node, List<Device>> nodeListMap = d.getDeviceList().stream().
                            collect(Collectors.groupingBy(Device::getNode));
                    List<String> inviteRangeList = new ArrayList<>();
                    if(StringUtils.isNotBlank(respTaskReq.getInviteRange())) {
                        inviteRangeList = Arrays.asList(respTaskReq.getInviteRange().split(","));
                    }
                    for (Node node : nodeListMap.keySet()) {
                        if (inviteRangeList.contains(node.getNoHouseholds())) {
                            double declareLoad = 0.00;//申报负荷
                            DemandRespStrategyNo strategyNo = new DemandRespStrategyNo();
                            strategyNo.setDrsId(strategy.getSId() + node.getNoHouseholds());//申报负荷id
                            strategyNo.setNoHouseholds(node.getNoHouseholds());//户号
                            if (d.isStrategyStatus()) {
                                declareLoad = nodeListMap.get(node).stream().mapToDouble(Device::getDeviceRatedPower).sum();
                            }
                            strategyNo.setDeclareLoad(declareLoad);//申报负荷
                            strategyNo.setDemandRespStrategy(strategy);
                            strategyNo.setDrsStatus(11);//原已申报改为未申报，创建任务状态目前应该是未申报
                            strategyNo.setNodeName(node.getNodeName());//节点名称
                            strategyNo.setNodeId(node.getNodeId());//节点id
                            strategyNo.setIsPlatform(2);//非第三方平台
                            strategyNo.setRespId(dr.getRespId());
                            strategyNoList.add(strategyNo);
                        }
                    }
                });
            }
//            策略信息入库
            if (sList != null && sList.size() > 0) {
                respStrategyRepository.saveAll(sList);
            }
//            自动申报负荷信息入库
            if (strategyNoList != null && strategyNoList.size() > 0) {
                logger.info("开始保存数据");
                noRepository.saveAll(strategyNoList);
            }
            logger.info("==========保存demand_resp_task数据===============");
            demandRespTaskRepository.save(dr);
            updateNodeInfo(respTaskReq,dr,"add");
        } else {
            if (!TimeUtil.isLegalDate(respTaskReq.getRsDate().length(), respTaskReq.getRsDate(), "yyyy-MM-dd")) {
                return ResponseResult.error("响应日期格式不正确");
            }
            if (!TimeUtil.isLegalDate(respTaskReq.getRsTime().length(), respTaskReq.getRsTime(), "yyyy-MM-dd HH:mm")) {
                return ResponseResult.error("响应开始时段格式不正确");
            }
            if (!TimeUtil.isLegalDate(respTaskReq.getReTime().length(), respTaskReq.getReTime(), "yyyy-MM-dd HH:mm")) {
                return ResponseResult.error("响应结束时段格式不正确");
            }
            DemandRespTask dr = new DemandRespTask();
            Date date = new Date();
            Date rsdate = TimeUtil.strDDToDate(respTaskReq.getRsTime()+ ":00","yyyy-MM-dd HH:mm:00");
            if (rsdate.before(date)) {
                return ResponseResult.error("响应开始时段不能小于当前时间");
            }
            dr.setRespId(date.getTime() + "");
            dr.setRsTime(rsdate);
            dr.setReTime(TimeUtil.strDDToDate(respTaskReq.getReTime()+":00", "yyyy-MM-dd HH:mm:00"));
            dr.setRsDate(TimeUtil.strDDToDate(respTaskReq.getRsDate(), "yyyy-MM-dd"));
            dr.setTaskCode(respTaskReq.getTaskCode());
            dr.setRespLoad(respTaskReq.getRespLoad());
            dr.setRespType(respTaskReq.getRespType());
            dr.setRespSubsidy(respTaskReq.getRespSubsidy());
            dr.setPowerGrid(user.getPowerGrid());
            dr.setDStatus(1);
            dr.setRespLevel(respTaskReq.getRespLevel());//响应级别
            if (StringUtils.isNotEmpty(respTaskReq.getFeedbackTime())) {//反馈日期
                dr.setFeedbackTime(TimeUtil.strToDateFormatYMDHMS(respTaskReq.getFeedbackTime()));
            }
            dr.setCreateBy(userId);//保存创建者
            //判断该用户角色key类型为 1(系统管理员) 2(普通管理员)
            boolean role = userService.isManger();
            Specification<ScheduleStrategy> spec = new Specification<ScheduleStrategy>() {
                @Override
                public Predicate toPredicate(Root<ScheduleStrategy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
//                    if (!role) {
//                        predicates.add(cb.equal(root.get("userId"), userId));
//                    }
                    predicates.add(cb.equal(root.get("userId"), userId));
                    predicates.add(cb.equal(root.get("isDemandResponse"), true));//查询自动参加需求响应
                    predicates.add(cb.equal(root.get("isStrategyStatus"), true));//查询策略状态为开启状态
//                    predicates.add(cb.in(root.get("deviceList")).value(deviceIds));//查询相关节点
                    if (user.getPowerGrid() == 1) {
                        predicates.add(cb.equal(root.get("powerGrid"), user.getPowerGrid()));//查询对应电网的策略
                    }
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    return criteriaQuery.getRestriction();
                }
            };
            logger.info("根据useid，查找创建的策略");
            //根据userId和电网类型，查找创建的策略
            List<ScheduleStrategy> strategyList = scheduleStrategyRepository.findAll(spec);
            //删除不对应的设备
            for (ScheduleStrategy scheduleStrategy : strategyList) {
                scheduleStrategy.getDeviceList();
                //
            }
            logger.info("根据useid，查找创建的策略结束");
            //申报负荷表
            List<DemandRespStrategyNo> strategyNoList = new ArrayList<>();
            //找到自动参加需求响应的策略信息
            List<DemandRespStrategy> sList = new ArrayList<>();
            if (strategyList != null && strategyList.size() > 0) {
                logger.info("===========找到相关策略===========");
                strategyList.forEach(d -> {
                    DemandRespStrategy strategy = new DemandRespStrategy();
                    strategy.setRespTask(dr);
                    strategy.setScheduleStrategy(d);
                    strategy.setSId(userId + dr.getRespId() + d.getStrategyId());
                    strategy.setCreateBy(d.getUserId());
                    sList.add(strategy);
                    logger.info("选择的运行策略，各节点的设备的负荷值");
                    //选择的运行策略，各节点的设备的负荷值
                    Map<Node, List<Device>> nodeListMap = d.getDeviceList().stream().
                            collect(Collectors.groupingBy(Device::getNode));
                    //现在这个node不符合条件
                    List<String> inviteRangeList = new ArrayList<>();
                    if(StringUtils.isNotBlank(respTaskReq.getInviteRange())) {
                        inviteRangeList = Arrays.asList(respTaskReq.getInviteRange().split(","));
                    }
                    for (Node node : nodeListMap.keySet()) {
                        if (inviteRangeList.contains(node.getNoHouseholds())) {
                            double declareLoad = 0.00;//申报负荷
                            DemandRespStrategyNo strategyNo = new DemandRespStrategyNo();
                            strategyNo.setDrsId(strategy.getSId() + node.getNoHouseholds());//申报负荷id
                            strategyNo.setNoHouseholds(node.getNoHouseholds());//户号
                            if (d.isStrategyStatus()) {
                                declareLoad = nodeListMap.get(node).stream().mapToDouble(Device::getDeviceRatedPower).sum();
                            }
                            strategyNo.setDeclareLoad(declareLoad);//申报负荷
                            strategyNo.setDemandRespStrategy(strategy);
                            strategyNo.setDrsStatus(11);//原已申报改为未申报，创建任务状态目前应该是未申报
                            strategyNo.setNodeName(node.getNodeName());//节点名称
                            strategyNo.setNodeId(node.getNodeId());//节点id
                            strategyNo.setIsPlatform(2);//非第三方平台
                            strategyNo.setRespId(dr.getRespId());
                            strategyNoList.add(strategyNo);
                            logger.info(strategyNo.getDrsId());
                        }
                    }
                });
            }
//            策略信息入库
            if (sList != null && sList.size() > 0) {
                respStrategyRepository.saveAll(sList);
            }
//            自动申报负荷信息入库
            if (strategyNoList != null && strategyNoList.size() > 0) {
                logger.info("开始保存数据");
                noRepository.saveAll(strategyNoList);
            }
            logger.info("==========保存demand_resp_task数据===============");
            demandRespTaskRepository.save(dr);
            updateNodeInfo(respTaskReq,dr,"add");
        }
            //策略信息入库
        //todo,这里暂时注释，发现会创建两种策略，一种普通用户前缀的一种admin前缀的
//            if (sList != null && sList.size() > 0) {
//                respStrategyRepository.saveAll(sList);
//            }
            //自动申报负荷信息入库
        //todo,这里暂时注释，发现会创建两种任务id，一种普通用户前缀的一种admin前缀的
//            if (strategyNoList != null && strategyNoList.size() > 0) {
//                logger.info("开始保存数据了");
//                noRepository.saveAll(strategyNoList);
//            }
//            SysJob sysJob = new SysJob();
//            sysJob.setJobName("需求响应任务" + respTaskReq.getTaskCode());
//            sysJob.setJobGroup("需求响应");
//            String[] rsTimeArr = respTaskReq.getRsTime().split(" ")[1].split(":");
//            String[] rsDateArr = respTaskReq.getRsDate().split("-");
//            sysJob.setCronExpression("0 " + Integer.valueOf(rsTimeArr[1]).intValue() + " "
//                    + Integer.valueOf(rsTimeArr[0]).intValue() + " "
//                    + Integer.valueOf(rsDateArr[2]).intValue() + " " +
//                    Integer.valueOf(rsDateArr[1]).intValue() + " ? " + Integer.valueOf(rsDateArr[0]).intValue());
//            sysJob.setInvokeTarget("demandRespTask.initRespTask('" + dr.getRespId() + "')");
//            sysJob.setMisfirePolicy("3");//计划执行错误策略（1立即执行 2执行一次 3放弃执行）
//            sysJob.setConcurrent("1");//0允许 1禁止
//            sysJob.setCreateBy(userId);
//            sysJob = sysJobService.insertJobDemand(sysJob);
//            sysJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());//状态（0正常 1暂停）
//            sysJobService.changeStatus(sysJob);
//            dr.setJobId(sysJob.getJobId());
//        logger.info("==========保存demand_resp_task数据===============");
//            demandRespTaskRepository.save(dr);
            //修改响应任务节点相关信息 add by maoyating 20240312
//            updateNodeInfo(respTaskReq,dr,"add");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseResult.error("录入需求响应任务异常 +" + e.getMessage());
//        }
        return ResponseResult.success();
    }
    /**
     * 修改响应任务节点相关信息
     * add by maoyating 20240312
     * @param respTaskReq
     * @param dr
     * @param opt add-新增 edit-修改
     */
    @Value("${suanfaOptimizationApi}")
    private String suanfaOptimizationApi;
    private void updateNodeInfo(DemandRespTaskReq respTaskReq,DemandRespTask dr,String opt){
        List<DemandRespStrategyModel> strategyModelList = new ArrayList<>();
        //第三方的运行策略，各节点的负荷值
        List<DemandRespStrategyNoModel> strategyNoModelList = new ArrayList<>();
        //符合邀约调节的用户编号resourceId  add by maoyating  20240312
        if(StringUtils.isNotBlank(respTaskReq.getInviteRange())){
            List<String> inviteRange = Arrays.asList(respTaskReq.getInviteRange().split(","));
            //得到本次新增的户号列表
            List<String> addList = inviteRange;
            //筛出不同户号的信息
            if(opt.equals("edit")){
                //得到目前库里的节点
                List<DemandRespStrategyNo> all = noRepository.findNoListByRespId(dr.getRespId());
                List<String> oldList = all.stream()
                        .map(DemandRespStrategyNo::getNoHouseholds)
                        .collect(Collectors.toList());
                //得到本次新增的户号列表
                addList=new ArrayList<>();
                addList = getDifference(inviteRange, oldList);
                //得到本次该删掉的户号列表
                List<String> delList = getDifference(oldList, inviteRange);
                if(delList!=null&&delList.size()>0){
                    //判断要删掉的户号是否为已申报，若已申报，直接给出提示
                    //删除本次需要删除的户号
                    noRepository.deleteByDrsIds(dr.getRespId(),delList);
                }
            }
            Specification<Node> spec1 = new Specification<Node>() {
                @Override
                public Predicate toPredicate(Root<Node> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.in(root.get("noHouseholds")).value(inviteRange));
                    return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };
            List<Node> nodeList = nodeRepository.findAll(spec1);
            Map<String, Node> nodeMap = nodeList.stream().collect(Collectors.toMap(Node::getNoHouseholds, n -> n));
            //后续可能只考虑在线节点生成策略
            List<Node> nodeOnlineList = nodeList.stream().filter(Node::getOnline).collect(Collectors.toList());
            String sId = "admin"+"_" + dr.getRespId() +"_"+ "getInvitation";
            DemandRespStrategyModel drsModel = new DemandRespStrategyModel();
            drsModel.setSId(sId);
            drsModel.setStrategyId("zhinengtuijian");
            drsModel.setCreateBy("admin");
            drsModel.setRespId(dr.getRespId());
            strategyModelList.add(drsModel);
            for (String noHouseholds : addList) {
                //判断运行策略是自动参加响应任务，还是手动;如果是自动，需要自动进行申报
                int status = 11;//未申报
                DemandRespStrategyNoModel strategyNo = new DemandRespStrategyNoModel();
                strategyNo.setDrsId(sId +"_"+ noHouseholds);//申报负荷id
                strategyNo.setNoHouseholds(noHouseholds);//户号
                Node node = nodeMap.get(noHouseholds);
                if (node != null) {
                    strategyNo.setNodeName(node.getNodeName());//节点名称
                    strategyNo.setNodeId(node.getNodeId());//节点id
                }
                if(dr.getRespLoad()!=null){
                    strategyNo.setDeclareLoad(dr.getRespLoad()/inviteRange.size());//todo 实际需要发给一斐进行计算得出
                }
                strategyNo.setSId(sId);
                strategyNo.setDrsStatus(status);
                strategyNo.setIsPlatform(1);//第三方平台
                strategyNo.setRespId(dr.getRespId());
                strategyNoModelList.add(strategyNo);
            }
            if(strategyModelList!=null && strategyModelList.size()>0){
                respStrategyService.batchInsert(strategyModelList);
            }
            //申报负荷信息入库
            if (strategyNoModelList != null && strategyNoModelList.size() > 0) {
                noService.batchInsert(strategyNoModelList);
            }
            Specification<AiLoadForecasting> spec = (root, criteriaQuery, cb) -> {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(root.get("nodeId").in(nodeList));
                predicates.add(cb.between(root.get("createdTime"), respTaskReq.getRsTime(), respTaskReq.getReTime()));
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.asc(root.get("createdTime"))); //按照createdTime升序排列
                return criteriaQuery.getRestriction();
            };
            if (nodeList.size() > 0) {
                nodeList.forEach(v -> {
                    List<IotTsKv> iotTsKvs = iotTsKvRepository.findAllForAlgorithm(v.getNodeId(),dr.getRsTime(),dr.getReTime());
                    Double forecastLoad = iotTsKvs.stream().filter(v1 -> v1.getPointName().equals("预定负荷") && v1.getPointValue() != null && !("-").equals(v1.getPointValue()))
                            .mapToDouble(vc -> Double.parseDouble(vc.getPointValue())).average().orElse(0);
//                    Double commandValue = forecastLoad - dr.getRespLoad() / nodeList.size() * 1.15;
                    Double commandValue = dr.getRespLoad() / nodeList.size() * 1.15;
                    logger.info("forecastLoad:{},commandValue:{},nodeList.size():{},nodeList:{}",forecastLoad,commandValue,nodeList.size(),nodeList);
                    InitVo initVo = new InitVo();
                    initVo.setWeather(
                            iotTsKvs.stream().filter(vw -> vw.getPointName().equals("温度"))
                                    .mapToDouble(vwe -> Double.parseDouble(vwe.getPointValue())).average().orElse(0)
                    );
                    initVo.setFixed_load(
                            iotTsKvs.stream().filter(vf -> vf.getPointName().equals("固定负荷"))
                                    .mapToDouble(vfi -> Double.parseDouble(vfi.getPointValue())).average().orElse(0)
                    );
                    initVo.setCh_water_outlet_temperature(
                            iotTsKvs.stream().filter(vt -> vt.getPointName().equals("出水温度"))
                                    .mapToDouble(vte -> Double.parseDouble(vte.getPointValue())).average().orElse(0)
                    );
                    Date date = new Date();
                    String id = dr.getRespId() + "_" + v.getNodeId() + "_" + date.getTime();
                    DemandStrategy strategy = new DemandStrategy();
                    strategy.setId(id);
                    strategy.setRespId(dr.getRespId());
                    strategy.setNodeId(v.getNodeId());
                    strategy.setSystemId("kongtiao");
                    strategy.setTimePoint(new Date(dr.getRsTime().getTime() - (900 * 1000)));
                    strategy.setLlm(true);
                    strategy.setState(3);
                    strategy.setNodeName(v.getNodeName());
                    strategy.setCommandValue(String.valueOf(commandValue));
                    strategy.setForecastLoad(String.valueOf(forecastLoad));
                    strategy.setForecastTime(date);
                    strategy.setNoHouseholds(v.getNoHouseholds());
                    //策略内容、预测调节负荷、预测调节后负荷、申报值默认当前（commandValue，算法返回）等待算法返回
                    demandStrategyRepository.save(strategy);
                    DemandAlgorithmVo json =new DemandAlgorithmVo(
                            dr.getRespId(),
                            v.getNodeId(),
                            "请根据以上数据生成一个基于xxx求解器的求解代码, 优化目标为...",
                            v.getNodeName(),
                            commandValue,
                            EnumExample.Weights.getNode(v.getNodeId()),
                            JSON.toJSONString(initVo),
                            Arrays.asList("ch_water_outlet_temperature")
                    );
                    logger.info("创建任务时发送算法进行负荷调节预测:{}",JSON.toJSONString(json));
                    CompletableFuture.runAsync(() -> {
                        try {
                            Thread.sleep(2000);
                            String result= HttpUtil.okHttpPost(suanfaOptimizationApi, JSON.toJSONString(json));
                            logger.info("修改响应任务节点相关信息result:{}",result);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                });
            }
        }
    }
    /**
     * 去掉并集后的list
     * @param list1
     * @param list2
     * @return
     */
    public static List<String> getDifference(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<>(list1);
        result.removeAll(list2);
        return result;
    }
    @ApiOperation("编辑任务")
    @UserLoginToken
    @RequestMapping(value = "/editTask", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editTask(@RequestBody @Valid DemandRespTaskReq respTaskReq) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");
        try {
            //判断id是否为空
            if (StringUtils.isBlank(respTaskReq.getRespId())) {
                return ResponseResult.error("id不能为空");
            }
            Date date = new Date();
            if (!TimeUtil.isLegalDate(respTaskReq.getRsDate().length(), respTaskReq.getRsDate(), "yyyy-MM-dd")) {
                return ResponseResult.error("响应日期格式不正确");
            }
            if (!TimeUtil.isLegalDate(respTaskReq.getRsTime().length(), respTaskReq.getRsTime(), "yyyy-MM-dd HH:mm")) {
                return ResponseResult.error("响应开始时段格式不正确");
            }
            if (!TimeUtil.isLegalDate(respTaskReq.getReTime().length(), respTaskReq.getReTime(), "yyyy-MM-dd HH:mm")) {
                return ResponseResult.error("响应结束时段格式不正确");
            }
            Date rsdate = TimeUtil.strDDToDate(respTaskReq.getRsTime() + ":00", "yyyy-MM-dd HH:mm:ss");
            if (rsdate.before(date)) {
                return ResponseResult.error("响应开始时段不能小于当前时间");
            }
            Optional<DemandRespTask> task = demandRespTaskRepository.findById(respTaskReq.getRespId());
            if (!task.isPresent()) {
                return ResponseResult.error("该需求响应不存在");
            }
            DemandRespTask dr = task.get();
            dr.setRsTime(rsdate);
            dr.setReTime(TimeUtil.strDDToDate(respTaskReq.getReTime()+":00", "yyyy-MM-dd HH:mm:00"));
            dr.setRsDate(TimeUtil.strDDToDate(respTaskReq.getRsDate(), "yyyy-MM-dd"));
            dr.setTaskCode(respTaskReq.getTaskCode());
            dr.setRespLoad(respTaskReq.getRespLoad());
            dr.setRespType(respTaskReq.getRespType());
            dr.setRespSubsidy(respTaskReq.getRespSubsidy());
            dr.setRespLevel(respTaskReq.getRespLevel());//响应级别
            if (StringUtils.isNotEmpty(respTaskReq.getFeedbackTime())) {//反馈日期
                dr.setFeedbackTime(TimeUtil.strToDateFormatYMDHMS(respTaskReq.getFeedbackTime()));
            }
            dr.setUpdateBy(userId);//保存创建者
            dr.setUpdateTime(date);
            demandRespTaskRepository.save(dr);
            demandStrategyRepository.updateStateByIds(respTaskReq.getRespId());
            //修改响应任务节点相关信息 add by maoyating 20240312
            updateNodeInfo(respTaskReq,dr,"edit");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("编辑需求响应任务异常 +" + e.getMessage());
        }
        return ResponseResult.success();
    }
    @ApiOperation("编辑任务价格--南网")
    @UserLoginToken
    @RequestMapping(value = "/editTaskPrice", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editTaskPrice(@RequestBody @Valid DemandRespTaskReq respTaskReq) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");
        try {
            //判断id是否为空
            if (StringUtils.isBlank(respTaskReq.getRespId())) {
                return ResponseResult.error("id不能为空");
            }
            Optional<DemandRespTask> task = demandRespTaskRepository.findById(respTaskReq.getRespId());
            if (!task.isPresent()) {
                return ResponseResult.error("该需求响应不存在");
            }
            DemandRespTask dr = task.get();
            Long count = noRepository.countNoListByRespIdBid(dr.getRespId(),4);
            if(count!=null && count>0){
                return ResponseResult.error("该需求响应已申报节点，不能手动修改价格");
            }
            dr.setRespSubsidy(respTaskReq.getRespSubsidy());
            dr.setUpdateBy(userId);//保存创建者
            dr.setUpdateTime(new Date());
            demandRespTaskRepository.save(dr);
            noRepository.updateDeclarePrice(respTaskReq.getRespId(),respTaskReq.getRespSubsidy());
            priceRepository.updatePrice(respTaskReq.getRespId(),respTaskReq.getRespSubsidy());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("编辑需求响应任务异常 +" + e.getMessage());
        }
        return ResponseResult.success();
    }
    @ApiOperation("删除任务")
    @UserLoginToken
    @RequestMapping(value = "/delTask", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult delTask(@RequestParam("respId") String respId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");
        try {
            //判断id是否为空
            if (StringUtils.isBlank(respId)) {
                return ResponseResult.error("id不能为空");
            }
            Optional<DemandRespTask> task = demandRespTaskRepository.findById(respId);
            if (!task.isPresent()) {
                return ResponseResult.error("该需求响应不存在");
            }
            DemandRespTask dr = task.get();
            Date date = new Date();
            dr.setUpdateBy(userId);//保存创建者
            dr.setUpdateTime(date);
            dr.setDStatus(0);//0-删除
            if (dr.getJobId() != null) {
                //直接删除定时任务
                Optional<SysJob> old = sysJobRepository.findById(dr.getJobId());
                SysJob oldJob = old.get();
                sysJobService.deleteJob(oldJob);
            }
            demandRespTaskRepository.save(dr);
            //todo 福建的需求，删除任务，需反馈需求响应false
        } catch (Exception e) {
            return ResponseResult.error("删除需求响应任务异常 +" + e.getMessage());
        }
        return ResponseResult.success();
    }
    @ApiOperation("废弃--搜索设备列表")
    @UserLoginToken
    @RequestMapping(value = "/getDeviceListByName", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getDeviceListByName(@RequestBody DemandStrategyModel model) {
        //同步最新设备策略信息
        //synDevice(model.getRespId());
        Specification<DemandRespStrategy> spec = new Specification<DemandRespStrategy>() {
            @Override
            public Predicate toPredicate(Root<DemandRespStrategy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (StringUtils.isNotBlank(model.getDeviceName())) {
                    predicates.add(cb.like(root.get("deviceName"), "%" + model.getDeviceName() + "%"));//查询状态为正常
                }
                predicates.add(cb.equal(root.get("respTask").get("respId"), model.getRespId()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        //当前页为第几页 默认 1开始
        int page = model.getNumber();
        int size = model.getPageSize();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DemandRespStrategy> datas = respStrategyRepository.findAll(spec, pageable);
        PageModel pageModel = new PageModel();
        //封装到pageUtil
        if (datas.getContent() != null && datas.getContent().size() > 0) {
            List<DemandRespStrategyReq> list = new ArrayList<>();
            datas.getContent().forEach(d -> {
                DemandRespStrategyReq newModel = new DemandRespStrategyReq();
                BeanUtils.copyProperties(d, newModel);
                list.add(newModel);
            });
            pageModel.setContent(list);
        } else {
            pageModel.setContent(datas.getContent());
        }
        //封装到pageUtil
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);
        return ResponseResult.success(pageModel);
    }
    @ApiOperation("查询该用户具有权限的策略名称")
    @UserLoginToken
    @RequestMapping(value = "/getStrategyList", method = {RequestMethod.POST})
    public ResponseResult<Map> getStrategyList(@RequestParam("respId") String respId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");
        Specification<DemandRespStrategy> respSpec = new Specification<DemandRespStrategy>() {
            @Override
            public Predicate toPredicate(Root<DemandRespStrategy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("createBy"), userId));
                predicates.add(cb.equal(root.get("respTask").get("respId"), respId));
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        List<DemandRespStrategy> respStrategyList = respStrategyRepository.findAll(respSpec);
        String oldStrategyId = "";
        if (respStrategyList != null && respStrategyList.size() > 0) {
            oldStrategyId = respStrategyList.get(0).getScheduleStrategy().getStrategyId();
        }
        //判断该用户角色key类型为 1(系统管理员) 2(普通管理员)
        boolean role = userService.isManger();
        Specification<ScheduleStrategy> spec = new Specification<ScheduleStrategy>() {
            @Override
            public Predicate toPredicate(Root<ScheduleStrategy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (!role) {
                    predicates.add(cb.equal(root.get("userId"), userId));
                }
                predicates.add(cb.equal(root.get("isStrategyStatus"), true));//查询策略状态为开启状态
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        //根据userId，查找创建的策略
        List<ScheduleStrategy> strategyList = scheduleStrategyRepository.findAll(spec);
        //所有的策略信息
        Map<String, Object> respMap = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        if (strategyList != null && strategyList.size() > 0) {
            strategyList.forEach(d -> {
                map.put(d.getStrategyId(), d.getStrategyName());
            });
        }
        respMap.put("oldStrategyId", oldStrategyId);
        respMap.put("strategyList", map);
        return ResponseResult.success(respMap);
    }
    @ApiOperation("根据可调负荷运行策略id获取设备列表")
    @UserLoginToken
    @RequestMapping(value = "/getDeviceListById", method = {RequestMethod.POST})
    public ResponseResult<Map> getDeviceListById(@RequestBody @Valid DemandStrategyModel model) {
        //获取所有的设备
        Specification<ScheduleStrategyDevice> specDevice = new Specification<ScheduleStrategyDevice>() {
            @Override
            public Predicate toPredicate(Root<ScheduleStrategyDevice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("strategyId"), model.getStrategyId()));
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        List<ScheduleStrategyDevice> strategyDeviceList = scheduleStrategyDeviceRepository.findAll(specDevice);
        if (strategyDeviceList != null && strategyDeviceList.size() > 0) {
            ScheduleStrategy scheduleStrategy = scheduleStrategyRepository.getOne(model.getStrategyId());
            List<String> deviceIdList = new ArrayList<>();
            strategyDeviceList.forEach(s -> {
                deviceIdList.add(s.getDeviceId());
            });
            Specification<Device> spec = new Specification<Device>() {
                @Override
                public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    if (StringUtils.isNotBlank(model.getDeviceName())) {
                        predicates.add(cb.like(root.get("deviceName"), "%" + model.getDeviceName() + "%"));
                    }
                    predicates.add(cb.in(root.get("deviceId")).value(deviceIdList));
                    Order order = cb.desc(root.get("deviceRatedPower"));
                    if (model.getDeviceRatedPowerSort() != null) {
                        if (model.getDeviceRatedPowerSort() == 1) {
                            order = cb.asc(root.get("deviceRatedPower"));
                        } else {
                            order = cb.desc(root.get("deviceRatedPower"));
                        }
                    }
                    if (model.getDeviceStatusSort() != null) {
                        if (model.getDeviceStatusSort() == 1) {
                            order = cb.asc(root.get("scheduleStrategy").get("status"));
                        } else {
                            order = cb.desc(root.get("scheduleStrategy").get("status"));
                        }
                    }
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    criteriaQuery.orderBy(order); //
                    return criteriaQuery.getRestriction();
                }
            };
            //当前页为第几页 默认 1开始
            int page = model.getNumber();
            int size = model.getPageSize();
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Device> datas = deviceRepository.findAll(spec, pageable);
            PageModel pageModel = new PageModel();
            Double totalDeviceRatedPower = 0.00;//总额定负荷
            Double totalActualLoad = 0.00;//总实际负荷
            //封装到pageUtil
            if (datas.getContent() != null && datas.getContent().size() > 0) {
                List<DemandRespStrategyReq> list = new ArrayList<>();
                datas.getContent().forEach(d -> {
                    DemandRespStrategyReq newModel = new DemandRespStrategyReq();
                    newModel.setNodeId(d.getNode().getNodeId());
                    newModel.setNodeName(d.getNode().getNodeName());//节点名称
                    newModel.setSystemId(d.getSystemType().getSystemId());
                    newModel.setSystemName(d.getSystemType().getSystemName());//系统名称
                    newModel.setDeviceId(d.getDeviceId());
                    newModel.setDeviceName(d.getDeviceName());//设备名称
                    newModel.setDeviceRatedPower(d.getDeviceRatedPower());//额定负荷
                    //如果策略true-开启状态，则实时负荷=额定负荷
//                    if(scheduleStrategy.isStrategyStatus()){
//                        newModel.setActualLoad(d.getDeviceRatedPower());//实时负荷
//                    }else{//如果策略false-关闭状态，则实时负荷=0
//                        newModel.setActualLoad(0.00);
//                    }
                    //设备 离线=关闭； 在线=开启
                    if (d.getOnline()) {
                        newModel.setSStatus(1);//策略状态
                    } else {
                        newModel.setSStatus(2);//策略状态
                    }
                    newModel.setNoHouseholds(d.getNode().getNoHouseholds());//户号
                    newModel.setStrategyId(scheduleStrategy.getStrategyId());//可调负荷运行策略id
                    list.add(newModel);
                });
                pageModel.setContent(list);
                Specification<Device> spec1 = new Specification<Device>() {
                    @Override
                    public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                        List<Predicate> predicates = new ArrayList<>();
                        if (StringUtils.isNotBlank(model.getDeviceName())) {
                            predicates.add(cb.like(root.get("deviceName"), "%" + model.getDeviceName() + "%"));
                        }
                        predicates.add(cb.in(root.get("deviceId")).value(deviceIdList));
                        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                };
                List<Device> deviceList = deviceRepository.findAll(spec1);
                for (Device d : deviceList) {
                    totalDeviceRatedPower += d.getDeviceRatedPower();
                }
                if (scheduleStrategy.isStrategyStatus()) {
                    //如果策略true-开启状态，则实时负荷=额定负荷
                    totalActualLoad = totalDeviceRatedPower;//实时负荷
                } else {
                    totalActualLoad = 0.00;
                }
            } else {
                pageModel.setContent(datas.getContent());
            }
            //封装到pageUtil
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);
            Map<String, Object> map = new HashMap<>();
            map.put("totalDeviceRatedPower", totalDeviceRatedPower);//总额定负荷
            map.put("totalActualLoad", totalActualLoad);//总实际负荷
            map.put("devieInfo", pageModel);//设备分页信息
            return ResponseResult.success(map);
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("totalDeviceRatedPower", 0);//总额定负荷
            map.put("totalActualLoad", 0);//总实际负荷
            map.put("devieInfo", null);//设备分页信息
            return ResponseResult.success(map);
        }
    }
    @ApiOperation("编辑策略")
    @UserLoginToken
    @RequestMapping(value = "/editStrategy", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editStrategy(
            @RequestParam("oldStrategyId") String oldStrategyId,
            @RequestParam("strategyId") String strategyId,
            @RequestParam("respId") String respId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");
        //根据用户id+任务id+原可调负荷运行策略id，进行更新
        try {
            Optional<ScheduleStrategy> scheduleStrategy = scheduleStrategyRepository.findById(strategyId);
            if (!scheduleStrategy.isPresent()) {
                return ResponseResult.error("该可调负荷运行策略不存在");
            }
            ScheduleStrategy s = scheduleStrategy.get();
            DemandRespStrategy dr = null;
            DemandRespTask demandRespTask = demandRespTaskRepository.findByRespId(respId);
            if (StringUtils.isNotEmpty(oldStrategyId)) {
                if (oldStrategyId.equals(strategyId)) {
                    return ResponseResult.error("新选择的策略一样");
                }
                Specification<DemandRespStrategy> spec1 = new Specification<DemandRespStrategy>() {
                    @Override
                    public Predicate toPredicate(Root<DemandRespStrategy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(cb.equal(root.get("scheduleStrategy").get("strategyId"), oldStrategyId));
                        predicates.add(cb.equal(root.get("respTask").get("respId"), respId));
//                        predicates.add(cb.equal(root.get("createBy"),userId ));
                        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                };
                Optional<DemandRespStrategy> demandRespStrategy = respStrategyRepository.findOne(spec1);
                if (!demandRespStrategy.isPresent()) {
                    return ResponseResult.error("该策略不存在");
                }
                dr = demandRespStrategy.get();
                dr.setScheduleStrategy(s);
                respStrategyRepository.save(dr);
            } else {
                if (demandRespTask == null) {
                    return ResponseResult.error("该需求响应不存在");
                }
                dr = new DemandRespStrategy();
                dr.setSId(userId + demandRespTask.getRespId() + s.getStrategyId());
                dr.setScheduleStrategy(s);
                dr.setCreateBy(userId);
                dr.setRespTask(demandRespTask);
                respStrategyRepository.save(dr);
            }
            //选择的运行策略，各节点的设备的负荷值
            List<DemandRespStrategyNo> strategyNoList = new ArrayList<>();
            //判断运行策略是自动参加响应任务，还是手动;如果是自动，需要自动进行申报
            int status = 11;//未申报
            if (s.isDemandResponse()) {
                status = 21;//已申报
            }
            if (s.getDeviceList() != null && s.getDeviceList().size() > 0) {
                Map<Node, List<Device>> nodeListMap = s.getDeviceList().stream().
                        collect(Collectors.groupingBy(Device::getNode));
                for (Node node : nodeListMap.keySet()) {
                    double declareLoad = 0.00;//申报负荷
                    DemandRespStrategyNo strategyNo = new DemandRespStrategyNo();
                    strategyNo.setDrsId(dr.getSId() + node.getNoHouseholds());//申报负荷id
                    strategyNo.setNoHouseholds(node.getNoHouseholds());//户号
                    //如果策略开启状态
                    if (s.isStrategyStatus()) {
                        declareLoad = nodeListMap.get(node).stream().mapToDouble(Device::getDeviceRatedPower).sum();
                        strategyNo.setRealTimeLoad(declareLoad);//实际响应负荷
                    } else {
                        strategyNo.setRealTimeLoad(0.00);//实际响应负荷
                    }
                    strategyNo.setDeclareLoad(declareLoad);//申报负荷
                    strategyNo.setDemandRespStrategy(dr);
                    strategyNo.setDrsStatus(status);//已申报
                    strategyNo.setNodeName(node.getNodeName());//节点名称
                    strategyNo.setNodeId(node.getNodeId());//节点id
                    strategyNo.setIsPlatform(2);//非第三方
                    if(status==21){
                        strategyNo.setWinningBid(3);//未发送给南网
                    }
                    //需要设置价格
                    if(demandRespTask.getRespSubsidy()!=null){
                        strategyNo.setDeclarePrice(demandRespTask.getRespSubsidy());
                    }
                    strategyNo.setRespId(dr.getRespTask().getRespId());
                    strategyNoList.add(strategyNo);
                    //TODO 向福建发送主动申报信息
                }
                //根据节点的申报额定功率，进行占比分配
                Double declare=strategyNoList.stream().mapToDouble(DemandRespStrategyNo::getDeclareLoad).sum();
                if(declare>demandRespTask.getRespLoad()){//如果节点之和的申报额定功率，大于下发的需求响应任务，则按占比进行分配
                    // 如果总申报量小于所有用户申报量的总和
                    double remaining = demandRespTask.getRespLoad();
                    if(remaining<0){
                        remaining=-remaining;
                    }
                    for(DemandRespStrategyNo sn:strategyNoList){
                        double ratio = (sn.getDeclareLoad() / declare);
                        double updateQuantity = (int) (ratio * demandRespTask.getRespLoad());
                        sn.setDeclareLoad(updateQuantity);
                        remaining -= updateQuantity;
                    }
                    // 检查是否所有申报量之和与总申报量相等
                    if (remaining != 0) {
                        // 如果不相等，则调整最后一个用户的申报量，使其与总申报量相等
                        strategyNoList.get(strategyNoList.size() - 1).setDeclareLoad(remaining);
                    }
                }
                noRepository.saveAll(strategyNoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("编辑策略异常 +" + e.getMessage());
        }
        return ResponseResult.success();
    }
    @ApiOperation("查询申报负荷列表")
    @UserLoginToken
    @RequestMapping(value = "/getDeclareList", method = {RequestMethod.POST})
    public ResponseResult<Map> getDeclareList(@RequestBody DemandStrategyModel model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");
        //判断该用户角色key类型为 1(系统管理员) 2(普通管理员)
        boolean role = userService.isManger();
        //查询所有相关的策略id
        List<String> sIdsList = new ArrayList<>();
//        if (role) {
//            sIdsList = respStrategyRepository.findSIds(model.getRespId());
//        } else {
//            sIdsList = respStrategyRepository.findSIdsByUserId(model.getRespId(), userId);
//        }
        sIdsList = respStrategyRepository.findSIdsByUserId(model.getRespId(), userId);
        if (sIdsList != null && sIdsList.size() > 0) {
            List<String> finalSIdsList = sIdsList;
            Specification<DemandRespStrategyNo> spec = new Specification<DemandRespStrategyNo>() {
                @Override
                public Predicate toPredicate(Root<DemandRespStrategyNo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.in(root.get("demandRespStrategy").get("sId")).value(finalSIdsList));
                    predicates.add(cb.notEqual(root.get("isPlatform"), 1));//非第三方平台的申报数据
                    predicates.add(cb.equal(root.get("respId"), model.getRespId()));//查询任务申报节点下的策略
                    return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };
            //当前页为第几页 默认 1开始
            int page = model.getNumber();
            int size = model.getPageSize();
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<DemandRespStrategyNo> datas = noRepository.findAll(spec, pageable);
            PageModel pageModel = new PageModel();
            List<DemandRespStrategyNoResp> noResps = new ArrayList<>();
            //封装到pageUtil
            if (datas.getContent() != null) {
                //查询，从哪里取基线负荷的值
                SysParam sysParam=sysParamRepository.findSysParamBySysParamKey(SysParamEnum.BaseLineForecastCfg.getId());
                JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
                String getMethod = "商汤";
                if (obj != null) {
                    if (obj.get("baseLineGetMethod") != null) {
                        getMethod=obj.get("baseLineGetMethod").toString();
                    }
                }
                //得到nodeIds,去ai表里查询ai基线负荷
                List<String> nodeIds = datas.stream().map(DemandRespStrategyNo::getNodeId).collect(Collectors.toList());
                //730需求
                //查询响应任务
                DemandRespTask task = demandRespTaskRepository.findByRespId(model.getRespId());
                //组装时间
                SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                dateSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
                timeSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//                String rsTime = dateSdf.format(task.getRsDate()) + " " + timeSdf.format(task.getRsTime());
//                String reTime = dateSdf.format(task.getRsDate()) + " " + timeSdf.format(task.getReTime());
                Date sDate = task.getRsTime();
                Date eDate = task.getReTime();
                Specification<AiLoadForecasting> spec1 = new Specification<AiLoadForecasting>() {
                    @Override
                    public Predicate toPredicate(Root<AiLoadForecasting> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                        List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                        predicates.add(cb.in(root.get("nodeId")).value(nodeIds));
                        predicates.add(cb.equal(root.get("systemId"), "nengyuanzongbiao"));
                        predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
                        criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                        return criteriaQuery.getRestriction();
                    }
                };
                //List<AiLoadForecasting> forecastingList = aiLoadRepository.findByNodeIdsMax(nodeIds);  20231023废弃
                List<AiLoadForecasting> forecastingList = aiLoadRepository.findAll(spec1);
                Map<String, List<AiLoadForecasting>> baseLoadMap = forecastingList.stream().collect(Collectors.groupingBy(AiLoadForecasting::getNodeId));
                for(DemandRespStrategyNo d:datas){
                    DemandRespStrategyNoResp noResp = new DemandRespStrategyNoResp();
                    noResp.setDrsId(d.getDrsId());
                    noResp.setNodeName(d.getNodeName());
                    noResp.setDrsStatus(d.getDrsStatus());
                    noResp.setDeclareLoad(d.getDeclareLoad());
                    noResp.setNoHouseholds(d.getNoHouseholds());
                    noResp.setStrategyId(d.getDemandRespStrategy().getScheduleStrategy().getStrategyId());
                    //20230605n zph  基线负荷 默认值为 -，类型为字符串
                    //double baseValue = com.example.vvpcommom.StringUtils.convertBaseLineValueToDouble(baseLoadMap.get(d.getNodeId()));
                    List<AiLoadForecasting> aiList = baseLoadMap.get(d.getNodeId());
                    noResp.setBaseLoad(getAvgBaseLoad(getMethod,aiList));
                    noResp.setDeclarePrice(d.getDeclarePrice());
                    noResps.add(noResp);
                }
            }
            pageModel.setContent(noResps);
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);
            Map<String, Object> map = new HashMap<>();
            Object[] obj = noRepository.findStrategyCount(sIdsList);
            if (obj != null) {
                Object[] objects = (Object[]) obj[0];
                map.put("invitation", objects[0]);//邀约对象
                map.put("totalDeclare", objects[1]);//申报负荷
            } else {
                List<DemandStrategy> demandStrategyList = demandStrategyRepository.findByRespId(model.getRespId());
                map.put("invitation", demandStrategyList.size());//邀约对象
                map.put("totalDeclare", demandStrategyList.stream().mapToDouble(v -> Double.parseDouble(v.getCommandValue())).sum());//申报负荷
            }
            map.put("devieInfo", pageModel);//设备分页信息
            return ResponseResult.success(map);
        } else {
            return ResponseResult.error("未选择策略信息");
        }
    }
    /**
     * 计算基线平均负荷
     * @param getMethod
     * @param aiList
     * @return
     */
    private String getAvgBaseLoad(String getMethod,List<AiLoadForecasting> aiList){
        String baseLoad = "-";
        if(aiList!=null && aiList.size()>0){
            //基线总和
            double baseLoadTotal = 0.00;
            // “-”的统计次数
            int count =0;
            if(getMethod.equals("商汤")){
                for(AiLoadForecasting a: aiList){
                    if (StringUtils.isEmpty(a.getBaselineLoadValue() == null ? null : a.getBaselineLoadValue().toString())
                            || "-".equals(a.getBaselineLoadValue() == null ? null : a.getBaselineLoadValue().toString())
                            || !com.example.vvpcommom.StringUtils.isNumber(a.getBaselineLoadValue() == null ? null : a.getBaselineLoadValue().toString())) {
                        count++;
                    }else {
                        baseLoadTotal += a.getBaselineLoadValue().doubleValue();
                    }
                }
                if(count<aiList.size()){
                    baseLoad=baseLoadTotal/aiList.size()+"";
                }
            }else{
                for(AiLoadForecasting a: aiList){
                    if (StringUtils.isEmpty(a.getBaselineLoadValueOther() == null ? null : a.getBaselineLoadValueOther().toString())
                            || "-".equals(a.getBaselineLoadValueOther() == null ? null : a.getBaselineLoadValueOther().toString())
                            || !com.example.vvpcommom.StringUtils.isNumber(a.getBaselineLoadValueOther() == null ? null : a.getBaselineLoadValueOther().toString())) {
                        count++;
                    }else {
                        baseLoadTotal += a.getBaselineLoadValueOther().doubleValue();
                    }
                }
                if(count<aiList.size()){
                    baseLoad=baseLoadTotal/aiList.size()+"";
                }
            }
        }
        return baseLoad;
    }
    @Value("${suanfaBackCalcApi}")
    private String suanfaBackCalcApi;
    @ApiOperation("编辑申报负荷")
    @UserLoginToken
    @RequestMapping(value = "/editDeclare", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editDeclare(
            @RequestParam("drsId") String drsId,
            @RequestParam("declareLoad") Double declareLoad,
            @RequestParam("declarePrice")Double declarePrice,
            @RequestParam("declareLoadBefore") Double declareLoadBefore) {
        //更新某个户号的申报负荷
        try {
            Optional<DemandRespStrategyNo> scheduleStrategy = noRepository.findById(drsId);
            if (!scheduleStrategy.isPresent()) {
                return ResponseResult.error("该申报信息不存在");
            }
            DemandRespStrategyNo dr = scheduleStrategy.get();
            if (dr.getDrsStatus() == 21) {//已申报
                return ResponseResult.error("该户号已申报，不允许修改");
            }
            List<DemandStrategy> demandStrategyList = demandStrategyRepository.findByRespIdAndNodeIdAndState(
                    scheduleStrategy.get().getRespId(),
                    scheduleStrategy.get().getNodeId(),1);
//            List<DemandStrategy> demandStrategyList = demandStrategyRepository.findByRespIdAndNodeId(
//                    scheduleStrategy.get().getRespId(),
//                    scheduleStrategy.get().getNodeId());
            if (demandStrategyList.size() == 0) {
                return ResponseResult.error("该申报策略不存在");
            }
            DemandStrategy find = demandStrategyList.get(0);
            find.setState(2);
            demandStrategyRepository.save(find);
            DemandStrategy demandStrategy = new DemandStrategy();
            BeanUtils.copyProperties(find, demandStrategy);
            String id = find.getRespId() + "_" + find.getNodeId() + "_" + new Date().getTime() + new Random().nextInt(100);
            demandStrategy.setId(id);
            demandStrategy.setState(1);
            demandStrategy.setLlm(false);
            //等待算法返回才修改策略不然按照上一个策略，后续修改：申报负荷，调节负荷、调节后负荷
            demandStrategyRepository.save(demandStrategy);
            if (drsId.contains("_")) {
                DemandAlgorithmVo json =new DemandAlgorithmVo(
                        demandStrategy.getRespId(),
                        demandStrategy.getNodeId(),
                        demandStrategy.getNodeName(),
                        declareLoadBefore * 1.15,
                        EnumExample.Weights.getNode(demandStrategy.getNodeId()),
                        demandStrategy.getStrategyContent(),
                        "opt_fx",
                        declareLoad * 1.15
                );
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(2000);
                        String result = HttpUtil.okHttpPost(suanfaBackCalcApi, JSON.toJSONString(json));
                        logger.info("编辑申报负荷请求算法result:{}",result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
            dr.setDeclareLoad(declareLoad);
            dr.setDeclarePrice(declarePrice);
            noRepository.updateDeclareLoad1(dr.getDrsId(), declareLoad);
//            noRepository.save(dr);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("编辑申报负荷异常 +" + e.getMessage());
        }
        return ResponseResult.success();
    }
    @ApiOperation("申报负荷提交")
    @UserLoginToken
    @RequestMapping(value = "/declareSubmit", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult declareSubmit(@RequestParam(value = "respId") String respId,
                                        @RequestParam("drsIds") List<String> drsIds) {
        //更新某个户号的申报负荷
        try {
            if (StringUtils.isBlank(respId)) {
                return ResponseResult.error("任务编码不能为空！");
            }
            if (drsIds.get(0).equals("undefined")) {
                return ResponseResult.error("节点id不能为空！");
            }
            DemandRespTask task = demandRespTaskRepository.findByRespId(respId);
            if(task!=null){
                //判断当前时间是否小于反馈截止时间
                if(task.getDStatus()==0){
                    return ResponseResult.error("该任务已过期/不存在！");
                }
                long diff = Math.abs(task.getFeedbackTime().getTime() - new Date().getTime());
                if (diff < 5000) {
                    return ResponseResult.error("接近反馈截止时间，申报失败！");
                }
                //判断某个户号是否被申报过，若被申报过，则给出提示
                if (drsIds != null && drsIds.size() > 0) {
                    List<DemandRespStrategyNo> noList = noRepository.findNoListByRespIdStatus(respId, 21);//查询已申报的状态的户号信息
                    if (noList != null && noList.size() > 0) {
                        //查询即将申报的申报信息
                        List<DemandRespStrategyNo> drsList = noRepository.findNoListByDrsIds(drsIds);
                        for (int i = 0; i < noList.size(); i++) {
                            DemandRespStrategyNo n = noList.get(i);
                            for (int j = 0; j < drsList.size(); j++) {
                                if (n.getNodeId().equals(drsList.get(j).getNodeId())) {
                                    return ResponseResult.error("户号" + n.getNoHouseholds() + "已被其他用户申报！");
                                }
                            }
                        }
                    }
                } else {
                    return ResponseResult.error("请先选择要申报的节点！");
                }
                //批量更新申报状态
                noRepository.updateStatus(drsIds);
            }
            //改为已申报
            demandRespTaskRepository.updateBatchStatus(new String[]{respId},2);
            //TODO 将申报信息发送给 福建
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("请先选择要申报的节点");
        }
        return ResponseResult.success();
    }
    @ApiOperation("获取第三方智慧能源平台列表")
    @UserLoginToken
    @PostMapping("/getSmartEnergySysParamList")
    public ResponseResult getSmartEnergySysParamList(@RequestParam("sysParamKey") Integer sysParamKey) {
        Specification<SysParam> spec = new Specification<SysParam>() {
            @Override
            public Predicate toPredicate(Root<SysParam> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("sysParamKey"), sysParamKey));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        List<SysParam> sysParam = sysParamRepository.findAll(spec);
        List<SmartEnergyModel> list = new ArrayList<>();
        if (sysParam != null && sysParam.size()>0) {
            sysParam.forEach(s->{
                SmartEnergyModel model = new SmartEnergyModel();
                model.setParamName(s.getSysParamName());
                model.setId(s.getId());
                model.setAddress(s.getSysParamValue());
                list.add(model);
            });
            return ResponseResult.success(list);
        }
        return ResponseResult.error("获取第三方智慧能源平台信息失败！");
    }
    @ApiOperation("第三方智慧能源平台-发送")
    @UserLoginToken
    @RequestMapping(value = "/thirdPlatformSend", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult thirdPlatformSend(@RequestParam(value = "respId") String respId,
                                            @RequestParam("platformId") String platformId) {
        try {
            if (StringUtils.isBlank(respId)) {
                return ResponseResult.error("任务编码不能为空！");
            }
            if (StringUtils.isBlank(platformId)) {
                return ResponseResult.error("平台id不能为空！");
            }
            //查询对应的任务id
            DemandRespTask task = demandRespTaskRepository.findByRespId(respId);
            //发送给相应的能源平台--高新兴用kafka形式，需要组成报文
            String jsonStr = transTaskKafka(task);
            String url = "http://localhost:" + port + "/v1/demandResponseInvitation";
            //发送数据到API接口
            HttpUtil.okHttpPost(url, jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("第三方智慧能源平台-发送异常 +" + e.getMessage());
        }
        return ResponseResult.success();
    }
    /**
     * 将需求响应任务转换为kafka报文
     *
     * @param task
     * @return
     */
    private String transTaskKafka(DemandRespTask task) {
        SimpleDateFormat formatter_ymd = new SimpleDateFormat("yyyy-MM-dd");
        formatter_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
        hm.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        DemandResponseInvitation taskKafka = new DemandResponseInvitation();
        taskKafka.setDemandId(task.getRespId());
        taskKafka.setDeleteFlag(0);//0-正常 1-取消
        taskKafka.setDemandDate(formatter_ymd.format(task.getRsDate()));
        //目前只有一个时间段
        String[] demandTime = new String[]{(hm.format(task.getRsTime()) + "-" + hm.format(task.getReTime()))};
        taskKafka.setDemandTime(demandTime);
        taskKafka.setDemandType(task.getRespType() + "");
        taskKafka.setDemandValue(task.getRespLoad());
        taskKafka.setDemandPrice(task.getRespSubsidy());
        List<DemandResponseInvitation> list = new ArrayList<>();
        list.add(taskKafka);
        return JSONObject.toJSONString(list);
    }
    @ApiOperation("第三方智慧能源平台-查询申报负荷列表")
    @UserLoginToken
    @RequestMapping(value = "/thirdPlatformDeclareList", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult thirdPlatformDeclareList(@RequestBody DemandStrategyModel model) {
        try {
            if (StringUtils.isBlank(model.getRespId())) {
                return ResponseResult.error("任务编码不能为空！");
            }
            if (StringUtils.isBlank(model.getPlatformId())) {
                return ResponseResult.error("平台id不能为空！");
            }
            //查询所有相关的策略id
            List<String> sIdsList = respStrategyRepository.findSIdsByPlatformId(model.getRespId(), model.getPlatformId());
            Specification<DemandRespStrategyNo> spec = new Specification<DemandRespStrategyNo>() {
                @Override
                public Predicate toPredicate(Root<DemandRespStrategyNo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.in(root.get("demandRespStrategy").get("sId")).value(sIdsList));
                    predicates.add(cb.equal(root.get("isPlatform"), 1));//查询第三方平台的数据
                    return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };
            //当前页为第几页 默认 1开始
            int page = model.getNumber();
            int size = model.getPageSize();
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<DemandRespStrategyNo> datas = noRepository.findAll(spec, pageable);
            PageModel pageModel = new PageModel();
            List<DemandRespStrategyNoResp> noResps = new ArrayList<>();
            //封装到pageUtil
            if (datas.getContent() != null) {
                //查询，从哪里取基线负荷的值
                SysParam sysParam=sysParamRepository.findSysParamBySysParamKey(SysParamEnum.BaseLineForecastCfg.getId());
                JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
                String getMethod = "商汤";
                if (obj != null) {
                    if (obj.get("baseLineGetMethod") != null) {
                        getMethod=obj.get("baseLineGetMethod").toString();
                    }
                }
                //得到nodeIds,去ai表里查询ai基线负荷
                List<String> nodeIds = datas.stream().map(DemandRespStrategyNo::getNodeId).collect(Collectors.toList());
                //730需求
                //查询响应任务
                DemandRespTask task = demandRespTaskRepository.findByRespId(model.getRespId());
                //组装时间
                SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                dateSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
                timeSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//                String rsTime = dateSdf.format(task.getRsDate()) + " " + timeSdf.format(task.getRsTime());
//                String reTime = dateSdf.format(task.getRsDate()) + " " + timeSdf.format(task.getReTime());
                Date sDate = task.getRsTime();
                Date eDate = task.getReTime();
                Specification<AiLoadForecasting> spec1 = new Specification<AiLoadForecasting>() {
                    @Override
                    public Predicate toPredicate(Root<AiLoadForecasting> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                        List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                        predicates.add(cb.in(root.get("nodeId")).value(nodeIds));
                        predicates.add(cb.equal(root.get("systemId"), "nengyuanzongbiao"));
                        predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
                        criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                        return criteriaQuery.getRestriction();
                    }
                };
                List<AiLoadForecasting> forecastingList = aiLoadRepository.findAll(spec1);
                Map<String, List<AiLoadForecasting>> baseLoadMap = forecastingList.stream().collect(Collectors.groupingBy(AiLoadForecasting::getNodeId));
                for(DemandRespStrategyNo d:datas.getContent()){
                    DemandRespStrategyNoResp noResp = new DemandRespStrategyNoResp();
                    noResp.setDrsId(d.getDrsId());
                    noResp.setNodeName(d.getNodeName());
                    noResp.setDrsStatus(d.getDrsStatus());
                    noResp.setDeclareLoad(d.getDeclareLoad());
                    noResp.setNoHouseholds(d.getNoHouseholds());
                    List<AiLoadForecasting> aiList = baseLoadMap.get(d.getNodeId());
                    noResp.setBaseLoad(getAvgBaseLoad(getMethod,aiList));
                    noResps.add(noResp);
                }
            }
            pageModel.setContent(noResps);
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);
            Map<String, Object> map = new HashMap<>();
            Object[] obj = noRepository.findStrategyCount(sIdsList);
            if (obj != null) {
                Object[] objects = (Object[]) obj[0];
                map.put("invitation", objects[0]);//邀约对象
                map.put("totalDeclare", objects[1]);//申报负荷
            } else {
                map.put("invitation", 0);//邀约对象
                map.put("totalDeclare", 0);//申报负荷
            }
            map.put("devieInfo", pageModel);//设备分页信息
            return ResponseResult.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("异常 +" + e.getMessage());
        }
    }
    @ApiOperation("第三方智慧能源平台-申报负荷提交")
    @UserLoginToken
    @RequestMapping(value = "/thirdPlatformDeclareSubmit", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult thirdPlatformDeclareSubmit(@RequestParam(value = "respId") String respId,
                                                     @RequestParam(value = "platformId") String platformId,
                                                     @RequestParam("drsIds") List<String> drsIds) {
        //更新某个户号的申报负荷
        try {
            if (StringUtils.isBlank(respId)) {
                return ResponseResult.error("任务id不能为空！");
            }
            if (StringUtils.isBlank(platformId)) {
                return ResponseResult.error("平台id不能为空！");
            }
            if (drsIds == null || drsIds.size() == 0) {
                return ResponseResult.error("请选择您要申报的内容！");
            }
            //批量更新申报状态
            noRepository.updateStatus(drsIds);
            Specification<DemandRespStrategyNo> spec = new Specification<DemandRespStrategyNo>() {
                @Override
                public Predicate toPredicate(Root<DemandRespStrategyNo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    CriteriaBuilder.In<List<String>> inClause = cb.in(root.get("drsId"));
                    inClause.value(drsIds);
                    predicates.add(inClause);
                    return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };
            //TODO 将申报信息发送给 福建
            //批量更新中标状态  本次默认1-已中标
            noRepository.updateWinningBid(drsIds, 1);
            //查询对应的任务id
            DemandRespTask task = demandRespTaskRepository.findByRespId(respId);
            List<DemandRespStrategyNo> scheduleStrategy = noRepository.findAll(spec);
            //将信息发给高新兴
            String jsonStr = transDeclareKafka(task, scheduleStrategy);
            String url = "http://localhost:" + port + "/v1/demandResponseInvitationResult";
            //发送数据到API接口
            HttpUtil.okHttpPost(url, jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("申报提交异常 +" + e.getMessage());
        }
        return ResponseResult.success();
    }
    /**
     * 将需求响应任务申报信息转换为kafka报文
     *
     * @param task
     * @return
     */
    private String transDeclareKafka(DemandRespTask task, List<DemandRespStrategyNo> noList) {
        SimpleDateFormat formatter_ymd = new SimpleDateFormat("yyyy-MM-dd");
        formatter_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
        hm.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        List<DemandResponseInvitationResult> list = new ArrayList<>();
        for (DemandRespStrategyNo no : noList) {
            DemandResponseInvitationResult taskKafka = new DemandResponseInvitationResult();
            taskKafka.setDemandId(task.getRespId());
            taskKafka.setDemandDate(formatter_ymd.format(task.getRsDate()));
            //目前只有一个时间段
            String[] demandTime = new String[]{(hm.format(task.getRsTime()) + "-" + hm.format(task.getReTime()))};
            taskKafka.setDemandTime(demandTime);
            taskKafka.setDemandType(task.getRespType() + "");
            taskKafka.setDemandValue(no.getDeclareLoad());//此时的负荷需求=申报负荷
            taskKafka.setDemandPrice(task.getRespSubsidy());
            taskKafka.setMeterAccountNumber(no.getNoHouseholds());
            taskKafka.setInvitationResult(no.getWinningBid() + "");//是否中标
            list.add(taskKafka);
        }
        return JSONObject.toJSONString(list);
    }
    /**
     * 查询价格详情--南网新增
     * @param model
     * @return
     */
    @ApiOperation("查询价格详情")
    @UserLoginToken
    @RequestMapping(value = "/getPriceList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getPriceList(@RequestBody DemandModel model) {
        try {
            if (StringUtils.isBlank(model.getRespId())) {
                return ResponseResult.error("任务id不能为空！");
            }
            PageModel pageModel = new PageModel();
            DemandRespTask task = demandRespTaskRepository.findByRespId(model.getRespId());
            //当前页为第几页 默认 1开始
            int pageSize = model.getPageSize();
            int pageNum = (model.getNumber() - 1) * pageSize;//pg 从0开始
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
            sdfTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Date startDate = sdfTime.parse(sdfTime.format(task.getRsTime()));
            Date endDate = sdfTime.parse(sdfTime.format(task.getReTime()));
            List<DemandRespPlanPrice> planPriceList=planPriceRepository.findByRespIdDateList(model.getRespId(),
                    startDate, endDate,pageSize,pageNum);
            pageModel.setContent(planPriceList);
            //总数
            int count = planPriceRepository.countByRespIdDate(model.getRespId(),
                    startDate, endDate);
            //封装到pageUtil
            pageModel.setTotalElements(count);
            return ResponseResult.success(pageModel);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("查询有误！");
        }
    }
    /**
     * 响应任务是否参加申报--南网
     * @param respId
     * @param declareStatus
     * @return
     */
    @ApiOperation("响应任务是否参加")
    @UserLoginToken
    @RequestMapping(value = "/editDeclareStatus", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editDeclareStatus(
            @RequestParam("respId") String respId,
            @RequestParam("declareStatus") Integer declareStatus) {
        //更新响应任务是否参加申报
        try {
            demandRespTaskRepository.updateStatus(respId, 4);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("编辑响应任务失败 +" + e.getMessage());
        }
        return ResponseResult.success();
    }
    @ApiOperation("南网-查询申报负荷列表")
    @UserLoginToken
    @RequestMapping(value = "/getCSPGDeclareList", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult getCSPGDeclareList(@RequestBody DemandStrategyModel model) {
        try {
            if (StringUtils.isBlank(model.getRespId())) {
                return ResponseResult.error("任务编码不能为空！");
            }
            //查询所有相关的策略id
            List<String> sIdsList = respStrategyRepository.findSIdsByPlatformId(model.getRespId(), "zhinengtuijian");
            Specification<DemandRespStrategyNo> spec = new Specification<DemandRespStrategyNo>() {
                @Override
                public Predicate toPredicate(Root<DemandRespStrategyNo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.in(root.get("demandRespStrategy").get("sId")).value(sIdsList));
                    predicates.add(cb.equal(root.get("isPlatform"), 1));//查询第三方平台的数据
                    return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };
            //当前页为第几页 默认 1开始
            int page = model.getNumber();
            int size = model.getPageSize();
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<DemandRespStrategyNo> datas = noRepository.findAll(spec, pageable);
            PageModel pageModel = new PageModel();
            List<DemandRespStrategyNoResp> noResps = new ArrayList<>();
            List<DemandStrategy> demandStrategyList = demandStrategyRepository.findByRespIdAndState(model.getRespId(),Arrays.asList(1));
            Map<String,DemandStrategy> demandStrategyMap = demandStrategyList.stream()
                    .collect(Collectors.toMap(DemandStrategy::getNodeId, demandStrategy -> demandStrategy));
            //封装到pageUtil
            if (datas.getContent() != null) {
                //查询，从哪里取基线负荷的值
                SysParam sysParam=sysParamRepository.findSysParamBySysParamKey(SysParamEnum.BaseLineForecastCfg.getId());
                JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
                String getMethod = "商汤";
                if (obj != null) {
                    if (obj.get("baseLineGetMethod") != null) {
                        getMethod=obj.get("baseLineGetMethod").toString();
                    }
                }
                //得到nodeIds,去ai表里查询ai基线负荷
                List<String> nodeIds = datas.stream().map(DemandRespStrategyNo::getNodeId).collect(Collectors.toList());
                //730需求
                //查询响应任务
                DemandRespTask task = demandRespTaskRepository.findByRespId(model.getRespId());
                //组装时间
                SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                dateSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
                timeSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                Date sDate = task.getRsTime();
                Date eDate = task.getReTime();
                Specification<AiLoadForecasting> spec1 = new Specification<AiLoadForecasting>() {
                    @Override
                    public Predicate toPredicate(Root<AiLoadForecasting> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                        List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                        predicates.add(cb.in(root.get("nodeId")).value(nodeIds));
                        predicates.add(cb.equal(root.get("systemId"), "nengyuanzongbiao"));
                        predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
                        criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                        return criteriaQuery.getRestriction();
                    }
                };
                List<AiLoadForecasting> forecastingList = aiLoadRepository.findAll(spec1);
                Map<String, List<AiLoadForecasting>> baseLoadMap = forecastingList.stream().collect(Collectors.groupingBy(AiLoadForecasting::getNodeId));
                for(DemandRespStrategyNo d:datas.getContent()){
                    DemandRespStrategyNoResp noResp = new DemandRespStrategyNoResp();
                    noResp.setDrsId(d.getDrsId());
                    noResp.setNodeName(d.getNodeName());
                    noResp.setDrsStatus(d.getDrsStatus());
                    noResp.setDeclareLoad(Math.round(d.getDeclareLoad() * 100.0) / 100.0);
                    noResp.setDeclarePrice(d.getDeclarePrice());
                    noResp.setNoHouseholds(d.getNoHouseholds());
                    List<AiLoadForecasting> aiList = baseLoadMap.get(d.getNodeId());
                    DemandStrategy demandStrategy = demandStrategyMap.get(d.getNodeId());
                    if (demandStrategy != null ) {
                        noResp.setForecastLoad(demandStrategy.getForecastLoad() == null ? null: demandStrategy.getForecastLoad());
                        noResp.setAdjustLoad(demandStrategy.getForecastAdjustLoad() == null ? null: demandStrategy.getForecastAdjustLoad());
                    } else {
                        noResp.setForecastLoad(null);
                        noResp.setAdjustLoad(null);
                    }
                    noResp.setBaseLoad(getAvgBaseLoad(getMethod,aiList));
                    noResps.add(noResp);
                }
            }
            pageModel.setContent(noResps);
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);
            Map<String, Object> map = new HashMap<>();
            Object[] obj = noRepository.findStrategyCount(sIdsList);
            if (obj != null) {
                Object[] objects = (Object[]) obj[0];
                map.put("invitation", objects[0]);//邀约对象
                map.put("totalDeclare", String.format("%.2f", objects[1]));//申报负荷
            } else {
                map.put("invitation", demandStrategyList.size());//邀约对象
                map.put("totalDeclare", String.format("%.2f", demandStrategyList.stream().mapToDouble(v -> Double.parseDouble(v.getCommandValue())).sum()));//申报负荷
            }
            map.put("devieInfo", pageModel);//设备分页信息
            return ResponseResult.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("异常 +" + e.getMessage());
        }
    }
    /**
     * 查询节点列表信息
     * add by maoyating 20240312
     * @return
     */
    @ApiOperation("查询节点列表信息")
    @UserLoginToken
    @RequestMapping(value = "/getNodeList", method = {RequestMethod.GET})
    public ResponseResult getNodeList(){
        //查询所有有权限的户号信息
        List<Node> all = nodeRepository.findAllByNodeIdIn(userService.getAllowLoadNodeIds());
        Map<String, Object> map = new HashMap<>();
        if(all!=null && all.size()>0){
            all.forEach(n->{
                DemandNodeRespModel model = new DemandNodeRespModel();
                BeanUtils.copyProperties(n,model);
                map.put(n.getNodeId(),model);
            });
        }
        return ResponseResult.success(map);
    }
    /**
     * 查询参与响应的节点信息
     * add by maoyating 20240312
     * @return
     */
    @ApiOperation("查询参与响应的节点信息")
    @UserLoginToken
    @RequestMapping(value = "/getDemandNodeList", method = {RequestMethod.POST})
    public ResponseResult getDemandNodeList(@RequestParam("respId") String respId){
        //查询已设置的户号信息
        List<DemandRespStrategyNo> all = noRepository.findNoListByRespId(respId);
        Map<String,String> map =new HashMap<>();
        if(all!=null && all.size()>0){
            all.forEach(n->{
                map.put(n.getNoHouseholds(),n.getNodeId());
            });
        }
        return ResponseResult.success(map);
    }
    /**
     * 手动出清 触发
     * add by maoyating 20240313
     */
    @RequestMapping(value = "updateClearing", method = {RequestMethod.POST})
    @ApiOperation("手动触发出清操作")
    public ResponseResult updateClearing(@RequestParam("respId") String respId) {
        try {
            Optional<DemandRespTask> task = demandRespTaskRepository.findById(respId);
            if (!task.isPresent()) {
                return ResponseResult.error("该需求响应不存在");
            }
            DemandRespTask dr = task.get();
            Date date = new Date();
            List<DemandRespPlan> list = new ArrayList<>();
            DemandRespPlan plan = new DemandRespPlan();
            plan.setPlanCode(dr.getRespId()+"_planCode");
            plan.setPlanName(dr.getRespId()+"_planName");
            Date rsDate = dr.getRsDate();
            plan.setPlanTime(rsDate);
            plan.setCreditCode(dr.getTaskCode()+"");
            plan.setCreateBy("admin");
            plan.setCreateTime(date);
            //中标的户号信息，需更新状态为1-已中标
            List<String> winningList = new ArrayList<>();
            plan.setPlanId(date.getTime()+dr.getRespId());
            plan.setInvitationId(dr.getRespId());
            list.add(plan);
            //查找已申报的户号信息
            List<DemandRespStrategyNo> all = noRepository.findNoListByRespId(dr.getRespId());
            if(all!=null && all.size()>0){
                all.forEach(d->{
                    //若为待出清已申报
                    if(d.getDrsStatus()==21){
                        winningList.add(d.getDrsId());
                    }
                });
            }
            //批量更新户号信息--已中标和出清成功
            noRepository.updateWinningBidAndDrsStatus(winningList,1,22);
            planRepository.saveAll(list);
            String[] invitationIds=new String[]{dr.getRespId()};
            //批量更新任务状态 已申报出清成功
            demandRespTaskRepository.updateBatchStatus(invitationIds,3);
            SysJob sysJob = new SysJob();
            sysJob.setJobName("需求响应任务" + dr.getTaskCode());
            sysJob.setJobGroup("需求响应");
            SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
            dateSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
            timeSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            String[] rsTimeArr = timeSdf.format(dr.getRsTime()).split(":");
            String[] rsDateArr = dateSdf.format(dr.getRsDate()).split("-");
            sysJob.setCronExpression("0 " + Integer.valueOf(rsTimeArr[1]).intValue() + " "
                    + Integer.valueOf(rsTimeArr[0]).intValue() + " "
                    + Integer.valueOf(rsDateArr[2]).intValue() + " " +
                    Integer.valueOf(rsDateArr[1]).intValue() + " ? " + Integer.valueOf(rsDateArr[0]).intValue());
            sysJob.setInvokeTarget("demandRespTask.initRespTask('" + dr.getRespId() + "')");
            sysJob.setMisfirePolicy("3");//计划执行错误策略（1立即执行 2执行一次 3放弃执行）
            sysJob.setConcurrent("1");//0允许 1禁止
            sysJob.setCreateBy("admin");
            sysJob = sysJobService.insertJobDemand(sysJob);
            sysJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());//状态（0正常 1暂停）
            sysJobService.changeStatus(sysJob);
            dr.setJobId(sysJob.getJobId());
            demandRespTaskRepository.updateBatchJobId(dr.getRespId(),sysJob.getJobId());
            return ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }
}