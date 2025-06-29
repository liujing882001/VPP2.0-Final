package com.example.vvpscheduling.service.ancillary.service;

import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.AncillaryServicesRepository;
import com.example.vvpdomain.SysJobRepository;
import com.example.vvpdomain.entity.AncillarySStrategy;
import com.example.vvpdomain.entity.AncillaryServices;
import com.example.vvpdomain.entity.SysJob;
import com.example.vvpscheduling.service.ISysJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Service("ancillaryServices")
public class AncillaryServicesServiceImpl implements IAncillaryServicesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AncillaryServicesServiceImpl.class);

    @Autowired
    private AncillaryServicesRepository servicesRepository;
    @Autowired
    private SysJobRepository sysJobRepository;
    @Autowired
    private ISysJobService sysJobService;

    @Override
    public void initAncillaryTask(String asId) {
        LOGGER.info("辅助服务任务执行中：" + asId);
        try {
            //查询此刻执行的任务信息
            AncillaryServices ancillaryServices = servicesRepository.findById(asId).get();
            ancillaryServices.setAStatus(2);//执行中

            //添加结束任务
            //新增
            SysJob sysJob = new SysJob();
            sysJob.setJobName("结束辅助服务任务" + ancillaryServices.getTaskCode());
            sysJob.setJobGroup("辅助服务");//TODO 不太确定
            SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
            dateSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
            timeSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            String[] reTimeArr = timeSdf.format(ancillaryServices.getAseTime()).split(":");
            String[] rsDateArr = dateSdf.format(ancillaryServices.getAssDate()).split("-");
            sysJob.setCronExpression("0 " + Integer.valueOf(reTimeArr[1]).intValue() + " "
                    + Integer.valueOf(reTimeArr[0]).intValue() + " "
                    + Integer.valueOf(rsDateArr[2]).intValue() + " " +
                    Integer.valueOf(rsDateArr[1]).intValue() + " ? " + Integer.valueOf(rsDateArr[0]).intValue());
            sysJob.setInvokeTarget("ancillaryServices.cancelAncillaryTask('" + asId + "')");
            sysJob.setMisfirePolicy("3");//计划执行错误策略（1立即执行 2执行一次 3放弃执行）
            sysJob.setConcurrent("1");//0允许 1禁止
            sysJob.setStatus("0");//状态（0正常 1暂停）
            sysJob.setCreateBy(ancillaryServices.getCreateBy());
            sysJob = sysJobRepository.save(sysJob);
            sysJobService.insertJob(sysJob);
            sysJob.setStatus("0");//状态（0正常 1暂停）
            sysJobService.changeStatus(sysJob);

            //更新老定时任务的状态为1
            SysJob oldJob = sysJobRepository.findById(ancillaryServices.getJobId()).get();
            oldJob.setStatus("1");
            oldJob.setUpdateBy("admin");
            sysJobService.changeStatus(oldJob);

            //更新响应任务的状态
            ancillaryServices.setJobId(sysJob.getJobId());
            servicesRepository.save(ancillaryServices);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("辅助服务任务执行有误：" + e.getMessage());
        }
    }

    @Override
    public void cancelAncillaryTask(String asId) {
        LOGGER.info("结束辅助服务任务执行中：" + asId);
        try {
            //把状态置为已结束  ,并计算收益
            AncillaryServices services = servicesRepository.findById(asId).get();
            services.setAStatus(3);//已完成

            double hour = TimeUtil.getDifferHour(services.getAssTime(), services.getAseTime());
            //实际响应负荷
            double actualLoad = services.getStrategyList().stream().mapToDouble(AncillarySStrategy::getActualLoad).sum();
            //实际响应电量
            double actualPower = actualLoad * hour;
            //收益
            double profit = actualPower * services.getAsSubsidy();

            services.setActualLoad(actualLoad);
            services.setActualPower(actualPower);
            services.setProfit(profit);

            //更新老定时任务的状态为1
            SysJob oldJob = sysJobRepository.findById(services.getJobId()).get();
            oldJob.setStatus("1");
            oldJob.setUpdateBy("admin");
            sysJobService.changeStatus(oldJob);

            servicesRepository.save(services);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("结束辅助服务任务执行有误：" + e.getMessage());
        }
    }
}
