package com.example.vvpdomain;

import com.example.vvpdomain.entity.SysJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

/**
 * @author zph
 * @description 定时任务调度表
 * @date 2022-07-01
 */
@Repository
public interface SysJobRepository extends JpaRepository<SysJob, Long>, JpaSpecificationExecutor<SysJob> {

    /**
     * 查询调度任务日志集合
     *
     * @return 操作日志集合
     */
    @Query(value = "select * \n" +
            "\t\tfrom sys_job where " +
            " if(ISNULL(?1),1=1,job_name like concat('%', ?1, '%')) " +
            " and if(ISNULL(?2),1=1,job_group=?2) " +
            " and if(ISNULL(?3),1=1,status=?3) " +
            " and if(ISNULL(?4),1=1,invoke_target=?4)  "
            , nativeQuery = true)
    public List<SysJob> selectJobList(String jobName, String jobGroup, String status, String invokeTarget);

    /**
     * 查询所有调度任务
     *
     * @return 调度任务列表
     */
    @Query(value = " select * \n" +
            "\t\tfrom sys_job", nativeQuery = true)
    public List<SysJob> selectJobAll();

    /**
     * 通过调度ID查询调度任务信息
     *
     * @param jobId 调度ID
     * @return 角色对象信息
     */
    @Query(value = " select * \n" +
            "\t\tfrom sys_job where job_id =:jobId ", nativeQuery = true)
    public SysJob selectJobById(@Param("jobId") Long jobId);

    /**
     * 通过调度ID删除调度任务信息
     *
     * @param jobId 调度ID
     * @return 结果
     */
    @Transactional//对事物的支持,操作超时等
    @Modifying//如果是删除或者是修改就需要加上此注解
    @Query(value = "delete from sys_job where job_id = :jobId", nativeQuery = true)
    public int deleteJobById(@Param("jobId") Long jobId);

    /**
     * 批量删除调度任务信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Transactional//对事物的支持,操作超时等
    @Modifying//如果是删除或者是修改就需要加上此注解
    @Query(value = "delete from sys_job where job_id in (:ids)", nativeQuery = true)
    public int deleteJobByIds(@Param("ids") Collection<String> ids);

    /**
     * 新增调度任务信息
     * 修改调度任务信息
     *
     * @param job 调度任务信息
     * @return 结果
     */
    @Override
    public SysJob saveAndFlush(SysJob job);


    public SysJob findByJobName(String jobName);


    public List<SysJob> findAllByJobGroup(String jobGroup);

}