package com.example.vvpweb.carbon;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.StringUtils;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.CaEmissionFactorRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.entity.CaEmissionFactor;
import com.example.vvpdomain.entity.Node;
import com.example.vvpservice.carbon.service.CaCollectionModelService;
import com.example.vvpweb.carbon.model.CaDisplacementAnalysisModel;
import com.example.vvpweb.carbon.model.SinkResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author maoyating
 * @description 碳资产-碳监控
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/carbon/monitoring")
@CrossOrigin
@Api(value = "碳资产管理-碳监控", tags = {"碳资产管理-碳监控"})
public class CaMonitoringController {

    @Autowired
    private CaEmissionFactorRepository caEmissionFactorRepository;
    @Autowired
    private CaCollectionModelService caCollectionModelService;
    @Autowired
    private NodeRepository nodeRepository;

    private DecimalFormat df = new DecimalFormat("0.00");

    @ApiOperation("获取碳排放量数据")
    @UserLoginToken
    @RequestMapping(value = "/getEmissionDataCount", method = {RequestMethod.POST})
    public ResponseResult getEmissionDataCount(@RequestParam("nodeId") String nodeId) {
        SimpleDateFormat formatterYmNumber = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat formatddNumber = new SimpleDateFormat("dd");
        Map<String, Object> objectMap = new HashMap<>();
        //当年的最后一天
        String thisYearEndDay = formatterYmNumber.format(TimeUtil.getLastOfYear(Integer.parseInt(TimeUtil.getThisYear())));
        String thisDay = formatterYmNumber.format(TimeUtil.getLastDay(1));
        String lastDay = formatterYmNumber.format(TimeUtil.getLastDay(2));
        int thisDayDD = Integer.valueOf(formatddNumber.format(TimeUtil.getLastDay(1)));
        int lastDayDD = Integer.valueOf(formatddNumber.format(TimeUtil.getLastDay(2)));

        String thisMonthStart = formatterYmNumber.format(new Date());
        String lastMonthStart = formatterYmNumber.format(TimeUtil.getLastMontOnDay());
        String lastMonthEnd = formatterYmNumber.format(TimeUtil.getLastMontThisDay());

        String thisYearStart = TimeUtil.getThisYear() + "01";
        String lastYearStart = (Integer.valueOf(TimeUtil.getThisYear()) - 1) + "01";
        String lastYearEnd = formatterYmNumber.format(TimeUtil.getLastYearThisDay());
        Integer[] keys = new Integer[]{1, 2, 3};
        Double thisDaySum = 0.0, lastDaySum = 0.0, thisMonthSum = 0.0, lastMonthSum = 0.0, thisYearSum = 0.0, lastYearSum = 0.0;
        for (Integer key : keys) {

//            Double thisDayData = caCollectionModelService.getEmissionDataCountYM(nodeId, key, thisDay, thisDay);//昨天和前天的数据
//            Double lastDayData = caCollectionModelService.getEmissionDataCountYM(nodeId, key, lastDay, lastDay);//前天的数据
//            thisDayData = thisDayData==0?0:thisDayData/(thisDayDD*1000.0);
//            lastDayData = lastDayData==0?0:lastDayData/(lastDayDD*1000.0);

            //将“碳足迹”中的范围1、2、3中的输入项，除以本月的天数，再乘以碳排放因子，获得昨天的碳排放量。
            Double thisDayData = caCollectionModelService.getEmissionDataCountDay(nodeId, key, thisDay, thisDay);//昨天和前天的数据
            Double lastDayData = caCollectionModelService.getEmissionDataCountDay(nodeId, key, lastDay, lastDay);//前天的数据
            thisDayData = thisDayData == 0 ? 0 : thisDayData / (thisDayDD * 1000.0);
            lastDayData = lastDayData == 0 ? 0 : lastDayData / (lastDayDD * 1000.0);

            Double thisMonthData = caCollectionModelService.getEmissionDataCountYM(nodeId, key, thisMonthStart, thisDay);
            thisMonthData = thisMonthData == 0 ? 0 : thisMonthData / 1000.0;
            Double lastMonthData = caCollectionModelService.getEmissionDataCountYM(nodeId, key, lastMonthStart, lastMonthEnd);
            lastMonthData = lastMonthData == 0 ? 0 : lastMonthData / 1000.0;
            //直接查到年底的数据
            Double thisYearData = caCollectionModelService.getEmissionDataCountYM(nodeId, key, thisYearStart, thisYearEndDay);
            thisYearData = thisYearData == 0 ? 0 : thisYearData / 1000.0;
            Double lastYearData = caCollectionModelService.getEmissionDataCountYM(nodeId, key, lastYearStart, lastYearEnd);
            lastYearData = lastYearData == 0 ? 0 : lastYearData / 1000.0;

            objectMap.put("thisDayScope" + key, thisDayData);//本日碳排放量
            objectMap.put("thisDayScope" + key + "Compare", lastDayData == 0 ? 0 : (thisDayData - lastDayData) / lastDayData * 100);//本日碳排放量同比
            objectMap.put("thisMonthScope" + key, thisMonthData);//本月碳排放量
            objectMap.put("thisMonthScope" + key + "Compare", lastMonthData == 0 ? 0 : (thisMonthData - lastMonthData) / lastMonthData * 100);//本月碳排放量同比
            objectMap.put("thisYearScope" + key, thisYearData);//本年碳排放量
            objectMap.put("thisYearScope" + key + "Compare", lastYearData == 0 ? 0 : (thisYearData - lastYearData) / lastYearData * 100);//本年碳排放量同比

            thisDaySum += thisDayData;
            lastDaySum += lastDayData;
            thisMonthSum += thisMonthData;
            lastMonthSum += lastMonthData;
            thisYearSum += thisYearData;
            lastYearSum += lastYearData;

        }
        objectMap.put("thisDayEmissionTotal", df.format(thisDaySum));//昨日总碳排放量
        objectMap.put("thisDayEmissionTotalCompare", df.format(lastDaySum == 0 ? 0 : (thisDaySum - lastDaySum) / lastDaySum * 100));////本日总碳排放量同比
        objectMap.put("thisMonthEmissionTotal", df.format(thisMonthSum));//本月总碳排放量
        objectMap.put("thisMonthEmissionTotalCompare", df.format(lastMonthSum == 0 ? 0 : (thisMonthSum - lastMonthSum) / lastMonthSum * 100));//本月总碳排放量
        objectMap.put("thisYearEmissionTotal", df.format(thisYearSum));//本年总碳排放量
        objectMap.put("thisYearEmissionTotalCompare", df.format(lastYearSum == 0 ? 0 : (thisYearSum - lastYearSum) / lastYearSum * 100));//本年总碳排放量
        return ResponseResult.success(objectMap);
    }

    @ApiOperation("获取不同范围碳排量分析")
    @UserLoginToken
    @RequestMapping(value = "/getDisplacementAnalysis", method = {RequestMethod.POST})
    public ResponseResult getDisplacementAnalysis(@RequestParam("nodeId") String nodeId,
                                                  @RequestParam("startTime") String startTime,
                                                  @RequestParam("endTime") String endTime) {
        Map<String, Object> objectMap = new HashMap<>();

        try {
            if (StringUtils.isEmpty(nodeId) || StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
                return ResponseResult.error("节点/起始日期/截止日期不能为空！");
            }
            //两个日期之间的具体月份
            List<String> monthDateList = TimeUtil.getMonthBetween(startTime, endTime);
            startTime = startTime.replaceAll("-", "");
            endTime = endTime.replaceAll("-", "");

            Double obj1 = 0.00;//范围一
            Double obj2 = 0.00;//范围二
            Double obj3 = 0.00;//范围三

            List<CaDisplacementAnalysisModel> caDisplacementAnalysisModels = new ArrayList<>();

            for (int i = 1; i <= 3; i++) {
                List<Object[]> tanPaiFang = caCollectionModelService.getDisplacementAnalysis(nodeId, i, startTime, endTime);
                List<CaDisplacementAnalysisModel> tempList = new ArrayList<>();
                if (tanPaiFang != null && tanPaiFang.size() > 0) {
                    for (Object[] object : tanPaiFang) {
                        CaDisplacementAnalysisModel caDisplacementAnalysisModel = new CaDisplacementAnalysisModel();
                        String dateTime = object[0].toString().substring(0, 4) + "-" + object[0].toString().substring(4, 6);
                        caDisplacementAnalysisModel.setDateTime(dateTime);

                        caDisplacementAnalysisModel.setScopetType(i + "");
                        double dischargeValue = Double.valueOf(object[1].toString());
                        dischargeValue = dischargeValue == 0 ? 0 : dischargeValue / 1000.0;
                        caDisplacementAnalysisModel.setDischargeValue(df.format(dischargeValue));
                        if (i == 1) {
                            obj1 += dischargeValue;
                        } else if (i == 2) {
                            obj2 += dischargeValue;
                        } else if (i == 3) {
                            obj3 += dischargeValue;
                        }

                        caDisplacementAnalysisModels.add(caDisplacementAnalysisModel);
                        tempList.add(caDisplacementAnalysisModel);
                    }
                }
                Map<String, List<CaDisplacementAnalysisModel>> map = tempList.stream().
                        collect(Collectors.groupingBy(CaDisplacementAnalysisModel::getDateTime));
                //日期为空的数据，补齐空值
                for (String month : monthDateList) {
                    List<CaDisplacementAnalysisModel> list = map.get(month);
                    if (list == null || list.size() == 0) {
                        CaDisplacementAnalysisModel caDisplacementAnalysisModel = new CaDisplacementAnalysisModel();
                        caDisplacementAnalysisModel.setDateTime(month);
                        caDisplacementAnalysisModel.setScopetType(i + "");
                        caDisplacementAnalysisModel.setDischargeValue(null);//直接赋空值
                        caDisplacementAnalysisModels.add(caDisplacementAnalysisModel);
                    }
                }
            }
            Map<String, List<CaDisplacementAnalysisModel>> map = caDisplacementAnalysisModels.stream().collect(
                    Collectors.groupingBy(CaDisplacementAnalysisModel::getScopetType));

            objectMap.put("scope1", df.format(obj1));//范围1
            objectMap.put("scope2", df.format(obj2));//范围2
            objectMap.put("scope3", df.format(obj3));//范围3
            objectMap.put("emissionTotal", df.format(obj1 + obj2 + obj3));//总碳排放量
            objectMap.put("emissionList", map);//折线图数据

            return ResponseResult.success(objectMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("查询有误");
        }
    }

    @ApiOperation("获取碳中和")
    @UserLoginToken
    @RequestMapping(value = "/getTradeDataCount", method = {RequestMethod.POST})
    public ResponseResult getTradeDataCount(@RequestParam("nodeId") String nodeId,
                                            @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
        //查询节点所属的省份
        Optional<Node> optionalNode = nodeRepository.findById(nodeId);
        if (!optionalNode.isPresent()) {
            return ResponseResult.error("该节点不存在");
        }
        Node node = optionalNode.get();
        //查询 碳排放因子 表，根据类型 找到对应的二氧化碳
        Specification<CaEmissionFactor> specEmission = new Specification<CaEmissionFactor>() {
            @Override
            public Predicate toPredicate(Root<CaEmissionFactor> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("sStatus"), 1));//状态
                //判断地址是否为空
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(node.getProvinceRegionName())) {
                    predicates.add(cb.equal(root.get("province"), node.getProvinceRegionName()));//省份
                } else if (org.apache.commons.lang3.StringUtils.isNotEmpty(node.getAddress())) {
                    predicates.add(cb.equal(root.get("province"), com.example.vvpcommom.StringUtils.getProvince(node.getAddress())));//省份
                }
                predicates.add(cb.equal(root.get("emissionFactorName"), "外购电力"));//碳排放因子名称 -- 外购电力
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        List<CaEmissionFactor> factorDatas = caEmissionFactorRepository.findAll(specEmission);
        double co2 = 0.00;
        if (factorDatas != null && factorDatas.size() > 0) {
            co2 = factorDatas.get(0).getCo2();
        }

        Map<String, Object> objectMap = new HashMap<>();
//        String lastTimeStart ="";
//        String lastTimeEnd ="";
//        if(startTime!=null) {
//            lastTimeStart = (Integer.valueOf(startTime.substring(0, 4)) - 1) + startTime.substring(4);
//        }
//        if(endTime!=null) {
//            lastTimeEnd = (Integer.valueOf(endTime.substring(0, 4)) - 1)+endTime.substring(4);
//        }

        Double obj1 = caCollectionModelService.getTradeDataCount(nodeId, 2, null, startTime, endTime);//绿电
        Double obj2 = caCollectionModelService.getTradeDataCount(nodeId, 3, null, startTime, endTime);//绿证
        Double trade = caCollectionModelService.getTradeDataCount(nodeId, 1, null, startTime, endTime);//碳交易量

//        Double obj1Last = caCollectionModelService.getTradeDataCount(nodeId,2,null, lastTimeStart,lastTimeEnd);//绿电
//        Double obj2Last = caCollectionModelService.getTradeDataCount(nodeId,3,null, lastTimeStart,lastTimeEnd);//绿证

//        Double guangFu = caCollectionModelService.getTradeDataCount(nodeId,2,2, startTime,endTime);//绿电-->光伏
        double guangFu = 0.00;//光伏需要从别的地方取数
//        Double guangFuLast = caCollectionModelService.getTradeDataCount(nodeId,2,1, lastTimeStart,lastTimeEnd);//绿电-->光伏

//        Double tjyl = obj1+obj2;
//        Double tjylLast = obj1Last+obj2Last;
        objectMap.put("greenElectricityBuy", df.format(obj1));//绿电购买量
//        objectMap.put("greenElectricityBuyCompare",df.format(obj1Last == 0 ? 0 : (obj1 - obj1Last) / obj1Last * 100));//绿电购买同比
        objectMap.put("greenSyndromeBuy", df.format(obj2));//绿证购买量
//        objectMap.put("greenSyndromeBuyCompare",df.format(obj2Last == 0 ? 0 : (obj2 - obj2Last) / obj2Last * 100));//绿证购买同比

        //将电量转换为碳
        // double transaction = ((obj1+obj2)*co2)/1000.0;
        objectMap.put("transaction", df.format(trade));//碳交易量
//        objectMap.put("transactionCompare",df.format(tjylLast == 0 ? 0 : (tjyl - tjylLast) / tjylLast * 100));//碳交易量同比
        objectMap.put("photovoltaicPowerGeneration", df.format(guangFu));//光伏发电量
//        objectMap.put("photovoltaicPowerGenerationCompare",df.format(guangFuLast));//光伏发电量同比

        //Double wind = caCollectionModelService.getTradeDataCount(nodeId,2,1, startTime,endTime);//绿电-->风能
        double wind = 0.00;//风电发电量
        objectMap.put("windPowerGeneration", wind);//风能发电量
        objectMap.put("windPowerGenerationCompare", "0");//风能发电量对比

        //总中和量 （t) = (绿证购买量 + 绿电购买量 + 风电发电量 + 光伏发电量 ）* 碳排放因子中的外购电力  + 碳交易量
        //将kg转为t
        double totalCo2 = ((obj1 + obj2 + wind + guangFu) * co2) / 1000.0 + trade;
        objectMap.put("totalCo2", df.format(totalCo2));//总碳中和量

        return ResponseResult.success(objectMap);
    }

    @ApiOperation("查询碳汇信息")
    @UserLoginToken
    @RequestMapping(value = "/getCarbonSink", method = {RequestMethod.POST})
    public ResponseResult getCarbonSink(@RequestParam("nodeId") String nodeId,
                                        @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {

        SinkResp sinkResp = new SinkResp();
        Object[] lhmj = caCollectionModelService.getSinkConfCount(nodeId, "lvhuamianji", "草坪", startTime, endTime);
        Double total = 0.0;

//        long monthDiff = 0L;
//        try {
//            //这里必须加1，比如查询2020-08至2020-08，应该为一个月
//            monthDiff = TimeUtil.getMonthDiff(startTime,endTime);
//        }catch (Exception e){
//            return ResponseResult.error("输入的日期异常 +" + e.getMessage());
//        }
        //greenCO2=CO2吸收量*月份*12*面积;
        if (lhmj != null) {
            sinkResp.setGreenArea(lhmj[0].toString());
            Double green = Double.valueOf(lhmj[1].toString());
            green = green == 0 ? 0 : green / 1000.0; //将二氧化碳转换单位
            sinkResp.setGreenEmissions(String.valueOf(green));
            total += green;

        }
        Object[] zzsm = caCollectionModelService.getSinkConfCount(nodeId, "zhongzhishumu", "树木", startTime, endTime);
        if (zzsm != null) {
            sinkResp.setTreeNum(zzsm[0].toString());
            Double green = Double.valueOf(zzsm[1].toString());
            green = green == 0 ? 0 : green / 1000.0; //将二氧化碳转换单位
            sinkResp.setTreeEmissions(green.toString());
            total += green;
        }
        sinkResp.setTotal(String.valueOf(total));
        List<Object[]> list = caCollectionModelService.getCaFactorList(nodeId, new String[]{"草坪", "树木"});
        for (Object[] object : list) {
            double co2 = Double.valueOf(object[1].toString());
            co2 = co2 / 30.0;
            if (object[0].toString().equals("草坪")) {
                sinkResp.setDayGreenCO2(String.valueOf(co2));
            } else if (object[0].toString().equals("树木")) {
                sinkResp.setDayTreeCO2(String.valueOf(co2));
            }
        }
        return ResponseResult.success(sinkResp);
    }

}