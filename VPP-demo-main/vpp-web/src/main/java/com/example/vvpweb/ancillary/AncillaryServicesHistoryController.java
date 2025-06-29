package com.example.vvpweb.ancillary;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.AncillarySStrategyRepository;
import com.example.vvpdomain.AncillaryServicesRepository;
import com.example.vvpdomain.entity.AncillarySStrategy;
import com.example.vvpdomain.entity.AncillaryServices;
import com.example.vvpweb.ancillary.model.AncillaryModel;
import com.example.vvpweb.ancillary.model.AncillaryRespModel;
import com.example.vvpweb.ancillary.model.AncillaryServicesRespModel;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author maoyating
 * @description 辅助服务-历史记录
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/ancillary_services/ancillary_history")
@CrossOrigin
@Api(value = "辅助服务-历史记录", tags = {"辅助服务-历史记录"})
public class AncillaryServicesHistoryController {

    private static Logger logger = LoggerFactory.getLogger(AncillaryServicesHistoryController.class);
    @Autowired
    private AncillaryServicesRepository asRepository;
    @Autowired
    private AncillarySStrategyRepository assRepository;

    @ApiOperation("查询历史记录列表")
    @UserLoginToken
    @RequestMapping(value = "/getASHistoryList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getASHistoryList(@RequestBody AncillaryModel model) {
        Specification<AncillaryServices> spec = new Specification<AncillaryServices>() {
            @Override
            public Predicate toPredicate(Root<AncillaryServices> root,
                                         CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("aStatus"), 3));//已执行任务
                predicates.add(cb.between(root.get("assDate"), model.getStartDate(), model.getEndDate()));
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.desc(root.get("assDate"))); //根据日期降序排
                return criteriaQuery.getRestriction();
            }
        };
        //当前页为第几页 默认 1开始
        int page = model.getNumber();
        int size = model.getPageSize();

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<AncillaryServices> datas = asRepository.findAll(spec, pageable);

        PageModel pageModel = new PageModel();
        if (datas.getContent() != null && datas.getContent().size() > 0) {
            List<AncillaryServicesRespModel> ancillaryServicesList = new ArrayList<>();
            datas.getContent().forEach(d -> {
                AncillaryServicesRespModel newModel = new AncillaryServicesRespModel();
                double hour = TimeUtil.getDifferHour(d.getAssTime(), d.getAseTime());
                //实际响应负荷
                double actualLoad = d.getStrategyList().stream().mapToDouble(AncillarySStrategy::getActualLoad).sum();
                //实际响应电量
                double actualPower = actualLoad * hour;

                d.setActualLoad(actualLoad);
                d.setActualPower(actualPower);

                BeanUtils.copyProperties(d, newModel);
                ancillaryServicesList.add(newModel);
            });
            pageModel.setContent(ancillaryServicesList);
        } else {
            pageModel.setContent(datas.getContent());
        }
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);
    }

    @ApiOperation("搜索策略详情列表")
    @UserLoginToken
    @RequestMapping(value = "/getASListByASId", method = {RequestMethod.GET})
    public ResponseResult<List> getASListByASId(@RequestParam("asId") String asId) {
        Specification<AncillarySStrategy> spec = new Specification<AncillarySStrategy>() {
            @Override
            public Predicate toPredicate(Root<AncillarySStrategy> root,
                                         CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("ancillaryServices").get("asId"), asId));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        List<AncillarySStrategy> datas = assRepository.findAll(spec);
        List<AncillaryRespModel> respModelList = new ArrayList<>();

        //根据楼宇节点分组统计
        Map<String, List<AncillarySStrategy>> listMap = datas.stream().collect(Collectors.groupingBy(d -> d.getNodeId()));
        double totalLoad = datas.stream().mapToDouble(AncillarySStrategy::getActualLoad).sum();//总实际负荷
        int num = listMap.size();//总遍历次数
        double rate = 100.00;//总占比
        for (String nodeId : listMap.keySet()) {
            List<AncillarySStrategy> strategyList = listMap.get(nodeId);
            double hour = TimeUtil.getDifferHour(strategyList.get(0).getAncillaryServices().getAssTime()
                    , strategyList.get(0).getAncillaryServices().getAseTime());
            AncillaryRespModel model = new AncillaryRespModel();

            model.setNodeName(listMap.get(nodeId).get(0).getNodeName());

            model.setDeviceRatedLoad(strategyList.stream().mapToDouble(AncillarySStrategy::getActualLoad).sum());//调节负荷
            model.setRegulatePower(model.getDeviceRatedLoad() * hour);//调节电量

            if (num > 1) {
                if (totalLoad > 0) {
                    BigDecimal bg = new BigDecimal(model.getDeviceRatedLoad() / totalLoad)
                            .setScale(2, RoundingMode.UP);
                    model.setPowerRate(bg.doubleValue());//调节电量占比
                    rate -= bg.doubleValue();
                } else {
                    model.setPowerRate(0.00);//调节电量占比
                }
            } else {
                model.setPowerRate(rate);//调节电量占比
            }
            num--;
            respModelList.add(model);

        }

        return ResponseResult.success(respModelList);
    }

}