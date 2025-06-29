package com.example.vvpservice.demand.service;

import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.AiLoadRepository;
import com.example.vvpdomain.DemandCalendarRepository;
import com.example.vvpdomain.DemandRespTaskRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.entity.AiLoadForecasting;
import com.example.vvpdomain.entity.DemandCalendar;
import com.example.vvpdomain.entity.Node;
import com.example.vvpservice.ai.service.AiLoadForecastingService;
import com.example.vvpservice.demand.model.BaseLineLoadModel;
import com.example.vvpservice.demand.model.DemandModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DemandCalendarServiceImpl implements DemandCalendarService {

    private static Logger logger = LoggerFactory.getLogger(DemandCalendarServiceImpl.class);

    @Resource
    AiLoadRepository aiLoadRepository;
    @Resource
    private DemandCalendarRepository calendarRepository;
    @Resource
    private DemandRespTaskRepository taskRepository;
    @Resource
    AiLoadForecastingService aiLoadForecastingService;
    @Resource
    private NodeRepository nodeRepository;

    @Override
    @Async
    public void generateBaseline(Date date) {
        getBaselineLoadData(date);
    }

    /**
     *         //工作日
     *         //1.响应日期前1天，向前取5天（不包括非工作日、已实施响应日、有序用电日），对应响应时段负荷
     *         //2.基线计算剔除响应时段内负荷均值<5个样本平均负荷25%  或者 >5个样本平均负荷200% 的样本
     *         //  剔除不足5个样本，向前旋转额，直至满5个
     *         //3.原则上不超过30天，超过30天，则选择4天样本，依次类推
     *         //计算公式：P
     *
     *         //非工作日（国家法定节假日除外）
     *         //1.响应日前1天，向前取3天的对应响应时段负荷（不包括工作日、已实施响应日、有序用电日）
     *         //2.计算剔除负荷均值<3个样本平均负荷25%  或者 >3个样本平均负荷200% 样本
     *         //  同上剔除不足3个样本，向前选择
     *         //3.原则上同 工作日，也是依次递减，公式一样
     *
     *
     *         //国家法定节假日
     *         //1.以上一年同一节假日 且 非响应日，有序用电日 对应时段的响应基线
     *         //2.若上一年同一节假日为已实施响应、有序用电日，则再取前一年数据
     *         //3.新接电用户无同期历史负荷的，采用非工作日基线计算方法
     *         //4.基线负荷的计算过程和结果信息应按照不低于三年时间保存
     *
     */
    public void getBaselineLoadData(Date date) {

        try {
//            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            sdfDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            Date date = sdfDate.parse("2023-07-07 11:00:00");

            // 判断今天属于什么日期状态
            DemandCalendar currentCalendar = calendarRepository.findByDate(date);
            if(currentCalendar!=null){
                if (currentCalendar.getDateType() ==1) { //1-工作日
                    weekday(currentCalendar.getDateType(),5,30,date,null);
                }else if(currentCalendar.getDateType() ==2){//2-非工作日
                    weekday(currentCalendar.getDateType(),3,30,date,null);
                }else if(currentCalendar.getDateType() ==3){//3-非计算日

                }else{//节假日
                    festival(currentCalendar.getDateType(),date);
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     *        * 1、推算6月28日15:00的基线，往前推5天的15：00的时刻的实时负荷值（6月23日-6月27日 每天15：00的实时负荷），
     *      * 例如6月24号15：00正好是有笔响应任务，就得把这个时刻的值剔除掉，再往前找一天。
     *      *
     *      * 2、负荷均值=5天15：00该时刻的实时负荷值（剔除过响应时间的时刻值）的总和 / 5。
     *      *
     *      * 3、再用算出来的负荷均值，去判断这五天15：00实时负荷的样本符不符合 <5个样本平均负荷25%  或者 >5个样本平均负荷200% ？
     *      * 否则剔除该天15：00的负荷值，再往前一天找，再重新计算负荷均值，对比样本。依此类推...
     *      *
     *      * 注意：不用考虑6月28日15：00是否是属于响应时间段，不需要关注未来的响应任务。
     *      * 只考虑计算的前5天内的15：00这刻的负荷值 是否属于 响应时刻，若是，则剔除。
     */
    private void weekday(int dateType, int sampleCount, int pageSize, Date date, List<String> nodeList){
        try{
            //每天15分钟的时间格式化
            List<String> everyTimeList = TimeUtil.generateTimeList();

            //最终需要保存的数据
            List<AiLoadForecasting> saveList = new ArrayList<>();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
            sdfTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdfDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
            fmt.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            //查询最近的30个工作日/非工作日,最多也就30个工作日/非工作日，一并查出
            List<Date> allDate = calendarRepository.findByDateTypePageSize(dateType,pageSize,date);
            List<String> aiDates = new ArrayList<>();

            for(int i=0;i<allDate.size();i++){
                aiDates.add(sdf.format(allDate.get(i)));
            }

            //查询这30个日期，有哪些日期 与响应任务时间有交集
            List<Object[]> demandDateList = taskRepository.findByRsDates(allDate);
            List<DemandModel> demandList = new ArrayList<>();
            if(demandDateList!=null && demandDateList.size()>0){
                demandDateList.forEach(d->{
                    DemandModel demand = new DemandModel();
                    try {
                        String rsDateStr = sdf.format(d[0]);
                        Date rsDate = sdf.parse(rsDateStr);
                        demand.setRsDate(rsDate);
                        String rsTime = sdfDate.format(d[1]);
                        Date rs = sdfDate.parse(rsTime);
                        demand.setRsTime(rs);
                        String reTime = sdfDate.format(d[2]);
                        Date re = sdfDate.parse(reTime);
                        demand.setReTime(re);

                        demand.setNodeId(d[3].toString());

                        demand.setStartDate(rs);
                        demand.setEndDate(re);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    demandList.add(demand);
                });
            }

            //查出所有30天的日期数据 ---此对象包含节点 统计时间等
            List<AiLoadForecasting> realTimeLoadList = new ArrayList<>();
            if(nodeList==null || nodeList.size()==0){
                realTimeLoadList = aiLoadRepository.findAllByCountDataTimes(aiDates);
            }else{
                //如果节点id不为空，说明是新用户，没有历史数据
                realTimeLoadList = aiLoadRepository.findAllByCountDataTimesAndNodes(aiDates,nodeList);
            }

            //先清洗样本
            //筛选出时间不处于响应时间段内的实时负荷值
            List<AiLoadForecasting> timeLoadList = isNotBetweenList(realTimeLoadList,demandList);

            //根据节点分组
            Map<String,List<AiLoadForecasting>> aiMap = timeLoadList.stream()
                    .collect(Collectors.groupingBy(AiLoadForecasting::getNodeId));
            if(timeLoadList!=null && timeLoadList.size()>0){
                //遍历每个节点的数据
                for(String nodeId:aiMap.keySet()){
                    List<BaseLineLoadModel> loadList = convertAi(aiMap.get(nodeId));

                    //根据时刻分组
                    Map<String,List<BaseLineLoadModel>> timeLoadMap = loadList.stream()
                            .collect(Collectors.groupingBy(BaseLineLoadModel::getCountTime));

                    Map<String,AiLoadForecasting> saveMap = new HashMap<>();

                    //根据时刻分组做计算
                    for(String time:timeLoadMap.keySet()){
                        //按时间降序
                        List<BaseLineLoadModel> baseLineLoadModelList = timeLoadMap.get(time).stream()
                                .sorted(Comparator.comparing(BaseLineLoadModel::getCountDataTime).reversed()).collect(Collectors.toList());

                        // 查询实时负荷值并计算基线负荷均值
                        double avgRealValue = calculateSampleAverage(baseLineLoadModelList,sampleCount);

                        Date countDateTime = sdfDate.parse(sdf.format(date)+" "+time);
                        String id = nodeId + "_" + loadList.get(0).getSystemId() + "_" + fmt.format(countDateTime);

                        AiLoadForecasting ai = new AiLoadForecasting();
                        ai.setId(id);
                        ai.setNodeId(nodeId);
                        ai.setSystemId(baseLineLoadModelList.get(0).getSystemId());
                        ai.setCountDataTime(countDateTime);
                        ai.setBaselineLoadValueOther(avgRealValue+"");

                        saveMap.put(time,ai);
                    }
                    //判断哪个时刻没有数据，补齐“-”
                    for(String time:everyTimeList){
                        if(!saveMap.containsKey(time)){
                            AiLoadForecasting ai = new AiLoadForecasting();
                            Date countDateTime = sdfDate.parse(sdf.format(date)+" "+time);
                            String id = nodeId + "_" + loadList.get(0).getSystemId() + "_" + fmt.format(countDateTime);

                            ai.setId(id);
                            ai.setNodeId(nodeId);
                            ai.setSystemId(loadList.get(0).getSystemId());
                            ai.setCountDataTime(countDateTime);
                            ai.setBaselineLoadValueOther("-");

                            saveMap.put(time,ai);

                            saveList.add(ai);
                        }else{
                            saveList.add(saveMap.get(time));
                        }
                    }

                }
            }
            //批量更新/新增基线负荷数据
            if(saveList!=null && saveList.size()>0){
                aiLoadForecastingService.batchInsertOrUpdate(saveList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 计算满足条件的平均样本值
     * @param list 已经筛选过响应时间 的实时负荷数据
     * @param sampleCount 最大样本天数
     *
     * //判断 此刻的实时负荷 是否 <5个样本总和的平均负荷25%  或者 >5个样本5个样本总和的平均负荷200% ，不足就往前查一个日期，直到30天
     * //以上5个样本都不满足，就按4个样本算，直到30天，若还不满足，按3个样本,直到1个样本...
     * @return
     */
    private static double calculateSampleAverage(List<BaseLineLoadModel> list,int sampleCount) {
        //平均负荷值
        double averageSampleValue = 0.0;
        double baseSum = 0.0;

        int day = 0;//天数临时变量
        while (sampleCount > 0) {
            day = sampleCount;

            //key:countDate
            Map<String,BaseLineLoadModel> map = new ConcurrentHashMap<>();
            //遍历所有符合条件的样本值
            for(int i=0;i<list.size();i++){
                if(i>=day){
                    break;
                }
                BaseLineLoadModel baseLine = list.get(i);
                //总和 ,必须要BigDecimal，不然直接相减会丢失精度
                baseSum=sum(baseSum,baseLine.getRealValue());
                map.put(baseLine.getCountData(),baseLine);
                //若等于最后要取值的天数-1，则判断进行第一次判断所取的样本值的平均值是否符合
                if(i==day-1){
                    //平均值
                    double average = baseSum / sampleCount;

                    // 检查样本值是否符合条件
                    for (String countDate : map.keySet()) {
                        BaseLineLoadModel base = map.get(countDate);
                        double realValue = Double.valueOf(base.getRealValue());
                        //如果不符合 sum-该样本值,继续往前找一个样本
                        if (realValue < average * 0.25 || realValue > average * 2) {
                            baseSum= sub(baseSum,base.getRealValue());
                            map.remove(countDate);

                            day+=1;//日期往前找
                        }
                    }
                }
            }
            if (map.size() >= sampleCount) {
                averageSampleValue = baseSum / sampleCount;
                break;
            }
            //如果没有break，说明之前的数据不达标，需要重新将总和置为0
            baseSum=0;
            sampleCount--;
        }

        return averageSampleValue;
    }

    /**
     * 筛选节点、统计时间不再在响应开始时间和结束结束时间之间（包括边界） 的AI历史数据
     * @param baseLineLoadModelList
     * @param demandModelList
     * @return
     */
    private static List<AiLoadForecasting> isNotBetweenList(List<AiLoadForecasting> baseLineLoadModelList, List<DemandModel> demandModelList) {
        List<AiLoadForecasting> loadModelList = new ArrayList<>();
        for(AiLoadForecasting baseLine:baseLineLoadModelList){
            boolean flag = true;
            out:
            for(DemandModel demandModel:demandModelList){
                if(baseLine.getNodeId().equals(demandModel.getNodeId())){
                    if(baseLine.getCountDataTime().getTime()>=demandModel.getStartDate().getTime()
                            && baseLine.getCountDataTime().getTime()<=demandModel.getEndDate().getTime()){
                        flag=false;
                        break out;
                    }
                }
            }
            if(flag){
                loadModelList.add(baseLine);
            }
        }
        return loadModelList;

    }


    /**
     * 转换AI数据
     * @param loadList
     * @return
     */
    private List<BaseLineLoadModel> convertAi(List<AiLoadForecasting> loadList){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        sdfTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        List<BaseLineLoadModel> lineList = new ArrayList<>();
        loadList.forEach(e->{
            BaseLineLoadModel model = new BaseLineLoadModel();
            model.setId(e.getId());
            model.setNodeId(e.getNodeId());
            model.setSystemId(e.getSystemId());
            model.setRealValue(e.getRealValue());
            model.setCountDataTime(e.getCountDataTime());
            model.setCountData(sdf.format(e.getCountDataTime()));
            model.setCountTime(sdfTime.format(e.getCountDataTime()));
            model.setCountYear(Integer.parseInt(model.getCountData().split("-")[0]));
            lineList.add(model);

        });
        return lineList;
    }

    /**
     * 节假日算法
     *
     * 以上一年同一节假日且非响应日有序用日对应时段的响应基线。
     * 若上一年同一节假日为已实施响应日、有序用电日，则再取前一年数据。
     * 新接电用户无同期历史负荷的，采用非工作日基线计算方法。
     *
     * 以上一年节假日XXX——》指的是上一年度的同节日，例如端午节总共几天，所算出来的均值，作为基线值
     * “新接电用户无同期历史负荷的，采用非工作日基线计算方法。”若是端午节，找上年端午节，没有找到历史数据，就按照非工作日基线计算实现。
     */
    private void festival(Integer dateType,Date date){
        try{
            //每天15分钟的时间格式化
            List<String> everyTimeList = TimeUtil.generateTimeList();

            //最终需要保存的数据
            List<AiLoadForecasting> saveList = new ArrayList<>();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
            sdfTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdfDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
            fmt.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");
            getYearFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            String currentYear = getYearFormat.format(date);

            //查询年份小于今年之前的所有相同节假日数据
            List<Date> allDate = calendarRepository.findByDateTypeYear(dateType,Integer.valueOf(currentYear));
            List<String> aiDates = new ArrayList<>();

            for(int i=0;i<allDate.size();i++){
                aiDates.add(sdf.format(allDate.get(i)));
            }

            //查询这些节假日数据，有哪些日期 与响应任务时间有交集
            List<Object[]> demandDateList = taskRepository.findByRsDates(allDate);
            List<DemandModel> demandList = new ArrayList<>();
            if(demandDateList!=null && demandDateList.size()>0){
                demandDateList.forEach(d->{
                    DemandModel demand = new DemandModel();
                    try {
                        String rsDateStr = sdf.format(d[0]);
                        Date rsDate = sdf.parse(rsDateStr);
                        demand.setRsDate(rsDate);
                        String rsTime = sdfDate.format(d[1]);
                        Date rs = sdfDate.parse(rsTime);
                        demand.setRsTime(rs);
                        String reTime = sdfDate.format(d[2]);
                        Date re = sdfDate.parse(reTime);
                        demand.setReTime(re);

                        demand.setNodeId(d[3].toString());

                        demand.setStartDate(rs);
                        demand.setEndDate(re);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    demandList.add(demand);
                });
            }

            //查出所有节假日数据 ---此对象包含节点 统计时间等
            List<AiLoadForecasting> realTimeLoadList = aiLoadRepository.findAllByCountDataTimes(aiDates);

            //先清洗样本
            //筛选出时间不处于响应时间段内的实时负荷值
            List<AiLoadForecasting> timeLoadList = isNotBetweenList(realTimeLoadList,demandList);

            //根据节点分组
            Map<String,List<AiLoadForecasting>> aiMap = timeLoadList.stream()
                    .collect(Collectors.groupingBy(AiLoadForecasting::getNodeId));
            if(timeLoadList!=null && timeLoadList.size()>0){
                //遍历每个节点的数据
                for(String nodeId:aiMap.keySet()){
                    List<BaseLineLoadModel> loadList = convertAi(aiMap.get(nodeId));

                    //根据时刻分组
                    Map<String,List<BaseLineLoadModel>> timeLoadMap = loadList.stream()
                            .collect(Collectors.groupingBy(BaseLineLoadModel::getCountTime));

                    Map<String,AiLoadForecasting> saveMap = new HashMap<>();

                    //根据时刻分组做计算
                    for(String time:timeLoadMap.keySet()){
                        //按时间降序
                        List<BaseLineLoadModel> baseLineLoadModelList = timeLoadMap.get(time).stream()
                                .sorted(Comparator.comparing(BaseLineLoadModel::getCountDataTime).reversed()).collect(Collectors.toList());

                        // 查询实时负荷值并计算基线负荷均值
                        double avgRealValue = calculateFestivalAverage(baseLineLoadModelList);

                        Date countDateTime = sdfDate.parse(sdf.format(date)+" "+time);
                        String id = nodeId + "_" + loadList.get(0).getSystemId() + "_" + fmt.format(countDateTime);

                        AiLoadForecasting ai = new AiLoadForecasting();
                        ai.setId(id);
                        ai.setNodeId(nodeId);
                        ai.setSystemId(baseLineLoadModelList.get(0).getSystemId());
                        ai.setCountDataTime(countDateTime);
                        ai.setBaselineLoadValueOther(avgRealValue+"");

                        saveMap.put(time,ai);
                    }
                    //判断哪个时刻没有数据，补齐“-”
                    for(String time:everyTimeList){
                        AiLoadForecasting ai = saveMap.get(time);
                        if(ai==null){
                            ai = new AiLoadForecasting();

                            Date countDateTime = sdfDate.parse(sdf.format(date)+" "+time);
                            String id = nodeId + "_" + loadList.get(0).getSystemId() + "_" + fmt.format(countDateTime);

                            ai.setId(id);
                            ai.setNodeId(nodeId);
                            ai.setSystemId(loadList.get(0).getSystemId());
                            ai.setCountDataTime(countDateTime);
                            ai.setBaselineLoadValueOther("-");

                            saveMap.put(time,ai);
                        }
                        saveList.add(ai);
                    }

                }
            }
            //批量更新/新增基线负荷数据
            if(saveList!=null && saveList.size()>0){
                aiLoadForecastingService.batchInsertOrUpdate(saveList);
            }
            //todo
            //根据楼宇分组判断，哪些楼宇没有以往的节假日的数据，若没有，则需要按照 非工作日的公式进行计算
            List<Node> nodeList = nodeRepository.findAllBySystemIdsContains("nengyuanzongbiao");
            Map<String,List<AiLoadForecasting>> nodeMap = saveList.stream().collect(Collectors.groupingBy(AiLoadForecasting::getNodeId));
            List<String> nodes = new ArrayList<>();
            nodeList.forEach(n->{
                //如果不包含某个楼宇
                if(!nodeMap.containsKey(n.getNodeId())){
                    nodes.add(n.getNodeId());
                }
            });
            //将以下没有数据的节点，按照 非工作日的公式进行计算
            if(nodes!=null && nodes.size()>0){
                weekday(2,3,30,date,nodes);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 计算满足条件的节假日平均样本值
     * @param list 已经筛选过响应时间 的实时负荷数据
     * 根据年份分组
     * //判断 此刻的实时负荷 是否 <n个样本总和的平均负荷25%  或者 >n个样本总和的平均负荷200% ，不足就往前查一年
     * @return
     */
    private static Double calculateFestivalAverage(List<BaseLineLoadModel> list) {
        //平均负荷值
        double averageSampleValue = 0.0;
        double baseSum = 0.0;
        //根据年份分组
        Map<Integer,List<BaseLineLoadModel>> yearMap = list.stream().collect(Collectors.groupingBy(BaseLineLoadModel::getCountYear));
        Set<Integer> keySet = yearMap.keySet();
        Iterator<Integer> iter = keySet.iterator();
        //根据年份遍历
        while (iter.hasNext()) {
            Integer key = iter.next();
            List<BaseLineLoadModel> yList = yearMap.get(key);
            //样本天数
            int sampleCount = yList.size();

            int day = 0;//天数临时变量
            while (sampleCount > 0) {
                day = sampleCount;

                //key:countDate
                Map<String, BaseLineLoadModel> map = new ConcurrentHashMap<>();
                //遍历所有符合条件的样本值
                for (int i = 0; i < list.size(); i++) {
                    if (i >= day) {
                        break;
                    }
                    BaseLineLoadModel baseLine = list.get(i);
                    //总和
                    baseSum=sum(baseSum,baseLine.getRealValue());
                    map.put(baseLine.getCountData(), baseLine);
                    //若等于最后要取值的天数-1，则判断进行第一次判断所取的样本值的平均值是否符合
                    if (i == day - 1) {
                        //平均值
                        double average = baseSum / sampleCount;

                        // 检查样本值是否符合条件
                        for (String countDate : map.keySet()) {
                            BaseLineLoadModel base = map.get(countDate);
                            double realValue = Double.valueOf(base.getRealValue());
                            //如果不符合 sum-该样本值,继续往前找一个样本
                            if (realValue < average * 0.25 || realValue > average * 2) {
                                baseSum=sub(baseSum,baseLine.getRealValue());
                                map.remove(countDate);

                                day += 1;//日期往前找
                            }
                        }
                    }
                }

                if (map.size() >= sampleCount) {
                    averageSampleValue = baseSum / sampleCount;
                    break;
                }
                //总和要清零
                baseSum=0;
                sampleCount--;
            }
            return averageSampleValue;
        }
        return null;
    }
    /**
     * double 相加
     * @param d1
     * @param d2
     * @return
     */
    public static double sum(double d1,String d2){
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.add(bd2).doubleValue();
    }


    /**
     * double 相减
     * @param d1
     * @param d2
     * @return
     */
    public static double sub(double d1,String d2){
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.subtract(bd2).doubleValue();
    }
}
