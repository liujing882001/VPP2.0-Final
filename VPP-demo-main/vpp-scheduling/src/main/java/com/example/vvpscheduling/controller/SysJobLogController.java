package com.example.vvpscheduling.controller;


import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpdomain.entity.SysJobLog;
import com.example.vvpscheduling.service.ISysJobLogService;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 调度日志操作处理
 */
@RestController
@RequestMapping("/monitor/jobLog")
public class SysJobLogController extends BaseExcelController {
    @Autowired
    private ISysJobLogService jobLogService;

    @Autowired
    private IExcelOutPutService iExcelOutPutService;


    /**
     * 查询定时任务调度日志列表
     */
    @RequestMapping(value = "list", method = {RequestMethod.POST})
    public ResponseResult<PageModel> list(@RequestBody PageSysJobLog psj) {
        long totalNumber = jobLogService.getTotalNumber();
        int pages = (int) (totalNumber / psj.getPageSize());
        if (totalNumber % psj.getPageSize() > 0) {
            pages = pages + 1;
        }
        List<SysJobLog> list = jobLogService.selectJobLogList(psj.getSysJobLog(), psj.getNumber(), psj.getPageSize());
        PageModel pageModel = new PageModel();
        pageModel.setPageSize(psj.getPageSize());
        pageModel.setContent(list);
        pageModel.setTotalPages(pages);
        pageModel.setTotalElements((int) totalNumber);
        pageModel.setNumber(psj.getNumber());

        return ResponseResult.success(pageModel);
    }

    /**
     * 导出定时任务调度日志列表
     */
    @RequestMapping(value = "export", method = {RequestMethod.POST})
    public void export(HttpServletResponse response, @RequestBody PageSysJobLog psj) {
        List<SysJobLog> list = jobLogService.selectJobLogList(psj.getSysJobLog(), psj.getNumber(), psj.getPageSize());
        exec(response, list, SysJobLog.class, iExcelOutPutService);
    }

    /**
     * 根据调度编号获取详细信息
     */
    @RequestMapping(value = "getInfoById", method = {RequestMethod.POST})
    public ResponseResult getInfo(@RequestParam("jobLogId") Long jobLogId) {
        return ResponseResult.success(jobLogService.selectJobLogById(jobLogId));
    }


    /**
     * 删除定时任务调度日志
     */
    @RequestMapping(value = "deleteByIds", method = {RequestMethod.POST})
    public ResponseResult remove(@RequestParam("jobLogIds") List<Long> jobLogIds) {
        return ResponseResult.success(jobLogService.deleteJobLogByIds(jobLogIds));
    }

    /**
     * 清空定时任务调度日志
     */
    @RequestMapping(value = "clean", method = {RequestMethod.POST})
    public ResponseResult clean() {
        jobLogService.cleanJobLog();
        return ResponseResult.success();
    }

    public static class PageSysJobLog {
        private SysJobLog sysJobLog;
        /**
         * 每页大小
         */
        private int pageSize;
        /**
         * 当前页为第几页 默认 1开始
         */
        private int number;

        public SysJobLog getSysJobLog() {
            return sysJobLog;
        }

        public void setSysJobLog(SysJobLog sysJobLog) {
            this.sysJobLog = sysJobLog;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}
