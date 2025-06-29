package com.example.vvpweb.carbon;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.CaEmissionFactorRepository;
import com.example.vvpdomain.CaNodeInfoRepository;
import com.example.vvpdomain.CaSinkConfRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.entity.CaEmissionFactor;
import com.example.vvpdomain.entity.CaSinkConf;
import com.example.vvpdomain.entity.Node;
import com.example.vvpweb.carbon.model.CaSinkReq;
import com.example.vvpweb.carbon.model.SinkResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author maoyating
 * @description 碳资产管理-碳汇
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/carbon/sink")
@CrossOrigin
@Api(value = "碳资产管理-碳汇", tags = {"碳资产管理-碳汇"})
public class CarbonSinkController {

    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private CaSinkConfRepository sinkConfRepository;
    @Autowired
    private CaNodeInfoRepository caNodeInfoRepository;
    @Autowired
    private CaEmissionFactorRepository emissionFactorRepository;

    @ApiOperation("查询碳汇信息")
    @UserLoginToken
    @RequestMapping(value = "/getCarbonSink", method = {RequestMethod.POST})
    public ResponseResult getCarbonSink(@RequestBody CaSinkReq model) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String endTime = TimeUtil.getMonthLastDay(simpleDateFormat.format(model.getEndTime()));
        LocalDate finalFirstDayOfMonth = LocalDate.parse(simpleDateFormat.format(model.getStartTime()) + "-01");
        LocalDate finalLastDayOfMonth = LocalDate.parse(endTime);
        System.out.println(finalFirstDayOfMonth + "   " + finalLastDayOfMonth);
        //先查询出日期区间的绿化面积与树木数量
        Specification<CaSinkConf> spec = new Specification<CaSinkConf>() {
            @Override
            public Predicate toPredicate(Root<CaSinkConf> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//节点id
                predicates.add(cb.between(root.get("addTime").as(LocalDate.class),
                        finalFirstDayOfMonth, finalLastDayOfMonth));//日期区间
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };

        List<CaSinkConf> datas = sinkConfRepository.findAll(spec);
        //判断是否为空
        if (datas != null && datas.size() > 0) {

            //查询节点所属的省份
            Optional<Node> optionalNode = nodeRepository.findById(model.getNodeId());
            if (!optionalNode.isPresent()) {
                return ResponseResult.error("该节点不存在");
            }
            Node node = optionalNode.get();

            SinkResp sinkResp = new SinkResp();

            List<String> cids = new ArrayList<>();//查询绿化面积与种植树木的换算
            cids.add("草坪");
            cids.add("树木");
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
                    CriteriaBuilder.In<List<String>> inClause = cb.in(root.get("emissionFactorName"));//排放因子名称
                    inClause.value(cids);
                    predicates.add(inClause);
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    return criteriaQuery.getRestriction();
                }
            };
            List<CaEmissionFactor> factorDatas = emissionFactorRepository.findAll(specEmission);
            Map<String, Double> emissionFactor = new HashMap<>();


            if (factorDatas != null && factorDatas.size() > 0) {
                emissionFactor = factorDatas.stream()
                        .collect(Collectors.groupingBy(CaEmissionFactor::getProvince))
                        .entrySet().iterator().next().getValue()
                        .stream()
                        .collect(Collectors.toMap(CaEmissionFactor::getEmissionFactorName,
                                CaEmissionFactor::getCo2));
            }

            Double greenConf = emissionFactor.get("草坪");
            double greenCO2 = 0.00;
            //算出绿化面积的总面积，再乘以 碳排放因子 里的二氧化碳
            double greenArea = datas.stream().filter(c -> c.getCType().equals("lvhuamianji"))
                    .mapToDouble(c -> c.getAttrNum()).sum();
            BigDecimal bigDecimalDouble = new BigDecimal(greenArea).setScale(2, BigDecimal.ROUND_HALF_UP);
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM");
            String start = f.format(model.getStartTime());
            String end = f.format(model.getEndTime());
//            long monthDiff = 0L;
//            try {
//                //这里必须加1，比如查询2020-08至2020-08，应该为一个月
//                monthDiff = TimeUtil.getMonthDiff(start,end);
//            }catch (Exception e){
//                return ResponseResult.error("输入的日期异常 +" + e.getMessage());
//            }

            sinkResp.setGreenArea(bigDecimalDouble + "");//绿化面积
            if (greenConf != null) {
                // greenCO2=CO2吸收量*月份%12*面积;
                // 单位是 kg/平米.年   所以转为月份要%12   kg换算t
//                greenCO2=greenArea*greenConf*monthDiff/(12.0*1000.0);
                greenCO2 = greenArea * greenConf / 1000.0;
                sinkResp.setGreenEmissions(greenCO2 + "");
            } else {
                sinkResp.setGreenEmissions("0");
            }

            //算出树木的总棵树，再乘以 碳排放因子 里的二氧化碳
            Double treeConf = emissionFactor.get("树木");
            double treeCO2 = 0.00;
            //算出绿化面积的总面积，再乘以 碳排放因子 里的二氧化碳
            double treeNum = datas.stream().filter(c -> c.getCType().equals("zhongzhishumu"))
                    .mapToDouble(c -> c.getAttrNum()).sum();

            sinkResp.setTreeNum(new Double(treeNum).longValue() + "");//树木数量
            if (greenConf != null) {
//                treeCO2=treeNum*treeConf*monthDiff/(12.0*1000.0);
                treeCO2 = treeNum * treeConf / 1000.0;
                sinkResp.setTreeEmissions(treeCO2 + "");
            } else {
                sinkResp.setTreeEmissions("0");
            }

            //绿化面积吸收的二氧化碳+树木吸收的二氧化碳 总和
            sinkResp.setTotal(String.valueOf(greenCO2 + treeCO2));//总吸收碳排放量
            return ResponseResult.success(sinkResp);
        }

        return ResponseResult.success();
    }

    @ApiOperation("查询树木/绿化面积列表")
    @UserLoginToken
    @RequestMapping(value = "/getTreeOrAreaList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getTreeOrAreaList(@RequestBody CaSinkReq model) {
        Specification<CaSinkConf> spec = new Specification<CaSinkConf>() {
            @Override
            public Predicate toPredicate(Root<CaSinkConf> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("cType"), model.getCType()));//类型
                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//节点id
                predicates.add(cb.between(root.get("addTime"), model.getStartTime(), model.getEndTime()));//日期区间
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.desc(root.get("addTime"))); //根据日期降序排
                return criteriaQuery.getRestriction();
            }
        };
        //当前页为第几页 默认 1开始
        int page = model.getNumber();
        int size = model.getPageSize();

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<CaSinkConf> datas = sinkConfRepository.findAll(spec, pageable);

        PageModel pageModel = new PageModel();
        //封装到pageUtil

        pageModel.setContent(datas.getContent());

        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);
    }

    @ApiOperation("录入树木/绿化面积信息")
    @UserLoginToken
    @RequestMapping(value = "/addTreeOrArea", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult addTree(@RequestBody @Valid CaSinkConf trade) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            if (StringUtils.isEmpty(trade.getCType())) {
                return ResponseResult.error("输入类型有误" + trade.getCType());
            }
            trade.setCId(UUID.randomUUID().toString());
            //判断日期是否重复
            Specification<CaSinkConf> spec = new Specification<CaSinkConf>() {
                @Override
                public Predicate toPredicate(Root<CaSinkConf> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.equal(root.get("cType"), trade.getCType()));//类型
                    predicates.add(cb.equal(root.get("addTime"), trade.getAddTime()));//日期
                    predicates.add(cb.equal(root.get("nodeId"), trade.getNodeId()));//节点id
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    return criteriaQuery.getRestriction();
                }
            };
            List<CaSinkConf> datas = sinkConfRepository.findAll(spec);
            if (datas != null && datas.size() > 0) {
                return ResponseResult.error("添加的月份已存在!");
            }
            sinkConfRepository.save(trade);
        } catch (Exception e) {
            return ResponseResult.error("录入树木信息异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    @ApiOperation("编辑树木/绿化面积信息")
    @UserLoginToken
    @RequestMapping(value = "/editTreeOrArea", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editTree(@RequestBody @Valid CaSinkConf trade) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            Optional<CaSinkConf> task = sinkConfRepository.findById(trade.getCId());
            if (!task.isPresent()) {
                return ResponseResult.error("该信息不存在");
            }
            if (StringUtils.isEmpty(trade.getCType())) {
                return ResponseResult.error("输入类型有误" + trade.getCType());
            }
            sinkConfRepository.save(trade);
        } catch (Exception e) {
            return ResponseResult.error("编辑信息异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    @ApiOperation("删除树木/绿化面积信息")
    @UserLoginToken
    @RequestMapping(value = "/delTreeOrArea", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult delTree(@RequestParam("cId") String cId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            //判断id是否为空
            if (StringUtils.isBlank(cId)) {
                return ResponseResult.error("id不能为空");
            }
            sinkConfRepository.deleteById(cId);
        } catch (Exception e) {
            return ResponseResult.error("删除异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

}