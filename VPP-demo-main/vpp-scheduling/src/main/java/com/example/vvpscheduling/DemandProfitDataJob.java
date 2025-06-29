package com.example.vvpscheduling;

import com.example.vvpcommom.EntityUtils;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.DemandProfitRepository;
import com.example.vvpdomain.DemandRespStrategyNoRepository;
import com.example.vvpdomain.entity.DemandProfit;
import com.example.vvpscheduling.model.DemandProfitRevertModel;
import com.example.vvpservice.demand.service.DemandCalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 每日需求响应收益计算
 * add by maoyating 20240319
 */
@Component("demandProfitData")
@EnableAsync
public class DemandProfitDataJob {

    private static Logger LOGGER = LoggerFactory.getLogger(DemandProfitDataJob.class);
    @Resource
    private DemandRespStrategyNoRepository noRepository;
    @Resource
    private DemandProfitRepository profitRepository;

    @Scheduled(cron = "0 4 0 * * ?") // 每天凌晨00:04:00执行任务
//@Scheduled(cron = "0 50 16 * * ?") // 每天凌晨00:01:00执行任务
    @Async
    public void profitJob() {
        try {
            LOGGER.info("每日需求响应收益的计算开始");
            Date date = new Date();
            //查询前一天所有的需求响应收益信息
            // 获取当前日期
            LocalDate today = LocalDate.now();
            // 获取前一天的日期
            LocalDate yesterday = today.minusDays(1);
            // 定义日期格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // 格式化日期
            String formattedDate = yesterday.format(formatter);
            List<Object[]> list = noRepository.findBySumRsDate(
                    TimeUtil.strDDToDate(formattedDate,"yyyy-MM-dd"), 25, 3);

            List<DemandProfit> profitList = new ArrayList<>();
            if (list != null && list.size() > 0) {
                //得到统计信息
                List<DemandProfitRevertModel> datas = EntityUtils.castEntity(list, DemandProfitRevertModel.class, new DemandProfitRevertModel());
                //根据每个节点区分
                Map<String,List<DemandProfitRevertModel>> nodeMap = datas.stream().collect(Collectors.groupingBy(DemandProfitRevertModel::getNode_id));

                for(String nodeId:nodeMap.keySet()){
                    List<DemandProfitRevertModel> nodeList = nodeMap.get(nodeId);
                    double totalCutPower = 0.00;//削峰电量
                    double totalFillPower = 0.00;//填谷电量
                    double totalDeclareLoad = 0.00;//总申报负荷

                    double totalCutProfit = 0.00;//削峰总收益
                    double totalFillProfit = 0.00;//填谷总收益
                    double totalVolumeProfit = 0.00;//容量收益
                    double totalProfit=0.00;//总收益
                    for (DemandProfitRevertModel d : nodeList) {
                        if (d.getResp_type() == 1) {
                            totalCutPower += d.getReal_time_load() != null ? d.getReal_time_load() : 0.00;
                            //统计削峰需求响应任务的总收益，总收益为电网返回的总收益，如果电网没有返回，这里为预估收益
                            if (d.getVolume_profit() != null) {
                                totalCutProfit += d.getVolume_profit();
                            } else if (d.getProfit() != null) {
                                totalCutProfit += d.getProfit();
                            }
                        } else if (d.getResp_type() == 2) {
                            totalFillPower += d.getReal_time_load() != null ? d.getReal_time_load() : 0.00;
                            //统计填谷需求响应任务的总收益，总收益为电网返回的总收益，如果电网没有返回，这里为预估收益
                            if (d.getVolume_profit() != null) {
                                totalFillProfit += d.getVolume_profit();
                            } else if (d.getProfit() != null) {
                                totalFillProfit += d.getProfit();
                            }
                        }
                        totalDeclareLoad += d.getDeclare_load() != null ? d.getDeclare_load() : 0.00;
                        //统计容量补贴总收益，总收益为电网返回的总收益，如果电网没有返回，这里为空
                        totalVolumeProfit += d.getVolume_profit() != null ? d.getVolume_profit() : 0.00;

                    }
                    //总收益
                    totalProfit=totalCutProfit + totalFillProfit + totalVolumeProfit;

                    String[] dateArr=formattedDate.split("-");

                    DemandProfit profit= new DemandProfit();
                    profit.setProfitId(formattedDate+"_"+nodeId);
                    profit.setNodeId(nodeId);
                    profit.setProfitDate(TimeUtil.strDDToDate(formattedDate,"yyyy-MM-dd"));
                    profit.setCreatedTime(date);
                    profit.setUpdateTime(date);
                    profit.setPeakProfit(totalCutProfit);
                    profit.setFillProfit(totalFillProfit);
                    profit.setVolumeProfit(totalVolumeProfit);
                    profit.setTotalProfit(totalProfit);
                    profit.setPeakPower(totalCutPower);
                    profit.setFillProfit(totalFillPower);
                    profit.setDeclareLoad(totalDeclareLoad);
                    profit.setProfitMonth(Integer.parseInt(dateArr[1]));
                    profit.setProfitYear(Integer.parseInt(dateArr[0]));
                    profit.setProfitYearMonth(Integer.parseInt(dateArr[0]+dateArr[1]));
                    profit.setProfitYearMonthStr(dateArr[0]+"-"+dateArr[1]);

                    profitList.add(profit);
                }
                //将查询到的数据，入库
                profitRepository.saveAll(profitList);
            }

            LOGGER.info("每日需求响应收益的计算结束");
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.info("每日需求响应收益的计算出现异常了！！！！！！");
        }
    }


}
