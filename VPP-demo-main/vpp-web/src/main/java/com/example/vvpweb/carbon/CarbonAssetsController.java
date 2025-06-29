package com.example.vvpweb.carbon;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.CaTradeRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.entity.CaTrade;
import com.example.vvpweb.carbon.model.CaTradeModel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author maoyating
 * @description 碳资产管理-碳资产
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/carbon/carbon_assets")
@CrossOrigin
@Api(value = "碳资产管理-碳中和", tags = {"碳资产管理-碳中和"})
public class CarbonAssetsController {

    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private CaTradeRepository caTradeRepository;

    @ApiOperation("查询各交易列表")
    @UserLoginToken
    @RequestMapping(value = "/getTradeList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getTradeList(@RequestBody CaTradeModel model) {
        Specification<CaTrade> spec = new Specification<CaTrade>() {
            @Override
            public Predicate toPredicate(Root<CaTrade> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("tStatus"), 1));//查询状态非 删除的数据
                predicates.add(cb.equal(root.get("tradeType"), model.getTradeType()));//交易类型
                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//节点id
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.desc(root.get("tradeDate"))); //根据日期降序排
                return criteriaQuery.getRestriction();
            }
        };
        //当前页为第几页 默认 1开始
        int page = model.getNumber();
        int size = model.getPageSize();

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<CaTrade> datas = caTradeRepository.findAll(spec, pageable);

        PageModel pageModel = new PageModel();
        //封装到pageUtil

        pageModel.setContent(datas.getContent());

        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);
    }

    @ApiOperation("录入交易信息")
    @UserLoginToken
    @RequestMapping(value = "/addTrade", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult addTrade(@RequestBody @Valid CaTrade trade) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            if (trade.getTradeType() == null || (trade.getTradeType() != 1 &&
                    trade.getTradeType() != 2 && trade.getTradeType() != 3)) {
                return ResponseResult.error("交易类型输入为空或有误！");
            }
            if (trade.getTradeType() == 2) {
                if (trade.getGreenType() == null || (trade.getGreenType() != 1 && trade.getGreenType() != 2)) {
                    return ResponseResult.error("绿电类型输入为空或有误！");
                }
            }
            if (trade.getTradeType() == 3) {
                if (trade.getCertificateType() == null ||
                        (trade.getCertificateType() != 1 && trade.getCertificateType() != 2)) {
                    return ResponseResult.error("绿证类型输入为空或有误！");
                }
            }
            //判断是否有重复数据添加
            Specification<CaTrade> spec = new Specification<CaTrade>() {
                @Override
                public Predicate toPredicate(Root<CaTrade> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.equal(root.get("tStatus"), 1));//查询状态非 删除的数据
                    predicates.add(cb.equal(root.get("tradeType"), trade.getTradeType()));//交易类型
                    predicates.add(cb.equal(root.get("nodeId"), trade.getNodeId()));//节点id
                    predicates.add(cb.equal(root.get("tradeDate"), trade.getTradeDate()));//交易日期
                    predicates.add(cb.equal(root.get("company"), trade.getCompany()));//公司名称
                    if (trade.getTradeType() == 2) {
                        predicates.add(cb.equal(root.get("greenType"), trade.getGreenType()));//绿电类型（1-光伏 2-风能）
                    }
                    if (trade.getTradeType() == 3) {
                        predicates.add(cb.equal(root.get("certificateType"), trade.getCertificateType()));//绿证类型（1-有补贴 2-无补贴）
                    }
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    return criteriaQuery.getRestriction();
                }
            };
            List<CaTrade> datas = caTradeRepository.findAll(spec);
            if (datas != null && datas.size() > 0) {
                return ResponseResult.error(trade.getCompany() + "交易日期已存在记录！");
            }

            trade.setTradeId(UUID.randomUUID().toString());
            trade.setTStatus(1);

            caTradeRepository.save(trade);
        } catch (Exception e) {
            return ResponseResult.error("录入交易信息异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    @ApiOperation("编辑交易信息")
    @UserLoginToken
    @RequestMapping(value = "/editTrade", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editTrade(@RequestBody @Valid CaTrade trade) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            Optional<CaTrade> task = caTradeRepository.findById(trade.getTradeId());
            if (!task.isPresent()) {
                return ResponseResult.error("该交易不存在");
            }
            Specification<CaTrade> spec = new Specification<CaTrade>() {
                @Override
                public Predicate toPredicate(Root<CaTrade> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.equal(root.get("tStatus"), 1));//查询状态非 删除的数据
                    predicates.add(cb.equal(root.get("tradeType"), trade.getTradeType()));//交易类型
                    predicates.add(cb.equal(root.get("nodeId"), trade.getNodeId()));//节点id
                    predicates.add(cb.equal(root.get("tradeDate"), trade.getTradeDate()));//交易日期
                    predicates.add(cb.equal(root.get("company"), trade.getCompany()));//公司名称
                    if (trade.getTradeType() == 2) {
                        predicates.add(cb.equal(root.get("greenType"), trade.getGreenType()));//绿电类型（1-光伏 2-风能）
                    }
                    if (trade.getTradeType() == 3) {
                        predicates.add(cb.equal(root.get("certificateType"), trade.getCertificateType()));//绿证类型（1-有补贴 2-无补贴）
                    }
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    return criteriaQuery.getRestriction();
                }
            };
            List<CaTrade> datas = caTradeRepository.findAll(spec);
            if (datas != null && datas.size() > 0) {
                for (CaTrade caTrade : datas) {
                    if (!caTrade.getTradeId().equals(trade.getTradeId())) {
                        return ResponseResult.error(trade.getCompany() + "交易日期已存在记录！");
                    }
                }
            }
            caTradeRepository.save(trade);
        } catch (Exception e) {
            return ResponseResult.error("编辑交易信息异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }

    @ApiOperation("删除交易信息")
    @UserLoginToken
    @RequestMapping(value = "/delTrade", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult delTask(@RequestParam("tradeId") String tradeId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");

        try {
            //判断id是否为空
            if (StringUtils.isBlank(tradeId)) {
                return ResponseResult.error("id不能为空");
            }
            caTradeRepository.updateBatchStatus(tradeId.split(","));
        } catch (Exception e) {
            return ResponseResult.error("删除交易异常 +" + e.getMessage());
        }

        return ResponseResult.success();
    }
}