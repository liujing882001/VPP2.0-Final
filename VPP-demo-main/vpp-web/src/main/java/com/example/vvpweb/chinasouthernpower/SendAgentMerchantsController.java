package com.example.vvpweb.chinasouthernpower;

import com.alibaba.excel.EasyExcel;
import com.example.vvpcommom.FieldCheckUtil;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpservice.chinasouthernpower.INoHouseholdsService;
import com.example.vvpservice.chinasouthernpower.model.Data;
import com.example.vvpservice.chinasouthernpower.model.RData;
import com.example.vvpservice.tunableload.model.RTLoadModel;
import com.example.vvpweb.chinasouthernpower.model.*;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**南方电网 深圳后海A栋冷源群控需求响应*/
@RestController
@RequestMapping("api/v2")
@CrossOrigin
@Api(value = "信诺-南网-负荷聚合商的需求响应", tags = {"信诺-南网-负荷聚合商的需求响应"})
public class SendAgentMerchantsController {

    private static Logger logger = LoggerFactory.getLogger(SendAgentMerchantsController.class);

    @Autowired
    private INoHouseholdsService iNoHouseholdsService;

    /**
     * 5.1 负荷聚合商代理用户上报接口
     * 由虚拟电厂平台主动调用负荷聚合商平台接口，拉取负荷聚合商代理商列表信息，
     * 一次性返回聚合商和用户的基本信息
     */
    @RequestMapping(value = "/sendAgentMerchants", method = {RequestMethod.POST})
    public SouthernPowerResult<List<AgentMerchantsResponse>> sendAgentMerchants(@RequestBody AgentMerchantsRequest request) {

        //CreditCode 负荷聚合商唯一标识（统一社会信用代码，共 18 位）
        if (FieldCheckUtil.checkStringNotEmpty(request.getCreditCode())
                && request.getCreditCode().length() == 18
                && FieldCheckUtil.checkStringNotEmpty(request.getEventType())
                && "agentMerchants".equals(request.getEventType())
                && FieldCheckUtil.checkStringNotEmpty(request.getSystemTime())) {

            try {

                InputStream inputStream = new ClassPathResource("/sindataexcel/agentMerchants.xlsx").getInputStream();
                AgentMerchantsExcelListener agentMerchantsExcelListener = new AgentMerchantsExcelListener();
                EasyExcel.read(inputStream, AgentMerchantsResponse.class, agentMerchantsExcelListener).sheet().doRead();
                List<AgentMerchantsResponse> agentMerchants = agentMerchantsExcelListener.getData();

                if (agentMerchants != null && agentMerchants.size() > 0) {

                    List<AgentMerchantsResponse> agentMerchantsResponses = agentMerchants.stream()
                            .filter(e -> e.getCreditCode().equals(request.getCreditCode()))
                            .collect(Collectors.toList());
                    if (agentMerchantsResponses == null || agentMerchantsResponses.size() == 0) {

                        return SouthernPowerResult.success(null);
                    }
                    return SouthernPowerResult.success(agentMerchants);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return SouthernPowerResult.success(null);
        }
        return SouthernPowerResult.error(RetCode.RET_5000);
    }


    /**
     * 5.2负荷聚合商可调节资源台账上报接口
     * 由虚拟电厂平台主动调用负荷聚合商平台接口，拉取可调资源台账信息；
     * 每月更新一次，更新后需要由供电局审批后才生效。
     */
    @RequestMapping(value = "/sendResources", method = {RequestMethod.POST})
    public SouthernPowerResult<ResourcesResponse> sendResources(@RequestBody ResourcesRequest request) {
        //代理用户唯一标识（统一社会信用代码，共 18 位）
        if (FieldCheckUtil.checkStringNotEmpty(request.getCreditCode())
                && request.getCreditCode().length() == 18
                && FieldCheckUtil.checkStringNotEmpty(request.getEventType())
                && "resourceInfo".equals(request.getEventType())
                && FieldCheckUtil.checkStringNotEmpty(request.getSystemTime())) {

            try {

                InputStream inputStream = new ClassPathResource("/sindataexcel/resourceInfo.xlsx").getInputStream();
                ResourcesInfoExcelListener resourcesInfoExcelListener = new ResourcesInfoExcelListener();
                EasyExcel.read(inputStream, ResourcesInfo.class, resourcesInfoExcelListener).sheet().doRead();
                List<ResourcesInfo> resourcesInfos = resourcesInfoExcelListener.getData();
                if (resourcesInfos != null && resourcesInfos.size() > 0) {

                    List<ResourcesInfo> data = resourcesInfos.stream()
                            .filter(e -> e.getCreditCode().equals(request.getCreditCode()))
                            .collect(Collectors.toList());

                    if (data != null && data.size() > 0) {
                        List<ResourcesInfo> limit = data.stream()
                                .skip((request.getPage() - 1) * request.getPageSize())
                                .limit(request.getPageSize())
                                .collect(Collectors.toList());
                        ResourcesResponse response = new ResourcesResponse();
                        response.setPage(request.getPage());
                        if (data.size() % request.getPageSize() == 0) {
                            response.setTotalPage(data.size() / request.getPageSize());
                        } else {
                            response.setTotalPage(data.size() / request.getPageSize() + 1);
                        }

                        response.setResources(limit);
                        return SouthernPowerResult.success(response);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return SouthernPowerResult.success(null);

        }
        return SouthernPowerResult.error(RetCode.RET_5000);
    }


    /**
     * 5.3负荷聚合商上报实时数据接口
     * 由虚拟电厂平台主动调用负荷聚合商平台接口，拉取实时负荷数据，频率为 1 分钟一次。
     */
    @RequestMapping(value = "/sendRealTimeData", method = {RequestMethod.POST})
    public SouthernPowerResult<RealTimeDataResponse> sendRealTimeData(@RequestBody RealTimeDataRequest request) {
        //负荷聚合商唯一标识（统一社会信用代码，共 18 位）
        if (FieldCheckUtil.checkStringNotEmpty(request.getCreditCode()) &&
                FieldCheckUtil.checkStringNotEmpty(request.getEventType()) &&
                "RTData".equals(request.getEventType())
                && FieldCheckUtil.checkStringNotEmpty(request.getSystemTime())) {

            RealTimeDataResponse response = new RealTimeDataResponse();

            try {
                InputStream inputStream = new ClassPathResource("/sindataexcel/resourceInfo.xlsx").getInputStream();
                ResourcesInfoExcelListener resourcesInfoExcelListener = new ResourcesInfoExcelListener();
                EasyExcel.read(inputStream, ResourcesInfo.class, resourcesInfoExcelListener).sheet().doRead();
                List<ResourcesInfo> resourcesInfos = resourcesInfoExcelListener.getData();

                if (resourcesInfos != null && resourcesInfos.size() > 0) {
                    List<ResourcesInfo> data = resourcesInfos.stream()
                            .filter(e -> e.getLoadAggregatorCreditCode().equals(request.getCreditCode()))
                            .collect(Collectors.toList());

                    if (data != null && data.size() > 0) {
                        List<ResourcesInfo> rs = data.stream()
                                .skip((request.getPage() - 1) * request.getPageSize())
                                .limit(request.getPageSize())
                                .collect(Collectors.toList());

                        List<RealData> limit = new ArrayList<>();

                        if (rs != null && rs.size() > 0) {
                            for (ResourcesInfo e : rs) {
                                RealData realData = new RealData();
                                realData.setResourceId(e.getResourceId());
                                realData.setResourceName(e.getResourceName());
                                realData.setResponseType(e.getResponseType());
                                realData.setMaxupCapacity("0");
                                realData.setMaxupRate("0");
                                realData.setMaxupLength("0");
                                realData.setMaxdownCapacity(e.getRatedPower() + "");
                                realData.setMaxdownRate(new BigDecimal(e.getRatedPower() + "").divide(new BigDecimal(2)) + "");
                                realData.setMaxdownLength("1");
                                realData.setResourceState(e.getResourceState());
                                realData.setStatusChangeTime(null);

                                realData.setResponseType(e.getResponseType());

                                boolean b = iNoHouseholdsService.noHouseholdsDeviceIsOnline(e.getResourceId());
                                realData.setStatus(b ? "ONLINE" : "OFFLINE");

                                Data da = iNoHouseholdsService.lastData(e.getResourceId());
                                if (da != null) {
                                    realData.setData(da.getRd());
                                    realData.setDateTime(TimeUtil.strDDToDate(TimeUtil.dateFormat(da.getTs()), "yyyy-MM-dd HH:mm"));
                                }

                                limit.add(realData);

                            }
                        }
                        response.setPage(request.getPage());
                        if (data.size() % request.getPageSize() == 0) {
                            response.setTotalPage(data.size() / request.getPageSize());
                        } else {
                            response.setTotalPage(data.size() / request.getPageSize() + 1);
                        }
                        response.setRealData(limit);
                        return SouthernPowerResult.success(response);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return SouthernPowerResult.success(null);

        }
        return SouthernPowerResult.error(RetCode.RET_5000);
    }

    /**
     * 5.8 负荷聚合商历史数据查询接口
     * 由虚拟电厂平台调用负荷聚合平台，获取某个的历史负荷数据信息（1 分钟间隔）；
     * 每次查询时间范围为 3 天以内；历史数据可以查询 1 个月内
     */
    @RequestMapping(value = "/getResourceHistoryData", method = {RequestMethod.POST})
    public SouthernPowerResult<List<HistoryData>> getResourceHistoryData(@RequestBody HistoryDataRequest request) {
        //负荷资源唯一标识户号
        if (FieldCheckUtil.checkStringNotEmpty(request.getResourceId()) &&
                FieldCheckUtil.checkStringNotEmpty(request.getEventType()) &&
                "historyData".equals(request.getEventType())
                && FieldCheckUtil.checkStringNotEmpty(request.getSystemTime())
                && FieldCheckUtil.checkStringNotEmpty(request.getStartTime())
                && FieldCheckUtil.checkStringNotEmpty(request.getEndTime())) {

            List<HistoryData> historyDataList = new ArrayList<>();
            try {
                Date start = TimeUtil.stringToDate(request.getStartTime());
                Date end = TimeUtil.stringToDate(request.getEndTime());
                List<Data> qd = iNoHouseholdsService.findHistoryData(request.getResourceId(), start, end);
                if (qd != null && qd.size() > 0) {
                    for (Data e : qd) {
                        HistoryData hd = new HistoryData();
                        hd.setResourceId(request.getResourceId());
                        hd.setDateTime(e.getTs());
                        hd.setData(e.getRd());

                        historyDataList.add(hd);
                    }
                }

                List<HistoryData> historyDataItems = historyDataList.stream()
                        .sorted(Comparator.comparing(HistoryData::getDateTime))
                        .collect(Collectors.toList());
                if (historyDataItems != null && historyDataItems.size() > 0) {
                    return SouthernPowerResult.success(historyDataItems);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return SouthernPowerResult.success(null);

        }
        return SouthernPowerResult.error(RetCode.RET_5000);
    }

}