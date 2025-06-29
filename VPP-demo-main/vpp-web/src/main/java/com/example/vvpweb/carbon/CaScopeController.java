package com.example.vvpweb.carbon;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.CaNodeInfoRepository;
import com.example.vvpdomain.CaScopeRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.entity.CaNodeInfo;
import com.example.vvpdomain.entity.CaScope;
import com.example.vvpweb.carbon.model.CaScopeModel;
import com.example.vvpweb.carbon.model.CaScopeOneResp;
import com.example.vvpweb.carbon.model.CaScopeThreeResp;
import com.example.vvpweb.carbon.model.CaScopeTwoResp;
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
import java.lang.reflect.Field;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author maoyating
 * @description 碳资产-碳模型
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/carbon/model")
@CrossOrigin
@Api(value = "碳资产管理-碳模型", tags = {"碳资产管理-碳模型"})
public class CaScopeController {

    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private CaNodeInfoRepository caNodeInfoRepository;
    @Autowired
    private CaScopeRepository caScopeRepository;

    @ApiOperation("楼宇基本信息")
    @UserLoginToken
    @RequestMapping(value = "/getNodeInfo", method = {RequestMethod.GET})
    public ResponseResult<CaNodeInfo> getNodeInfo(@RequestParam("nodeId") String nodeId) {
        Optional<CaNodeInfo> info = caNodeInfoRepository.findById(nodeId);
        if (!info.isPresent()) {
            return ResponseResult.success();
        } else {
            return ResponseResult.success(info.get());
        }
    }

    @ApiOperation("编辑楼宇信息")
    @UserLoginToken
    @RequestMapping(value = "/editNodeInfo", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editNodeInfo(@RequestBody @Valid CaNodeInfo req) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            caNodeInfoRepository.save(req);
        } catch (Exception e) {
            return ResponseResult.error("编辑楼宇信息异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    @ApiOperation("查询范围一")
    @UserLoginToken
    @RequestMapping(value = "/getScopeOneList", method = {RequestMethod.POST})
    public ResponseResult getScopeOneList(@RequestBody CaScopeModel model) {
        Specification<CaScope> spec = new Specification<CaScope>() {
            @Override
            public Predicate toPredicate(Root<CaScope> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("sStatus"), 1));//查询状态非 删除的数据
                predicates.add(cb.equal(root.get("scopeType"), 1));//范围(1-范围一 2-范围二 3-范围三)
                //燃烧排放类型（1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
                predicates.add(cb.equal(root.get("dischargeType"), model.getDischargeType()));
                predicates.add(cb.equal(root.get("caYear"), model.getCaYear()));//年份
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };

        List<CaScope> datas = caScopeRepository.findAll(spec);
        if (datas != null && datas.size() > 0) {
            List<CaScopeOneResp> oneRespList = new ArrayList<>();

            //按月份分组
            Map<Integer, List<CaScope>> monthMap = datas.stream().collect(groupingBy(CaScope::getCaMonth));
            for (Integer month : monthMap.keySet()) {
                List<CaScope> scopeList = monthMap.get(month);
                CaScopeOneResp oneResp = new CaScopeOneResp();
                oneResp.setCaMonth(month);
                oneResp.setCaYear(model.getCaYear());
                oneResp.setDischargeType(model.getDischargeType());
                scopeList.forEach(c -> {
                    //燃烧排放实体（11-天然气 12-煤气 13-柴油 21-公务车 31-冷机 32-分体空调 33-灭火器）
                    switch (c.getDischargeEntity()) {
                        case 11:
                            oneResp.setNaturalGas(c.getDischargeValue() + "");
                            break;
                        case 12:
                            oneResp.setCoalGas(c.getDischargeValue() + "");
                            break;
                        case 13:
                            oneResp.setDieselOil(c.getDischargeValue() + "");
                            break;
                        case 21:
                            oneResp.setOfficialVehicle(c.getDischargeValue() + "");
                            break;
                        case 31:
                            //冷机参数（1-冷机制冷剂类型 2-每台冷机制冷剂数量 3-冷机数量）
                            if (c.getRefrigerator() == 1) {
                                oneResp.setRefrigerant(c.getDischargeValue() + "");
                            } else if (c.getRefrigerator() == 2) {
                                oneResp.setRefrigerantNum(c.getDischargeValue() + "");
                            } else {
                                oneResp.setRefrigeratorNum(c.getDischargeValue() + "");
                            }
                            break;
                        case 32:
                            oneResp.setAirCNum(c.getDischargeValue() + "");
                            break;
                        case 33:
                            oneResp.setFNum(c.getDischargeValue() + "");
                            break;
                        default:
                            break;
                    }
                });
                oneRespList.add(oneResp);
            }
            return ResponseResult.success(oneRespList);
        }
        return ResponseResult.success();
    }

    @ApiOperation("新增范围一信息")
    @UserLoginToken
    @RequestMapping(value = "/addScopeOne", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult addTrade(@RequestBody @Valid CaScopeOneResp model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            Date date = new Date();
            List<CaScope> caScopeList = new ArrayList<>();

            Field[] fields = model.getClass().getDeclaredFields();
            for (int i = 0, len = fields.length; i < len; i++) {
                //这个是，有的字段是用private修饰的 将他设置为可读
                // fields[i].setAccessible(true);
                String fieldName = fields[i].getName();//属性
                String fieldValue = String.valueOf(fields[i].get(model));//属性值

                CaScope caScope = new CaScope();
                caScope.setScopeId(UUID.randomUUID().toString());
                caScope.setScopeType(1);
                caScope.setCaMonth(model.getCaMonth());
                caScope.setCaYear(model.getCaYear());
                caScope.setDischargeType(model.getDischargeType());
                caScope.setCreatedTime(date);
                caScope.setSStatus(1);
                caScope.setDischargeValue(Double.valueOf(fieldValue));

                if (model.getDischargeType() == 1) {
                    switch (fieldName) {
                        case "naturalGas"://天然气m²
                            caScope.setDischargeEntity(11);
                            break;
                        case "coalGas"://煤气m²
                            caScope.setDischargeEntity(12);
                            break;
                        case "dieselOil"://柴油m²
                            caScope.setDischargeEntity(13);
                            break;
                        default:
                            break;
                    }
                } else if (model.getDischargeType() == 2) {
                    switch (fieldName) {
                        case "officialVehicle"://公务车(辆)
                            caScope.setDischargeEntity(21);
                            break;
                        default:
                            break;
                    }
                } else {
                    caScope.setCaMonth(1);//按年统计的信息，默认设置月份为1
                    switch (fieldName) {

                        case "refrigerant"://冷机制冷剂类型
                            caScope.setDischargeEntity(31);
                            caScope.setRefrigerator(1);

                            break;
                        case "refrigerantNum"://每台冷机制冷剂数量
                            caScope.setDischargeEntity(31);
                            caScope.setRefrigerator(2);
                            break;
                        case "refrigeratorNum"://冷机数量
                            caScope.setDischargeEntity(31);
                            caScope.setRefrigerator(3);
                            break;
                        case "airCNum"://分体空调数量
                            caScope.setDischargeEntity(32);
                            break;
                        case "fNum"://灭火器数量
                            caScope.setDischargeEntity(33);
                            break;
                        default:
                            break;
                    }


                }

                caScopeList.add(caScope);
            }
            caScopeRepository.saveAll(caScopeList);
        } catch (Exception e) {
            return ResponseResult.error("录入信息异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }


    @ApiOperation("查询范围二")
    @UserLoginToken
    @RequestMapping(value = "/getScopeTwoList", method = {RequestMethod.POST})
    public ResponseResult getScopeTwoList(@RequestBody CaScopeModel model) {
        Specification<CaScope> spec = new Specification<CaScope>() {
            @Override
            public Predicate toPredicate(Root<CaScope> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("sStatus"), 1));//查询状态非 删除的数据
                predicates.add(cb.equal(root.get("scopeType"), 2));//范围(1-范围一 2-范围二 3-范围三)
                predicates.add(cb.equal(root.get("caYear"), model.getCaYear()));//年份
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };

        List<CaScope> datas = caScopeRepository.findAll(spec);
        if (datas != null && datas.size() > 0) {
            List<CaScopeTwoResp> oneRespList = new ArrayList<>();
            //按月份分组
            Map<Integer, List<CaScope>> monthMap = datas.stream().collect(groupingBy(CaScope::getCaMonth));
            for (Integer month : monthMap.keySet()) {
                List<CaScope> scopeList = monthMap.get(month);
                CaScopeTwoResp oneResp = new CaScopeTwoResp();
                oneResp.setCaMonth(month);
                oneResp.setCaYear(model.getCaYear());
                scopeList.forEach(c -> {
                    //范围二购买内容（1-外购电力（kWh）2-外购热力（KJ））
                    switch (c.getScopeTwo()) {
                        case 1:
                            oneResp.setPower(c.getDischargeValue() + "");
                            break;
                        case 2:
                            oneResp.setHeatingPower(c.getDischargeValue() + "");
                            break;
                        default:
                            break;
                    }
                });
                oneRespList.add(oneResp);
            }
            return ResponseResult.success(oneRespList);
        }
        return ResponseResult.success();
    }

    @ApiOperation("新增范围二信息")
    @UserLoginToken
    @RequestMapping(value = "/addScopeTwo", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult addScopeTwo(@RequestBody @Valid CaScopeTwoResp model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            Date date = new Date();
            List<CaScope> caScopeList = new ArrayList<>();

            Field[] fields = model.getClass().getDeclaredFields();
            for (int i = 0, len = fields.length; i < len; i++) {
                //这个是，有的字段是用private修饰的 将他设置为可读
                // fields[i].setAccessible(true);
                String fieldName = fields[i].getName();//属性
                String fieldValue = String.valueOf(fields[i].get(model));//属性值

                CaScope caScope = new CaScope();
                caScope.setScopeId(UUID.randomUUID().toString());
                caScope.setScopeType(2);
                caScope.setCaMonth(model.getCaMonth());
                caScope.setCaYear(model.getCaYear());

                caScope.setCreatedTime(date);
                caScope.setSStatus(1);
                caScope.setDischargeValue(Double.valueOf(fieldValue));
                switch (fieldName) {
                    case "power"://外购电力（kWh）
                        caScope.setScopeTwo(1);

                        break;
                    case "heatingPower"://外购热力（KJ）
                        caScope.setScopeTwo(2);
                        break;
                    default:
                        break;
                }

                caScopeList.add(caScope);
            }
            caScopeRepository.saveAll(caScopeList);
        } catch (Exception e) {
            return ResponseResult.error("录入信息异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    @ApiOperation("查询范围三")
    @UserLoginToken
    @RequestMapping(value = "/getScopeThreeList", method = {RequestMethod.POST})
    public ResponseResult getScopeThreeList(@RequestBody CaScopeModel model) {
        Specification<CaScope> spec = new Specification<CaScope>() {
            @Override
            public Predicate toPredicate(Root<CaScope> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("sStatus"), 1));//查询状态非 删除的数据
                predicates.add(cb.equal(root.get("scopeType"), 3));//范围(1-范围一 2-范围二 3-范围三)
                predicates.add(cb.equal(root.get("caYear"), model.getCaYear()));//年份
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };

        List<CaScope> datas = caScopeRepository.findAll(spec);
        if (datas != null && datas.size() > 0) {
            List<CaScopeThreeResp> oneRespList = new ArrayList<>();
            //按月份分组
            Map<Integer, List<CaScope>> monthMap = datas.stream().collect(groupingBy(CaScope::getCaMonth));
            for (Integer month : monthMap.keySet()) {
                List<CaScope> scopeList = monthMap.get(month);
                CaScopeThreeResp oneResp = new CaScopeThreeResp();
                oneResp.setCaMonth(month);
                oneResp.setCaYear(model.getCaYear());
                scopeList.forEach(c -> {
                    //范围三类型（1-差旅-飞机(km) 2-差旅-火车（km) 3-差旅-私家车(辆) 4-自来水(t) 5-纸张消耗(张)）
                    switch (c.getScopeThree()) {
                        case 1:
                            oneResp.setTPlane(c.getDischargeValue() + "");
                            break;
                        case 2:
                            oneResp.setTTrain(c.getDischargeValue() + "");
                            break;
                        case 3:
                            oneResp.setTCar(c.getDischargeValue() + "");
                            break;
                        case 4:
                            oneResp.setTTapWater(c.getDischargeValue() + "");
                            break;
                        case 5:
                            oneResp.setTPaper(c.getDischargeValue() + "");
                            break;
                        default:
                            break;
                    }
                });
                oneRespList.add(oneResp);
            }
            return ResponseResult.success(oneRespList);
        }
        return ResponseResult.success();
    }

    @ApiOperation("新增范围三信息")
    @UserLoginToken
    @RequestMapping(value = "/addScopeThree", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult addScopeThree(@RequestBody @Valid CaScopeThreeResp model) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            Date date = new Date();
            List<CaScope> caScopeList = new ArrayList<>();

            Field[] fields = model.getClass().getDeclaredFields();
            for (int i = 0, len = fields.length; i < len; i++) {
                //这个是，有的字段是用private修饰的 将他设置为可读
                // fields[i].setAccessible(true);
                String fieldName = fields[i].getName();//属性
                String fieldValue = String.valueOf(fields[i].get(model));//属性值

                CaScope caScope = new CaScope();
                caScope.setScopeId(UUID.randomUUID().toString());
                caScope.setScopeType(3);
                caScope.setCaMonth(model.getCaMonth());
                caScope.setCaYear(model.getCaYear());
                caScope.setCreatedTime(date);
                caScope.setSStatus(1);
                caScope.setDischargeValue(Double.valueOf(fieldValue));

                switch (fieldName) {
                    case "tPlane"://差旅-飞机(km)
                        caScope.setScopeThree(1);

                        break;
                    case "tTrain"://差旅-火车（km)
                        caScope.setScopeThree(2);
                        break;
                    case "tCar"://差旅-私家车(辆)
                        caScope.setScopeThree(3);
                        break;
                    case "tTapWater"://自来水(t)
                        caScope.setScopeThree(4);
                        break;
                    case "tPaper"://纸张消耗(张)
                        caScope.setScopeThree(5);
                        break;
                    default:
                        break;
                }
                caScopeList.add(caScope);
            }
            caScopeRepository.saveAll(caScopeList);
        } catch (Exception e) {
            return ResponseResult.error("录入信息异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

}