package com.example.vvpweb.carbon;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.StringUtils;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.CaEmissionFactorRepository;
import com.example.vvpdomain.CaNodeInfoRepository;
import com.example.vvpdomain.CaScopeRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.entity.CaScope;
import com.example.vvpweb.carbon.model.CaFootmarkModel;
import com.example.vvpweb.carbon.model.CaFootmarkReq;
import com.example.vvpweb.carbon.model.CaSubFootmarkModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carbon/footmark")
@CrossOrigin
@Api(value = "碳资产管理-碳足迹", tags = {"碳资产管理-碳足迹"})
public class CarbonFootmarkController {

    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private CaScopeRepository scopeRepository;
    @Autowired
    private CaNodeInfoRepository caNodeInfoRepository;
    @Autowired
    private CaEmissionFactorRepository emissionFactorRepository;

    @ApiOperation("查询碳足迹信息")
    @UserLoginToken
    @RequestMapping(value = "/getFootmark", method = {RequestMethod.POST})
    public ResponseResult getCarbonSink(@RequestBody CaFootmarkReq model) {

        Specification<CaScope> spec = new Specification<CaScope>() {
            @Override
            public Predicate toPredicate(Root<CaScope> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//节点id
                predicates.add(cb.equal(root.get("scopeType"), model.getScopeType()));//范围(1-范围一 2-范围二 3-范围三)
                if (model.getDischargeType() != null) {
                    predicates.add(cb.equal(root.get("dischargeType"), model.getDischargeType()));//燃烧排放类型
                }
                predicates.add(cb.equal(root.get("caYear"), model.getYear()));//年份
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.desc(root.get("caMonth"))); //根据日期降序排
                return criteriaQuery.getRestriction();
            }
        };

        List<CaScope> datas = scopeRepository.findAll(spec);
        //判断是否为空
        if (datas != null && datas.size() > 0) {
            //根据月份分组
            Map<Integer, List<CaScope>> monthList = datas.stream().collect(Collectors.groupingBy(CaScope::getCaMonth));

            return ResponseResult.success(monthList);

        }

        return ResponseResult.success();
    }

    @ApiOperation("编辑足迹信息")
    @UserLoginToken
    @RequestMapping(value = "/editFootmark", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editFootmark(@RequestBody @Valid CaFootmarkModel model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            List<CaScope> editList = new ArrayList<>();
            //全部新增
            for (int month : model.getMonthList().keySet()) {
                List<CaSubFootmarkModel> list = model.getMonthList().get(month);
                //日期
                list.forEach(e -> {
                    CaScope scope = new CaScope();

                    scope.setScopeType(model.getScopeType());
                    StringBuilder id = new StringBuilder();
                    //id的规则=年份-月份-范围-类型-节点id      用这个规则的作用：不会重复，新增编辑通通搞定
                    id.append(model.getYear() + "-" + month + "-" + model.getScopeType());
                    if (model.getScopeType() == 1) {
                        id.append("-" + model.getDischargeType());
                        scope.setDischargeType(model.getDischargeType());
                        if (e.getDischargeEntity() != null) {
                            scope.setDischargeEntity(e.getDischargeEntity());
                            id.append(e.getDischargeEntity());
                        }
                        if (e.getRefrigerator() != null) {
                            scope.setRefrigerator(e.getRefrigerator());
                            id.append(e.getRefrigerator());
                        }
                    } else if (model.getScopeType() == 2) {
                        id.append("-" + e.getScopeTwo());
                        scope.setScopeTwo(e.getScopeTwo());
                    } else if (model.getScopeType() == 3) {
                        id.append("-" + e.getScopeThree());
                        scope.setScopeThree(e.getScopeThree());
                    }
                    id.append("-" + model.getNodeId());
                    scope.setScopeId(id.toString());

                    scope.setDischargeValue(e.getDischargeValue());
                    scope.setCaYear(model.getYear());
                    scope.setCaMonth(month);
                    String yearMonth = model.getYear() + StringUtils.getNumber(month, 2);//月份补零
                    scope.setCaYearMonth(Integer.parseInt(yearMonth));
                    scope.setSStatus(1);
                    scope.setNodeId(model.getNodeId());

                    editList.add(scope);
                });
            }

            scopeRepository.saveAll(editList);
        } catch (Exception e) {
            return ResponseResult.error("编辑信息异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }


}