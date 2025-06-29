package com.example.vvpscheduling.service.demand.service;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.StringUtils;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpscheduling.service.ISysJobService;
import com.example.vvpscheduling.util.constant.ScheduleConstants;
import com.example.vvpservice.controlservice.IDeviceControl;
import com.example.vvpservice.demand.model.DemandResponseInvitationStatinfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("demandRespTask")
public class DemandRespTaskServiceImpl implements IDemandRespTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemandRespTaskServiceImpl.class);
    @Autowired
    private DemandRespTaskRepository respTaskRepository;
    @Autowired
    private SysJobRepository sysJobRepository;
    @Autowired
    private ISysJobService sysJobService;
    @Autowired
    private DemandRespStrategyNoRepository noRepository;
    @Resource
    private DemandRespStrategyRepository strategyRepository;
    @Resource
    private IDeviceControl deviceControl;
    @Resource
    private AiLoadRepository aiLoadRepository;
    @Value("${server.port}")
    private int port;
    @Resource
    private SysParamRepository sysParamRepository;

    @Override
    public void initRespTask(String respId) {
        LOGGER.info("需求响应任务执行中：" + respId);
        try {
            //把状态置为执行中
            DemandRespTask respTaskReq = respTaskRepository.findByRespId(respId);

            //更新老定时任务的状态为1
            SysJob oldJob = sysJobRepository.findById(respTaskReq.getJobId()).get();
            oldJob.setStatus(ScheduleConstants.Status.PAUSE.getValue());
            oldJob.setUpdateBy("admin");
            sysJobService.changeStatus(oldJob);

            //防止重启时，有些脏数据或者未执行完的定时任务
            //判断该笔任务为 未开始或者执行中
            if (respTaskReq.getDStatus() == 1 || respTaskReq.getDStatus() == 2) {
                SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                dateSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
                timeSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                String reTime = dateSdf.format(respTaskReq.getRsDate()) + " " + timeSdf.format(respTaskReq.getReTime());
                Date now = new Date();

                // 判断，如果执行时间超过，就不用再新建结束任务，直接结束该需求响应
                if (TimeUtil.strToDateFormatYMDHMS(reTime).after(now)) {
                    respTaskReq.setDStatus(2);//执行中
                    //添加结束任务
                    //新增
                    SysJob sysJob = new SysJob();
                    sysJob.setJobName("结束需求响应任务" + respTaskReq.getTaskCode());
                    sysJob.setJobGroup("需求响应");


                    String[] reTimeArr = timeSdf.format(respTaskReq.getReTime()).split(":");
                    String[] rsDateArr = dateSdf.format(respTaskReq.getRsDate()).split("-");
                    sysJob.setCronExpression("0 " + Integer.valueOf(reTimeArr[1]).intValue() + " "
                            + Integer.valueOf(reTimeArr[0]).intValue() + " "
                            + Integer.valueOf(rsDateArr[2]).intValue() + " " +
                            Integer.valueOf(rsDateArr[1]).intValue() + " ? " + Integer.valueOf(rsDateArr[0]).intValue());
                    sysJob.setInvokeTarget("demandRespTask.cancelRespTask('" + respId + "')");
                    sysJob.setMisfirePolicy("3");//计划执行错误策略
                    sysJob.setConcurrent("1");//0允许 1禁止
                    sysJob.setCreateBy(respTaskReq.getCreateBy());
                    sysJob = sysJobService.insertJobDemand(sysJob);

                    sysJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());//状态（0正常 1暂停）
                    sysJobService.changeStatus(sysJob);

                    //更新响应任务的状态
                    respTaskReq.setJobId(sysJob.getJobId());
                    respTaskRepository.save(respTaskReq);

                    //查询策略户号相关的内容
                    List<DemandRespStrategyNo> noList = noRepository.findNoListByRespId(respId);
                    List<String> sId = new ArrayList<>();
                    if (noList != null && noList.size() > 0) {
                        List<DemandRespStrategyNo> newList = new ArrayList<>();
                        noList.forEach(n -> {
                            //若状态为11-未申报
                            if (n.getDrsStatus() == 11) {
                                n.setDrsStatus(12);//执行中未申报
                            } else if (n.getDrsStatus() == 22) {//之前是若状态为21-已申报，因为目前自动出清，暂时改为是22已申报已出清
                                n.setDrsStatus(24);//执行中已申报
                                sId.add(n.getDemandRespStrategy().getSId());
                            }
                            newList.add(n);
                        });
                        noRepository.saveAll(newList);
                    }

                    // 执行策略
                    List<String> strategyList = strategyRepository.findStrategyIdsBySId(respId,sId);
                    log.info("strategyList:{}",strategyList);
                    if (strategyList != null && strategyList.size() > 0) {
                        System.out.println("需求响应任务respId = " + respId + "发送命令到IOT");
                        //todo，iot控制需要修改的地方这里选用的策略是还是需要创建的策略而不是算法推荐的策略需要修改，
                        deviceControl.sendDeviceControlCommandMessageDemand(strategyList, respTaskReq.getRespType(), respTaskReq.getDStatus(),respId);
                    }
                } else {
                    //结束任务
                    endTask(respTaskReq);
                }
            }

        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.error("需求响应任务执行有误：" + e.getMessage());
        }
    }

    @Override
    public void cancelRespTask(String respId) {
        LOGGER.info("结束需求响应任务执行中：" + respId);
        try {
            //把状态置为已结束  ,并计算收益
            Optional<DemandRespTask> task = respTaskRepository.findById(respId);

            if (task.isPresent()) {
                DemandRespTask respTaskReq = task.get();
                //更新老定时任务的状态为1
                SysJob oldJob = sysJobRepository.findById(respTaskReq.getJobId()).get();
                oldJob.setStatus(ScheduleConstants.Status.PAUSE.getValue());
                oldJob.setUpdateBy("admin");
                sysJobService.changeStatus(oldJob);

                if (respTaskReq.getDStatus() != 3 && respTaskReq.getDStatus() != 0) {
                    endTask(respTaskReq);
                }

            }
        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.error("结束需求响应任务执行有误：" + e.getMessage());
        }
    }

    /**
     * 结束任务相关处理
     *
     * @param respTaskReq
     */
    private void endTask(DemandRespTask respTaskReq) {
        try {
            respTaskReq.setDStatus(3);//已完成
            int declareStatus = 1;//默认置为未申报状态

            //得到小时差值
            double hour = TimeUtil.getDifferHour(respTaskReq.getRsTime(), respTaskReq.getReTime());

            //实际响应负荷
            double actualLoad = 0.00;
            //实际申报负荷
            double declareLoad = 0.00;
            //实际响应电量
            double totalActualPower = 0.00;
            //总收益
            double totalProfit = 0;
            //查询策略户号相关的内容
            List<DemandRespStrategyNo> noList = noRepository.findNoListByRespId(respTaskReq.getRespId());
            double respSubsidy = respTaskReq.getRespSubsidy() == null ? 0 : respTaskReq.getRespSubsidy();

            List<DemandRespStrategyNo> newList = new ArrayList<>();
            List<DemandRespStrategyNo> kafkaList = new ArrayList<>();

            if (noList != null && noList.size() > 0) {
                //获取节点
                List<String> nodeList = noList.stream().map(n -> n.getNodeId()).collect(Collectors.toList());
                //拼接开始、结束日期，查询ai_load_forecasting表里的片段值
                SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                dateSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
                timeSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//                String rsTime = dateSdf.format(respTaskReq.getRsDate()) + " " + timeSdf.format(respTaskReq.getRsTime());
//                String reTime = dateSdf.format(respTaskReq.getRsDate()) + " " + timeSdf.format(respTaskReq.getReTime());
                Date sDate =respTaskReq.getRsTime();
                Date eDate = respTaskReq.getReTime();

                //得到响应期间的片段的统计值日期15分钟一个点
                List<Date> dateList = TimeUtil.demandSplit15Minutes(sDate, eDate);
                Specification<AiLoadForecasting> spec = new Specification<AiLoadForecasting>() {
                    @Override
                    public Predicate toPredicate(Root<AiLoadForecasting> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                        List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                        predicates.add(cb.in(root.get("nodeId")).value(nodeList));
                        //原是nengyuanzongbiao测试用kongtiaoxitong
                        predicates.add(cb.equal(root.get("systemId"), "kongtiaoxitong"));
                        predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
                        criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                        criteriaQuery.orderBy(cb.asc(root.get("countDataTime"))); //按照createTime升序排列
                        return criteriaQuery.getRestriction();
                    }
                };
                //查到所有节点的片段值
                List<AiLoadForecasting> loadForecastingList = aiLoadRepository.findAll(spec);
                Map<String, List<AiLoadForecasting>> nodeLoadMap = loadForecastingList.stream().
                        collect(Collectors.groupingBy(AiLoadForecasting::getNodeId));

                //查询，从哪里取基线负荷的值
                SysParam sysParam=sysParamRepository.findSysParamBySysParamKey(SysParamEnum.BaseLineForecastCfg.getId());
                JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
                String getMethod = "商汤";
                if (obj != null) {
                    if (obj.get("baseLineGetMethod") != null) {
                        getMethod=obj.get("baseLineGetMethod").toString();
                    }
                }

                for (DemandRespStrategyNo n : noList) {
                    //实际响应负荷电量
                    double realTimeLoad = 0.00;
                    //实际响应申报电量
                    double actualPower = 0.00;
                    //收益
                    double profit = 0.00;

                    List<AiLoadForecasting> forecastingList = nodeLoadMap.get(n.getNodeId());

                    //每个时刻点对应的实际负荷
                    Map<Date, Double> realLoadMap = new HashMap<>();
                    if (forecastingList != null && forecastingList.size() > 0) {
                        boolean flag = false; //当前循环默认没取到值
                        int iCount = 0;//累加少加次数
                        double realTimeLoadTemp = 0d;//实际响应负荷 临时变量
                        double realTimeLoadTempF = 0d;//实际响应负荷 前一次能取到值 临时变量
                        //如果需求响应时间段内，数据缺失，我们就按照离缺失时间点最近的时间点统计（但必须是在需求响应时间段内）
                        for (int z = 0; z < dateList.size(); z++) {
                            Date date = dateList.get(z);
                            b:
                            for (int i = 0; i < forecastingList.size(); i++) {
                                realTimeLoadTemp = 0d;
                                AiLoadForecasting ai = forecastingList.get(i);
                                if (Date.from(ai.getCountDataTime().atZone(ZoneId.systemDefault()).toInstant()).equals(date)) {
                                    //20230605n zph  基线负荷 默认值为 -，类型为字符串
                                    double baseValue = 0.0;
                                    if(getMethod.equals("商汤")){
                                        baseValue=StringUtils.convertBaseLineValueToDouble(ai.getBaselineLoadValue().toString());
                                    }else{
                                        baseValue=StringUtils.convertBaseLineValueToDouble(ai.getBaselineLoadValueOther());
                                    }

                                    double realValue = StringUtils.convertBaseLineValueToDouble(ai.getRealValue().toString());

                                    //实时响应负荷=实际负荷-基线负荷
                                    if (respTaskReq.getRespType() == 1) {//削峰
                                        realTimeLoadTemp = (baseValue - realValue);
                                    } else {//填谷
                                        realTimeLoadTemp = (realValue - baseValue);
                                    }
                                    realTimeLoadTempF = realTimeLoadTemp;
                                    // realTimeLoad += realTimeLoadTemp;
                                    realLoadMap.put(Date.from(ai.getCountDataTime().atZone(ZoneId.systemDefault()).toInstant()), realTimeLoadTemp);
                                    flag = true;
                                    break b;
                                } else {
                                    flag = false;
                                }
                            }
                            //当前循环没取到值 循环一次加一次
                            if (!flag) {
                                iCount++;//累加循环次数
                            } else if (flag && iCount > 0) {//当前循环取到值
                                for (int k = 0; k < iCount; k++) {//少加几次加几次
                                    //  realTimeLoad += realTimeLoadTemp;//用当前响应负荷 补足之前少加次数
                                    realLoadMap.put(date, realTimeLoadTemp);
                                }
                                flag = false;//为下次循环 默认没取到值
                                iCount = 0;//次数清空
                            }
                            //次数不为空，且是最后一次
                            if (iCount > 0 && z == dateList.size() - 1) {
                                for (int k = 0; k < iCount; k++) {//少加几次加几次
                                    //   realTimeLoad += realTimeLoadTempF;//累加之前能取到值的一次
                                    realLoadMap.put(date, realTimeLoadTempF);
                                }
                            }
                        }
                        //（基线负荷-实际负荷）*(需求响应分钟数)/15
                        //计算总电量的示例
                        //12:06-12:15对应12:15的值
                        //12:16-12:30对应12:30的值
                        //12:31-12:45对应12:45的值
                        //12:46-12:50对应12:45的值
                        LocalDateTime startLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(sDate.getTime()), ZoneOffset.of("+8"));
                        LocalDateTime endLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(eDate.getTime()), ZoneOffset.of("+8"));
                        LocalDateTime lastQuarter = startLt.truncatedTo(ChronoUnit.HOURS)
                                .plusMinutes(15 * (startLt.getMinute() / 15));

                        double minuteTemp = 0;
                        while (!endLt.isBefore(lastQuarter)) {
                            Date date = new Date(lastQuarter.toInstant(ZoneOffset.ofHoursMinutes(+8, 0)).toEpochMilli());
                            //如果日期不在开始日期之前，则加入结果集
                            if (!date.before(sDate)) {
                                double minute = TimeUtil.getDifferMinute(sDate, date) - minuteTemp;
                                realTimeLoad += realLoadMap.get(date) * minute / 15;
                                minuteTemp += minute;
                            }
                            lastQuarter = lastQuarter.plusMinutes(15);
                        }
                        //如果结束日期大于日期片段，最后一次处理
                        Date end = dateList.get(dateList.size() - 1);
                        Double minute = TimeUtil.getDifferMinute(end, eDate);
                        if (minute > 0 && minute < 15) {
                            realTimeLoad += realLoadMap.get(dateList.get(dateList.size() - 1)) * minute / 15;
                        }
                    }

                    //若状态为12-执行中未申报
                    if (n.getDrsStatus() == 12) {
                        n.setDrsStatus(15);//已结束未申报
                        realTimeLoad = 0;
                    } else if (n.getDrsStatus() == 24) {//若状态为24-执行中已申报
                        n.setDrsStatus(25);//已结束已申报

                        double declareLoadOld = n.getDeclareLoad() == null ? 0 : n.getDeclareLoad();
                        declareLoad += declareLoadOld;
                        //实际响应申报电量
                        actualPower = declareLoadOld * hour;

                        declareStatus = 2;//申报状态为已申报

                        //收益 = 削减/填谷电量 * 响应补贴，若收益为负值，则置为0  废弃
                      //  profit = realTimeLoad * hour * respSubsidy;
                        //收益= 申报电量*响应补贴
                        profit=declareLoad*hour*respSubsidy;
                        n.setRealTimeLoad(realTimeLoad);//已申报的节点才会算统计值
                        if (profit < 0) {
                            profit = 0;
                        }
                        totalActualPower += realTimeLoad;

                        if (n.getIsPlatform() != null && n.getIsPlatform() == 1) {//如果是第三方平台
                            kafkaList.add(n);
                        }
                    }
                    totalProfit += profit;
                    n.setProfit(profit);
                    newList.add(n);
                }
                noRepository.saveAll(newList);
            }

            //实际申报电量
            double declarePower = declareLoad * hour;

            // respTaskReq.setActualLoad(actualLoad);
            respTaskReq.setActualPower(totalActualPower);
            respTaskReq.setProfit(totalProfit);//总收益
            respTaskReq.setDeclareLoad(declareLoad);//总申报负荷
            respTaskReq.setDeclarePower(declarePower);//总申报电量
           // respTaskReq.setDeclareStatus(declareStatus);//申报状态 南网在下发的时候，已经设置成已申报出清成功状态
            respTaskRepository.save(respTaskReq);

            // 执行策略
            List<String> strategyList = strategyRepository.findStrategyIds(respTaskReq.getRespId());
            if (strategyList != null && strategyList.size() > 0) {
                System.out.println("需求响应结束任务respId = " + respTaskReq.getRespId() + "发送命令到IOT");
                deviceControl.sendDeviceControlCommandMessageDemand(strategyList, respTaskReq.getRespType(), respTaskReq.getDStatus(),respTaskReq.getRespId());
            }

            //发给第三方平台
//            if (kafkaList != null && kafkaList.size() > 0) {
//                String jsonStr = transDeclareKafka(respTaskReq, kafkaList);
//                String url = "http://localhost:" + port + "/v1/demandResponseInvitationStatinfo";
//                //发送数据到API接口
//                HttpUtil.okHttpPost(url, jsonStr);
//            }
        } catch (Exception e) {
            LOGGER.info("需求响应结束任务出异常：" + e.getMessage());
//            e.printStackTrace();
        }
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

        List<DemandResponseInvitationStatinfo> list = new ArrayList<>();

        for (DemandRespStrategyNo no : noList) {
            if (no.getIsPlatform() != null && no.getIsPlatform() == 1) {//如果是第三方平台
                DemandResponseInvitationStatinfo taskKafka = new DemandResponseInvitationStatinfo();
                taskKafka.setDemandId(task.getRespId());
                taskKafka.setDemandDate(formatter_ymd.format(task.getRsDate()));

                //目前只有一个时间段
                String[] demandTime = new String[]{(hm.format(task.getRsTime()) + "-" + hm.format(task.getReTime()))};
                taskKafka.setDemandTime(demandTime);
                taskKafka.setDemandType(task.getRespType() + "");
                taskKafka.setDemandValue(no.getDeclareLoad());//此时的负荷需求=申报负荷
                taskKafka.setDemandPrice(task.getRespSubsidy());
                taskKafka.setMeterAccountNumber(no.getNoHouseholds());
                taskKafka.setStatValue(no.getRealTimeLoad());//实际响应负荷

                list.add(taskKafka);
            }
        }

        return JSONObject.toJSONString(list);
    }
}
