package com.example.vvpscheduling.controller;


import com.example.vvpcommom.RequestHeaderContext;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.DemandRespStrategyRepository;
import com.example.vvpdomain.ScheduleStrategyDeviceRepository;
import com.example.vvpdomain.ScheduleStrategyRepository;
import com.example.vvpdomain.SysJobRepository;
import com.example.vvpdomain.entity.ScheduleStrategy;
import com.example.vvpdomain.entity.SysJob;
import com.example.vvpscheduling.service.ISysJobService;
import com.example.vvpservice.prouser.service.IUserService;
import io.swagger.annotations.Api;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/run_schedule/run_strategy_delete")
@CrossOrigin
@Api(value = "运行调度-运行策略-可调负荷运行策略删除", tags = {"运行调度-运行策略-可调负荷运行策略删除"})
public class ScheduleStrategyDeleteController {

    @Autowired
    private ISysJobService jobService;

    @Autowired
    private SysJobRepository sysJobRepository;

    @Autowired
    private ScheduleStrategyRepository scheduleStrategyRepository;

    @Autowired
    private ScheduleStrategyDeviceRepository scheduleStrategyDeviceRepository;
    @Resource
    private DemandRespStrategyRepository demandRespStrategyRepository;

    @Autowired
    private IUserService userService;

    /**
     * 删除策略
     */
    @UserLoginToken
    @RequestMapping(value = "deleteStrategy", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult deleteStrategy(@RequestParam("strategyId") String strategyId) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(strategyId)) {
            return ResponseResult.error("可调负荷运行策略编号不能为空！");
        }
        List<String> list = new ArrayList<>();
        list.add(strategyId);

        //zph 20230507   判定需求响应里面是否有关联
        List<String> demandRespStrategies = demandRespStrategyRepository.findBeAssociatedWithStrategyIds(list);

        if (demandRespStrategies != null && demandRespStrategies.size() > 0) {

            return ResponseResult.error("此策略已与需求响应任务关联，删除失败!");
        }


        ScheduleStrategy scheduleStrategy = null;
        if (userService.isManger()) {
            scheduleStrategy = scheduleStrategyRepository.findByStrategyId(strategyId);
        } else {
            String userId = RequestHeaderContext.getInstance().getUserId();
            scheduleStrategy = scheduleStrategyRepository.findByUserIdAndStrategyId(userId, strategyId);
        }

        if (scheduleStrategy != null) {

            List<Long> collect = sysJobRepository.findAllByJobGroup(scheduleStrategy.getStrategyId()).
                    stream().map(SysJob::getJobId).collect(Collectors.toList());

            try {
                jobService.deleteJobByIds(collect);
            } catch (SchedulerException e) {
                e.printStackTrace();
                return ResponseResult.error("删除策略失败");
            }

            scheduleStrategyDeviceRepository.deleteAllByStrategyId(strategyId);

            try {
                scheduleStrategyRepository.delete(scheduleStrategy);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseResult.error("删除策略失败,策略被其它服务应用");
            }

        } else {
            return ResponseResult.error("删除策略不存在");
        }

        return ResponseResult.success();
    }


    /**
     * 删除策略
     */
    @UserLoginToken
    @RequestMapping(value = "batchDeleteStrategy", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult batchDeleteStrategy(@RequestParam("strategyIdList") List<String> strategyIdList) {

        if (strategyIdList == null || strategyIdList.size() == 0) {
            return ResponseResult.error("可调负荷运行策略编号不能为空！");
        }
        //zph 20230507   判定需求响应里面是否有关联
        List<String> demandRespStrategies = demandRespStrategyRepository.findBeAssociatedWithStrategyIds(strategyIdList);

        if (demandRespStrategies != null && demandRespStrategies.size() > 0) {

            return ResponseResult.error("此策略已与需求响应任务关联，删除失败!");
        }

        List<ScheduleStrategy> scheduleStrategyList = null;
        if (userService.isManger()) {
            scheduleStrategyList = scheduleStrategyRepository.findAllByStrategyIdIn(strategyIdList);
        } else {
            String userId = RequestHeaderContext.getInstance().getUserId();
            scheduleStrategyList = scheduleStrategyRepository.findAllByStrategyIdInAndUserId(strategyIdList, userId);
        }

        for (ScheduleStrategy scheduleStrategy : scheduleStrategyList) {
            if (scheduleStrategy != null) {

                List<Long> collect = sysJobRepository.findAllByJobGroup(scheduleStrategy.getStrategyId()).
                        stream().map(SysJob::getJobId).collect(Collectors.toList());

                try {
                    jobService.deleteJobByIds(collect);
                } catch (SchedulerException e) {
                    return ResponseResult.error("删除策略失败");
                }

                scheduleStrategyDeviceRepository.deleteAllByStrategyId(scheduleStrategy.getStrategyId());

                try {
                    scheduleStrategyRepository.delete(scheduleStrategy);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseResult.error("删除策略失败,策略被其它服务应用");
                }

            }
        }

        return ResponseResult.success();
    }


    /**
     * 启动，关闭策略
     */
    @UserLoginToken
    @RequestMapping(value = "changeStrategyStatus", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult changeStrategyStatus(@RequestParam("strategyId") String strategyId,
                                               @RequestParam(value = "strategyStatus") boolean strategyStatus) {

        if (org.apache.commons.lang3.StringUtils.isEmpty(strategyId)) {
            return ResponseResult.error("可调负荷运行策略编号不能为空！");
        }

        if (!strategyStatus) {
            List<String> list = new ArrayList<>();
            list.add(strategyId);

            //zph 20230507   判定需求响应里面是否有关联
            List<String> demandRespStrategies = demandRespStrategyRepository.findBeAssociatedWithStrategyIds(list);

            if (demandRespStrategies != null && demandRespStrategies.size() > 0) {

                return ResponseResult.error("此策略已与需求响应任务关联，不能关闭!");
            }
        }


        ScheduleStrategy scheduleStrategy = null;
        if (userService.isManger()) {
            scheduleStrategy = scheduleStrategyRepository.findByStrategyId(strategyId);
        } else {
            String userId = RequestHeaderContext.getInstance().getUserId();
            scheduleStrategy = scheduleStrategyRepository.findByUserIdAndStrategyId(userId, strategyId);
        }

        if (scheduleStrategy == null) {
            return ResponseResult.error("策略不存在或者没有权限更新该策略！");
        }
        if (strategyStatus) {
            scheduleStrategy.setStrategyStatus(strategyStatus);
        } else {
            scheduleStrategy.setStrategyStatus(strategyStatus);

            List<Long> collect = sysJobRepository.findAllByJobGroup(scheduleStrategy.getStrategyId()).
                    stream().map(SysJob::getJobId).collect(Collectors.toList());

            try {
                jobService.deleteJobByIds(collect);
            } catch (SchedulerException e) {
                return ResponseResult.error("关闭策略，删除策略任务");
            }

        }

        scheduleStrategyRepository.save(scheduleStrategy);
        return ResponseResult.success();


    }
}
