package com.example.vvpscheduling.service.impl;

import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.SysJobRepository;
import com.example.vvpdomain.entity.SysJob;
import com.example.vvpscheduling.service.ISysJobService;
import com.example.vvpscheduling.util.CronUtils;
import com.example.vvpscheduling.util.ScheduleUtils;
import com.example.vvpscheduling.util.constant.ScheduleConstants;
import com.example.vvpscheduling.util.exception.TaskException;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 定时任务调度信息 服务层
 */
@Service
public class SysJobServiceImpl implements ISysJobService {
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SysJobRepository jobMapper;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @PostConstruct
    public void init() throws SchedulerException, TaskException {
        try {
            scheduler.clear();
            List<SysJob> jobList = jobMapper.findAll();
            Date now = new Date();
            for (SysJob job : jobList) {
                try {
                    if (job.getJobGroup().equals("需求响应") || job.getJobGroup().equals("辅助服务")) {
                        if (job.getStatus().equals(ScheduleConstants.Status.NORMAL.getValue())) {
                            //如果需求响应执行时间已过，需要重新设置任务执行时间，  否则会报错，任务也无法执行达到闭环
                            Date date = CronUtils.getNextExecution(job.getCronExpression());
                            if (date == null) {
                                //重新设置该任务的cron表达式,将该任务的时间设置为当前时间延后一分钟
                                Date addDate = TimeUtil.dateAddMinutes(now, 1);
                                SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                dateSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                                String[] dateStr = dateSdf.format(addDate).split(" ");
                                String[] rsDateArr = dateStr[0].split("-");
                                String[] reTimeArr = dateStr[1].split(":");
                                job.setCronExpression("0 " + Integer.valueOf(reTimeArr[1]).intValue() + " "
                                        + Integer.valueOf(reTimeArr[0]).intValue() + " "
                                        + Integer.valueOf(rsDateArr[2]).intValue() + " " +
                                        Integer.valueOf(rsDateArr[1]).intValue() + " ? " + Integer.valueOf(rsDateArr[0]).intValue());
                            }
                            ScheduleUtils.createScheduleJob(scheduler, job);
                        }
                    } else {
                        ScheduleUtils.createScheduleJob(scheduler, job);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取quartz调度器的计划任务列表
     *
     * @param job 调度信息
     * @return
     */
    @Override
    public List<SysJob> selectJobList(SysJob job) {
        return jobMapper.selectJobList(job.getJobName(), job.getJobGroup(), job.getStatus(), job.getInvokeTarget());
    }

    /**
     * 通过调度任务ID查询调度信息
     *
     * @param jobId 调度任务ID
     * @return 调度任务对象信息
     */
    @Override
    public SysJob selectJobById(Long jobId) {
        return jobMapper.selectJobById(jobId);
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pauseJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        SysJob save = jobMapper.save(job);
        int rows = 0;
        if (save != null) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            rows = 1;
        }
        return rows;
    }

    /**
     * 恢复任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resumeJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        SysJob save = jobMapper.save(job);
        int rows = 0;
        if (save != null) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            rows = 1;
        }
        return rows;
    }

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        int rows = jobMapper.deleteJobById(jobId);
        if (rows > 0) {
            scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 批量删除调度信息
     *
     * @param jobIds 需要删除的任务ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByIds(List<Long> jobIds) throws SchedulerException {
        for (Long jobId : jobIds) {
            SysJob job = jobMapper.selectJobById(jobId);
            deleteJob(job);
        }
    }

    /**
     * 任务调度状态修改
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(SysJob job) throws SchedulerException {
        int rows = 0;
        String status = job.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            rows = resumeJob(job);
        } else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            rows = pauseJob(job);
        }
        return rows;
    }

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        SysJob properties = selectJobById(job.getJobId());
        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
        scheduler.triggerJob(ScheduleUtils.getJobKey(jobId, jobGroup), dataMap);
    }

    /**
     * 新增任务
     *
     * @param job 调度信息 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertJob(SysJob job) throws SchedulerException, TaskException {
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        SysJob save = jobMapper.save(job);
        int rows = 0;
        if (save != null) {
            ScheduleUtils.createScheduleJob(scheduler, job);
            rows = 1;
        }
        return rows;
    }

    /**
     * 新增任务--需求响应
     *
     * @param job 调度信息 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysJob insertJobDemand(SysJob job) throws SchedulerException, TaskException {
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        SysJob save = jobMapper.save(job);
        int rows = 0;
        if (save != null) {
            ScheduleUtils.createScheduleJob(scheduler, job);
            rows = 1;
        }
        return save;
    }

    /**
     * 更新任务的时间表达式
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateJob(SysJob job) throws SchedulerException, TaskException {
        SysJob properties = selectJobById(job.getJobId());
        properties.setCronExpression(job.getCronExpression());
        properties.setUpdateBy(job.getUpdateBy());
        SysJob save = jobMapper.save(properties);
        int rows = 0;
        if (save != null) {
            updateSchedulerJob(save, properties.getJobGroup());
            rows = 1;
        }
        return rows;
    }

    /**
     * 更新任务
     *
     * @param job      任务对象
     * @param jobGroup 任务组名
     */
    public void updateSchedulerJob(SysJob job, String jobGroup) throws SchedulerException, TaskException {
        Long jobId = job.getJobId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    @Override
    public boolean checkCronExpressionIsValid(String cronExpression) {
        return CronUtils.isValid(cronExpression);
    }

    @Override
    public SysJob selectJobByName(String jobName) {
        return jobMapper.findByJobName(jobName);
    }
}
