package com.example.vvpweb.demand;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.AiLoadForecasting;
import com.example.vvpdomain.entity.DemandRespStrategyNo;
import com.example.vvpdomain.entity.DemandRespTask;
import com.example.vvpdomain.entity.SysParam;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.demand.model.DemandModel;
import com.example.vvpweb.demand.model.DemandRespStrategyNoResp;
import com.example.vvpweb.demand.model.DemandRespTaskRespModel;
import com.example.vvpweb.demand.model.DemandStrategyModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author maoyating
 * @description 需求响应-实时监测
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/demand_resp/resp_monitor")
@CrossOrigin
@Api(value = "需求响应-实时监测", tags = {"需求响应-实时监测"})
public class DemandRespMonitorController {

    private static Logger logger = LoggerFactory.getLogger(DemandRespMonitorController.class);
    @Autowired
    private DemandRespTaskRepository demandRespTaskRepository;
    @Autowired
    private DemandRespStrategyRepository respStrategyRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private DemandRespStrategyNoRepository noRepository;
    @Autowired
    private IotTsKvMeteringDevice96Repository device96Repository;
    @Autowired
    private ScheduleStrategyDeviceRepository strategyDeviceRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Resource
    private AiLoadRepository aiLoadRepository;
    @Resource
    private SysParamRepository sysParamRepository;

    @ApiOperation("查询实时监测列表")
    @UserLoginToken
    @RequestMapping(value = "/getRespMonitorList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getRespMonitorList(@RequestBody DemandModel model) {
        Specification<DemandRespTask> spec = new Specification<DemandRespTask>() {
            @Override
            public Predicate toPredicate(Root<DemandRespTask> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("dStatus"), 2));//执行中
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

                Order order = cb.desc(root.get("taskCode"));
                //纯日期不好排序，用任务编号来判断即可
                if (model.getRsTimeSort() != null) {
                    if (model.getRsTimeSort() == 1) {
                        order = cb.asc(root.get("taskCode"));
                    } else {
                        order = cb.desc(root.get("taskCode"));
                    }
                }
                if (model.getRespTypeSort() != null) {
                    if (model.getRespTypeSort() == 1) {
                        order = cb.asc(root.get("respType"));
                    } else {
                        order = cb.desc(root.get("respType"));
                    }

                }
                if (model.getRespLevelSort() != null) {
                    if (model.getRespLevelSort() == 1) {
                        order = cb.asc(root.get("respLevel"));
                    } else {
                        order = cb.desc(root.get("respLevel"));
                    }

                }
                if (model.getRespSubsidySort() != null) {
                    if (model.getRespSubsidySort() == 1) {
                        order = cb.asc(root.get("respSubsidy"));
                    } else {
                        order = cb.desc(root.get("respSubsidy"));
                    }

                }
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(order); //
                return criteriaQuery.getRestriction();
            }
        };
        //当前页为第几页 默认 1开始
        int page = model.getNumber();
        int size = model.getPageSize();

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<DemandRespTask> datas = demandRespTaskRepository.findAll(spec, pageable);

        PageModel pageModel = new PageModel();

        if (datas.getContent() != null && datas.getContent().size() > 0) {
            List<DemandRespTaskRespModel> list = new ArrayList<>();
            datas.getContent().forEach(d -> {
                DemandRespTaskRespModel newModel = new DemandRespTaskRespModel();
                BeanUtils.copyProperties(d, newModel);
                list.add(newModel);
            });
            pageModel.setContent(list);
        } else {
            pageModel.setContent(datas.getContent());
        }

        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);
    }

    @ApiOperation("获取详情列表")
    @UserLoginToken
    @RequestMapping(value = "/getDeviceListByRespId", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getDeviceListByRespId(@RequestBody DemandStrategyModel model) {
        PageModel pageModel = new PageModel();
        try {
            //得到该用户的节点列表
            List<String> nodeIds = userService.getAllowLoadNodeIds();
            if (nodeIds != null && nodeIds.size() > 0) {
                //当前页为第几页 默认 1开始
                int pageSize = model.getPageSize();
                int pageNum = (model.getNumber() - 1) * pageSize;//pg 从0开始
//                List<DemandRespStrategyNo> datas = noRepository.findByRespIdAndNodeIds(model.getRespId(), nodeIds,
//                        pageSize, pageNum);
                List<DemandRespStrategyNo> datas = noRepository.findByRespIdAndNodeIdsAndDrsStatus(model.getRespId(), nodeIds, 24, pageSize, pageNum);
                List<DemandRespStrategyNoResp> noResps = new ArrayList<>();
                //封装到pageUtil
                if (datas != null && datas.size() > 0) {
                    //查询，从哪里取基线负荷的值
                    SysParam sysParam=sysParamRepository.findSysParamBySysParamKey(SysParamEnum.BaseLineForecastCfg.getId());
                    JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
                    String getMethod = "商汤";
                    if (obj != null) {
                        if (obj.get("baseLineGetMethod") != null) {
                            getMethod=obj.get("baseLineGetMethod").toString();
                        }
                    }
                    //得到nodeIds,去ai表里查询最新的ai基线负荷
                    List<AiLoadForecasting> forecastingList = aiLoadRepository.findByNodeIdsMax(nodeIds);
                    Map<String, AiLoadForecasting> baseLoadMap = forecastingList.stream().collect(Collectors.toMap(AiLoadForecasting::getNodeId,
                            Function.identity()));
                    for(DemandRespStrategyNo d:datas){
                        DemandRespStrategyNoResp noResp = new DemandRespStrategyNoResp();
                        noResp.setDrsId(d.getDrsId());
                        noResp.setNodeName(d.getNodeName());
                        noResp.setDrsStatus(d.getDrsStatus());
                        noResp.setDeclareLoad(d.getDeclareLoad());
                        noResp.setNoHouseholds(d.getNoHouseholds());
                        AiLoadForecasting ai = baseLoadMap.get(d.getNodeId());
                        String baseLoad = "-";
                        if (ai != null) {
                            if(getMethod.equals("商汤")){
                                baseLoad=ai.getBaselineLoadValue();
                            }else{
                                baseLoad=ai.getBaselineLoadValueOther();
                            }
                            if (ai.getRealValue() != null) {
                                //当前负荷
                                noResp.setNowLoad(ai.getRealValue());
                            }
                        }
                        noResp.setBaseLoad(baseLoad);
                        //实时响应负荷
                        noResp.setRealTimeLoad(d.getRealTimeLoad());

                        noResps.add(noResp);
                    }
                }
                pageModel.setContent(noResps);
                pageModel.setTotalElements(noRepository.countRespIdAndNodeIds(model.getRespId(), nodeIds));

                return ResponseResult.success(pageModel);
            } else {
                pageModel.setTotalElements(0);
                return ResponseResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
            pageModel.setTotalElements(0);
            return ResponseResult.success();
        }
    }

}