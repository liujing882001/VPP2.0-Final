package com.example.vvpweb.ancillary;

import com.example.vvpcommom.*;
import com.example.vvpdomain.AncillarySStrategyRepository;
import com.example.vvpdomain.AncillaryServicesRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.SysJobRepository;
import com.example.vvpdomain.entity.*;
import com.example.vvpscheduling.service.ISysJobService;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.ancillary.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author maoyating
 * @description 辅助服务-服务任务
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/ancillary_services/ancillary_task")
@CrossOrigin
@Api(value = "辅助服务-服务任务", tags = {"辅助服务-服务任务"})
public class AncillaryServicesTaskController {

    private static Logger logger = LoggerFactory.getLogger(AncillaryServicesTaskController.class);
    @Autowired
    private AncillaryServicesRepository asRepository;
    @Autowired
    private AncillarySStrategyRepository assRepository;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private SysJobRepository sysJobRepository;
    @Autowired
    private ISysJobService sysJobService;

    /**
     * 自动获取任务
     */
    @ApiOperation("自动获取任务")
    @UserLoginToken
    @RequestMapping(value = "/getASTask", method = {RequestMethod.GET})
    public ResponseResult getASTask() {
        //TODO 从哪获取？？
        return ResponseResult.success();
    }

    @ApiOperation("查询任务列表")
    @UserLoginToken
    @RequestMapping(value = "/getASTaskList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getTaskList(@RequestBody AncillaryModel model) {
        Specification<AncillaryServices> spec = new Specification<AncillaryServices>() {
            @Override
            public Predicate toPredicate(Root<AncillaryServices> root,
                                         CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.notEqual(root.get("aStatus"), 0));//查询状态非 删除的数据
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.desc(root.get("assDate"))); //根据日期降序排
                return criteriaQuery.getRestriction();
            }
        };
        //当前页为第几页 默认 1开始
        int page = model.getNumber();
        int size = model.getPageSize();

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<AncillaryServices> datas = asRepository.findAll(spec, pageable);

        PageModel pageModel = new PageModel();
        //封装到pageUtil
        if (datas.getContent() != null && datas.getContent().size() > 0) {
            List<AncillaryServicesRespModel> list = new ArrayList<>();
            datas.getContent().forEach(d -> {
                AncillaryServicesRespModel newModel = new AncillaryServicesRespModel();
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
    }

    @ApiOperation("录入辅助任务")
    @UserLoginToken
    @RequestMapping(value = "/addASTask", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult addASTask(@RequestBody @Valid AncillaryServicesReq req) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            if (!TimeUtil.isLegalDate(req.getAssDate().length(), req.getAssDate(), "yyyy-MM-dd")) {
                return ResponseResult.error("辅助日期格式不正确");
            }
            if (!TimeUtil.isLegalDate(req.getAssTime().length(), req.getAssTime(), "HH:mm")) {
                return ResponseResult.error("辅助开始时段格式不正确");
            }
            if (!TimeUtil.isLegalDate(req.getAseTime().length(), req.getAseTime(), "HH:mm")) {
                return ResponseResult.error("辅助结束时段格式不正确");
            }
            Date date = new Date();
            Date rsdate = TimeUtil.strDDToDate(req.getAssDate() + " " +
                    req.getAssTime() + ":00", "yyyy-MM-dd HH:mm:ss");
            if (rsdate.before(date)) {
                return ResponseResult.error("辅助开始时段不能小于当前时间");
            }
            AncillaryServices dr = new AncillaryServices();

            dr.setAsId(date.getTime() + "");
            dr.setAssTime(TimeUtil.strDDToDate(req.getAssTime(), "HH:mm"));
            dr.setAseTime(TimeUtil.strDDToDate(req.getAseTime(), "HH:mm"));
            dr.setAssDate(TimeUtil.strDDToDate(req.getAssDate(), "yyyy-MM-dd"));
            dr.setTaskCode(req.getTaskCode());
            dr.setAsSubsidy(req.getAsSubsidy());
            dr.setAsLoad(req.getAsLoad());
            dr.setAsType(req.getAsType());
            dr.setAStatus(1);//未开始

            dr.setCreateBy(userId);//保存创建者

            //查询有权限的节点及设备信息 策略id+响应需求状态+设备负荷信息
            List<Node> all = nodeRepository.findAllByNodeIdIn(userService.getAllowLoadNodeIds());
            List<Device> list = new ArrayList<>();
            all.forEach(e -> {
                list.addAll(e.getDeviceList());
            });
            List<AncillarySStrategy> sList = new ArrayList<>();
            //过滤 非参加需求响应的社保
            if (list != null && list.size() > 0) {
//                list.forEach(l->{
//                    if(l.getScheduleStrategy()!=null){
//                        //添加 需求侧响应计划的 设备
//                        if(l.getScheduleStrategy().isDemandResponse()){
//                            AncillarySStrategy strategy = new AncillarySStrategy();
//
//                            strategy.setSId(date.getTime()+l.getDeviceId());
//                            strategy.setAncillaryServices(dr);
////                            strategy.setNodeId(l.getScheduleStrategy().getNode().getNodeId());
////                            strategy.setNodeName(l.getScheduleStrategy().getNode().getNodeName());
//                            strategy.setSystemId(l.getSystemType().getSystemId());
//                            strategy.setSystemName(l.getSystemType().getSystemName());
//                            strategy.setDeviceId(l.getDeviceId());
//                            strategy.setDeviceName(l.getDeviceName());
//                            strategy.setDeviceRatedPower(l.getDeviceRatedPower());
//                            strategy.setActualLoad(l.getDeviceRatedPower());
//
//                            strategy.setSStatus(1);
//
//                            sList.add(strategy);
//                        }
//                    }
//                });
            }
            //设备策略信息入库
            if (sList != null) {
                assRepository.saveAll(sList);
            }

            //添加定时任务
//            if(dr.getAsType()!=3){ //非备用
            SysJob sysJob = new SysJob();
            sysJob.setJobName("辅助服务任务" + req.getTaskCode());
            sysJob.setJobGroup("辅助服务");//TODO 不太确定
            String[] rsTimeArr = req.getAssTime().split(":");
            String[] rsDateArr = req.getAssDate().split("-");
            sysJob.setCronExpression("0 " + Integer.valueOf(rsTimeArr[1]).intValue() + " "
                    + Integer.valueOf(rsTimeArr[0]).intValue() + " "
                    + Integer.valueOf(rsDateArr[2]).intValue() + " " +
                    Integer.valueOf(rsDateArr[1]).intValue() + " ? "
                    + Integer.valueOf(rsDateArr[0]).intValue());
            sysJob.setInvokeTarget("ancillaryServices.initAncillaryTask('" + dr.getAsId() + "')");
            sysJob.setMisfirePolicy("3");//计划执行错误策略（1立即执行 2执行一次 3放弃执行）
            sysJob.setConcurrent("1");//0允许 1禁止
            sysJob.setStatus("0");//状态（0正常 1暂停）
            sysJob.setCreateBy(userId);
            sysJob = sysJobRepository.save(sysJob);
            sysJobService.insertJob(sysJob);

            sysJob.setStatus("0");//状态（0正常 1暂停）
            sysJobService.changeStatus(sysJob);
            dr.setJobId(sysJob.getJobId());
//            }

            asRepository.save(dr);

        } catch (Exception e) {
            return ResponseResult.error("录入辅助服务任务异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    @ApiOperation("编辑辅助任务")
    @UserLoginToken
    @RequestMapping(value = "/editASTask", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editASTask(@RequestBody @Valid AncillaryServicesReq req) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            //判断id是否为空
            if (StringUtils.isBlank(req.getAsId())) {
                return ResponseResult.error("id不能为空");
            }
            Optional<AncillaryServices> task = asRepository.findById(req.getAsId());
            if (!task.isPresent()) {
                throw new AuthException("该辅助服务不存在");
            }
            AncillaryServices dr = task.get();

            if (!TimeUtil.isLegalDate(req.getAssDate().length(), req.getAssDate(), "yyyy-MM-dd")) {
                return ResponseResult.error("辅助日期格式不正确");
            }
            if (!TimeUtil.isLegalDate(req.getAssTime().length(), req.getAssTime(), "HH:mm")) {
                return ResponseResult.error("辅助开始时段格式不正确");
            }
            if (!TimeUtil.isLegalDate(req.getAseTime().length(), req.getAseTime(), "HH:mm")) {
                return ResponseResult.error("辅助结束时段格式不正确");
            }
            Date date = new Date();
            Date rsdate = TimeUtil.strDDToDate(req.getAssDate() + " " +
                    req.getAssTime() + ":00", "yyyy-MM-dd HH:mm:ss");
            if (rsdate.before(date)) {
                return ResponseResult.error("辅助开始时段不能小于当前时间");
            }

            String[] rsTimeArr = req.getAssTime().split(":");
            String[] rsDateArr = req.getAssDate().split("-");
            if (req.getAsType() != 3) {
                if (dr.getJobId() != null) {
                    //修改
                    if (!req.getTaskCode().equals(dr.getTaskCode())) {
                        Optional<SysJob> old = sysJobRepository.findById(dr.getJobId());
                        SysJob oldJob = old.get();

                        oldJob.setJobName("辅助服务任务" + req.getTaskCode());
                        oldJob.setJobGroup("辅助服务");//TODO 不太确定
                        oldJob.setCronExpression("0 " + Integer.valueOf(rsTimeArr[1]).intValue() + " "
                                + Integer.valueOf(rsTimeArr[0]).intValue() + " "
                                + Integer.valueOf(rsDateArr[2]).intValue() + " "
                                + Integer.valueOf(rsDateArr[1]).intValue() + " ? "
                                + Integer.valueOf(rsDateArr[0]).intValue());
                        oldJob.setUpdateBy(userId);

                        sysJobService.updateJob(oldJob);

                    }
                } else {
                    //新增
                    SysJob sysJob = new SysJob();
                    sysJob.setJobName("辅助服务任务" + req.getTaskCode());
                    sysJob.setJobGroup("辅助服务");//TODO 不太确定
                    sysJob.setCronExpression("0 " + Integer.valueOf(rsTimeArr[1]).intValue() + " "
                            + Integer.valueOf(rsTimeArr[0]).intValue() + " "
                            + Integer.valueOf(rsDateArr[2]).intValue() + " " +
                            Integer.valueOf(rsDateArr[1]).intValue() + " ? "
                            + Integer.valueOf(rsDateArr[0]).intValue());
                    sysJob.setInvokeTarget("ancillaryServices.initAncillaryTask('" + dr.getAsId() + "')");
                    sysJob.setMisfirePolicy("3");//计划执行错误策略（1立即执行 2执行一次 3放弃执行）
                    sysJob.setConcurrent("1");//0允许 1禁止
                    sysJob.setStatus("0");//状态（0正常 1暂停）
                    sysJob.setCreateBy(userId);
                    sysJob = sysJobRepository.save(sysJob);
                    sysJobService.insertJob(sysJob);

                    sysJob.setStatus("0");//状态（0正常 1暂停）
                    sysJobService.changeStatus(sysJob);

                    dr.setJobId(sysJob.getJobId());
                }

            } else {
                if (dr.getJobId() != null) {
                    //直接删除定时任务
                    Optional<SysJob> old = sysJobRepository.findById(dr.getJobId());
                    SysJob oldJob = old.get();
                    sysJobService.deleteJob(oldJob);

                    dr.setJobId(null);
                }
            }

            dr.setAssTime(TimeUtil.strDDToDate(req.getAssTime(), "HH:mm"));
            dr.setAseTime(TimeUtil.strDDToDate(req.getAseTime(), "HH:mm"));
            dr.setAssDate(TimeUtil.strDDToDate(req.getAssDate(), "yyyy-MM-dd"));
            dr.setTaskCode(req.getTaskCode());
            dr.setAsSubsidy(req.getAsSubsidy());
            dr.setAsLoad(req.getAsLoad());
            dr.setAsType(req.getAsType());

            dr.setUpdateBy(userId);//保存创建者
            dr.setUpdateTime(date);

            asRepository.save(dr);

        } catch (Exception e) {
            return ResponseResult.error("编辑辅助服务异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    @ApiOperation("删除辅助任务")
    @UserLoginToken
    @RequestMapping(value = "/delASTask", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult delASTask(@RequestParam("asId") String asId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            //判断id是否为空
            if (StringUtils.isBlank(asId)) {
                return ResponseResult.error("id不能为空");
            }
            Optional<AncillaryServices> task = asRepository.findById(asId);
            if (!task.isPresent()) {
                throw new AuthException("该辅助服务不存在");
            }
            AncillaryServices dr = task.get();
            Date date = new Date();

            dr.setUpdateBy(userId);//保存创建者
            dr.setUpdateTime(date);
            dr.setAStatus(0);//0-删除

            if (dr.getJobId() != null) {
                //直接删除定时任务
                Optional<SysJob> old = sysJobRepository.findById(dr.getJobId());
                SysJob oldJob = old.get();
                sysJobService.deleteJob(oldJob);
            }

            asRepository.save(dr);
        } catch (Exception e) {
            return ResponseResult.error("删除辅助服务异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    @ApiOperation("搜索设备列表")
    @UserLoginToken
    @RequestMapping(value = "/getASDeviceListByName", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getASDeviceListByName(@RequestBody AncillarySStrategyModel model) {
        //先同步辅助服务的设备
        // synDevice(model.getAsId());
        Specification<AncillarySStrategy> spec = new Specification<AncillarySStrategy>() {
            @Override
            public Predicate toPredicate(Root<AncillarySStrategy> root,
                                         CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (StringUtils.isNotBlank(model.getDeviceName())) {
                    predicates.add(cb.like(root.get("deviceName"), "%" + model.getDeviceName() + "%"));//查询状态为正常
                }
                predicates.add(cb.equal(root.get("ancillaryServices").get("asId"), model.getAsId()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        //当前页为第几页 默认 1开始
        int page = model.getNumber();
        int size = model.getPageSize();

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<AncillarySStrategy> datas = assRepository.findAll(spec, pageable);

        PageModel pageModel = new PageModel();
        //封装到pageUtil
        //封装到pageUtil
        if (datas.getContent() != null && datas.getContent().size() > 0) {
            List<AncillarySStrategyReq> list = new ArrayList<>();
            datas.getContent().forEach(d -> {
                AncillarySStrategyReq newModel = new AncillarySStrategyReq();
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
    }

    @ApiOperation("新增辅助策略")
    @UserLoginToken
    @RequestMapping(value = "/addASStrategy", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult addASStrategy(@RequestBody @Valid AncillarySStrategyReq respStrategyReq) {
        try {

            AncillarySStrategy dr = new AncillarySStrategy();
            Date date = new Date();

            dr.setSId(date.getTime() + "");
            dr.setAncillaryServices(asRepository.findById(respStrategyReq.getAsId()).get());
            dr.setNodeId(respStrategyReq.getNodeId());
            dr.setNodeName(respStrategyReq.getNodeName());
            dr.setSystemId(respStrategyReq.getSystemId());
            dr.setSystemName(respStrategyReq.getSystemName());
            dr.setDeviceId(respStrategyReq.getDeviceId());
            dr.setDeviceName(respStrategyReq.getDeviceName());
            dr.setDeviceRatedPower(respStrategyReq.getDeviceRatedPower());
            dr.setActualLoad(respStrategyReq.getActualLoad());
            dr.setSStatus(1);

            assRepository.save(dr);

        } catch (Exception e) {
            return ResponseResult.error("新增设备信息异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    @ApiOperation("编辑辅助策略")
    @UserLoginToken
    @RequestMapping(value = "/editASStrategy", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editASStrategy(@RequestParam("sStatus") Integer sStatus,
                                         @RequestParam("sId") String sId,
                                         @RequestParam("deviceRatedPower") Double deviceRatedPower,
                                         @RequestParam("actualLoad") Double actualLoad) {
        try {
            Optional<AncillarySStrategy> task = assRepository.findById(sId);
            if (!task.isPresent()) {
                throw new AuthException("该设备策略不存在");
            }
            AncillarySStrategy dr = task.get();

            dr.setSStatus(sStatus);
            dr.setDeviceRatedPower(deviceRatedPower);
            dr.setActualLoad(actualLoad);
            dr.setUpdateTime(new Date());

            assRepository.save(dr);

        } catch (Exception e) {
            return ResponseResult.error("编辑辅助策略异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    /**
     * 同步参加辅助服务的设备
     *
     * @param asId
     */
    public void synDevice(String asId) {
        Optional<AncillaryServices> task = asRepository.findById(asId);
        AncillaryServices dr = task.get();

        Date date = new Date();
        //查询有权限的节点及设备信息 策略id+响应需求状态+设备负荷信息
        List<Node> all = nodeRepository.findAllByNodeIdIn(userService.getAllowLoadNodeIds());
        List<Device> list = new ArrayList<>();
        all.forEach(e -> {
            list.addAll(e.getDeviceList());
        });
        List<AncillarySStrategy> sList = new ArrayList<>();
        //过滤 非参加需求响应的社保
        if (list != null && list.size() > 0) {
//            list.forEach(l->{
//                if(l.getScheduleStrategy()!=null){
//                    //添加 需求侧响应计划的 设备
//                    if(l.getScheduleStrategy().isDemandResponse()){
//                        AncillarySStrategy strategy = new AncillarySStrategy();
//
//                        strategy.setSId(date.getTime()+l.getDeviceId());
//                        strategy.setAncillaryServices(dr);
////                        strategy.setNodeId(l.getScheduleStrategy().getNode().getNodeId());
////                        strategy.setNodeName(l.getScheduleStrategy().getNode().getNodeName());
//                        strategy.setSystemId(l.getSystemType().getSystemId());
//                        strategy.setSystemName(l.getSystemType().getSystemName());
//                        strategy.setDeviceId(l.getDeviceId());
//                        strategy.setDeviceName(l.getDeviceName());
//                        strategy.setDeviceRatedPower(l.getDeviceRatedPower());
//                        strategy.setActualLoad(l.getDeviceRatedPower());
//
//                        strategy.setSStatus(1);
//
//                        sList.add(strategy);
//                    }
//                }
//            });

            //根据辅助服务任务，查询所有的设备策略信息
            Specification<AncillarySStrategy> spec = new Specification<AncillarySStrategy>() {
                @Override
                public Predicate toPredicate(Root<AncillarySStrategy> root,
                                             CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.equal(root.get("ancillaryServices").get("asId"), asId));
                    return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            List<AncillarySStrategy> datas = assRepository.findAll(spec);
            if (datas != null && datas.size() > 0) {
                //先过滤device找不到的设备信息,直接删除策略
                List<AncillarySStrategy> nullList = datas.stream().filter(d -> d.getDeviceId() == null).collect(Collectors.toList());

                Map<String, List<AncillarySStrategy>> allMap = sList.stream()
                        .collect(groupingBy(AncillarySStrategy::getDeviceId));
                Map<String, List<AncillarySStrategy>> datasMap = datas.stream()
                        .filter(d -> d.getDeviceId() != null)
                        .collect(groupingBy(AncillarySStrategy::getDeviceId));
                //对比设备
                List<AncillarySStrategy> newList = new ArrayList<>();

                for (String device : allMap.keySet()) {
                    List<AncillarySStrategy> strategyList = datasMap.get(device);
                    if (strategyList != null && strategyList.size() > 0) {
                        newList.addAll(strategyList);
                    } else {
                        newList.addAll(allMap.get(device));
                    }
                }

                //如果有些设备不存在，或者没有参加策略，需要进行删除
                if (nullList != null && nullList.size() > 0) {
                    assRepository.deleteInBatch(nullList);
                }
                //设备策略信息入库
                if (newList != null && newList.size() > 0) {
                    assRepository.saveAll(newList);
                }
            } else {
                //设备策略信息入库
                if (sList != null) {
                    assRepository.saveAll(sList);
                }
            }
        } else {
            //如果没有参加辅助服务的设备，全部删除
            assRepository.deleteAsId(asId);
        }
    }
}