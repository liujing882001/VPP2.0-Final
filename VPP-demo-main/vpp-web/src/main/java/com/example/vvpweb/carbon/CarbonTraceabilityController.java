package com.example.vvpweb.carbon;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.CaEmissionFactorRepository;
import com.example.vvpdomain.CaNodeInfoRepository;
import com.example.vvpdomain.CaScopeRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.entity.CaEmissionFactor;
import com.example.vvpdomain.entity.CaScope;
import com.example.vvpdomain.entity.Node;
import com.example.vvpweb.carbon.model.TraceabilityReq;
import com.example.vvpweb.carbon.model.TraceabilityResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author maoyating
 * @description 碳资产管理-碳溯源
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/carbon/traceability")
@CrossOrigin
@Api(value = "碳资产管理-碳溯源", tags = {"碳资产管理-碳溯源"})
public class CarbonTraceabilityController {

    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private CaScopeRepository scopeRepository;
    @Autowired
    private CaEmissionFactorRepository emissionFactorRepository;
    @Autowired
    private CaNodeInfoRepository caNodeInfoRepository;

    @ApiOperation("查询碳溯源列表")
    @UserLoginToken
    @RequestMapping(value = "/getTraceabilityList", method = {RequestMethod.POST})
    public ResponseResult getTraceabilityList(@RequestBody TraceabilityReq model) {
        int startTime = Integer.parseInt(model.getStartTime().replace("-", ""));
        int endTime = Integer.parseInt(model.getEndTime().replace("-", ""));
        Specification<CaScope> spec = new Specification<CaScope>() {
            @Override
            public Predicate toPredicate(Root<CaScope> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("sStatus"), 1));//查询状态 正常
                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//楼宇节点id
                predicates.add(cb.equal(root.get("scopeType"), model.getScopeType()));//范围(1-范围一 2-范围二 3-范围三)
                predicates.add(cb.between(root.get("caYearMonth"), startTime, endTime));//查询对应年月
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        List<CaScope> datas = scopeRepository.findAll(spec);

        //查询节点所属的省份
        Optional<Node> optionalNode = nodeRepository.findById(model.getNodeId());
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
                if (StringUtils.isNotEmpty(node.getAddress())) {
                    predicates.add(cb.equal(root.get("province"), com.example.vvpcommom.StringUtils.getProvince(node.getAddress())));//省份
                } else if (StringUtils.isNotEmpty(node.getProvinceRegionName())) {
                    predicates.add(cb.equal(root.get("province"), node.getProvinceRegionName()));//省份
                }
                predicates.add(cb.equal(root.get("scopeType"), model.getScopeType()));//范围
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        List<CaEmissionFactor> factorDatas = emissionFactorRepository.findAll(specEmission);

        List<TraceabilityResp> respList = new ArrayList<>();

        //得到碳排放因子配置
        Map<String, CaEmissionFactor> factorMap = new HashMap<>();
        if (factorDatas != null && factorDatas.size() > 0) {
            factorDatas = factorDatas.stream()
                    .collect(Collectors.groupingBy(CaEmissionFactor::getProvince))
                    .entrySet().iterator().next().getValue();
            factorMap = factorDatas.stream().collect(
                    Collectors.toMap(c -> c.getEmissionFactorName(), Function.identity()));

        }

//        long monthDiff =0L;
//        try {
//            monthDiff = TimeUtil.getMonthDiff(model.getStartTime(),model.getEndTime());
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        //如果是范围一
        if (model.getScopeType() == 1) {
            // 范围一具体类型：总和
            Map<Integer, Double> scopeTwoMap = datas.stream().collect(Collectors.groupingBy(CaScope::getDischargeEntity,
                    Collectors.summingDouble(CaScope::getDischargeValue)));
            for (Integer scope : scopeTwoMap.keySet()) {
                TraceabilityResp tr = new TraceabilityResp();
                String dischargeEntity = "";
                String unit = "";
                //燃烧排放实体（11-天然气 12-煤气 13-柴油 21-公务车 31-冷机 32-分体空调 33-灭火器）
                switch (scope) {
                    case 11:
                        dischargeEntity = "天然气";
                        unit = "m³";
                        break;
                    case 12:
                        dischargeEntity = "煤气";
                        unit = "m³";
                        break;
                    case 13:
                        dischargeEntity = "柴油";
                        unit = "m³";
                        break;
                    case 21:
                        dischargeEntity = "公务车";
                        unit = "辆";
                        break;
                    case 31:
                        dischargeEntity = "冷机制冷剂";
                        unit = "kg";
                        break;
                    case 32:
                        dischargeEntity = "空调氟利昂";
                        unit = "kg";
                        break;
                    case 33:
                        dischargeEntity = "灭火器";
                        unit = "个";
                        break;
                    default:
                        break;
                }
                CaEmissionFactor factor = factorMap.get(dischargeEntity);

                tr.setDischargeEntity(dischargeEntity);//类型
                tr.setNum(String.valueOf(scopeTwoMap.get(scope)));//数量
                tr.setUnit(unit);//单位
                tr.setFactor(dischargeEntity);//碳排放因子
                if (factor != null) {
                    double emission = scopeTwoMap.get(scope) * factor.getCo2();
//                    if(scope==21 || scope==33){
//                        //这两种类型需要换算年
//                        emission = emission*monthDiff/(12d*1000.0);  //  二氧化碳排放量需要转化单位 1t=1000kg
//                    }else{
                    emission = emission / 1000.0; //  二氧化碳排放量需要转化单位 1t=1000kg
//                    }
                    BigDecimal bd = new BigDecimal(emission);
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    tr.setEmission(bd.toString());//二氧化碳排放量
                    tr.setDischargeType(String.valueOf(factor.getDischargeType()));//燃烧源
                }
                respList.add(tr);
            }
        } else {
            //如果是范围二
            if (model.getScopeType() == 2) {

                // 范围二类型：总和
                Map<Integer, Double> scopeTwoMap = datas.stream().collect(Collectors.groupingBy(CaScope::getScopeTwo,
                        Collectors.summingDouble(CaScope::getDischargeValue)));
                for (Integer scope : scopeTwoMap.keySet()) {
                    TraceabilityResp tr = new TraceabilityResp();
                    String dischargeEntity = "";
                    String unit = "";
                    switch (scope) {
                        case 1:
                            dischargeEntity = "外购电力";
                            unit = "kWh";
                            break;
                        case 2:
                            dischargeEntity = "外购热力";
                            unit = "KJ";
                            break;
                        default:
                            break;
                    }
                    CaEmissionFactor factor = factorMap.get(dischargeEntity);

                    tr.setDischargeEntity(dischargeEntity);//类型
                    tr.setNum(String.valueOf(scopeTwoMap.get(scope)));//数量
                    tr.setUnit(unit);//单位
                    tr.setFactor(dischargeEntity);//碳排放因子
                    if (factor != null) {
                        BigDecimal bd = new BigDecimal(scopeTwoMap.get(scope) * factor.getCo2() / 1000.0);
                        bd = bd.setScale(2, RoundingMode.HALF_UP);
                        tr.setEmission(bd.toString());//二氧化碳排放量
                        // tr.setDischargeType(factor.getDescription());//燃烧源
                    }
                    respList.add(tr);
                }

            } else if (model.getScopeType() == 3) {
                //如果是范围三
                // 范围三类型：总和
                Map<Integer, Double> scopeThreeMap = datas.stream().collect(Collectors.groupingBy(CaScope::getScopeThree,
                        Collectors.summingDouble(CaScope::getDischargeValue)));

                for (Integer scope : scopeThreeMap.keySet()) {
                    TraceabilityResp tr = new TraceabilityResp();
                    String dischargeEntity = "";
                    String unit = "";
                    //范围三类型（1-差旅-飞机(km) 2-差旅-火车（km) 3-差旅-私家车(辆) 4-自来水(t) 5-纸张消耗(张)）
                    switch (scope) {
                        case 1:
                            dischargeEntity = "差旅-飞机";
                            unit = "km";
                            break;
                        case 2:
                            dischargeEntity = "差旅-火车";
                            unit = "km";
                            break;
                        case 3:
                            dischargeEntity = "差旅-私家车";
                            unit = "辆";
                            break;
                        case 4:
                            dischargeEntity = "自来水";
                            unit = "t";
                            break;
                        case 5:
                            dischargeEntity = "纸张消耗";
                            unit = "张";
                            break;
                        default:
                            break;
                    }
                    CaEmissionFactor factor = factorMap.get(dischargeEntity);

                    tr.setDischargeEntity(dischargeEntity);//类型
                    tr.setNum(String.valueOf(scopeThreeMap.get(scope)));//数量
                    tr.setUnit(unit);//单位
                    tr.setFactor(dischargeEntity);//碳排放因子
                    if (factor != null) {
                        BigDecimal bd = new BigDecimal(scopeThreeMap.get(scope) * factor.getCo2() / 1000.0);
                        bd = bd.setScale(2, RoundingMode.HALF_UP);
                        tr.setEmission(bd.toString());//二氧化碳排放量
                        //  tr.setDischargeType(factor.getDescription());//燃烧源
                    }
                    respList.add(tr);
                }

            }

        }


        return ResponseResult.success(respList);
    }


}