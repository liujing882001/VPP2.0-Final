package com.example.vvpweb.loadmanagement;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import com.example.vvpweb.BaseExcelController;
import com.example.vvpweb.loadmanagement.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zph
 * @description 资源管理-AI预测
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/load_management/ai_prediction")
@CrossOrigin
@Api(value = "资源管理-AI预测", tags = {"资源管理-AI预测"})
public class AIPredictionController extends BaseExcelController {

    private static Logger LOGGER = LoggerFactory.getLogger(AIPredictionController.class);

    @Resource
    private IExcelOutPutService iExcelOutPutService;


    //region 负荷预测
    @Resource
    private AiLoadRepository aiLoadRepository;
    @Resource
    private AiLoadStatisticalPrecisionRepository aiLoadStatisticalPrecisionRepository;
    @Resource
    private AiPvRepository aiPvRepository;
    @Resource
    private SysParamRepository sysParamRepository;
    @Resource
    private AiPvStatisticalPrecisionRepository aiPvStatisticalPrecisionRepository;
    @Resource
    private IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;
    //endregion


    //region 光伏预测
    @ApiOperation("负荷预测-Chart")
    @UserLoginToken
    @RequestMapping(value = "loadPredictionChart", method = {RequestMethod.POST})
    public ResponseResult<List<AiLoadModelResponse>> loadPredictionChart(@RequestBody AiModel model) {
        try {

            if (model == null) {
                return ResponseResult.error("参数为空，请重新输入!");
            }
            if (model.getStartTs().after(model.getEndTs())) {

                return ResponseResult.error("开始时间不能大于结束时间!");
            }

            SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
            sdf_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat sdf_ymd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf_ymd_hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            Date sDate = sdf_ymd_hms.parse(sdf_ymd.format(model.getStartTs()) + " 00:00:00");
            Date eDate = sdf_ymd_hms.parse(sdf_ymd.format(model.getEndTs()) + " 23:59:59");

            Specification<AiLoadForecasting> spec = (root, criteriaQuery, cb) -> {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
//                predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
                predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.asc(root.get("countDataTime"))); //按照createTime升序排列
                return criteriaQuery.getRestriction();
            };
            List<AiLoadModelResponse> list = new ArrayList<>();

            List<AiLoadForecasting> loadForecastingList = aiLoadRepository.findAll(spec);
            if (loadForecastingList != null && loadForecastingList.size() > 0) {
                //查询，从哪里取基线负荷的值 update by maoyating
                SysParam sysParam=sysParamRepository.findSysParamBySysParamKey(SysParamEnum.BaseLineForecastCfg.getId());
                JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
                String getMethod = "商汤";
                if (obj != null) {
                    if (obj.get("baseLineGetMethod") != null) {
                        getMethod=obj.get("baseLineGetMethod").toString();
                    }
                }

                Date nowDate = new Date();
                for (AiLoadForecasting e : loadForecastingList) {
                    if (e != null) {
                        AiLoadModelResponse response = new AiLoadModelResponse();
                        //update by maoyating
                        if(getMethod.equals("商汤")){
                            response.setBaselineLoadValue(e.getBaselineLoadValue());
                        }else{
                            response.setBaselineLoadValue(e.getBaselineLoadValueOther());
                        }
                        //临时增加逻辑nowDate，当前时间在创建时间之前才会显示实际负荷
                        if (nowDate.after(e.getCreatedTime())) {
                            response.setRealValue(e.getRealValue());
                        }
                        response.setCurrentForecastValue(e.getPredictValue());
                        response.setUltraShortTermForecastValue(e.getUltraShortTermForecastValue());
                        response.setTimeStamp(e.getCountDataTime());
                        list.add(response);
                    }
                }
            }
            return ResponseResult.success(list);
        } catch (Exception ex) {
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }

    @ApiOperation("分页-负荷预测列表")
    @UserLoginToken
    @RequestMapping(value = "loadPredictionListPage", method = {RequestMethod.POST})
    public ResponseResult<PageModel> loadPredictionChartList(@RequestBody AiPageModel model) {
        try {
            if (model == null) {
                return ResponseResult.error("参数为空，请重新输入!");
            }
            if (model.getStartTs().after(model.getEndTs())) {

                return ResponseResult.error("开始时间不能大于结束时间!");
            }
            SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
            sdf_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat sdf_ymd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf_ymd_hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Date sDate = sdf_ymd_hms.parse(sdf_ymd.format(model.getStartTs()) + " 00:00:00");
            Date eDate = sdf_ymd_hms.parse(sdf_ymd.format(model.getEndTs()) + " 23:59:59");

            Specification<AiLoadForecasting> spec = new Specification<AiLoadForecasting>() {
                @Override
                public Predicate toPredicate(Root<AiLoadForecasting> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                    predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
//                    predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
                    predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    criteriaQuery.orderBy(cb.asc(root.get("countDataTime"))); //按照createTime升序排列
                    return criteriaQuery.getRestriction();
                }
            };
            List<AiLoadModelResponse> list = new ArrayList<>();

            Page<AiLoadForecasting> datas = aiLoadRepository.findAll(spec, PageRequest.of((model.getNumber()) - 1, model.getPageSize()));
            if (datas.getContent() != null && datas.getContent().size() > 0) {
                //查询，从哪里取基线负荷的值 update by maoyating
                SysParam sysParam=sysParamRepository.findSysParamBySysParamKey(SysParamEnum.BaseLineForecastCfg.getId());
                JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
                String getMethod = "商汤";
                if (obj != null) {
                    if (obj.get("baseLineGetMethod") != null) {
                        getMethod=obj.get("baseLineGetMethod").toString();
                    }
                }
                Date nowDate = new Date();
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                for(AiLoadForecasting e:datas){
                    AiLoadModelResponse response = new AiLoadModelResponse();
                    if(getMethod.equals("商汤")){
                        response.setBaselineLoadValue(e.getBaselineLoadValue());
                    }else{
                        response.setBaselineLoadValue(e.getBaselineLoadValueOther());
                    }
                    //临时增加逻辑nowDate，当前时间在创建时间之前才会显示实际负荷
                    if (nowDate.after(e.getCreatedTime())) {
                        response.setRealValue(e.getRealValue());
                    }
                    response.setCurrentForecastValue(e.getPredictValue());
                    response.setUltraShortTermForecastValue(e.getUltraShortTermForecastValue());
                    response.setTimeStamp(e.getCountDataTime());
                    list.add(response);
                }
            }

            PageModel pageModel = new PageModel();
            pageModel.setContent(list);
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);

            return ResponseResult.success(pageModel);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }

    @ApiOperation("负荷预测列表-信息数据导出")
    @UserLoginToken
    @RequestMapping(value = "loadPredictionListExcel", method = {RequestMethod.POST})
    public void loadPredictionListExcel(HttpServletResponse response, @RequestBody AiModel model) {

        try {
            ResponseResult<List<AiLoadModelResponse>> result = loadPredictionChart(model);

            exec(response, result.getData(), AiLoadModelResponse.class, iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    @ApiOperation("分页-负荷预测精度统计")
    @UserLoginToken
    @RequestMapping(value = "loadPredictionPrecisionStatisticsListPage", method = {RequestMethod.POST})
    public ResponseResult<PageModel> loadPredictionPrecisionStatisticsListPage(@RequestBody AiPageModel model) {
        try {
            if (model == null) {
                return ResponseResult.error("参数为空，请重新输入!");
            }
            if (model.getStartTs().after(model.getEndTs())) {

                return ResponseResult.error("开始时间不能大于结束时间!");
            }
            SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
            sdf_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));


            Date sDate = sdf_ymd.parse(sdf_ymd.format(model.getStartTs()));
            Date eDate = sdf_ymd.parse(sdf_ymd.format(model.getEndTs()));

            Specification<AiLoadForecastingStatisticalPrecision> spec = new Specification<AiLoadForecastingStatisticalPrecision>() {
                @Override
                public Predicate toPredicate(Root<AiLoadForecastingStatisticalPrecision> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
                    predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
                    predicates.add(cb.between(root.get("countDate"), sDate, eDate));
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    criteriaQuery.orderBy(cb.asc(root.get("countDate"))); //按照createTime升序排列
                    return criteriaQuery.getRestriction();
                }
            };
            List<AiStatisticalPrecisionModelResponse> list = new ArrayList<>();

            Page<AiLoadForecastingStatisticalPrecision> datas = aiLoadStatisticalPrecisionRepository.findAll(spec, PageRequest.of((model.getNumber()) - 1, model.getPageSize()));
            if (datas.getContent() != null && datas.getContent().size() > 0) {
                datas.getContent().stream().forEach(e -> {
                    AiStatisticalPrecisionModelResponse response = new AiStatisticalPrecisionModelResponse();
                    response.setCurrentForecast(e.getCurrentForecast());
                    response.setUltraShortTermForecast(e.getUltraShortTermForecast());
                    response.setTimeStamp(sdf_ymd.format(e.getCountDate()));
                    list.add(response);
                });
            }

            PageModel pageModel = new PageModel();
            pageModel.setContent(list);
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);

            return ResponseResult.success(pageModel);

        } catch (Exception ex) {
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }

    @ApiOperation("分页-负荷预测精度统计导出")
    @UserLoginToken
    @RequestMapping(value = "loadPredictionPrecisionStatisticsListPageExcel", method = {RequestMethod.POST})
    public void loadPredictionPrecisionStatisticsListPageExcel(HttpServletResponse response, @RequestBody AiPageModel model) {
        try {

            SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
            sdf_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            Date sDate = sdf_ymd.parse(sdf_ymd.format(model.getStartTs()));
            Date eDate = sdf_ymd.parse(sdf_ymd.format(model.getEndTs()));

            Specification<AiLoadForecastingStatisticalPrecision> spec = new Specification<AiLoadForecastingStatisticalPrecision>() {
                @Override
                public Predicate toPredicate(Root<AiLoadForecastingStatisticalPrecision> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
                    predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
                    predicates.add(cb.between(root.get("countDate"), sDate, eDate));
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    criteriaQuery.orderBy(cb.asc(root.get("countDate"))); //按照createTime升序排列
                    return criteriaQuery.getRestriction();
                }
            };
            List<AiStatisticalPrecisionModelResponse> list = new ArrayList<>();

            List<AiLoadForecastingStatisticalPrecision> datas = aiLoadStatisticalPrecisionRepository.findAll(spec);
            if (datas != null && datas.size() > 0) {
                datas.stream().forEach(e -> {
                    AiStatisticalPrecisionModelResponse rep = new AiStatisticalPrecisionModelResponse();
                    rep.setCurrentForecast(e.getCurrentForecast());
                    rep.setUltraShortTermForecast(e.getUltraShortTermForecast());
                    rep.setTimeStamp(sdf_ymd.format(e.getCountDate()));
                    list.add(rep);
                });
            }

            exec(response, list, AiStatisticalPrecisionModelResponse.class, iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("光伏预测-Chart")
    @UserLoginToken
    @RequestMapping(value = "pvPredictionChart", method = {RequestMethod.POST})
    public ResponseResult<List<AiPvModelResponse>> pvPredictionChart(@RequestBody AiModel model) {
        try {
            if (model == null) {
                return ResponseResult.error("参数为空，请重新输入!");
            }
            if (model.getStartTs().after(model.getEndTs())) {

                return ResponseResult.error("开始时间不能大于结束时间!");
            }
            SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
            sdf_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat sdf_ymd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf_ymd_hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Date sDate = sdf_ymd_hms.parse(sdf_ymd.format(model.getStartTs()) + " 00:00:00");
            Date eDate = sdf_ymd_hms.parse(sdf_ymd.format(model.getEndTs()) + " 23:59:59");

//            Specification<AiPvForecasting> spec = (root, criteriaQuery, cb) -> {
//                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
//                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
//                predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
//                predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
//                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
//                criteriaQuery.orderBy(cb.asc(root.get("countDataTime"))); //按照createTime升序排列
//                return criteriaQuery.getRestriction();
//            };
            Specification<AiLoadForecasting> spec1 = new Specification<AiLoadForecasting>() {
                @Override
                public Predicate toPredicate(Root<AiLoadForecasting> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                    predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
//                    predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
                    predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    criteriaQuery.orderBy(cb.asc(root.get("countDataTime"))); //按照createTime升序排列
                    return criteriaQuery.getRestriction();
                }
            };
            List<AiPvModelResponse> list = new ArrayList<>();
            List<AiLoadForecasting> aiLoadForecastings = aiLoadRepository.findAll(spec1);
            if (aiLoadForecastings != null && aiLoadForecastings.size() > 0) {
                for (AiLoadForecasting e : aiLoadForecastings) {
                    if (e != null) {
                        AiPvModelResponse response = new AiPvModelResponse();
                        response.setCurrentForecastValue(e.getPredictValue());
                        response.setRealValue(e.getRealValue());
                        response.setTimeStamp(e.getCountDataTime());
                        list.add(response);
                    }
                }
            }
            Specification<IotTsKvMeteringDevice96> spec2 = (root, criteriaQuery, cb) -> {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
//                predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
                predicates.add(cb.equal(root.get("pointDesc"), "load"));
                predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.asc(root.get("countDataTime"))); //按照createTime升序排列
                return criteriaQuery.getRestriction();
            };
            Map<Date, Double> pvReal1Map = iotTsKvMeteringDevice96Repository.findAll(spec2).stream().collect(Collectors.toMap(IotTsKvMeteringDevice96::getCountDataTime, IotTsKvMeteringDevice96::getHTotalUse,(existing, replacement) -> replacement));
            list.forEach(response -> {
                Double value = pvReal1Map.get(response.getTimeStamp());
                if (value != null) {
                    value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
                }
                response.setRealValue(String.valueOf(value));
            });
//            List<AiPvForecasting> pvForecastingList = aiPvRepository.findAll(spec);
//            if (pvForecastingList != null && pvForecastingList.size() > 0) {
//                for (AiPvForecasting e : pvForecastingList) {
//                    if (e != null) {
//                        AiPvModelResponse response = new AiPvModelResponse();
//                        response.setCurrentForecastValue(e.getCurrentForecastValue());
//                        response.setMediumTermForecastValue(e.getMediumTermForecastValue());
//                        response.setRealValue(e.getRealValue());
//                        response.setUltraShortTermForecastValue(e.getUltraShortTermForecastValue());
//                        response.setTimeStamp(e.getCountDataTime());
//                        list.add(response);
//                    }
//                }
//            }
            return ResponseResult.success(list);
        } catch (Exception ex) {
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }

    //region 光伏发电预测精度统计

    @ApiOperation("分页-光伏预测列表")
    @UserLoginToken
    @RequestMapping(value = "pvPredictionListPage", method = {RequestMethod.POST})
    public ResponseResult<PageModel> pvPredictionListPage(@RequestBody AiPageModel model) throws ParseException {
//        try {
            if (model == null) {
                return ResponseResult.error("参数为空，请重新输入!");
            }
            if (model.getStartTs().after(model.getEndTs())) {

                return ResponseResult.error("开始时间不能大于结束时间!");
            }
            SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
            sdf_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat sdf_ymd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf_ymd_hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Date sDate = sdf_ymd_hms.parse(sdf_ymd.format(model.getStartTs()) + " 00:00:00");
            Date eDate = sdf_ymd_hms.parse(sdf_ymd.format(model.getEndTs()) + " 23:59:59");

//            Specification<AiPvForecasting> spec = (root, criteriaQuery, cb) -> {
//                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
//                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
//                predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
//                predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
//                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
//                criteriaQuery.orderBy(cb.asc(root.get("countDataTime"))); //按照createTime升序排列
//                return criteriaQuery.getRestriction();
//            };
//            List<AiPvModelResponse> list = new ArrayList<>();

//            Page<AiPvForecasting> datas = aiPvRepository.findAll(spec, PageRequest.of((model.getNumber()) - 1, model.getPageSize()));
//            if (datas.getContent() != null && datas.getContent().size() > 0) {
//                datas.getContent().stream().forEach(e -> {
//                    AiPvModelResponse response = new AiPvModelResponse();
//                    response.setCurrentForecastValue(e.getCurrentForecastValue());
//                    response.setMediumTermForecastValue(e.getMediumTermForecastValue());
//                    response.setRealValue(e.getRealValue());
//                    response.setUltraShortTermForecastValue(e.getUltraShortTermForecastValue());
//                    response.setTimeStamp(e.getCountDataTime());
//                    list.add(response);
//                });
//            }
            Specification<AiLoadForecasting> spec1 = (root, criteriaQuery, cb) -> {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
                predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
                predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.asc(root.get("countDataTime"))); //按照createTime升序排列
                return criteriaQuery.getRestriction();
            };
            Page<AiLoadForecasting> datas = aiLoadRepository.findAll(spec1, PageRequest.of((model.getNumber()) - 1, model.getPageSize()));
            List<AiPvModelResponse> list = new ArrayList<>();
            if (datas.getContent() != null && datas.getContent().size() > 0) {
                datas.getContent().stream().forEach(e -> {
                    AiPvModelResponse response = new AiPvModelResponse();
                    response.setCurrentForecastValue(e.getPredictValue());
                    response.setRealValue(e.getRealValue());
                    response.setTimeStamp(e.getCountDataTime());
                    list.add(response);
                });
            }
            Specification<IotTsKvMeteringDevice96> spec2 = (root, criteriaQuery, cb) -> {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
                predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
                predicates.add(cb.equal(root.get("pointDesc"), "load"));
                predicates.add(cb.between(root.get("countDataTime"), sDate, eDate));
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.asc(root.get("countDataTime"))); //按照createTime升序排列
                return criteriaQuery.getRestriction();
            };
            Map<Date, Double> pvReal1Map = iotTsKvMeteringDevice96Repository.findAll(spec2).stream().collect(Collectors.toMap(IotTsKvMeteringDevice96::getCountDataTime, IotTsKvMeteringDevice96::getHTotalUse,(existing, replacement) -> replacement));
            list.forEach(response -> {
                        Double value = pvReal1Map.get(response.getTimeStamp());
                        if (value != null) {
                            value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
                        }
                        response.setRealValue(String.valueOf(value));
                    });

            PageModel pageModel = new PageModel();
            pageModel.setContent(list);
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);

            return ResponseResult.success(pageModel);
//
//        } catch (Exception ex) {
//            return ResponseResult.error("数据参数有误，请检查！");
//        }
    }

    @ApiOperation("光伏预测列表-信息数据导出")
    @UserLoginToken
    @RequestMapping(value = "pvPredictionListExcel", method = {RequestMethod.POST})
    public void pvPredictionListExcel(HttpServletResponse response, @RequestBody AiModel model) {

        try {
            ResponseResult<List<AiPvModelResponse>> result = pvPredictionChart(model);

            exec(response, result.getData(), AiPvModelResponse.class, iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("分页-光伏发电预测精度统计")
    @UserLoginToken
    @RequestMapping(value = "pvPredictionPrecisionStatisticsListPage", method = {RequestMethod.POST})
    public ResponseResult<PageModel> pvPredictionPrecisionStatisticsListPage(@RequestBody AiPageModel model) {
        try {
            if (model == null) {
                return ResponseResult.error("参数为空，请重新输入!");
            }
            if (model.getStartTs().after(model.getEndTs())) {

                return ResponseResult.error("开始时间不能大于结束时间!");
            }
            SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
            sdf_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            Date sDate = sdf_ymd.parse(sdf_ymd.format(model.getStartTs()));
            Date eDate = sdf_ymd.parse(sdf_ymd.format(model.getEndTs()));

            Specification<AiPvForecastingStatisticalPrecision> spec = (root, criteriaQuery, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
                predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
                predicates.add(cb.between(root.get("countDate"), sDate, eDate));
                criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(cb.asc(root.get("countDate"))); //按照createTime升序排列
                return criteriaQuery.getRestriction();
            };
            List<AiStatisticalPrecisionModelResponse> list = new ArrayList<>();

            Page<AiPvForecastingStatisticalPrecision> datas = aiPvStatisticalPrecisionRepository.findAll(spec, PageRequest.of((model.getNumber()) - 1, model.getPageSize()));
            if (datas.getContent() != null && datas.getContent().size() > 0) {
                datas.getContent().stream().forEach(e -> {
                    AiStatisticalPrecisionModelResponse response = new AiStatisticalPrecisionModelResponse();
                    response.setCurrentForecast(e.getCurrentForecast());
                    response.setMediumTermForecast(e.getMediumTermForecast());
                    response.setUltraShortTermForecast(e.getUltraShortTermForecast());
                    response.setTimeStamp(sdf_ymd.format(e.getCountDate()));
                    list.add(response);
                });
            }
//            AiStatisticalPrecisionModelResponse response = avgPrecisionStatistics(model.getNodeId(), model.getSystemId(), sDate, eDate);
//            if (response != null) {
//
//                list.add(response);
//            }
            PageModel pageModel = new PageModel();
            pageModel.setContent(list);
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);

            return ResponseResult.success(pageModel);

        } catch (Exception ex) {
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }


    @ApiOperation("分页-光伏发电预测精度统计导出")
    @UserLoginToken
    @RequestMapping(value = "pvPredictionPrecisionStatisticsListPageExcel", method = {RequestMethod.POST})
    public void pvPredictionPrecisionStatisticsListPageExcel(HttpServletResponse response, @RequestBody AiPageModel model) {
        try {

            SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
            sdf_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            Date sDate = sdf_ymd.parse(sdf_ymd.format(model.getStartTs()));
            Date eDate = sdf_ymd.parse(sdf_ymd.format(model.getEndTs()));

            Specification<AiPvForecastingStatisticalPrecision> spec = new Specification<AiPvForecastingStatisticalPrecision>() {
                @Override
                public Predicate toPredicate(Root<AiPvForecastingStatisticalPrecision> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
                    predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
                    predicates.add(cb.between(root.get("countDate"), sDate, eDate));
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    criteriaQuery.orderBy(cb.asc(root.get("countDate"))); //按照createTime升序排列
                    return criteriaQuery.getRestriction();
                }
            };
            List<AiStatisticalPrecisionModelResponse> list = new ArrayList<>();

            List<AiPvForecastingStatisticalPrecision> datas = aiPvStatisticalPrecisionRepository.findAll(spec);
            if (datas != null && datas.size() > 0) {
                datas.stream().forEach(e -> {
                    AiStatisticalPrecisionModelResponse rep = new AiStatisticalPrecisionModelResponse();
                    rep.setCurrentForecast(e.getCurrentForecast());
                    rep.setMediumTermForecast(e.getMediumTermForecast());
                    rep.setUltraShortTermForecast(e.getUltraShortTermForecast());
                    rep.setTimeStamp(sdf_ymd.format(e.getCountDate()));
                    list.add(rep);
                });
            }
//            AiStatisticalPrecisionModelResponse rep = avgPrecisionStatistics(model.getNodeId(), model.getSystemId(), sDate, eDate);
//            if (rep != null) {
//
//                list.add(rep);
//            }
            exec(response, list, AiStatisticalPrecisionModelResponse.class, iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion


}
