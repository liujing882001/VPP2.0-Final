package com.example.vvpscheduling.controller;


import com.example.vvpcommom.ResponseResult;
import com.example.vvpdomain.entity.SysJob;
import com.example.vvpscheduling.service.ISysJobService;
import com.example.vvpscheduling.util.CronUtils;
import com.example.vvpscheduling.util.exception.TaskException;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.example.vvpcommom.ResponseResult.error;

/**
 * 调度任务信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends BaseExcelController {
    @Autowired
    private ISysJobService jobService;

    @Autowired
    private IExcelOutPutService iExcelOutPutService;

    /**
     * 查询定时任务列表
     */
    @RequestMapping(value = "list", method = {RequestMethod.POST})
    public ResponseResult<List<SysJob>> list(@RequestBody SysJob sysJob) {
        List<SysJob> list = jobService.selectJobList(sysJob);
        return ResponseResult.success(list);
    }

    /**
     * 导出定时任务列表
     */
    @RequestMapping(value = "export", method = {RequestMethod.POST})
    public void export(HttpServletResponse response, @RequestBody SysJob sysJob) {
        List<SysJob> list = jobService.selectJobList(sysJob);

        exec(response, list, SysJob.class, iExcelOutPutService);
    }

    /**
     * 获取定时任务详细信息
     */
    @RequestMapping(value = "getInfoById", method = {RequestMethod.POST})
    public ResponseResult getInfo(@RequestParam("jobId") Long jobId) {
        return ResponseResult.success(jobService.selectJobById(jobId));
    }

    /**
     * 新增定时任务
     */
    @RequestMapping(value = "addJob", method = {RequestMethod.POST})
    public ResponseResult add(@RequestBody SysJob job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            return error("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }

        SysJob sysJob = jobService.selectJobByName(job.getJobName());
        if (sysJob != null) {
            return error("新增任务'" + job.getJobName() + "'失败，任务名称已经存在");
        }

        //        job.setCreateBy(getUsername());

        return ResponseResult.success(jobService.insertJob(job));
    }

    /**
     * 修改定时任务
     */
    @RequestMapping(value = "updateJob", method = {RequestMethod.POST})
    public ResponseResult edit(@RequestParam("jobId") Long jobId,
                               @RequestParam("cronExpression") String cronExpression) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(cronExpression)) {
            return error("修改任务Cron表达式" + cronExpression + "失败，Cron表达式不正确");
        }
        SysJob job = new SysJob();
        job.setJobId(jobId);
        job.setCronExpression(cronExpression);

//        job.setUpdateBy(getUsername());


        return ResponseResult.success(jobService.updateJob(job));
    }

    /**
     * 定时任务状态修改
     */
    @RequestMapping(value = "changeStatus", method = {RequestMethod.POST})
    public ResponseResult changeStatus(@RequestParam("jobId") Long jobId,
                                       @RequestParam("status") String status) throws SchedulerException {
        SysJob newJob = jobService.selectJobById(jobId);
        newJob.setStatus(status);
        return ResponseResult.success(jobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @RequestMapping(value = "run", method = {RequestMethod.POST})
    public ResponseResult run(@RequestBody SysJob job) throws SchedulerException {
        jobService.run(job);
        return ResponseResult.success();
    }

    /**
     * 删除定时任务
     */

    @RequestMapping(value = "deleteByIds", method = {RequestMethod.POST})
    public ResponseResult remove(@RequestParam("jobIds") List<Long> jobIds) throws SchedulerException, TaskException {
        jobService.deleteJobByIds(jobIds);
        return ResponseResult.success();
    }
}
