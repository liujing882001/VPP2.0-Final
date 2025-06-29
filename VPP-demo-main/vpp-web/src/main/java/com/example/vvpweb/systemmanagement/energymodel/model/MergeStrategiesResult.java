package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
public class MergeStrategiesResult {

    /**
     * 开始日期
     */
    private Date startDate;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束日期
     */
    private Date endDate;
    /**
     * 结束时间
     */
    private Date endTime;
    List<StrategyDistributionModel> result;
//    public MergeStrategiesResult(String startDate,String startTime,String endDate,String endTime,List<EnergyStrategyDistributionModel> result) throws ParseException {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
//        this.startDate = format.parse(startDate);
//        this.startTime = dateFormat.parse(startTime);
//        this.endDate = format.parse(endDate);
//        this.endTime = dateFormat.parse(endTime);
//        this.result = result;
//    }
    public MergeStrategiesResult(List<StrategyDistributionModel> dataList) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date earliestStartDate = null;
        Date latestEndDate = null;
        for (StrategyDistributionModel model : dataList) {
            Date startDate = model.getStartDate();
            Date endDate = model.getEndDate();

            if (earliestStartDate == null || startDate.before(earliestStartDate)) {
                earliestStartDate = startDate;
            }

            if (latestEndDate == null || endDate.after(latestEndDate)) {
                latestEndDate = endDate;
            }
        }
        this.startDate = earliestStartDate;
        this.endDate = latestEndDate;
        this.result = dataList;
    }
}
