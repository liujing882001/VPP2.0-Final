package com.example.vvpweb.loadmanagement;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.BiStorageEnergyResourcesRepository;
import com.example.vvpdomain.entity.BiStorageEnergyResources;
import com.example.vvpservice.exceloutput.model.BiStorageEnergyResourcesExport;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.BaseExcelController;
import com.example.vvpweb.loadmanagement.model.BiStorageEnergyModel;
import com.example.vvpweb.loadmanagement.model.StorageEnergyResourcesResponse;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author zph
 * @description 资源管理-储能资源
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/load_management/storage_energy")
@CrossOrigin
@Api(value = "资源管理-储能资源", tags = {"资源管理-储能资源"})
public class EnergyStorageResourcesController extends BaseExcelController {

    @Resource
    BiStorageEnergyResourcesRepository biStorageEnergyResourcesRepository;
    @Resource
    NodeController nodeController;
    @Autowired
    private IUserService userService;
    @Resource
    private IExcelOutPutService iExcelOutPutService;

    @ApiOperation("储能列表信息")
    @UserLoginToken
    @RequestMapping(value = "storageEnergyList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> storageEnergyList(@RequestBody BiStorageEnergyModel model) {

        List<String> idList = userService.getAllowStorageEnergyNodeIds();

        if (model != null && idList != null && idList.size() > 0) {
            Specification<BiStorageEnergyResources> spec = new Specification<BiStorageEnergyResources>() {
                @Override
                public Predicate toPredicate(Root<BiStorageEnergyResources> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
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

            Page<BiStorageEnergyResources> datas = biStorageEnergyResourcesRepository.findAll(spec, pageable);

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

    @ApiOperation("储能基本信息(负荷管理，灵活性支援管理)")
    @UserLoginToken
    @RequestMapping(value = "storageEnergyCount", method = {RequestMethod.POST})
    public ResponseResult<StorageEnergyResourcesResponse> storageEnergyCount() {
        try {

            //电站容量
            Double capacity = (double) 0;
            //电站功率
            Double load = (double) 0;
            // soc
            double soc = (double) 0;
            //soh
            double soh = (double) 0;
            //当前可充容量kwh
            Double inCapacity = (double) 0;
            //当前可放容量kwh
            Double outCapacity = (double) 0;
            //最大可充功率kw
            Double maxInLoad = (double) 0;
            // 最大可放功率kw
            Double maxOutLoad = (double) 0;
            //储能数量
            int storedEnergyNumber = 0;

            SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
            String now_ymd = fmt_ymd.format(new Date());

            List<String> nodeIds = new ArrayList<>();
            ResponseResult<List<NodeNameResponse>> result = nodeController.storageEnergyNodeNameList();
            if (result != null
                    && result.getCode() == 200
                    && result.getData() != null
                    && result.getData().size() > 0) {

                List<NodeNameResponse> nodeList = result.getData();
                nodeList.stream().forEach(p -> {
                    nodeIds.add(p.getId());
                });
                storedEnergyNumber = nodeList.size();
            }


            if (nodeIds != null && nodeIds.size() > 0) {

                List<BiStorageEnergyResources> resourcesItems = biStorageEnergyResourcesRepository.findAllByNodeIdIn(nodeIds);
                if (resourcesItems != null && resourcesItems.size() > 0) {

                    List<BiStorageEnergyResources> resourcesList = resourcesItems
                            .stream()
                            .filter(p -> p.getIsEnabled() == true && p.getOnline() == true)
                            .collect(Collectors.toList());
                    if (resourcesList != null && resourcesList.size() > 0) {
                        capacity = resourcesList.stream().mapToDouble(c -> c.getCapacity()).sum();
                        load = resourcesList.stream().mapToDouble(c -> c.getLoad()).sum();


                        Double soc_权 = resourcesList.stream()
                                .reduce(0.0, (x, y) -> x + (y.getCapacity() * y.getSoc()), Double::sum);
                        Double soh_权 = resourcesList.stream()
                                .reduce(0.0, (x, y) -> x + (y.getCapacity() * y.getSoh()), Double::sum);

                        soc = capacity != 0 ? soc_权 / capacity : capacity;
                        soh = capacity != 0 ? soh_权 / capacity : capacity;

                        inCapacity = capacity * (1 - soc);
                        outCapacity = capacity * soc;


//                        maxInLoad = resourcesList.stream().filter(c -> fmt_ymd.format(c.getTs()).equals(now_ymd) && c.getOnline())
//                                .mapToDouble(c -> c.getMaxInLoad()).sum();
//                        maxOutLoad = resourcesList.stream().filter(c -> fmt_ymd.format(c.getTs()).equals(now_ymd) && c.getOnline())
//                                .mapToDouble(c -> c.getMaxOutLoad()).sum();

                        //20240430 zph 改为sum所有节点的最大充放电能力
                        maxInLoad = resourcesList.stream().mapToDouble(c -> c.getMaxInLoad()).sum();
                        maxOutLoad = resourcesList.stream().mapToDouble(c -> c.getMaxOutLoad()).sum();
                    }
                }
            }
            StorageEnergyResourcesResponse response = new StorageEnergyResourcesResponse();
            response.setLoad(load <= 0 ? 0 : Double.parseDouble(String.format("%.2f", load)));
            response.setCapacity(capacity <= 0 ? 0 : Double.parseDouble(String.format("%.2f", capacity)));
            response.setSoh(soh <= 0 ? 0 : Double.parseDouble(String.format("%.4f", soh)));
            response.setSoc(soc <= 0 ? 0 : Double.parseDouble(String.format("%.4f", soc)));
            response.setInCapacity(inCapacity <= 0 ? 0 : Double.parseDouble(String.format("%.2f", inCapacity)));
            response.setOutCapacity(outCapacity <= 0 ? 0 : Double.parseDouble(String.format("%.2f", outCapacity)));
            response.setMaxInLoad(maxInLoad <= 0 ? 0 : Double.parseDouble(String.format("%.2f", maxInLoad)));
            response.setMaxOutLoad(maxOutLoad <= 0 ? 0 : Double.parseDouble(String.format("%.2f", maxOutLoad)));
            response.setStoredenergy(storedEnergyNumber);

            return ResponseResult.success(response);

        } catch (Exception ex) {
        }
        return ResponseResult.error("获取数据失败.");
    }

    @ApiOperation("储能资源列表信息导出")
    @UserLoginToken
    @RequestMapping(value = "storageEnergyListExcel", method = {RequestMethod.POST})
    public void storageEnergyListExcel(HttpServletResponse response, @RequestBody BiStorageEnergyModel model) {
        try {

            model.setNumber(1);
            model.setPageSize(Integer.MAX_VALUE);

            ResponseResult<PageModel> pageModelResponseResult = storageEnergyList(model);
            List<BiStorageEnergyResources> content = (List<BiStorageEnergyResources>) (pageModelResponseResult.getData().getContent());
            List<BiStorageEnergyResourcesExport> exports = new ArrayList<>();
            if (content != null && !content.isEmpty()) {
                content.forEach(e -> {
                    BiStorageEnergyResourcesExport export = new BiStorageEnergyResourcesExport();
                    export.setCapacity(e.getCapacity());
                    export.setLoad(e.getLoad());
                    export.setNodeName(e.getNodeName());
                    export.setOnline(e.getOnline());
                    export.setInEnergy(e.getInCapacity());
                    export.setOutEnergy(e.getOutCapacity());
                    export.setMaxInLoad(e.getMaxInLoad());
                    export.setMaxOutLoad(e.getMaxOutLoad());
                    export.setSoc(e.getSoc());
                    export.setSoh(e.getSoh());
                    export.setTs(e.getTs());

                    exports.add(export);
                });
            }
            exec(response, exports, BiStorageEnergyResourcesExport.class, iExcelOutPutService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
