package com.example.vvpscheduling;

import com.example.vvpcommom.CronUtils;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.ScheduleStrategyRepository;
import com.example.vvpdomain.SysJobRepository;
import com.example.vvpdomain.entity.ScheduleStrategy;
import com.example.vvpdomain.entity.SysJob;
import com.example.vvpscheduling.service.ISysJobService;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableAsync
public class ScheduleStrategyJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleStrategyJob.class);

    @Autowired
    private ScheduleStrategyRepository scheduleStrategyRepository;

    @Autowired
    private ISysJobService iSysJobService;

    @Autowired
    private SysJobRepository sysJobRepository;

    /**
     * 更新策略下的任务
     */
    @Scheduled(initialDelay = 1000 * 5, fixedDelay = 60 * 1000)
    @Async
    @Transactional
    public void produceSysJobsOfScheduleStrategy() {
        List<ScheduleStrategy> all = scheduleStrategyRepository.findAll();
        all.forEach(e -> {

            try {

                // 策略为启动状态
                if (e.isStrategyStatus() && !e.isDemandResponse()) {

                    if (e.getCronExpression() != null && !"".equals(e.getCronExpression())) {

                        /**
                         * 运行策略类型 0 一次性 或者 1 周期性
                         */
                        if (e.getRunStrategy() == 0) {
                            String exeDateTime = CronUtils.onceExeTime(e.getCronExpression());
                            /**
                             * 一次执行任务时间过去一天后，删除一次执行任务
                             */
                            if (TimeUtil.stringToDate(exeDateTime).before(TimeUtil.dateAddDay(new Date(), -1))) {
                                doRemoveService(e.getStrategyId());
                            } else {
                                if (TimeUtil.stringToDate(exeDateTime).after(new Date())) {
                                    doService(e.getStrategyId(),
                                            e.getStrategyId(),
                                            e.getCronExpression(),
                                            "deviceControl.sendDeviceControlCommandMessage('" + e.getStrategyId() + "')",
                                            e.getStrategyName());
                                }
                            }

                        } else {
                            doService(e.getStrategyId(),
                                    e.getStrategyId(),
                                    e.getCronExpression(),
                                    "deviceControl.sendDeviceControlCommandMessage('" + e.getStrategyId() + "')",
                                    e.getStrategyName());
                        }


                    }

                } else {
                    doRemoveService(e.getStrategyId());
                }

            } catch (Exception ex) {
                LOGGER.error("检查策略{}任务出现异常,异常{}", e.getStrategyName(), ex.getMessage());
            }

        });
    }

    void doRemoveService(String groupName) {
        List<Long> collect = sysJobRepository.findAllByJobGroup(groupName).
                stream().map(SysJob::getJobId).collect(Collectors.toList());
        try {
            if (!collect.isEmpty()) {
                iSysJobService.deleteJobByIds(collect);
            }
        } catch (SchedulerException e) {
            LOGGER.error("关闭策略{}任务失败,异常{}", groupName, e.getMessage());
        }
    }

    void doService(String jobName, String jobGroup, String cron, String invokeTarget, String remark) {
        SysJob byStrategyName = iSysJobService.selectJobByName(jobName);
        if (byStrategyName != null && byStrategyName.getCronExpression().equals(cron)) {
            return;
        }

        SysJob onSysJob = null;
        if (byStrategyName == null) {
            onSysJob = new SysJob();
        } else {
            onSysJob = byStrategyName;
        }
        onSysJob.setJobName(jobName);
        onSysJob.setJobGroup(jobGroup);
        onSysJob.setCronExpression(cron);
        onSysJob.setInvokeTarget(invokeTarget);
        onSysJob.setConcurrent("1");
        onSysJob.setMisfirePolicy("3");
        onSysJob.setRemark(remark);
        try {
                    /*
                        插入任务默认关闭
                     */
            iSysJobService.insertJob(onSysJob);
            onSysJob.setStatus("0");

                    /*
                        更新启动任务
                     */
            iSysJobService.changeStatus(onSysJob);
        } catch (Exception ex) {
            LOGGER.error("生成策略{}任务失败,异常{}", jobName, ex.getMessage());
        }

    }
}
