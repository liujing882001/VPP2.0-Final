package com.example.vvpweb.carbon;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.CaEmissionFactorRepository;
import com.example.vvpdomain.entity.CaEmissionFactor;
import com.example.vvpweb.carbon.model.CaEmissionModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author maoyating
 * @description 碳资产-碳排放因子
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/carbon/emission")
@CrossOrigin
@Api(value = "碳资产管理-碳排放因子", tags = {"碳资产管理-碳排放因子"})
public class CaEmissionFactorController {

    @Autowired
    private CaEmissionFactorRepository caEmissionFactorRepository;


    @ApiOperation("获取碳排放因子列表")
    @UserLoginToken
    @RequestMapping(value = "/getEmissionFactorList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getEmissionFactorList(@RequestBody CaEmissionModel model) {
        Specification<CaEmissionFactor> spec = new Specification<CaEmissionFactor>() {
            @Override
            public Predicate toPredicate(Root<CaEmissionFactor> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("sStatus"), 1));
                predicates.add(cb.equal(root.get("province"), model.getProvince()));//年份
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.desc(root.get("updateTime"))); //根据日期降序排
                return criteriaQuery.getRestriction();
            }
        };
        //当前页为第几页 默认 1开始
        int page = model.getNumber();
        int size = model.getPageSize();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CaEmissionFactor> datas = caEmissionFactorRepository.findAll(spec, pageable);
        PageModel pageModel = new PageModel();
        pageModel.setContent(datas.getContent());
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);
        return ResponseResult.success(pageModel);
    }

    @ApiOperation("新增碳排放因子")
    @UserLoginToken
    @RequestMapping(value = "/addEmissionFactor", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult addEmissionFactor(@RequestBody @Valid CaEmissionFactor caEmissionFactor) {
        try {
            if (caEmissionFactor.getScopeType() != null) {
                if((caEmissionFactor.getScopeType() != 1
                        && caEmissionFactor.getScopeType() != 2 && caEmissionFactor.getScopeType() != 3)){
                    return ResponseResult.error("范围输入有误！");
                }
                if (caEmissionFactor.getScopeType() == 1 && caEmissionFactor.getDischargeType() == null) {
                    return ResponseResult.error("燃烧排放类型不能为空！");
                }
                if (caEmissionFactor.getDischargeType() != null) {
                    if (caEmissionFactor.getDischargeType() != 1 &&
                            caEmissionFactor.getDischargeType() != 2 && caEmissionFactor.getDischargeType() != 3) {
                        return ResponseResult.error("燃烧排放类型输入有误！");

                    }
                }
            }
            if (StringUtils.isBlank(caEmissionFactor.getEmissionFactorName()))
                return ResponseResult.error("碳排放因子名称不能为空！");
            List<CaEmissionFactor> ca = caEmissionFactorRepository.findByNameAndProvince(caEmissionFactor.getEmissionFactorName(), caEmissionFactor.getProvince());
            if (ca.size() > 0) return ResponseResult.error("碳排放因子已存在！");
            Date date = new Date();
            caEmissionFactor.setEmissionFactorId(UUID.randomUUID().toString());
            caEmissionFactor.setCreatedTime(date);
            caEmissionFactor.setSStatus(1);
            caEmissionFactorRepository.save(caEmissionFactor);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("录入信息异常 +" + e.getMessage());
        }
        return ResponseResult.success();
    }

    @ApiOperation("修改碳排放因子")
    @UserLoginToken
    @RequestMapping(value = "/editEmissionFactor", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editEmissionFactor(@RequestBody @Valid CaEmissionFactor caEmissionFactor) {
        try {
            CaEmissionFactor oldCEF = caEmissionFactorRepository.findById(caEmissionFactor.getEmissionFactorId()).orElse(null);
            if (oldCEF == null) {
                return ResponseResult.error("碳排放因子不存在,请刷新页面。");
            }
            //碳排放因子编号 对应 范围一的燃烧排放实体/范围二购买内容/范围三类型
            oldCEF.setEmissionFactorNum(caEmissionFactor.getEmissionFactorNum());
            oldCEF.setEmissionFactorName(caEmissionFactor.getEmissionFactorName());
            oldCEF.setCo2(caEmissionFactor.getCo2());
            oldCEF.setUnit(caEmissionFactor.getUnit());
            oldCEF.setScopeType(caEmissionFactor.getScopeType());
            oldCEF.setDischargeType(caEmissionFactor.getDischargeType());
            oldCEF.setInitialValue(caEmissionFactor.getInitialValue());
            oldCEF.setProvince(caEmissionFactor.getProvince());
            oldCEF.setDescription(caEmissionFactor.getDescription());
            oldCEF.setUpdateTime(new Date());//修改时间
            caEmissionFactorRepository.save(oldCEF);
            return ResponseResult.success(oldCEF);
        } catch (Exception e) {
            return ResponseResult.error("修改信息异常 +" + e.getMessage());
        }
    }

    @ApiOperation("删除碳排放因子")
    @UserLoginToken
    @RequestMapping(value = "/removeEmissionFactor", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult removeEmissionFactor(@RequestParam("emissionFactorId") String emissionFactorId) {
        try {
            CaEmissionFactor caEmissionFactor = caEmissionFactorRepository.findById(emissionFactorId).orElse(null);
            if (caEmissionFactor == null) {
                return ResponseResult.error("碳排放因子不存在,请刷新页面。");
            }
            caEmissionFactor.setSStatus(0);
            caEmissionFactorRepository.save(caEmissionFactor);
            return ResponseResult.success("删除成功！");
        } catch (Exception e) {
            return ResponseResult.error("删除信息异常 +" + e.getMessage());
        }
    }
}