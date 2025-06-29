package com.example.vvpweb.demand;

import com.alibaba.fastjson.JSON;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.entity.Node;
import com.example.vvpservice.demand.model.DeclareInfo;
import com.example.vvpservice.demand.model.DemandRespStrategyModel;
import com.example.vvpservice.demand.model.DemandRespStrategyNoModel;
import com.example.vvpservice.demand.service.DemandRespStrategyNoService;
import com.example.vvpservice.demand.service.DemandRespStrategyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/demandKafka")
@CrossOrigin
@Api(value = "需求响应-接收高新兴kafka数据", tags = {"需求响应-接收高新兴kafka数据"})
public class DemandKafkaDataController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemandKafkaDataController.class);
    @Resource
    private NodeRepository nodeRepository;
    @Resource
    private DemandRespStrategyService respStrategyService;
    @Resource
    private DemandRespStrategyNoService noService;

    /**
     * 交互序号-2 申报响应负荷（Topic）名称
     * demand_response_invitation_response
     * <p>
     * 1、能源平台收到虚拟电厂平台的日前邀约信息后，根据自身平台计算出响应，推送至约定kafka对应topic，
     * 虚拟电厂平台消费该topic
     * 2、每条信息为数组形式，最多2000
     */
    @RequestMapping(value = "demandResponseInvitationResponse", method = {RequestMethod.POST})
    @ApiOperation("申报响应负荷（Topic）")
    public ResponseResult demandResponseInvitationResponse(@RequestBody String json) {
        try {
            if (StringUtils.isNotEmpty(json)) {

                List<DeclareInfo> list = JSON.parseArray(json, DeclareInfo.class);
                if (list != null && list.size() > 0) {
                    List<DemandRespStrategyModel> strategyModelList = new ArrayList<>();
                    //选择的运行策略，各节点的负荷值
                    List<DemandRespStrategyNoModel> strategyNoList = new ArrayList<>();

                    //根据需求响应的id进行分组
                    Map<String, List<DeclareInfo>> demandMap = list.stream().collect(Collectors.groupingBy(DeclareInfo::getDemandId));
                    List<String> noHouseholdsList = list.stream().map(DeclareInfo::getMeterAccountNumber).collect(Collectors.toList());
                    Specification<Node> spec1 = new Specification<Node>() {
                        @Override
                        public Predicate toPredicate(Root<Node> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                            List<Predicate> predicates = new ArrayList<>();
                            predicates.add(cb.in(root.get("noHouseholds")).value(noHouseholdsList));

                            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                        }
                    };
                    List<Node> nodeList = nodeRepository.findAll(spec1);
                    Map<String, Node> nodeMap = nodeList.stream().collect(Collectors.toMap(Node::getNoHouseholds, n -> n));

                    for (String demandId : demandMap.keySet()) {
                        // 处理每个消息
                        DemandRespStrategyModel dr = new DemandRespStrategyModel();
                        dr.setSId("admin" + demandId + "demand_response_invitation_response");
                        dr.setStrategyId("demand_response_invitation_response");
                        dr.setCreateBy("admin");
                        dr.setRespId(demandId);

                        strategyModelList.add(dr);

                        for (DeclareInfo declareInfo : demandMap.get(demandId)) {
                            //判断运行策略是自动参加响应任务，还是手动;如果是自动，需要自动进行申报
                            int status = 11;//未申报

                            DemandRespStrategyNoModel strategyNo = new DemandRespStrategyNoModel();
                            strategyNo.setDrsId(dr.getSId() + declareInfo.getMeterAccountNumber());//申报负荷id
                            strategyNo.setNoHouseholds(declareInfo.getMeterAccountNumber());//户号
                            strategyNo.setDeclareLoad(declareInfo.getAvailableValue());//申报负荷
                            strategyNo.setSId(dr.getSId());
                            strategyNo.setDrsStatus(status);
                            Node node = nodeMap.get(declareInfo.getMeterAccountNumber());
                            if (node != null) {
                                strategyNo.setNodeName(node.getNodeName());//节点名称
                                strategyNo.setNodeId(node.getNodeId());//节点id
                            }
                            strategyNo.setIsPlatform(1);//第三方平台
                            strategyNoList.add(strategyNo);
                        }
                    }

                    boolean flag = respStrategyService.batchInsert(strategyModelList);
                    if (flag) {
                        noService.batchInsert(strategyNoList);
                    }
                }
            }
            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.error(e.getMessage());
        }
    }

}
