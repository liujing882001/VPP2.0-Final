package com.example.vvpweb.loadmanagement;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.AnalysisEnergyMonthRepository;
import com.example.vvpdomain.AnalysisLoadDayRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.SysDictTypeRepository;
import com.example.vvpdomain.entity.AnalysisEnergyMonth;
import com.example.vvpdomain.entity.AnalysisLoadDay;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import com.example.vvpservice.tree.model.StructTreeResponse;
import com.example.vvpservice.tree.service.ITreeLabelService;
import com.example.vvpweb.BaseExcelController;
import com.example.vvpweb.loadmanagement.model.EnergyResourceModel;
import com.example.vvpweb.loadmanagement.model.ListModel;
import com.example.vvpweb.loadmanagement.model.LoadResourceModel;
import com.example.vvpweb.loadmanagement.model.SubModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zph
 * @description 负荷管理-负荷资源
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/load_management/load_resources")
@CrossOrigin
@Api(value = "资源管理-负荷资源", tags = {"资源管理-负荷资源"})
public class LoadResourcesController extends BaseExcelController {

    @Resource
    NodeRepository nodeRepository;
    @Resource
    SysDictTypeRepository sysDictTypeRepository;
    @Resource
    AnalysisEnergyMonthRepository analysisEnergyMonthRepository;
    @Resource
    AnalysisLoadDayRepository analysisLoadDayRepository;
    @Resource
    private ITreeLabelService iTreeLabelService;
    @Resource
    private IExcelOutPutService iExcelOutPutService;

    /**
     * 可调负荷 负荷 - 能耗 节点树
     *
     * @return
     */
    @UserLoginToken
    @RequestMapping(value = "areaLoadShortView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> areaLoadShortView() {

        return ResponseResult.success(iTreeLabelService.areaLoadNoSystemId_NY_ShortView());
    }

    @UserLoginToken
    @RequestMapping(value = "runAreaLoadShortView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> runAreaLoadShortView() {

        return ResponseResult.success(iTreeLabelService.runAreaLoadNoSystemId_NY_ShortView());
    }

    @UserLoginToken
    @RequestMapping(value = "typeLoadShortView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> typeLoadShortView() {

        return ResponseResult.success(iTreeLabelService.typeLoadNoSystemId_NY_ShortView());
    }

    @ApiOperation(value = "负荷资源-负荷")
    @UserLoginToken
    @RequestMapping(value = "analysisLoadList", method = {RequestMethod.POST})
    public ResponseResult<LoadResourceModel> analysisLoadList(@RequestBody ListModel model) {

        if (model == null) {
            return ResponseResult.error("参数为空，请重新输入!");
        }
        if (model.getStartTime().after(model.getEndTime())) {

            return ResponseResult.error("开始时间不能大于结束时间!");
        }

        SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
        fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));


        SimpleDateFormat fmt_ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        fmt_ymdhm.setTimeZone(TimeZone.getTimeZone("GMT+8"));


        SimpleDateFormat fmt_ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt_ymdhms.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        if (model != null && model.getSubModelList() != null && model.getSubModelList().size() > 0) {
            try {
                Date startTime = fmt_ymdhms.parse(fmt_ymd.format(model.getStartTime()) + " 00:00:00");
                Date endDt = fmt_ymdhms.parse(fmt_ymd.format(model.getEndTime()) + " 23:59:59");

                Date dt = fmt_ymdhms.parse(fmt_ymdhms.format(new Date()));
                Date endTime = endDt.after(dt) ? dt : endDt;

                List<AnalysisLoadDay> analysisLoadDays = new ArrayList<>();

                for (SubModel subModel : model.getSubModelList()) {
                    if (StringUtils.isNotEmpty(subModel.getType())) {
                        List<AnalysisLoadDay> list = null;
                        switch (subModel.getType()) {
                            case "NODE":
                                list = analysisLoadDayRepository.findAllByNodeIdAndSystemIdAndTsBetween(subModel.getNodeId(),
                                        "nengyuanzongbiao", startTime, endTime);
                                break;
                            case "SYSTEM":
                                list = analysisLoadDayRepository.findAllByNodeIdAndSystemIdAndTsBetween(subModel.getNodeId()
                                        , subModel.getSystemId(), startTime, endTime);
                                break;
                        }
                        if (list != null && list.size() > 0) {
                            analysisLoadDays.addAll(list);
                        }
                    }
                }

                List<Map<String, Object>> listMap = new ArrayList<>();

                if (analysisLoadDays != null && analysisLoadDays.size() > 0) {
                    analysisLoadDays.forEach(analysisLoadDay -> {
                        if (analysisLoadDay != null) {

                            String ts = fmt_ymdhm.format(analysisLoadDay.getTs());
                            String nodeName = analysisLoadDay.getNodeName();
                            String systemName = analysisLoadDay.getSystemName();

                            Map<String, Object> rep = new HashMap<String, Object>();
                            rep.put("ts", ts);
                            rep.put("name", nodeName + "_" + systemName);
                            rep.put("value", StringUtils.isNotEmpty(analysisLoadDay.getLoadValue()) ? Double.parseDouble(analysisLoadDay.getLoadValue()) : (double) 0);
                            listMap.add(rep);
                        }
                    });
                }


                //listMap是查询出来数据集合

                //lmap--临时map，用来存放判断的key
                Map<String, Object> lmap = new HashMap<String, Object>();
                //valueList--最终返回数据的集合
                List<Map<String, Object>> valueList = new ArrayList<>();
                //valueMap--存放在valueList中的map
                Map<String, Object> valueMap = new HashMap<String, Object>();
                //循环遍历拿到查询的数据
                for (Map<String, Object> map : listMap) {

                    String ts = map.get("ts").toString();
                    String name = map.get("name").toString();
                    String value = map.get("value").toString();
                    //判断条件，来实现行转列
                    if (!lmap.containsKey(ts)) {
                        valueMap = new HashMap<String, Object>();
                        valueMap.put("时间", ts);
                        valueMap.put(name, value);
                        valueMap.put("合计", value);
                        valueList.add(valueMap);
                        //需要的条件用作key，valueMap做value，带入else语句情况下
                        lmap.put(ts, valueMap);
                    } else {
                        valueMap = (Map<String, Object>) lmap.get(ts);
                        valueMap.put(name, value);
                        valueMap.put("合计", Double.parseDouble(valueMap.get("合计").toString()) + Double.parseDouble(value));
                    }
                }

                valueList.sort(Comparator.comparing((Map<String, Object> h) -> (h.get("时间").toString())));

                AnalysisLoadDay maxAnalysisLoadDay = analysisLoadDays.stream()
                        .max(
                                Comparator.comparing(
                                        analysisLoadDay -> StringUtils.isNotEmpty(analysisLoadDay.getLoadValue()) ? Double.parseDouble(analysisLoadDay.getLoadValue()) : (double) 0)
                        ).get();

                LoadResourceModel loadResourceModel = new LoadResourceModel();
                loadResourceModel.setTotal(maxAnalysisLoadDay == null ? "0" : maxAnalysisLoadDay.getLoadValue());
                loadResourceModel.getData().addAll(valueList);

                return ResponseResult.success(loadResourceModel);

            } catch (Exception ex) {
            }
        }

        return ResponseResult.error("获取数据失败!");
    }

    @ApiOperation(value = "负荷资源-电量")
    @UserLoginToken
    @RequestMapping(value = "analysisEnergyList", method = {RequestMethod.POST})
    public ResponseResult<EnergyResourceModel> analysisEnergyList(@RequestBody ListModel model) {

        if (model == null) {
            return ResponseResult.error("参数为空，请重新输入!");
        }
        if (model.getStartTime().after(model.getEndTime())) {

            return ResponseResult.error("开始时间不能大于结束时间!");
        }

        SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
        fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        if (model != null && model.getSubModelList() != null && model.getSubModelList().size() > 0) {
            try {

                String startTime = fmt_ymd.format(model.getStartTime());
                Date endDt = fmt_ymd.parse(fmt_ymd.format(model.getEndTime()));

                Date dt = fmt_ymd.parse(fmt_ymd.format(new Date()));
                String endTime = fmt_ymd.format(endDt.after(dt) ? dt : endDt);

                List<AnalysisEnergyMonth> analysisEnergyMonths = new ArrayList<>();

                for (SubModel subModel : model.getSubModelList()) {

                    if (StringUtils.isNotEmpty(subModel.getType())) {
                        List<AnalysisEnergyMonth> list = null;
                        switch (subModel.getType()) {
                            case "NODE":
                                list = analysisEnergyMonthRepository.findAllByNodeIdAndSystemIdAndTsBetween(subModel.getNodeId(),
                                        "nengyuanzongbiao", startTime, endTime);
                                break;
                            case "SYSTEM":
                                list = analysisEnergyMonthRepository.findAllByNodeIdAndSystemIdAndTsBetween(subModel.getNodeId(),
                                        subModel.getSystemId(), startTime, endTime);
                                break;
                        }
                        if (list != null && list.size() > 0) {
                            analysisEnergyMonths.addAll(list);
                        }
                    }
                }
                List<Map<String, Object>> listMap = new ArrayList<>();
                if (analysisEnergyMonths != null && analysisEnergyMonths.size() > 0) {

                    Map<String, List<AnalysisEnergyMonth>> loadMap = analysisEnergyMonths.stream()
                            .collect(Collectors.groupingBy(a -> a.getNodeId()
                                    + a.getSystemId()
                                    + fmt_ymd.format(a.getTs())));

                    loadMap.forEach((system_id_ymd_ts, mapLoadList) -> {
                        if (mapLoadList != null && mapLoadList.size() > 0) {

                            String node_id = mapLoadList.get(0).getNodeId();
                            String system_id = mapLoadList.get(0).getSystemId();
                            String ts = fmt_ymd.format(mapLoadList.get(0).getTs());
                            String system_name = mapLoadList.get(0).getSystemName();
                            String node_name = mapLoadList.get(0).getNodeName();

                            Double system_value = mapLoadList.stream().mapToDouble(p -> Double.parseDouble(StringUtils.isNotEmpty(p.getEnergyValue()) ? p.getEnergyValue() : "0")).sum();

                            Map<String, Object> rep = new HashMap<String, Object>();
                            rep.put("ts", ts);
                            rep.put("name", node_name + "_" + system_name);
                            rep.put("value", system_value);
                            listMap.add(rep);
                        }
                    });
                }


                //listMap是查询出来数据集合

                //lmap--临时map，用来存放判断的key
                Map<String, Object> lmap = new HashMap<String, Object>();
                //valueList--最终返回数据的集合
                List<Map<String, Object>> valueList = new ArrayList<>();
                //valueMap--存放在valueList中的map
                Map<String, Object> valueMap = new HashMap<String, Object>();
                //循环遍历拿到查询的数据
                for (Map<String, Object> map : listMap) {

                    String ts = map.get("ts").toString();
                    String name = map.get("name").toString();
                    String value = map.get("value").toString();
                    //判断条件，来实现行转列
                    if (!lmap.containsKey(ts)) {
                        valueMap = new HashMap<String, Object>();
                        valueMap.put("时间", ts);
                        valueMap.put(name, value);
                        valueMap.put("合计", value);
                        valueList.add(valueMap);
                        //需要的条件用作key，valueMap做value，带入else语句情况下
                        lmap.put(ts, valueMap);
                    } else {
                        valueMap = (Map<String, Object>) lmap.get(ts);
                        valueMap.put(name, value);
                        valueMap.put("合计", Double.parseDouble(valueMap.get("合计").toString()) + Double.parseDouble(value));
                    }
                }
                valueList.sort(Comparator.comparing((Map<String, Object> h) -> (h.get("时间").toString())));


                double sumEnergy = analysisEnergyMonths.stream()
                        .mapToDouble(c -> StringUtils.isNotEmpty(c.getEnergyValue()) ? Double.parseDouble(c.getEnergyValue()) : (double) 0).sum();


                EnergyResourceModel energyResourceModel = new EnergyResourceModel();
                energyResourceModel.setTotal(String.valueOf(sumEnergy));
                energyResourceModel.getData().addAll(valueList);

                return ResponseResult.success(energyResourceModel);

            } catch (Exception ex) {
            }
        }

        return ResponseResult.error("获取数据失败!");
    }

    @ApiOperation(value = "负荷资源-电量数据导出")
    @UserLoginToken
    @RequestMapping(value = "analysisEnergyListExcel", method = {RequestMethod.POST})
    public void analysisEnergyListExcel(HttpServletResponse response, @RequestBody ListModel model) {
        try {
            exec(response, analysisEnergyList(model).getData().getData(), iExcelOutPutService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @ApiOperation(value = "负荷资源-负荷 数据导出")
    @UserLoginToken
    @RequestMapping(value = "analysisLoadListExcel", method = {RequestMethod.POST})
    public void analysisLoadListExcel(HttpServletResponse response, @RequestBody ListModel model) {
        try {
            exec(response, analysisLoadList(model).getData().getData(), iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
