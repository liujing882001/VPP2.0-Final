package com.example.vvpweb.ancillary;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.AncillarySStrategyRepository;
import com.example.vvpdomain.AncillaryServicesRepository;
import com.example.vvpdomain.entity.AncillarySStrategy;
import com.example.vvpdomain.entity.AncillaryServices;
import com.example.vvpweb.ancillary.model.AncillaryModel;
import com.example.vvpweb.ancillary.model.AncillarySStrategyReq;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author maoyating
 * @description 辅助服务-实时监测
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/ancillary_services/ancillary_monitor")
@CrossOrigin
@Api(value = "辅助服务-实时监测", tags = {"辅助服务-实时监测"})
public class AncillaryServicesMonitorController {

    private static Logger logger = LoggerFactory.getLogger(AncillaryServicesMonitorController.class);
    @Autowired
    private AncillaryServicesRepository asRepository;
    @Autowired
    private AncillarySStrategyRepository assRepository;

    @ApiOperation("查询实时监测列表")
    @UserLoginToken
    @RequestMapping(value = "/getASMonitorList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getASMonitorList(@RequestBody AncillaryModel model) {
        Specification<AncillaryServices> spec = new Specification<AncillaryServices>() {
            @Override
            public Predicate toPredicate(Root<AncillaryServices> root,
                                         CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("aStatus"), model.getStatus()));
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
        //封装到pageUtil
        if (datas.getContent() != null && datas.getContent().size() > 0) {
            List<AncillaryServicesRespModel> list = new ArrayList<>();
            datas.getContent().forEach(d -> {
                AncillaryServicesRespModel newModel = new AncillaryServicesRespModel();
                //辅助服务负荷
                double actualLoad = d.getStrategyList().stream().mapToDouble(AncillarySStrategy::getActualLoad).sum();

                d.setActualLoad(actualLoad);
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

    @ApiOperation("搜索策略详情列表")
    @UserLoginToken
    @RequestMapping(value = "/getASMonitorListByASId", method = {RequestMethod.GET})
    public ResponseResult<List> getASDeviceListByName(@RequestParam("asId") String asId) {
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
        if (datas != null && datas.size() > 0) {
            List<AncillarySStrategyReq> list = new ArrayList<>();
            datas.forEach(d -> {
                AncillarySStrategyReq newModel = new AncillarySStrategyReq();
                BeanUtils.copyProperties(d, newModel);
                list.add(newModel);
            });
            return ResponseResult.success(list);
        }

        return ResponseResult.success();
    }

}