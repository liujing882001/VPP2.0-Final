package com.example.vvpweb.loadmanagement;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.BiPvResourcesRepository;
import com.example.vvpdomain.IotTsKvMeteringDevice96Repository;
import com.example.vvpdomain.entity.BiPvResources;
import com.example.vvpdomain.entity.IotTsKvMeteringDevice96;
import com.example.vvpservice.exceloutput.model.BiPvResourcesExport;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.BaseExcelController;
import com.example.vvpweb.loadmanagement.model.BiPvModel;
import com.example.vvpweb.loadmanagement.model.PVResourcesResponse;
import com.example.vvpweb.loadmanagement.model.PvModel;
import com.example.vvpweb.loadmanagement.model.PvModelResponse;
import com.example.vvpweb.systemmanagement.nodemodel.NodeController;
import com.example.vvpweb.systemmanagement.nodemodel.model.NodeNameResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * @author zph
 * @description 负荷管理-发电资源
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/load_management/power_generation")
@CrossOrigin
@Api(value = "负荷管理-光伏资源", tags = {"负荷管理-光伏资源"})
public class PVResourcesController extends BaseExcelController {

    @Resource
    BiPvResourcesRepository biPvResourcesRepository;
    @Resource
    NodeController nodeController;
    @Resource
    IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;
    @Autowired
    private IUserService userService;
    @Resource
    private IExcelOutPutService iExcelOutPutService;

    @ApiOperation("光伏列表信息")
    @UserLoginToken
    @RequestMapping(value = "pVList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> pVList(@RequestBody BiPvModel model) {

        List<String> idList = userService.getAllowPvNodeIds();
        if (model != null && idList != null && idList.size() > 0) {

            Specification<BiPvResources> spec = new Specification<BiPvResources>() {
                @Override
                public Predicate toPredicate(Root<BiPvResources> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况

                    switch (model.getType()) {
                        case 1:
                            predicates.add(cb.equal(root.get("isEnabled"), true));
                            predicates.add(cb.equal(root.get("online"), true));
                            break;
                        case 2:
                            predicates.add(cb.equal(root.get("isEnabled"), true));
                            predicates.add(cb.equal(root.get("online"), false));
                            break;
                        case 3:
                            predicates.add(cb.equal(root.get("isEnabled"), false));
                            break;
                    }

                    CriteriaBuilder.In<Object> in = cb.in(root.get("nodeId"));
                    for (String nodeId : idList) {
                        in.value(nodeId);
                        predicates.add(cb.and(in));
                    }
                    if (StringUtils.isNotEmpty(model.getStationName())) {
                        predicates.add(cb.like(root.get("nodeName"), "%" + model.getStationName() + "%"));
                    }
                    return cb.and(predicates.toArray(new Predicate[predicates.size()]));//以and的形式拼接查询条件，也可以用.or()
                }
            };
            //传入分页条件和排序条件拿到分页对象
            //当前页为第几页 默认 1开始
            int page = model.getNumber();
            int size = model.getPageSize();

            Pageable pageable = PageRequest.of(page - 1, size);

            Page<BiPvResources> datas = biPvResourcesRepository.findAll(spec, pageable);

            PageModel pageModel = new PageModel();
            //封装到pageUtil
            pageModel.setContent(datas.getContent());
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);

            return ResponseResult.success(pageModel);
        }
        return ResponseResult.success(null);
    }

    @ApiOperation("光伏基本信息(负荷管理，灵活性支援管理)")
    @UserLoginToken
    @RequestMapping(value = "pvCount", method = {RequestMethod.POST})
    public ResponseResult<PVResourcesResponse> pvCount() {
        try {
            SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");


            //光伏装机容量
            double pvCapacity = 0;
            //累计电量
            double energy = 0;
            //当日发电量
            double now_energy = 0;
            //瞬时功率
            double load = 0;
            int photovoltaic = 0;
            String ts = fmt_ymd.format(new Date());


            List<String> nodeIds = new ArrayList<>();
            ResponseResult<List<NodeNameResponse>> result = nodeController.pvNodeNameList();
            if (result != null
                    && result.getCode() == 200
                    && result.getData() != null
                    && result.getData().size() > 0) {

                List<NodeNameResponse> nodeList = result.getData();
                nodeList.stream().forEach(node -> {
                    nodeIds.add(node.getId());
                });
                photovoltaic = nodeList.size();
            }


            if (nodeIds != null && nodeIds.size() > 0) {

                List<BiPvResources> resourcesItems = biPvResourcesRepository.findAllByNodeIdIn(nodeIds);
                if (resourcesItems != null && resourcesItems.size() > 0) {

                    List<BiPvResources> resourcesList = resourcesItems
                            .stream()
                            .filter(p -> p.getIsEnabled() == true && p.getOnline() == true)
                            .collect(Collectors.toList());

                    if (resourcesList != null && resourcesList.size() > 0) {
                        pvCapacity = resourcesList.stream().mapToDouble(c -> c.getCapacity()).sum();
                        energy = resourcesList.stream().mapToDouble(c -> c.getEnergy()).sum();
                        now_energy = resourcesList.stream().filter(c -> fmt_ymd.format(c.getTs()).equals(ts)).mapToDouble(c -> c.getNowEnergy()).sum();

                        load = resourcesList.stream().filter(c -> fmt_ymd.format(c.getTs()).equals(ts) && c.getOnline() == true)
                                .mapToDouble(c -> c.getLoad()).sum();
                    }
                }
            }

            PVResourcesResponse pvResourcesResponse = new PVResourcesResponse();
            pvResourcesResponse.setLoad(load <= 0 ? 0 : Double.parseDouble(String.format("%.2f", load)));
            pvResourcesResponse.setCapacity(pvCapacity <= 0 ? 0 : Double.parseDouble(String.format("%.2f", pvCapacity)));
            pvResourcesResponse.setNowEnergy(now_energy <= 0 ? 0 : Double.parseDouble(String.format("%.2f", now_energy)));
            pvResourcesResponse.setEnergy(energy <= 0 ? 0 : Double.parseDouble(String.format("%.2f", energy)));
            pvResourcesResponse.setPhotovoltaic(photovoltaic);

            return ResponseResult.success(pvResourcesResponse);
        } catch (Exception ex) {
        }
        return ResponseResult.error("获取数据失败.");
    }

    @ApiOperation("光伏Chart")
    @UserLoginToken
    @RequestMapping(value = "pvChartList", method = {RequestMethod.POST})
    public ResponseResult<List<PvModelResponse>> pvChartList(@RequestBody PvModel model) {
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
            Date endDt = sdf_ymd.parse(sdf_ymd.format(model.getEndTs()));

            Date dt = sdf_ymd.parse(sdf_ymd.format(new Date()));
            Date eDate = endDt.after(dt) ? dt : endDt;

            Specification<IotTsKvMeteringDevice96> spec = new Specification<IotTsKvMeteringDevice96>() {
                @Override
                public Predicate toPredicate(Root<IotTsKvMeteringDevice96> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                    List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                    predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
                    predicates.add(cb.equal(root.get("systemId"), "nengyuanzongbiao"));
                    predicates.add(cb.equal(root.get("pointDesc"), "load"));
                    predicates.add(cb.equal(root.get("configKey"), "metering_device"));
                    predicates.add(cb.between(root.get("countDate"), sDate, eDate));
                    criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                    criteriaQuery.orderBy(cb.asc(root.get("countDataTime"))); //按照createTime升序排列
                    return criteriaQuery.getRestriction();
                }
            };
            List<PvModelResponse> list = new ArrayList<>();

            List<IotTsKvMeteringDevice96> device96s = iotTsKvMeteringDevice96Repository.findAll(spec);
            if (device96s != null && device96s.size() > 0) {
                device96s.stream().forEach(e -> {
                    PvModelResponse response = new PvModelResponse();
                    response.setActivePower(e.getHTotalUse());
                    response.setTimeStamp(e.getCountDataTime());
                    list.add(response);
                });
            }
            return ResponseResult.success(list);
        } catch (Exception ex) {
            return ResponseResult.error("数据参数有误，请检查！");
        }
    }

    @ApiOperation("光伏列表信息数据导出")
    @UserLoginToken
    @RequestMapping(value = "pVListExcel", method = {RequestMethod.POST})
    public void pVListExcel(HttpServletResponse response, @RequestBody BiPvModel model) {

        try {
            model.setNumber(1);
            model.setPageSize(Integer.MAX_VALUE);

            ResponseResult<PageModel> pageModelResponseResult = pVList(model);
            List<BiPvResources> content = (List<BiPvResources>) (pageModelResponseResult.getData().getContent());
            List<BiPvResourcesExport> exports = new ArrayList<>();
            if (content != null && !content.isEmpty()) {
                content.forEach(e -> {
                    BiPvResourcesExport export = new BiPvResourcesExport();
                    export.setCapacity(e.getCapacity());
                    export.setEnergy(e.getEnergy());
                    export.setLoad(e.getLoad());
                    export.setNodeName(e.getNodeName());
                    export.setNowEnergy(e.getNowEnergy());
                    export.setOnline(e.getOnline());
                    export.setTs(e.getTs());

                    exports.add(export);
                });
            }
            exec(response, exports, BiPvResourcesExport.class, iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @ApiOperation("光伏Chart数据导出")
    @UserLoginToken
    @RequestMapping(value = "pvChartListExcel", method = {RequestMethod.POST})
    public void pvChartListExcel(HttpServletResponse response, @RequestBody PvModel model) {

        try {
            ResponseResult<List<PvModelResponse>> pageModelResponseResult = pvChartList(model);

            exec(response, pageModelResponseResult.getData(), PvModelResponse.class, iExcelOutPutService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
