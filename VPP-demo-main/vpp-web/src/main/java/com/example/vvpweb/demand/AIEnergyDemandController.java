package com.example.vvpweb.demand;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.*;
import com.example.vvpcommom.Enum.DemandStatusEnum;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpweb.demand.aigorithmmodel.DemandAlgorithmVo;
import com.example.vvpweb.demand.aigorithmmodel.EnumExample;
import com.example.vvpweb.demand.aigorithmmodel.InitVo;
import com.example.vvpweb.demand.aigorithmmodel.UpdateLoadVo;
import com.example.vvpweb.demand.model.*;
import com.example.vvpweb.demand.model.factory.DemandForecastFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import com.example.vvpweb.demand.model.AIEnergyDemandQureyModel;
import com.example.vvpweb.demand.model.DemandRespStrategyNoResp;
import com.example.vvpweb.demand.model.DemandRespTaskRespModel;

@RestController
@RequestMapping("/AIEnergy")
@CrossOrigin
@Api(value = "智能助手-需求预测", tags = {"智能助手-需求预测"})
public class AIEnergyDemandController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AIEnergyDemandController.class);

    @Autowired
    private DemandRespTaskRepository demandRespTaskRepository;
    @Autowired
    private DemandRespStrategyRepository respStrategyRepository;
    @Autowired
    private DemandRespStrategyNoRepository noRepository;
    @Resource
    private SysParamRepository sysParamRepository;
    @Resource
    private AiLoadRepository aiLoadRepository;
    @Resource
    private DemandStrategyRepository demandStrategyRepository;
    @Resource
    DeviceRepository deviceRepository;
    @Resource
    private NodeRepository nodeRepository;

    @ApiOperation("查询任务编号列表")
    @UserLoginToken
    @RequestMapping(value = "/getTaskCodeList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getTaskList(@RequestBody AIEnergyDemandQureyModel model) {
        try {
            List<Integer> statusList = new ArrayList<>();
            statusList.add(DemandStatusEnum.notStart.getId());
            statusList.add(DemandStatusEnum.executing.getId());
            statusList.add(DemandStatusEnum.completed.getId());
            statusList.add(DemandStatusEnum.absent.getId());
            Specification<DemandRespTask> spec = new Specification<DemandRespTask>() {
                @Override
                public Predicate toPredicate(Root<DemandRespTask> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    if(model.getStatus()==DemandStatusEnum.all.getId()){
                        predicates.add(cb.in(root.get("dStatus")).value(statusList));
                    }else{
                        predicates.add(cb.equal(root.get("dStatus"), model.getStatus()));
                    }
                    if(model.getRespType()!=DemandStatusEnum.all.getId()) {
                        predicates.add(cb.equal(root.get("respType"),model.getRespType()));
                    }
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    Order order = cb.desc(root.get("taskCode"));
                    criteriaQuery.orderBy(order);
                    return criteriaQuery.getRestriction();
                }
            };

            List<DemandRespTask> datas = demandRespTaskRepository.findAll(spec)
                    .stream()
                    .filter(v2 -> v2.getDStatus() != 4 || v2.getDStatus() != 0)
                    .collect(Collectors.toList());

            PageModel pageModel = new PageModel();
            //封装到pageUtil
            if (datas != null && datas.size() > 0) {
                List<DemandRespTaskRespModel> list = new ArrayList<>();
                datas.forEach(d -> {
                    DemandRespTaskRespModel newModel = new DemandRespTaskRespModel();
                    BeanUtils.copyProperties(d, newModel);
                    list.add(newModel);
                });
                pageModel.setContent(list);
            }
            return ResponseResult.success(pageModel);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.error("查询失败");
        }
    }

    @ApiOperation("根据任务编号查询决策链信息")
    @UserLoginToken
    @PassToken
    @RequestMapping(value = "/getDecisionChainList", method = {RequestMethod.POST})
    public ResponseResult getDecisionChainList(@RequestParam("respId") String respId) {
        try {
            //更新已过期的响应任务
//            demandRespTaskRepository.updateExpiredTasks();
            if (StringUtils.isBlank(respId)) {
                return ResponseResult.error("任务编码不能为空！");
            }
            Date date = new Date();
            //查询响应详情
            DemandRespTask task=demandRespTaskRepository.findByRespId(respId);
            DemandRespTaskRespModel newModel = new DemandRespTaskRespModel();
            if(task!=null){
                BeanUtils.copyProperties(task, newModel);
            //总响应电量
            }

            //查询所有相关的策略id
            List<String> sIdsList;
            sIdsList = respStrategyRepository.findSIdsByPlatformId(respId, "zhinengtuijian");
            if (sIdsList.isEmpty()) {
                sIdsList = respStrategyRepository.findSIdsByPlatformId(respId, "getInvitation");
            }
            List<String> finalSIdsList = sIdsList;
            Specification<DemandRespStrategyNo> spec = new Specification<DemandRespStrategyNo>() {
                @Override
                public Predicate toPredicate(Root<DemandRespStrategyNo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.in(root.get("demandRespStrategy").get("sId")).value(finalSIdsList));
                    predicates.add(cb.equal(root.get("isPlatform"), 1));//查询第三方平台的数据

                    return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            List<DemandRespStrategyNo> datas = noRepository.findAll(spec);

            List<DemandRespStrategyNoResp> noResps = new ArrayList<>();

            double totalAdjustLoad = 0.00;//总调节负荷
            //封装到pageUtil
            if (datas != null) {
                //得到nodeIds,去ai表里查询ai基线负荷
                List<String> nodeIds = datas.stream().map(DemandRespStrategyNo::getNodeId).collect(Collectors.toList());

                //组装时间
                SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                dateSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
                timeSdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                Date sDate = task.getRsTime();
                Date eDate = task.getReTime();

                Specification<AiLoadForecasting> spec1 = new Specification<AiLoadForecasting>() {
                    @Override
                    public Predicate toPredicate(Root<AiLoadForecasting> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                        List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                        predicates.add(cb.in(root.get("nodeId")).value(nodeIds));
                        predicates.add(cb.equal(root.get("systemId"), "kongtiao"));
                        predicates.add(cb.between(root.get("createdTime"), sDate, eDate));
                        criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                        return criteriaQuery.getRestriction();
                    }
                };
                List<AiLoadForecasting> forecastingList = aiLoadRepository.findAll(spec1);

                Map<String, List<AiLoadForecasting>> baseLoadMap = forecastingList.stream().collect(Collectors.groupingBy(AiLoadForecasting::getNodeId));
                List<Node> nodeList= nodeRepository.findAllByNodeIdIn(nodeIds);
                Map<String, Node> nodeMap = nodeList.stream().collect(Collectors.toMap(Node::getNodeId,n->n));

                List<DemandStrategy> demandStrategyList = new ArrayList<>(demandStrategyRepository.findByRespIdAndState(respId, Arrays.asList(1, 3))
                        .stream()
                        .collect(Collectors.toMap(
                                DemandStrategy::getNodeId,
                                demandStrategy -> demandStrategy,
                                (existing, replacement) -> existing
                        ))
                        .values());

                Map<String,DemandStrategy> demandStrategyMap = demandStrategyList.stream().collect(Collectors.toMap(DemandStrategy::getNodeId,n->n));

                for(DemandRespStrategyNo d:datas){
                    DemandRespStrategyNoResp noResp = new DemandRespStrategyNoResp();
                    noResp.setDrsId(d.getDrsId());
                    noResp.setNodeName(d.getNodeName());//节点名称
                    noResp.setDrsStatus(d.getDrsStatus());//申报状态
                    noResp.setDeclareLoad(d.getDeclareLoad());//申报负荷
                    noResp.setNoHouseholds(d.getNoHouseholds());//户号
                    noResp.setProfit(d.getProfit());//预估收益
                    noResp.setRealTimeLoad(d.getRealTimeLoad());//响应电量

                    DemandStrategy strategy =demandStrategyMap.get(d.getNodeId());

                    if (baseLoadMap.containsKey(d.getNodeId())) {
                        List<AiLoadForecasting> aiList = baseLoadMap.get(d.getNodeId());
                        if (strategy.getForecastLoad() == null || strategy.getForecastLoad().equals("0")) {
                            noResp.setForecastLoad(
                                    String.valueOf(
                                            aiList.stream()
                                                    .filter(v -> v.getCurrentForecastValue() != null && !("-").equals(v.getCurrentForecastValue()))
                                                    .mapToDouble(v2 -> Double.parseDouble(v2.getCurrentForecastValue())).average().orElse(0)
                                    ));
                        } else {
                            noResp.setForecastLoad(strategy.getForecastLoad());
                        }//预测负荷
                        noResp.setNowLoad(
                                String.valueOf(
                                        aiList.stream().filter(v -> v.getRealValue() != null && !("-").equals(v.getRealValue()))
                                                .mapToDouble(v2 -> Double.parseDouble(v2.getRealValue())).average().orElse(0)
                                ));//实际负荷
                        noResp.setBaseLoad(
                                String.valueOf(
                                        aiList.stream().filter(v -> v.getBaselineLoadValue() != null && !("-").equals(v.getBaselineLoadValue()))
                                                .mapToDouble(v2 -> Double.parseDouble(v2.getBaselineLoadValue())).average().orElse(0)
                                ));//基线负荷
                    }
                    if (noResp.getBaseLoad() != null && noResp.getNowLoad() != null) {
                        noResp.setResponseLoad(
                                Double.parseDouble(noResp.getBaseLoad())
                                        - Double.parseDouble(noResp.getNowLoad())
                        );//响应负荷 基线-实际
                    }

                    String adjustLoad1 = strategy.getForecastAdjustLoad() == null || strategy.getForecastAdjustLoad().equals("0") ? "-" : strategy.getForecastAdjustLoad();;
                    if (adjustLoad1.equals("-")) {
                        if(strategy != null && strategy.getForecastAdjustedLoad() != null && noResp.getForecastLoad() != null){
                            double adjustLoad = (Double.parseDouble(noResp.getForecastLoad())
                                    - Double.parseDouble(strategy.getForecastAdjustedLoad()));
                            noResp.setAdjustLoad(String.valueOf(adjustLoad));//调节负荷 预测的负荷-调节后的负荷=调节负荷
                            //总负荷叠加
                            totalAdjustLoad += adjustLoad;
                        }
                    } else {
                        noResp.setAdjustLoad(adjustLoad1);
                        totalAdjustLoad += Double.parseDouble(adjustLoad1);

                    }

                    noResp.setOnline(nodeMap.get(d.getNodeId()).getOnline());//节点是否在线

                    noResps.add(noResp);
                }
            }
            newModel.setAdjustLoad(totalAdjustLoad);//总调节负荷


            if (noResps != null) {
                //总申报负荷
                newModel.setDeclareLoad(noResps.stream().mapToDouble(DemandRespStrategyNoResp::getDeclareLoad).sum());
                //总预测负荷
                newModel.setForecastLoad(String.valueOf(noResps.stream().mapToDouble(v -> Double.parseDouble(v.getForecastLoad() != null ? v.getForecastLoad() : "0")).sum()));
            }
            newModel.setNowLoad(
                    noResps.stream().filter(v ->v.getNowLoad() != null && !v.getNowLoad().equals("-"))
                    .mapToDouble(v2 -> Double.parseDouble(v2.getNowLoad())).average().orElse(0)
            );//总实际负荷
            newModel.setBaseLoad(
                    noResps.stream().filter(v -> v.getBaseLoad() != null && !v.getBaseLoad().equals("-"))
                            .mapToDouble(v2 -> Double.parseDouble(v2.getBaseLoad())).average().orElse(0)
            );//总基线负荷
            newModel.setActualLoad(newModel.getBaseLoad() - newModel.getNowLoad());//总响应负荷
            Map<String, Object> map = new HashMap<>();
            map.put("nodeList",noResps);//节点信息
            map.put("respTask", newModel);//需求响应信息
            return ResponseResult.success(map);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("异常 +" + e.getMessage());
        }

    }

    /**
     * 计算预测平均负荷
     * @param aiList
     * @return
     */
    private String getAvgAdjustLoad(List<AiLoadForecasting> aiList){
        String forecastLoad = "-";

        if(aiList!=null && aiList.size()>0){
//            //基线总和
//            double forecastTotal = 0.00;
//            // “-”的统计次数
//            int count =0;
//            for(AiLoadForecasting a: aiList){
//                if (StringUtils.isEmpty(a.getCurrentForecastValue())
//                        || "-".equals(a.getCurrentForecastValue())
//                        || !com.example.vvpcommom.StringUtils.isNumber(a.getCurrentForecastValue())) {
//                    count++;
//                }else {
//                    forecastTotal += Double.parseDouble(a.getCurrentForecastValue());
//                }
//            }
//            if(count<aiList.size()){
//                forecastLoad=forecastTotal/aiList.size()+"";
//            }
            forecastLoad = String.valueOf(aiList.stream()
                    .filter(v1 -> v1.getCurrentForecastValue() != null)
                    .filter(v -> !v.getCurrentForecastValue().equals("-")).mapToDouble(v2 -> Double.parseDouble(v2.getCurrentForecastValue()))
                    .sum()
                    /aiList.size());
        }
        return forecastLoad;
    }

    @ApiOperation("需求预测-查询任务下节点编号")
    @UserLoginToken
    @RequestMapping(value = "demandNodeQuery", method = {RequestMethod.POST})
    public ResponseResult<List<Map<String,String>>> demandForecastQuery(@RequestBody strategyModel model) {
        try {
            //todo,check\
            List<DemandRespStrategyNo> demandRespStrategyNos = noRepository.findByRespId(model.getRespId());
            List<Map<String,String>> list = new ArrayList<>();
            Set<String> set = new HashSet<>();
            Map<String,String> map = new HashMap<>();
            if (demandRespStrategyNos.size() == 0) {
                map.put("value","");
                map.put("label","全部节点");
                list.add(map);
            }

            demandRespStrategyNos.forEach(v ->{
                if (!set.contains(v.getNodeId())) {
                    Map<String,String> map1 = new HashMap<>();
                    map1.put("value",v.getNodeId());
                    System.out.println(v.getNodeId());
                    map1.put("label",v.getNodeName());
                    System.out.println(v.getNodeName());
                    set.add(v.getNodeId());
                    list.add(map1);
                }
            });

            return ResponseResult.success(list);
        } catch (Exception ex) {
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }
    @ApiOperation("需求预测-Chart")
    @UserLoginToken
    @RequestMapping(value = "demandForecastChart", method = {RequestMethod.POST})
    public ResponseResult<List<DemandForecastResponse>> demandForecastChart(@RequestBody DemandLoadRegulationModel model) throws ParseException {
        try {
            if (model == null) {
                return ResponseResult.error("参数为空，请重新输入!");
            }
            //rsTime和reTime查询时间
            Date sDate = new Date(model.getRsTime().get().getTime());
            Date eDate = new Date(model.getReTime().get().getTime());
            //sTime和eTime任务响应时间
            Date rsDate = new Date(model.getSTime().get().getTime());
            Date reDate = new Date(model.getETime().get().getTime());
            List<String> nodeIds;
            if (model.getNodeId().equals("")) {
                nodeIds = noRepository.findByRespId(model.getRespId())
                        .stream().map(DemandRespStrategyNo::getNodeId).collect(Collectors.toList());
            } else {
                nodeIds = new ArrayList<>();
                nodeIds.add(model.getNodeId());
            }
            Specification<AiLoadForecasting> spec = (root, criteriaQuery, cb) -> {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(root.get("nodeId").in(nodeIds));//对应SQL语句：select * from ### where username= code
                predicates.add(cb.equal(root.get("systemId"), "kongtiao"));
                predicates.add(cb.between(root.get("createdTime"), sDate, eDate));
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.asc(root.get("createdTime"))); //按照createdTime升序排列
                return criteriaQuery.getRestriction();
            };
            Specification<DemandStrategy> spec2 = (root, criteriaQuery, cb) -> {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(cb.equal(root.get("respId"), model.getRespId()));
                predicates.add(root.get("nodeId").in(nodeIds));//对应SQL语句：select * from ### where username= code
                predicates.add(cb.equal(root.get("systemId"), "kongtiao"));
                predicates.add(cb.equal(root.get("state"), 1));
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.asc(root.get("forecastTime"))); //按照regulationTime升序排列
                return criteriaQuery.getRestriction();
            };

            List<AiLoadForecasting> loadForecastingList = aiLoadRepository.findAll(spec);
            if (loadForecastingList.size() <= 0 )  {
                return ResponseResult.error("无数据");
            }
            List<DemandStrategy> demandStrategyList = demandStrategyRepository.findAll(spec2);


            List<DemandForecastResponse> responses = new DemandForecastFactory()
                    .toVOList(loadForecastingList,
                            demandStrategyList.stream().filter(vdsl -> vdsl.getForecastAdjustedLoad() != null).collect(Collectors.toList()),
                            sDate,
                            eDate,
                            rsDate,
                            reDate);
            return ResponseResult.success(responses);
        } catch (Exception ex) {
            LOGGER.info("需求预测-Chartex:{}",ex.getMessage());
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }

    @ApiOperation("策略预测调节-新增(算法)")
    @UserLoginToken
    @RequestMapping(value = "strategyAdd", method = {RequestMethod.POST})
    public ResponseResult<DemandStrategy> strategyAdd(@RequestBody UpdateLoadVo model) {
        try {
            List<DemandStrategy> findList = demandStrategyRepository
                    .findByRespIdAndNodeIdAndState(model.getResp_id(), model.getNode_id(),3);
            if (findList == null || findList.isEmpty()) {
                return ResponseResult.error("任务节点不存在！");
            }
            //填补addTask后的策略数据，策略内容、预测调节负荷、预测调节后负荷、申报值
            DemandStrategy strategy = findList.get(0);
//            Double forecastAdjustedLoad = Double.valueOf(model.getOpt_fx() != null ? model.getOpt_fx() : "0");
//            Double forecastAdjustLoad = Double.parseDouble(strategy.getForecastLoad()) - forecastAdjustedLoad;

            Double forecastAdjustLoad = Double.valueOf(model.getOpt_fx() != null ? model.getOpt_fx() : "0");
            Double forecastAdjustedLoad = Double.valueOf(strategy.getForecastLoad()) - forecastAdjustLoad;

            strategy.setState(1);
            strategy.setStrategyContent(model.getOpt_x());
            strategy.setForecastAdjustedLoad(String.valueOf(forecastAdjustedLoad));
            strategy.setForecastAdjustLoad(String.valueOf(forecastAdjustLoad));
            strategy.setCommandValue(String.valueOf(forecastAdjustLoad / 1.15));
            strategy.setForecastTime(new Date());

            //修改对应节点申报值
            noRepository.updateDeclareLoad(strategy.getRespId(), strategy.getNodeId(), Double.valueOf(strategy.getCommandValue()));
            return ResponseResult.success(demandStrategyRepository.save(strategy));
        } catch (Exception ex) {
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }

    @Value("${suanfaBackCalcApi}")
    private String suanfaBackCalcApi;

    @ApiOperation("策略-修改并建立新生效-前端")
    @UserLoginToken
    @RequestMapping(value = "strategyUpdate", method = {RequestMethod.POST})
    public ResponseResult<DemandStrategy> strategyUpdate(@RequestBody strategyModel model) {
        try {
            //todo,check
            DemandStrategy find = demandStrategyRepository.findById(model.getId()).orElse(null);
            if (find == null) {
                return ResponseResult.error("数据有误，请检查！");
            }
            find.setState(2);
            demandStrategyRepository.save(find);

            DemandStrategy strategy = new DemandStrategy();
            BeanUtils.copyProperties(find, strategy);
            strategy.setId(find.getRespId() + "_" + find.getNodeId() + "_" + new Date().getTime() + new Random().nextInt(100));
            strategy.setState(1);
            strategy.setLlm(false);
            strategy.setForecastTime(new Date());

            //等待算法返回才修改策略不然按照上一个策略，后续修改：策略内容，调节负荷、调节后负荷
            InitVo jsonObject = JSONObject.parseObject(find.getStrategyContent(),InitVo.class);
            jsonObject.setCh_water_outlet_temperature(Double.valueOf(model.getStrategyContentBefore()));
//            find.setStrategyContent(JSON.toJSONString(jsonObject));

            DemandAlgorithmVo json =new DemandAlgorithmVo(
                    find.getRespId(),
                    find.getNodeId(),
                    find.getNodeName(),
                    Double.valueOf(find.getCommandValue()),
                    EnumExample.Weights.getNode(find.getNodeId()),
                    JSON.toJSONString(jsonObject),
                    "ch_water_outlet_temperature",
                    Double.valueOf(model.getStrategyContent())
            );
            LOGGER.info("修改并建立新生效:{}",JSON.toJSONString(json));
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(2000);
                    String result= HttpUtil.okHttpPost(suanfaBackCalcApi, JSON.toJSONString(json));
                    LOGGER.info("修改响应任务节点相关信息result:{}",result);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            return ResponseResult.success(demandStrategyRepository.save(strategy));
        } catch (Exception ex) {
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }
    @ApiOperation("策略-修改策略调整负荷-算法")
    @UserLoginToken
    @RequestMapping(value = "strategyUpdateLoad", method = {RequestMethod.POST})
    public ResponseResult<DemandStrategy> strategyUpdateLoad(@RequestBody UpdateLoadVo model) {
        try {
            //todo,check
            List<DemandStrategy> findList = demandStrategyRepository
                    .findByRespIdAndNodeIdAndState(model.getResp_id(), model.getNode_id(),1);
            if (findList == null) {
                return ResponseResult.error("无可修改策略");
            }
            DemandStrategy find = findList.get(0);
            if (findList.size() >= 2) {
                List<DemandStrategy> subList= findList.subList(1, findList.size());
                subList.forEach(v ->v.setState(2));
                demandStrategyRepository.saveAll(subList);
            }
            //修改：策略内容/预测申报负荷（调节符合/1.15），调节负荷、调节后负荷
//            Double forecastAdjustedLoad = Double.valueOf(model.getOpt_fx() != null ? model.getOpt_fx() : "0");
//            Double forecastAdjustLoad = Double.parseDouble(find.getForecastLoad()) - forecastAdjustedLoad;
            Double forecastAdjustLoad = Double.valueOf(model.getOpt_fx() != null ? model.getOpt_fx() : "0");
            Double forecastAdjustedLoad = Double.parseDouble(find.getForecastLoad()) - forecastAdjustLoad;

            find.setStrategyContent(model.getOpt_x());
            find.setForecastAdjustedLoad(String.valueOf(forecastAdjustedLoad));
            find.setForecastAdjustLoad(String.valueOf(forecastAdjustLoad));
            find.setCommandValue(String.valueOf(forecastAdjustLoad / 1.15));
            find.setState(1);
            find.setForecastTime(new Date());

            //修改对应节点申报负荷
            noRepository.updateDeclareLoad(find.getRespId(),find.getNodeId(), Double.valueOf(find.getCommandValue()));

            return ResponseResult.success(demandStrategyRepository.save(find));
        } catch (Exception ex) {
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }
    @ApiOperation("策略-查询")
    @UserLoginToken
    @RequestMapping(value = "strategyQuery", method = {RequestMethod.POST})
    public ResponseResult<List<StrategyQueryVo>> strategyQuery(@RequestBody strategyModel model) {
        try {
            //todo,check\
            List<StrategyQueryVo> list = new ArrayList<>();
            List<DemandStrategy> demandStrategyList = demandStrategyRepository.findByRespIdAndState(model.getRespId(),Arrays.asList(1,3));
            demandStrategyList.forEach(v ->
                    list.add(new StrategyQueryVo(
                            v,
                            deviceRepository.
                                    findAllByNode_NodeId(v.getNodeId())
                                    .stream().map(Device::getDeviceName)
                                    .collect(Collectors.toList())))
            );
            return ResponseResult.success(list);
        } catch (Exception ex) {
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }
    @ApiOperation("策略-批量确认")
    @UserLoginToken
    @RequestMapping(value = "strategyEnsure", method = {RequestMethod.POST})
    public ResponseResult<List<StrategyQueryVo>> strategyEnsure(@RequestBody strategyModel model) {
        try {
            demandStrategyRepository.updateEnsureByIds(model.getIds());
            return ResponseResult.success();
        } catch (Exception ex) {
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }
    @ApiOperation("查询带申报任务状态和个数")
    @UserLoginToken
    @RequestMapping(value = "/getTaskState", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getTaskState() {
        try {
            Specification<DemandRespTask> spec = (root, criteriaQuery, cb) -> {
                Predicate dStatusPredicate = cb.or(
                        cb.isNull(root.get("dStatus")),
                        cb.equal(root.get("dStatus"), 1)
                );
                Predicate declareStatusPredicate = cb.or(
                        cb.isNull(root.get("declareStatus")),
                        cb.equal(root.get("declareStatus"), 1)
                );
                Predicate finalPredicate = cb.and(dStatusPredicate, declareStatusPredicate);
                criteriaQuery.where(finalPredicate);
                Order order = cb.desc(root.get("taskCode"));
                criteriaQuery.orderBy(order);
                return criteriaQuery.getRestriction();
            };


            List<DemandRespTask> datas = demandRespTaskRepository.findAll(spec)
                    .stream()
                    .filter(v2 -> v2.getDStatus() != 4 || v2.getDStatus() != 0)
                    .collect(Collectors.toList());
            PageModel pageModel = new PageModel();
            //封装到pageUtil
            if (datas != null && datas.size() > 0) {
                List<DemandRespTaskRespModel> list = new ArrayList<>();
                datas.forEach(d -> {
                    DemandRespTaskRespModel newModel = new DemandRespTaskRespModel();
                    BeanUtils.copyProperties(d, newModel);
                    list.add(newModel);
                });
                pageModel.setContent(list);
                pageModel.setTotalElements(list.size());
            }
            return ResponseResult.success(pageModel);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.error("查询失败");
        }
    }
//    @ApiOperation("策略日志-查询")
//    @UserLoginToken
//    @RequestMapping(value = "strategyLogQuery", method = {RequestMethod.POST})
//    public ResponseResult<List<DemandStrategy>> strategyLogQuery(@RequestBody strategyModel model) {
//        try {
//            //todo,check
//            return ResponseResult.success(
//                    demandStrategyRepository.findByRespId(model.getRespId())
//            );
//        } catch (Exception ex) {
//            return ResponseResult.error("数据参数有误，请检查！");
//        }
//    }
}
