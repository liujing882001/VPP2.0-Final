package com.example.vvpdomain;

import com.example.vvpdomain.entity.SysJobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author zph
 * @description 定时任务调度日志表
 * @date 2022-07-01
 */
@Repository
public interface SysJobLogRepository extends JpaRepository<SysJobLog, Long>, JpaSpecificationExecutor<SysJobLog> {

    /**
     * 获取quartz调度器日志的计划任务
     *
     * @return 调度任务日志集合
     */
    @Query(value = "select * \n" +
            "\t\tfrom sys_job_log where " +
            " if(ISNULL(?1),1=1,job_name like concat('%', ?1, '%')) " +
            " and if(ISNULL(?2),1=1,job_group=?2) " +
            " and if(ISNULL(?3),1=1,status=?3) " +
            " and if(ISNULL(?4),1=1,invoke_target=?4)  " +
            " and if(ISNULL(?5),1=1,to_char(start_time,'yyyymmdd')>=to_char(?5,'yyyymmdd')) " +
            " and if(ISNULL(?6),1=1,to_char(stop_time,'yyyymmdd')>=to_char(?6,'yyyymmdd'))   " +
            " limit ?7 ,?8"
            , nativeQuery = true)
    public List<SysJobLog> selectJobLogList(String jobName, String jobGroup, String status, String invokeTarget, Date beginTime, Date endTime, int startRecord, int size);

    /**
     * 查询所有调度任务日志
     *
     * @return 调度任务日志列表
     */
    @Query(value = " select * \n" +
            "\t\tfrom sys_job_log", nativeQuery = true)
    public List<SysJobLog> selectJobLogAll();

    /**
     * 通过调度任务日志ID查询调度信息
     *
     * @param jobLogId 调度任务日志ID
     * @return 调度任务日志对象信息
     */
    @Query(value = " select * \n" +
            "\t\tfrom sys_job_log where job_log_id =:jobLogId", nativeQuery = true)
    public SysJobLog selectJobLogById(@Param("jobLogId") Long jobLogId);

    /**
     * 新增任务日志
     *
     * @param jobLog 调度日志信息
     * @return 结果
     */
    @Override
    public SysJobLog saveAndFlush(SysJobLog jobLog);

    /**
     * 批量删除调度日志信息
     *
     * @param logIds 需要删除的数据ID
     * @return 结果
     */
    @Transactional//对事物的支持,操作超时等
    @Modifying//如果是删除或者是修改就需要加上此注解
    @Query(value = "delete from sys_job_log where job_log_id in (:logIds)", nativeQuery = true)
    public int deleteJobLogByIds(@Param("logIds") Collection<Long> logIds);

    /**
     * 删除任务日志
     *
     * @param jobId 调度日志ID
     * @return 结果
     */
    @Transactional//对事物的支持,操作超时等
    @Modifying//如果是删除或者是修改就需要加上此注解
    @Query(value = "delete from sys_job_log where job_log_id =:jobId", nativeQuery = true)
    public int deleteJobLogById(@Param("jobId") Long jobId);

    /**
     * 清空任务日志
     */
    @Transactional//对事物的支持,操作超时等
    @Modifying//如果是删除或者是修改就需要加上此注解
    @Query(value = "truncate table sys_job_log", nativeQuery = true)
    public void cleanJobLog();

    @Query(value = "select count(*) from  sys_job_log", nativeQuery = true)
    long getTotalNumber();
}