package com.example.vvpweb.profitmanagement;

import com.example.vvpcommom.PinyinUtils;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import com.example.vvpservice.nodeprofit.service.INodeProfitService;
import com.example.vvpservice.tree.model.StructTreeResponse;
import com.example.vvpservice.tree.service.ITreeLabelService;
import com.example.vvpweb.BaseExcelController;
import com.example.vvpweb.profitmanagement.model.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@RestController
@RequestMapping("/profit_management/node_profit")
@CrossOrigin
@Api(value = "收益管理-收益管理", tags = {"收益管理-收益管理"})
public class NodeProfitController extends BaseExcelController {


    @Autowired
    private NodeRepository nodeRepository;


    @Autowired
    private NodeProfitRepository nodeProfitRepository;

    @Autowired
    private NodeProfitMonthForecastingRepository nodeProfitMonthForecastingRepository;

    @Autowired
    private NodeProfitDayForecastingRepository nodeProfitDayForecastingRepository;


    @Autowired
    private INodeProfitService iNodeProfitService;

    @Autowired
    private CfgStorageEnergyShareProportionRepository shareProportionRepository;

    @Autowired
    private CfgPhotovoltaicDiscountRateRepository discountRateRepository;

    @Autowired
    private ITreeLabelService iTreeLabelService;

    @Resource
    private IExcelOutPutService iExcelOutPutService;

    /**
     * 收益类型
     */
    @UserLoginToken
    @RequestMapping(value = "profitTypeList", method = {RequestMethod.POST})
    public ResponseResult<List<ProfitTypeModel>> profitTypeList() {
        List<ProfitTypeModel> profitTypeModels = new ArrayList<>();
        Arrays.asList(ProfitType.values()).forEach(e -> {
            ProfitTypeModel model = new ProfitTypeModel();
            model.setCode(e.getCode());
            model.setName(e.getName());

            profitTypeModels.add(model);
        });

        return ResponseResult.success(profitTypeModels);

    }


    /**
     * 节点名列表
     */
    @UserLoginToken
    @RequestMapping(value = "areaNodeNameListByProfitType", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> nodeNameListByProfitType(@RequestParam("code") int code) {


        List<StructTreeResponse> treeResponses = new ArrayList<>();

        ProfitType profitType = ProfitType.getProfitType(code);
        if (profitType == null) {
            return ResponseResult.error("非法的收益类型码");
        }

        if (ProfitType.PV_PROFIT.equals(profitType)) {
            treeResponses = iTreeLabelService.pvNodeTree();
        }
        if (ProfitType.ALL_PROFIT.equals(profitType)) {
            treeResponses = iTreeLabelService.pvAndStorageEnergyNodeTree();

        }
        if (ProfitType.CHANGE_PROFIT.equals(profitType)) {
            treeResponses = iTreeLabelService.storageEnergyNodeTree();
        }

        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }

    /**
     * 节点名列表
     */
    @UserLoginToken
    @RequestMapping(value = "runAreaNodeNameListByProfitType", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> runNodeNameListByProfitType(@RequestParam("code") int code) {


        List<StructTreeResponse> treeResponses = new ArrayList<>();

        ProfitType profitType = ProfitType.getProfitType(code);
        if (profitType == null) {
            return ResponseResult.error("非法的收益类型码");
        }

        if (ProfitType.PV_PROFIT.equals(profitType)) {
            treeResponses = iTreeLabelService.runPvNodeTree();
        }
        if (ProfitType.ALL_PROFIT.equals(profitType)) {
            treeResponses = iTreeLabelService.runPvAndStorageEnergyNodeTree();

        }
        if (ProfitType.CHANGE_PROFIT.equals(profitType)) {
            treeResponses = iTreeLabelService.runStorageEnergyNodeTree();
        }

        if (treeResponses != null && treeResponses.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(treeResponses, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(treeResponses);
    }

    @UserLoginToken
    @RequestMapping(value = "getDayProfitListExcel", method = {RequestMethod.POST})
    public void getDayProfitListExcel(HttpServletResponse response, @RequestBody ProfitDayRequest profitDayRequest) {

        try {
            ResponseResult<List<Map<String, Object>>> result = getDayProfitList(profitDayRequest);

            exec(response, result.getData(), iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @UserLoginToken
    @RequestMapping(value = "getMonthProfitListExcel", method = {RequestMethod.POST})
    public void getMonthProfitListExcel(HttpServletResponse response, @RequestBody ProfitMonthRequest profitMonthRequest) {

        try {
            ResponseResult<List<Map<String, Object>>> result = getMonthProfitList(profitMonthRequest);

            exec(response, result.getData(), iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @UserLoginToken
    @RequestMapping(value = "getYearProfitListExcel", method = {RequestMethod.POST})
    public void getYearProfitListExcel(HttpServletResponse response, @RequestBody ProfitYearRequest profitYearRequest) {

        try {
            ResponseResult<List<Map<String, Object>>> result = getYearProfitList(profitYearRequest);

            exec(response, result.getData(), iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到节点日收益列表
     */
    @UserLoginToken
    @RequestMapping(value = "getDayProfitList", method = {RequestMethod.POST})
    public ResponseResult<List<Map<String, Object>>> getDayProfitList(@RequestBody ProfitDayRequest profitDayRequest) {

        List<Map<String, Object>> mapList = new ArrayList<>();
        List<Node> allByNodeIdIn = nodeRepository.findAllByNodeIdIn(profitDayRequest.getNodeId());

        List<Date> dates = TimeUtil.truncateToSplitDayContainEndDate(profitDayRequest.getStartTs(), profitDayRequest.getEndTs());


        List<String> pvNodeIdList = iNodeProfitService.getPvNodeIdList();
        List<String> storeEnergyNodeIdList = iNodeProfitService.getStoreEnergyNodeIdList();

        Map<String, Object> total = new HashMap<>();
        total.put("节点", "合计");
        total.put("合计", 0.00d);

        allByNodeIdIn.forEach(e ->
                mapList.add(doProduceNewResponse(pvNodeIdList, storeEnergyNodeIdList, e,
                        profitDayRequest.getStartTs(),
                        profitDayRequest.getEndTs(),
                        dates,
                        profitDayRequest.getUserType(),
                        total,
                        false,
                        false))
        );

        mapList.add(total);

        return ResponseResult.success(mapList);
    }



    /**
     * 得到节点月收益列表
     */
    @UserLoginToken
    @RequestMapping(value = "getMonthProfitList", method = {RequestMethod.POST})
    public ResponseResult<List<Map<String, Object>>> getMonthProfitList(@RequestBody ProfitMonthRequest profitMonthRequest) {

        List<Map<String, Object>> mapList = new ArrayList<>();
        List<Node> allByNodeIdIn = nodeRepository.findAllByNodeIdIn(profitMonthRequest.getNodeId());

        List<Date> dates = TimeUtil.truncateToSplitMonth(profitMonthRequest.getStartTs(), profitMonthRequest.getEndTs());


        List<String> pvNodeIdList = iNodeProfitService.getPvNodeIdList();
        List<String> storeEnergyNodeIdList = iNodeProfitService.getStoreEnergyNodeIdList();

        Map<String, Object> total = new HashMap<>();
        total.put("节点", "合计");
        total.put("合计", 0.00d);

        allByNodeIdIn.forEach(e ->
                mapList.add(doProduceNewResponse(pvNodeIdList, storeEnergyNodeIdList, e,
                        profitMonthRequest.getStartTs(),
                        TimeUtil.getMonthEnd(profitMonthRequest.getEndTs()),
                        dates,
                        profitMonthRequest.getUserType(),
                        total,
                        false,
                        true))
        );

        mapList.add(total);

        return ResponseResult.success(mapList);
    }

    /**
     * 得到节点年收益列表
     */
    @UserLoginToken
    @RequestMapping(value = "getYearProfitList", method = {RequestMethod.POST})
    public ResponseResult<List<Map<String, Object>>> getYearProfitList(@RequestBody ProfitYearRequest profitYearRequest) {

        List<Map<String, Object>> mapList = new ArrayList<>();
        List<Node> allByNodeIdIn = nodeRepository.findAllByNodeIdIn(profitYearRequest.getNodeId());

        List<Date> dates = TimeUtil.truncateToSplitYear(profitYearRequest.getStartTs(), profitYearRequest.getEndTs());

        List<String> pvNodeIdList = iNodeProfitService.getPvNodeIdList();
        List<String> storeEnergyNodeIdList = iNodeProfitService.getStoreEnergyNodeIdList();

        Map<String, Object> total = new HashMap<>();
        total.put("节点", "合计");
        total.put("合计", 0.00d);

        allByNodeIdIn.forEach(e ->
                mapList.add(doProduceNewResponse(pvNodeIdList, storeEnergyNodeIdList, e,
                        profitYearRequest.getStartTs(),
                        TimeUtil.getYearEnd(profitYearRequest.getEndTs()),
                        dates,
                        profitYearRequest.getUserType(),
                        total,
                        true,
                        false))
        );

        mapList.add(total);

        return ResponseResult.success(mapList);
    }

    private Map<String, Object> doProduceNewResponse(List<String> pvNodeIdList, List<String> storeEnergyNodeIdList, Node e, Date start, Date end, List<Date> dates, ProfitUserType userType, Map<String, Object> total, boolean year,boolean month) {
        Map<String, Object> mo = new HashMap<>();
        mo.put("节点", e.getNodeName());

        if (ProfitUserType.USER_ALL.equals(userType)) {

        } else if (ProfitUserType.CONSUMER.equals(userType)) {
            mo.put("电力用户", iNodeProfitService.getConsumer(e.getNodeId()));
            total.put("电力用户", "合计");

        } else if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
            mo.put("虚拟电厂运营商", iNodeProfitService.getLoadIntegrator(e.getNodeId()));
            total.put("虚拟电厂运营商", "合计");
        }

        boolean pvContains = pvNodeIdList.contains(e.getNodeId());
        boolean storeEnergyContains = storeEnergyNodeIdList.contains(e.getNodeId());

        if(year){

            List<NodeProfitMonthForecasting> allByNodeIdAndProfitDateMonthBetween = nodeProfitMonthForecastingRepository.findAllByNodeIdAndProfitDateMonthBetween(e.getNodeId(), TimeUtil.getYearStart(start), TimeUtil.getYearEnd(end));

            Map<String, List<NodeProfitMonthForecasting>> collect = allByNodeIdAndProfitDateMonthBetween.stream().collect(groupingBy((p) -> TimeUtil.toYStr(p.getProfitDateMonth())));


            collect.keySet().forEach(l-> {
                BigDecimal v = null;
                if (l.compareTo(TimeUtil.toYStr(new Date())) <= 0) {
                    v = new BigDecimal(collect.get(l).stream().mapToDouble(s->s.getProfitValue()).sum());
                } else {
                    v = new BigDecimal(collect.get(l).stream().mapToDouble(s->s.getProfitForecastValue()).sum());
                }

                if (storeEnergyContains) {
                    CfgStorageEnergyShareProportion share = shareProportionRepository.findByNodeIdAndSystemIdAndOrder(e.getNodeId(), "nengyuanzongbiao", Integer.valueOf(TimeUtil.toYmNumberStr(TimeUtil.getYearStart(start))));

                    if (share != null) {
                        if (ProfitUserType.CONSUMER.equals(userType)) {
                            v = v.multiply(new BigDecimal(share.getPowerUserProp()));
                        }
                        if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
                            v = v.multiply(new BigDecimal(share.getLoadProp()));

                        }
                    } else {
                        if (!ProfitUserType.USER_ALL.equals(userType)) {
                            v = BigDecimal.ZERO;
                        }
                    }
                }

                if (pvContains) {
                    CfgPhotovoltaicDiscountRate discountRate = discountRateRepository.findByNodeIdAndSystemIdAndOrder(e.getNodeId(), "nengyuanzongbiao", Integer.valueOf(TimeUtil.toYmNumberStr(TimeUtil.getYearStart(start))));

                    if (discountRate != null) {

                        if (ProfitUserType.CONSUMER.equals(userType)) {
                            v = v.multiply(new BigDecimal(discountRate.getPowerUserProp()));
                        }
                        if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
                            v = v.multiply(new BigDecimal(discountRate.getLoadProp()));

                        }
                    } else {
                        if (!ProfitUserType.USER_ALL.equals(userType)) {
                            v = BigDecimal.ZERO;
                        }
                    }
                }

           /*   收益管理- 小于零的收益取反
                if (v.compareTo(BigDecimal.ZERO) <0) {
                    v = v.abs();
                }*/

                mo.put(l,v.doubleValue());

            });

        }
        else if(month) {

            List<NodeProfitMonthForecasting> allByNodeIdAndProfitDateMonthBetween = nodeProfitMonthForecastingRepository.findAllByNodeIdAndProfitDateMonthBetween(e.getNodeId(), start, end);

            allByNodeIdAndProfitDateMonthBetween.forEach(l->{
                BigDecimal v = null;
                if(l.getProfitDateMonth().compareTo(TimeUtil.getMonthStart(new Date()))<=0){
                    v = new BigDecimal(l.getProfitValue());
                }else {
                    v = new BigDecimal(l.getProfitForecastValue());
                }


                if (storeEnergyContains) {
                    CfgStorageEnergyShareProportion share = shareProportionRepository.findByNodeIdAndSystemIdAndOrder(e.getNodeId(), "nengyuanzongbiao", Integer.valueOf(TimeUtil.toYmNumberStr(l.getProfitDateMonth())));

                    if (share != null) {
                        if (ProfitUserType.CONSUMER.equals(userType)) {
                            v = v.multiply(new BigDecimal(share.getPowerUserProp()));
                        }
                        if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
                            v = v.multiply(new BigDecimal(share.getLoadProp()));

                        }
                    } else {
                        if (!ProfitUserType.USER_ALL.equals(userType)) {
                            v = BigDecimal.ZERO;
                        }
                    }
                }

                if (pvContains) {
                    CfgPhotovoltaicDiscountRate discountRate = discountRateRepository.findByNodeIdAndSystemIdAndOrder(e.getNodeId(), "nengyuanzongbiao", Integer.valueOf(TimeUtil.toYmNumberStr(l.getProfitDateMonth())));

                    if (discountRate != null) {

                        if (ProfitUserType.CONSUMER.equals(userType)) {
                            v = v.multiply(new BigDecimal(discountRate.getPowerUserProp()));
                        }
                        if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
                            v = v.multiply(new BigDecimal(discountRate.getLoadProp()));

                        }
                    } else {
                        if (!ProfitUserType.USER_ALL.equals(userType)) {
                            v = BigDecimal.ZERO;
                        }
                    }
                }

    /*   收益管理- 小于零的收益取反
                if (v.compareTo(BigDecimal.ZERO) <0) {
                    v = v.abs();
                }*/

                mo.put(TimeUtil.toYmStr(l.getProfitDateMonth()), v.doubleValue());


            });


        }
        else {

            List<NodeProfitDayForecasting> allByNodeIdAndProfitDateMonthBetween = nodeProfitDayForecastingRepository.findAllByNodeIdAndProfitDateDayBetween(e.getNodeId(), start, end);

            Map<String,CfgStorageEnergyShareProportion> cfgsMap = new HashMap<>();

            Map<String,CfgPhotovoltaicDiscountRate> cfgpMap = new HashMap<>();

            allByNodeIdAndProfitDateMonthBetween.forEach(l->{
                BigDecimal v = null;
                if(l.getProfitDateDay().compareTo(new Date())<=0){
                    v = new BigDecimal(l.getProfitValue());
                }else {
                    v = new BigDecimal(l.getProfitForecastValue());
                }

                String monthStr = TimeUtil.toYmStr(l.getProfitDateDay());

                if (storeEnergyContains) {
                    CfgStorageEnergyShareProportion share = null;
                    if(!cfgsMap.keySet().contains(monthStr)){
                        share = shareProportionRepository.findByNodeIdAndSystemIdAndOrder(e.getNodeId(), "nengyuanzongbiao", Integer.valueOf(TimeUtil.toYmNumberStr(l.getProfitDateDay())));
                        cfgsMap.put(monthStr,share);
                    }else {
                        share = cfgsMap.get(monthStr);
                    }

                    if (share != null) {
                        if (ProfitUserType.CONSUMER.equals(userType)) {
                            v = v.multiply(new BigDecimal(share.getPowerUserProp()));
                        }
                        if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
                            v = v.multiply(new BigDecimal(share.getLoadProp()));

                        }
                    } else {
                        if (!ProfitUserType.USER_ALL.equals(userType)) {
                            v = BigDecimal.ZERO;
                        }
                    }
                }

                if (pvContains) {
                    CfgPhotovoltaicDiscountRate discountRate = null;
                    if(!cfgpMap.keySet().contains(monthStr)){
                        discountRate = discountRateRepository.findByNodeIdAndSystemIdAndOrder(e.getNodeId(), "nengyuanzongbiao", Integer.valueOf(TimeUtil.toYmNumberStr(l.getProfitDateDay())));
                        cfgpMap.put(monthStr,discountRate);
                    }else {
                        discountRate = cfgpMap.get(monthStr);
                    }

                    if (discountRate != null) {

                        if (ProfitUserType.CONSUMER.equals(userType)) {
                            v = v.multiply(new BigDecimal(discountRate.getPowerUserProp()));
                        }
                        if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
                            v = v.multiply(new BigDecimal(discountRate.getLoadProp()));

                        }
                    } else {
                        if (!ProfitUserType.USER_ALL.equals(userType)) {
                            v = BigDecimal.ZERO;
                        }
                    }
                }
/*   收益管理- 小于零的收益取反
                if (v.compareTo(BigDecimal.ZERO) <0) {
                    v = v.abs();
                }*/

                mo.put(TimeUtil.toYmdStr(l.getProfitDateDay()), v.doubleValue());


            });

        }

        dates.forEach(df -> {

            String dateStr = "";
            if (year) {
                dateStr = TimeUtil.toYStr(df);
            } else if(month){
                dateStr = TimeUtil.toYmStr(df);
            }else {
                dateStr = TimeUtil.toYmdStr(df);
            }

            if(mo.get(dateStr)==null){
                mo.put(dateStr,0.00d);
            }

            Object o = total.get(dateStr);
            if (o == null) {
                total.put(dateStr, mo.get(dateStr));
            } else {
                total.put(dateStr, new BigDecimal((double) o).add(new BigDecimal((double) mo.get(dateStr))).doubleValue());
            }

            total.put("合计", new BigDecimal((double) total.get("合计")).add(new BigDecimal((double) mo.get(dateStr))).doubleValue());

            Object hj = mo.get("合计");
            if (hj == null) {
                mo.put("合计", new BigDecimal((double) mo.get(dateStr)).doubleValue());
            } else {
                mo.put("合计", new BigDecimal((double) hj).add(new BigDecimal(((Double) mo.get(dateStr)).doubleValue())).doubleValue());
            }

        });

        return mo;

    }




//    private Map<String, Object> doProduceResponse(List<String> pvNodeIdList, List<String> storeEnergyNodeIdList, Node e, Date start, Date end, List<Date> dates, ProfitUserType userType, Map<String, Object> total, boolean year) {
//
//        List<NodeProfit> allByProfitDateBefore = nodeProfitRepository.findAllByNodeIdAndProfitDateBetweenOrderByProfitDateAsc(e.getNodeId(), start, end);
//
//        Map<String, List<NodeProfit>> collect = null;
//        if (year) {
//            collect = allByProfitDateBefore.stream().collect(groupingBy(el -> TimeUtil.toYStr(el.getProfitDate())));
//
//        } else {
//            collect = allByProfitDateBefore.stream().collect(groupingBy(el -> TimeUtil.toYmStr(el.getProfitDate())));
//
//        }
//
//        Map<String, Object> mo = new HashMap<>();
//        mo.put("节点", e.getNodeName());
//
//        if (ProfitUserType.USER_ALL.equals(userType)) {
//
//        } else if (ProfitUserType.CONSUMER.equals(userType)) {
//            mo.put("电力用户", iNodeProfitService.getConsumer(e.getNodeId()));
//            total.put("电力用户", "合计");
//
//        } else if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
//            mo.put("虚拟电厂运营商", iNodeProfitService.getLoadIntegrator(e.getNodeId()));
//            total.put("虚拟电厂运营商", "合计");
//        }
//
//        boolean pvContains = pvNodeIdList.contains(e.getNodeId());
//        boolean storeEnergyContains = storeEnergyNodeIdList.contains(e.getNodeId());
//
//        Map<String, List<NodeProfit>> finalCollect = collect;
//        dates.forEach(df -> {
//
//            String dateStr = "";
//            if (year) {
//                dateStr = TimeUtil.toYStr(df);
//            } else {
//                dateStr = TimeUtil.toYmStr(df);
//            }
//
//            List<NodeProfit> nodeProfits = finalCollect.get(dateStr);
//            if (nodeProfits != null && !nodeProfits.isEmpty()) {
//
//                if (year) {
//
//                    Map<String, List<NodeProfit>> yearMonthCollect = nodeProfits.stream().collect(groupingBy(el -> TimeUtil.toYmNumberStr(el.getProfitDate())));
//                    String finalDateStr = dateStr;
//
//
//                    yearMonthCollect.keySet().forEach(dk -> {
//
//                        CfgStorageEnergyShareProportion share = shareProportionRepository.findByNodeIdAndSystemIdAndOrder(e.getNodeId(), "nengyuanzongbiao", Integer.valueOf(dk));
//
//                        CfgPhotovoltaicDiscountRate discountRate = discountRateRepository.findByNodeIdAndSystemIdAndOrder(e.getNodeId(), "nengyuanzongbiao", Integer.valueOf(dk));
//
//                        List<NodeProfit> monthNodeProfit = yearMonthCollect.get(dk);
//
//                        double v = monthNodeProfit.stream().map(l -> {
//                            return new BigDecimal(l.getProfitValue());
//                        }).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
//
//
//                        if (storeEnergyContains) {
//                            if (share != null) {
//                                if (ProfitUserType.CONSUMER.equals(userType)) {
//                                    v = new BigDecimal(v).multiply(new BigDecimal(share.getPowerUserProp())).doubleValue();
//                                }
//                                if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
//                                    v = new BigDecimal(v).multiply(new BigDecimal(share.getLoadProp())).doubleValue();
//
//                                }
//                            } else {
//                                if (!ProfitUserType.USER_ALL.equals(userType)) {
//                                    v = 0.00d;
//                                }
//                            }
//                        }
//
//                        if (pvContains) {
//                            if (discountRate != null) {
//                                if (ProfitUserType.CONSUMER.equals(userType)) {
//                                    v = new BigDecimal(v).multiply(new BigDecimal(discountRate.getPowerUserProp())).doubleValue();
//                                }
//                                if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
//                                    v = new BigDecimal(v).multiply(new BigDecimal(discountRate.getLoadProp())).doubleValue();
//
//                                }
//                            } else {
//                                if (!ProfitUserType.USER_ALL.equals(userType)) {
//                                    v = 0.00d;
//                                }
//                            }
//                        }
//
//                        Object hj = mo.get(finalDateStr);
//                        if (hj == null) {
//                            mo.put(finalDateStr, new BigDecimal(v).doubleValue());
//                        } else {
//                            mo.put(finalDateStr, new BigDecimal((double) hj).add(new BigDecimal(v)).doubleValue());
//                        }
//
//                    });
//
//                    Object yearTotalValue = mo.get(finalDateStr);
//                    if (yearTotalValue != null) {
//                        double v = (double) yearTotalValue;
//                        if (v < 0) {
//                            mo.put(finalDateStr, 0.00d);
//                        }
//
//                    }
//
//
//                } else {
//                    CfgStorageEnergyShareProportion share = shareProportionRepository.findByNodeIdAndSystemIdAndOrder(e.getNodeId(), "nengyuanzongbiao", Integer.valueOf(TimeUtil.toYmNumberStr(df)));
//
//                    CfgPhotovoltaicDiscountRate discountRate = discountRateRepository.findByNodeIdAndSystemIdAndOrder(e.getNodeId(), "nengyuanzongbiao", Integer.valueOf(TimeUtil.toYmNumberStr(df)));
//
//                    double v = nodeProfits.stream().map(l -> {
//                        return new BigDecimal(l.getProfitValue());
//                    }).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
//
//                    if (storeEnergyContains) {
//                        if (share != null) {
//                            if (ProfitUserType.CONSUMER.equals(userType)) {
//                                v = new BigDecimal(v).multiply(new BigDecimal(share.getPowerUserProp())).doubleValue();
//                            }
//                            if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
//                                v = new BigDecimal(v).multiply(new BigDecimal(share.getLoadProp())).doubleValue();
//
//                            }
//                        } else {
//                            if (!ProfitUserType.USER_ALL.equals(userType)) {
//                                v = 0.00d;
//                            }
//                        }
//                    }
//
//                    if (pvContains) {
//                        if (discountRate != null) {
//                            if (ProfitUserType.CONSUMER.equals(userType)) {
//                                v = new BigDecimal(v).multiply(new BigDecimal(discountRate.getPowerUserProp())).doubleValue();
//                            }
//                            if (ProfitUserType.LOAD_INTEGRATOR.equals(userType)) {
//                                v = new BigDecimal(v).multiply(new BigDecimal(discountRate.getLoadProp())).doubleValue();
//
//                            }
//                        } else {
//                            if (!ProfitUserType.USER_ALL.equals(userType)) {
//                                v = 0.00d;
//                            }
//                        }
//                    }
//
//                    if (v < 0) {
//                        v = 0.00d;
//                    }
//
//
//                    mo.put(dateStr, v);
//                }
//
//
//            } else {
//                mo.put(dateStr, 0.00d);
//            }
//
//            Object o = total.get(dateStr);
//            if (o == null) {
//                total.put(dateStr, mo.get(dateStr));
//            } else {
//                total.put(dateStr, new BigDecimal((double) o).add(new BigDecimal((double) mo.get(dateStr))).doubleValue());
//            }
//
//            total.put("合计", new BigDecimal((double) total.get("合计")).add(new BigDecimal((double) mo.get(dateStr))).doubleValue());
//
//            Object hj = mo.get("合计");
//            if (hj == null) {
//                mo.put("合计", new BigDecimal((double) mo.get(dateStr)).doubleValue());
//            } else {
//                mo.put("合计", new BigDecimal((double) hj).add(new BigDecimal((double) mo.get(dateStr))).doubleValue());
//            }
//
//        });
//
//        return mo;
//
//    }

}
