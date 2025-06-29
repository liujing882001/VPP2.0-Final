//package com.example.vvpweb.demand;
//
//import com.alibaba.fastjson.JSON;
//import com.example.vvpcommom.HttpUtil;
//import com.example.vvpcommom.ResponseResult;
//import com.example.vvpcommom.UserLoginToken;
//import com.example.vvpdomain.AiLoadRepository;
//import com.example.vvpdomain.IotTsKvMeteringDevice96Repository;
//import com.example.vvpdomain.IotTsKvRepository;
//import com.example.vvpdomain.LibraryRidRepository;
//import com.example.vvpdomain.entity.AiLoadForecasting;
//import com.example.vvpdomain.entity.IotTsKv;
//import com.example.vvpdomain.entity.IotTsKvMeteringDevice96;
//import com.example.vvpdomain.entity.LibraryRid;
//import com.example.vvpscheduling.util.monitoringPlatform.PlatformUtils;
//import com.example.vvpscheduling.util.monitoringPlatform.ReportModel;
//import com.example.vvpweb.demand.model.*;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import java.security.cert.X509Certificate;
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//@Slf4j
//@RestController
//@RequestMapping("/push")
//@CrossOrigin
//@Api(value = "推送", tags = {"推送"})
//public class MonitoringPushController {
//    private static final Logger logger = LoggerFactory.getLogger(MonitoringPushController.class);
//    public static final String DN_ID = "10010103000078";
//    @Resource
//    private LibraryRidRepository libraryRidRepository;
//    @Resource
//    private IotTsKvRepository iotTsKvRepository;
//    @Resource
//    private IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;
//    @Resource
//    private AiLoadRepository aiLoadRepository;
////    public static final String parameterValue = "http://120.92.130.58:9001";
//    public static final String parameterValue = "https://203.110.167.251:9001/";
//    private String registrationID = "b2e9b0d4-a8c7-45ce-9fe6-e151ccfe18b7";
//
//    /**
//     * 获取token
//     * @param
//     * @return
//     */
//    @ApiOperation("获取token")
//    @UserLoginToken
//    @RequestMapping(value = "/TokenRequest", method = {RequestMethod.POST})
//    public ResponseResult<TokenResponse> TokenRequest() {
//        try {
//            UUID uuid = UUID.randomUUID();
//            TokenRequest request = new TokenRequest();
//            request.setRoot("TokenRequest");
//            request.setVersion(1);
//            request.setRequestID(uuid.toString());
//            request.setDnID(DN_ID);
//
//            logger.info("TokenRequest==========================>" + JSON.toJSONString(request));
//            String result = okHttpTokenPost(parameterValue + "TokenRequest", JSON.toJSONString(request));
//            logger.info("TokenResponse==========================>" + result);
//            ObjectMapper om = new ObjectMapper();
//            TokenResponse tokenResponse = om.readValue(result, TokenResponse.class);
//            return ResponseResult.success(tokenResponse);
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseResult.error("获取token失败");
//        }
//    }
//
//    @ApiOperation("创建注册请求")
//    @UserLoginToken
//    @RequestMapping(value = "/CreateRegistrationRequest", method = {RequestMethod.POST})
//    public ResponseResult<CreateRegistrationResponse> CreateRegistrationRequest() {
//        try {
//            //获取token
//            String token = TokenRequest().getData().getToken();
//
//            UUID uuid = UUID.randomUUID();
//            CreateRegistrationRequest request = new CreateRegistrationRequest();
//            request.setRoot("CreateRegistrationRequest");
//            request.setVersion(1);
//            request.setRequestID(uuid.toString());
//            request.setDnID(DN_ID);
//            request.setDnName("徐汇图书馆");
//            request.setRegistrationID(registrationID);
//            request.setReportOnly(false);
//            request.setPullMode(true);
//            request.setSignature(false);
//            request.setTransport(TransportType.REST);
//            request.setTransportAddress("http://47.122.43.145:49090");
//
//            logger.info("CreateRegistrationRequest====================>" + JSON.toJSONString(request));
//            String result = okHttpPostToken(parameterValue + "CreateRegistrationRequest", JSON.toJSONString(request), token);
//            logger.info("CreateRegistrationResponse====================>" + result);
//            ObjectMapper om = new ObjectMapper();
//            CreateRegistrationResponse createRegistrationResponse = om.readValue(result, CreateRegistrationResponse.class);
//            return ResponseResult.success(createRegistrationResponse);
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseResult.error("失败");
//        }
//    }
//
//    @ApiOperation("取消注册请求")
//    @UserLoginToken
//    @RequestMapping(value = "/CancelRegistrationRequest", method = {RequestMethod.POST})
//    public ResponseResult<CancelRegistrationResponse> CancelRegistrationRequest(@org.springframework.web.bind.annotation.RequestBody CancelRegistrationRequest request) {
//        try {
//            logger.info("CancelRegistrationRequest====================>" + JSON.toJSONString(request));
//            String result = HttpUtil.okHttpPost(parameterValue + "CancelRegistrationRequest", JSON.toJSONString(request));
//            logger.info("CancelRegistrationResponse====================>" + result);
//            ObjectMapper om = new ObjectMapper();
//            CancelRegistrationResponse cancelRegistrationResponse = om.readValue(result, CancelRegistrationResponse.class);
//            return ResponseResult.success(cancelRegistrationResponse);
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseResult.error("失败");
//        }
//    }
//
//    @ApiOperation("查询注册请求")
//    @UserLoginToken
//    @RequestMapping(value = "/QueryRegistrationRequest", method = {RequestMethod.POST})
//    public ResponseResult<CreateRegistrationResponse> QueryRegistrationRequest() {
//        try {
//            //获取token
//            String token = TokenRequest().getData().getToken();
//            UUID uuid = UUID.randomUUID();
//            QueryRegistrationRequest request = new QueryRegistrationRequest();
//            request.setRoot("QueryRegistrationRequest");
//            request.setVersion(1);
//            request.setRequestID(uuid.toString());
//            request.setDnID(DN_ID);
//
//            logger.info("QueryRegistrationRequest====================>" + JSON.toJSONString(request));
//            String result = okHttpPostToken(parameterValue + "QueryRegistrationRequest", JSON.toJSONString(request), token);
//            logger.info("QueryRegistrationResponse====================>" + result);
//            ObjectMapper om = new ObjectMapper();
//            CreateRegistrationResponse createRegistrationResponse = om.readValue(result, CreateRegistrationResponse.class);
//            return ResponseResult.success(createRegistrationResponse);
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseResult.error("失败");
//        }
//    }
//
//    @ApiOperation("查询事件请求")
//    @UserLoginToken
//    @RequestMapping(value = "/QueryEventRequest", method = {RequestMethod.POST})
//    public ResponseResult<QueryEventResponse> QueryEventRequest() {
//        try {
//            //获取token
//            String token = TokenRequest().getData().getToken();
//            QueryEventRequest request = new QueryEventRequest();
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            Date date = new Date();
//
//            UUID uuid = UUID.randomUUID();
//            request.setRoot("QueryEventRequest");
//            request.setVersion(1);
//            request.setRequestID(uuid.toString());
//            request.setDnID(DN_ID);
//            request.setReplyLimit(1);
//            request.setDtstart("2019-11-22T12:00:00");
//            request.setDuration("PnM");
//
//            logger.info("QueryEventRequest====================>" + JSON.toJSONString(request));
//            String result = okHttpPostToken(parameterValue + "QueryEventRequest", JSON.toJSONString(request), token);
//            logger.info("QueryEventResponse====================>" + result);
//            ObjectMapper om = new ObjectMapper();
//            QueryEventResponse queryEventResponse = om.readValue(result, QueryEventResponse.class);
//            return ResponseResult.success(queryEventResponse);
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseResult.error("失败");
//        }
//    }
//
//    @ApiOperation("元数据报告注册")
//    @UserLoginToken
//    @RequestMapping(value = "/RegisterReportRequest", method = {RequestMethod.POST})
//    public ResponseResult<String> RegisterReportRequest() {
//        try {
//            //获取token
//            String token = TokenRequest().getData().getToken();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            Date date = new Date();
//            String dateStr= sdf.format(date);
//
//            RegisterReportRequest request = new RegisterReportRequest();
//            UUID uuid = UUID.randomUUID();
//            request.setRoot("RegisterReportRequest");
//            request.setVersion(1);
//            request.setRequestID(uuid.toString());
//            request.setDnID(DN_ID);
//            request.setReportRequestID("MetaDataReport");
//            List<MetaDataReport> report = new ArrayList<>();
//            List<MetricName> dataList = Arrays.asList(MetricName.AP, MetricName.AP_E, MetricName.AP_PE, MetricName.REGULATE_UP, MetricName.REGULATE_DOWN, MetricName.RESPONSE_TIME, MetricName.CONTROL_RATE, MetricName.DURATION, MetricName.F_REGULATE_UP, MetricName.F_REGULATE_DOWN, MetricName.F_RESPONSE_TIME, MetricName.F_CONTROL_RATE, MetricName.F_DURATION);
//            for (int i=0; i<dataList.size(); i++) {
//                MetaDataReport metaDataReport = new MetaDataReport();
//                ReportDescription reportDescription = new ReportDescription();
//
//                MetricDescription metric = new MetricDescription();
//                metric.setMetricName(dataList.get(i));
//                metric.setMultiplier(UnitMultiplier.k);
//                metric.setSymbol(UnitSymbol.W);
//
//                Target reportDataSource = new Target();
//                List<String> resourceID = new ArrayList<>();
//                resourceID.add("10010104053622");
//                reportDataSource.setResourceID(resourceID);
//
//                reportDescription.setRID(i);
//                reportDescription.setReadingType(ReadingType.Summed);
//                reportDescription.setMetric(metric);
//                EndDeviceAsset endDeviceAsset = new EndDeviceAsset();
//                endDeviceAsset.setMrid("Vpp");
//                reportDescription.setReportSubject(endDeviceAsset);
//                reportDescription.setReportDataSource(reportDataSource);
//
//                metaDataReport.setCreatedDateTime(dateStr);
//                metaDataReport.setReportDescription(reportDescription);
//                report.add(metaDataReport);
//
//                LibraryRid libraryRid = new LibraryRid();
//                libraryRid.setId(DN_ID + "_" + "10010104053622" + "_" + reportDescription.getRID());
//                libraryRid.setDnID(DN_ID);
//                libraryRid.setResourceID("10010104053622");
//                libraryRid.setRID(reportDescription.getRID());
//                libraryRid.setRidDesc(metric.getMetricName().getDesc());
//                libraryRid.setDeviceSn("XTHWDB-54XH");
//                libraryRid.setSystemId("nengyuanzongbiao");
//                libraryRidRepository.save(libraryRid);
//            }
//            request.setReport(report);
//            logger.info("RegisterReportRequest====================>" + JSON.toJSONString(request));
//            String result = okHttpPostToken(parameterValue + "RegisterReportRequest", JSON.toJSONString(request), token);
//            logger.info("RegisterReportResponse====================>" + result);
//            return ResponseResult.success("元数据注册报告成功");
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseResult.error("元数据注册报告失败");
//        }
//    }
//
//    /**
//     * 虚拟电厂监测数据上报
//     * @param
//     * @return
//     */
//    //从5分钟开始每隔十五分钟执行一次
//    @Scheduled(cron="0 5/15 * * * *")
//    @ApiOperation("实时数据报告上报")
//    @UserLoginToken
//    @RequestMapping(value = "/MomentDataReportRequest", method = {RequestMethod.POST})
//    public ResponseResult<DataReportResponse> MomentDataReportRequest() {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//
//            SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
//            sdf5.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            Date date = new Date();
//
//            Calendar calendar1 = Calendar.getInstance();
//            calendar1.setTime(date);
//            calendar1.add(Calendar.MINUTE, -6);
//            Calendar calendar2 = Calendar.getInstance();
//            calendar2.setTime(date);
//            calendar2.add(Calendar.MINUTE, -4);
//            Calendar calendar5 = Calendar.getInstance();
//            calendar5.setTime(date);
//            calendar5.add(Calendar.MINUTE, -5);
//            String dateStr = sdf5.format(calendar5.getTime());
//
//            List<LibraryRid> libraryRidList = libraryRidRepository.findAllByDnID(DN_ID);
//
//            DecimalFormat format = new DecimalFormat("#.00");
//            //获取token
//            String token = TokenRequest().getData().getToken();
//
//            MomentDataReportRequest request = new MomentDataReportRequest();
//            UUID uuid = UUID.randomUUID();
//            request.setRoot("MomentDataReportRequest");
//            request.setVersion(1);
//            request.setRequestID(uuid.toString());
//            request.setDnID(DN_ID);
//            request.setCreatedDateTime(dateStr);
//
//            List<PointData> pointData = new ArrayList<>();
//
//            List<String> nodeList = new ArrayList<>();
//            nodeList.add("488067feec453899dcbe8d2660e39c7c");
//            for (LibraryRid libraryRid : libraryRidList) {
//                PointData pointData1 = new PointData();
//                String ridDesc = libraryRid.getRidDesc();
//                String deviceSn = libraryRid.getDeviceSn();
//                String resourceID = libraryRid.getResourceID();
//
//                pointData1.setRID(libraryRid.getRID());
//                pointData1.setQuality(DataQuality.good);
//                //要查的数据要和设备id对应起来
//                List<IotTsKvMeteringDevice96> iotTsKvMeteringDevice96loads = iotTsKvMeteringDevice96Repository.findHTotalUseByDeviceSnAndNodePostType(calendar1.getTime(), calendar2.getTime(), "load", deviceSn);
//                logger.info("========================" + ridDesc);
//                switch (ridDesc) {
//                    case "有功功率":
//                        List<IotTsKv> iotTsKvs = iotTsKvRepository.findPointValueByDeviceSnAndPointName(calendar1.getTime(), calendar2.getTime(), deviceSn, "输出负荷");
//                        if (iotTsKvs != null && iotTsKvs.size() > 0) {
//                            pointData1.setValue(Double.parseDouble(format.format(Double.parseDouble(iotTsKvs.get(0).getPointValue()))));
//                            pointData1.setTimestamp(sdf.format(iotTsKvs.get(0).getTs()));
//                        } else {
//                            pointData1.setValue(0d);
//                            pointData1.setTimestamp(dateStr);
//                        }
//                        pointData.add(pointData1);
//                        break;
//                    case "有功电能":
//                        if (iotTsKvMeteringDevice96loads != null && iotTsKvMeteringDevice96loads.size() > 0) {
//                            pointData1.setValue(Double.parseDouble(format.format(iotTsKvMeteringDevice96loads.get(0).getHTotalUse())));
//                            pointData1.setTimestamp(sdf.format(iotTsKvMeteringDevice96loads.get(0).getCountDataTime()));
//                        } else {
//                            pointData1.setValue(0d);
//                            pointData1.setTimestamp(dateStr);
//                        }
//                        pointData.add(pointData1);
//                        break;
//                    case "发电量":
//                        List<IotTsKvMeteringDevice96> iotTsKvMeteringDevice96pvs = iotTsKvMeteringDevice96Repository.findHTotalUseByDeviceSnAndNodePostType(calendar1.getTime(), calendar2.getTime(), "pv", deviceSn);
//                        if (iotTsKvMeteringDevice96pvs != null && iotTsKvMeteringDevice96pvs.size() > 0) {
//                            pointData1.setValue(Double.parseDouble(format.format(iotTsKvMeteringDevice96pvs.get(0).getHTotalUse())));
//                            pointData1.setTimestamp(sdf.format(iotTsKvMeteringDevice96pvs.get(0).getCountDataTime()));
//                        } else {
//                            pointData1.setValue(0d);
//                            pointData1.setTimestamp(dateStr);
//                        }
//                        pointData.add(pointData1);
//                        break;
//                    case "实时上调能力(KW)":
//                        List<AiLoadForecasting> aiLoadForecastings = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, "nengyuanzongbiao", calendar1.getTime(), calendar2.getTime());
//                        logger.info("aiLoadForecastings===" + aiLoadForecastings.size() + "iotTsKvMeteringDevice96loads===" + iotTsKvMeteringDevice96loads.size());
//                        if (aiLoadForecastings != null && aiLoadForecastings.size() > 0 && iotTsKvMeteringDevice96loads != null && iotTsKvMeteringDevice96loads.size() > 0) {
//                            pointData1.setValue(Double.parseDouble(format.format(Double.parseDouble(aiLoadForecastings.get(0).getPredictValue()) - iotTsKvMeteringDevice96loads.get(0).getHTotalUse())));
//                            pointData1.setTimestamp(sdf.format(iotTsKvMeteringDevice96loads.get(0).getCountDataTime()));
//                        } else {
//                            pointData1.setValue(0d);
//                            pointData1.setTimestamp(dateStr);
//                        }
//                        pointData.add(pointData1);
//                        break;
//                    case "实时下调能力(KW)":
//                        List<AiLoadForecasting> aiLoadForecastings1 = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, "nengyuanzongbiao", calendar1.getTime(), calendar2.getTime());
//                        logger.info("aiLoadForecastings1===" + aiLoadForecastings1.size() + "iotTsKvMeteringDevice96loads===" + iotTsKvMeteringDevice96loads.size());
//                        if (aiLoadForecastings1 != null && aiLoadForecastings1.size() > 0 && iotTsKvMeteringDevice96loads != null && iotTsKvMeteringDevice96loads.size() > 0) {
//                            pointData1.setValue(Double.parseDouble(format.format(Double.parseDouble(aiLoadForecastings1.get(0).getPredictValue()) - iotTsKvMeteringDevice96loads.get(0).getHTotalUse())));
//                            pointData1.setTimestamp(sdf.format(iotTsKvMeteringDevice96loads.get(0).getCountDataTime()));
//                        } else {
//                            pointData1.setValue(0d);
//                            pointData1.setTimestamp(dateStr);
//                        }
//                        pointData.add(pointData1);
//                        break;
//                    case "实时爬坡速度(KW/min)":
//                        Calendar calendar3 = Calendar.getInstance();
//                        calendar3.setTime(date);
//                        calendar3.add(Calendar.MINUTE, +24);
//                        Calendar calendar4 = Calendar.getInstance();
//                        calendar4.setTime(date);
//                        calendar4.add(Calendar.MINUTE, +26);
//                        List<IotTsKvMeteringDevice96> iotTsKvMeteringDevice96s1 = iotTsKvMeteringDevice96Repository.findHTotalUseByDeviceSnAndNodePostType(calendar3.getTime(), calendar4.getTime(), "load", deviceSn);
//                        if (iotTsKvMeteringDevice96loads != null && iotTsKvMeteringDevice96loads.size() > 0 && iotTsKvMeteringDevice96s1 != null && iotTsKvMeteringDevice96s1.size() > 0) {
//                            pointData1.setValue(Double.parseDouble(format.format((iotTsKvMeteringDevice96loads.get(0).getHTotalUse() - iotTsKvMeteringDevice96s1.get(0).getHTotalUse()) / 30)));
//                            pointData1.setTimestamp(sdf.format(iotTsKvMeteringDevice96loads.get(0).getCountDataTime()));
//                        } else {
//                            pointData1.setValue(0d);
//                            pointData1.setTimestamp(dateStr);
//                        }
//                        pointData.add(pointData1);
//                        break;
//                    case "实时持续时间(min)":
//                        pointData1.setValue(15d);
//                        pointData1.setTimestamp(dateStr);
//                        pointData.add(pointData1);
//                        break;
//                    case "实时响应时间(S)":
//                        pointData1.setValue(5d);
//                        pointData1.setTimestamp(dateStr);
//                        pointData.add(pointData1);
//                        break;
//                }
//            }
//            request.setPointData(pointData);
//            logger.info("MomentDataReportRequest====================>" + JSON.toJSONString(request));
//            String result = okHttpPostToken(parameterValue + "MomentDataReportRequest", JSON.toJSONString(request), token);
//            logger.info("MomentDataReportResponse====================>" + result);
//            ObjectMapper om = new ObjectMapper();
//            DataReportResponse dataReportResponse = om.readValue(result, DataReportResponse.class);
//            return ResponseResult.success(dataReportResponse);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseResult.error("实时数据上报失败");
//        }
//    }
//
//    @Scheduled(cron="0 30 5 * * *")
//    @ApiOperation("曲线数据报告上报")
//    @UserLoginToken
//    @RequestMapping(value = "/IntervalDataReportRequest", method = {RequestMethod.POST})
//    public ResponseResult<DataReportResponse> IntervalDataReportRequest() {
//        //每天五点半报送明天96点数据
//        try {
//            //获取token
//            String token = TokenRequest().getData().getToken();
//            UUID uuid = UUID.randomUUID();
//            DecimalFormat format = new DecimalFormat("#.00");
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
//            sdf1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            Date date = new Date();
//            String dateStr= sdf.format(date);
//            IntervalDataReportRequest request = new IntervalDataReportRequest();
//            //获取查询时间范围
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            calendar.set(Calendar.HOUR_OF_DAY, 0);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
//
//            Calendar calendar1 = Calendar.getInstance();
//            calendar1.add(Calendar.DAY_OF_MONTH, 2);
//            calendar1.set(Calendar.HOUR_OF_DAY, 0);
//            calendar1.set(Calendar.MINUTE, 0);
//            calendar1.set(Calendar.SECOND, 0);
//            calendar1.set(Calendar.MILLISECOND, 0);
//
//            request.setRoot("IntervalDataReportRequest");
//            request.setVersion(1);
//            request.setRequestID(uuid.toString());
//            request.setDnID(DN_ID);
//            request.setReportRequestID("IntervalDataReport");
//            request.setCreatedDateTime(dateStr);
//
//            List<LibraryRid> libraryRidList = libraryRidRepository.findAllByDnID(DN_ID);
//
//            List<PointCurveData> pointCurveData = new ArrayList<>();
//            for (LibraryRid libraryRid : libraryRidList) {
//                PointCurveData pointCurveData1 = new PointCurveData();
//                pointCurveData1.setRID(libraryRid.getRID());
//                String ridDesc = libraryRid.getRidDesc();
//                String deviceSn = libraryRid.getDeviceSn();
//                //要查的数据要和设备id对应起来
//                String resourceID = libraryRid.getResourceID();
//                String nodeId = libraryRid.getNodeId();
//                List<String> nodeList = new ArrayList<>();
//                nodeList.add("488067feec453899dcbe8d2660e39c7c");
//                String systemId = libraryRid.getSystemId();
//                IrregularCurve irregular = new IrregularCurve();
//                List<IotTsKvMeteringDevice96> iotTsKvMeteringDevice96s = iotTsKvMeteringDevice96Repository.findHTotalUseByDeviceSnAndNodePostType(calendar.getTime(), calendar1.getTime(), "load", deviceSn);
//                switch (ridDesc) {
//                    case "有功功率":
//                        List<AiLoadForecasting> aiLoadForecastings = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, systemId, calendar.getTime(), calendar1.getTime());
//                        if (aiLoadForecastings != null && aiLoadForecastings.size() > 0) {
//                            List<com.example.vvpweb.demand.model.Data> values = new ArrayList<>();
//                            for (AiLoadForecasting aiLoadForecasting : aiLoadForecastings) {
//                                com.example.vvpweb.demand.model.Data data = new Data();
//                                data.setValue(Double.parseDouble(format.format(Double.parseDouble(aiLoadForecasting.getPredictAdjustableAmount()))));
//                                data.setTimestamp(sdf1.format(aiLoadForecasting.getCountDataTime()));
//                                data.setQuality(DataQuality.good);
//                                values.add(data);
//                            }
//                            irregular.setValues(values);
//                        }
//                        pointCurveData1.setIrregular(irregular);
//                        pointCurveData.add(pointCurveData1);
//                        break;
//                    case "有功电能":
//                        List<AiLoadForecasting> aiLoadForecastings6 = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, systemId, calendar.getTime(), calendar1.getTime());
//                        if (aiLoadForecastings6 != null && aiLoadForecastings6.size() > 0) {
//                            List<com.example.vvpweb.demand.model.Data> values = new ArrayList<>();
//                            for (AiLoadForecasting aiLoadForecasting : aiLoadForecastings6) {
//                                com.example.vvpweb.demand.model.Data data = new Data();
//                                data.setValue(Double.parseDouble(format.format(Double.parseDouble(aiLoadForecasting.getPredictAdjustableAmount()))));
//                                data.setTimestamp(sdf1.format(aiLoadForecasting.getCountDataTime()));
//                                data.setQuality(DataQuality.good);
//                                values.add(data);
//                            }
//                            irregular.setValues(values);
//                        }
//                        pointCurveData1.setIrregular(irregular);
//                        pointCurveData.add(pointCurveData1);
//                        break;
//                    case "发电量":
//                        List<AiLoadForecasting> aiLoadForecastings4 = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, systemId, calendar.getTime(), calendar1.getTime());
//                        if (aiLoadForecastings4 != null && aiLoadForecastings4.size() > 0) {
//                            List<com.example.vvpweb.demand.model.Data> values = new ArrayList<>();
//                            for (AiLoadForecasting aiLoadForecasting : aiLoadForecastings4) {
//                                com.example.vvpweb.demand.model.Data data = new Data();
//                                data.setValue(Double.parseDouble(format.format(Double.parseDouble(aiLoadForecasting.getPredictAdjustableAmount()))));
//                                data.setTimestamp(sdf1.format(aiLoadForecasting.getCountDataTime()));
//                                data.setQuality(DataQuality.good);
//                                values.add(data);
//                            }
//                            irregular.setValues(values);
//                        }
//                        pointCurveData1.setIrregular(irregular);
//                        pointCurveData.add(pointCurveData1);
//                        break;
//                    case "日前申报上调能力(KW)":
//                        List<AiLoadForecasting> aiLoadForecastings3 = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, systemId, calendar.getTime(), calendar1.getTime());
//                        if (aiLoadForecastings3 != null && aiLoadForecastings3.size() > 0) {
//                            List<com.example.vvpweb.demand.model.Data> values = new ArrayList<>();
//                            for (AiLoadForecasting aiLoadForecasting : aiLoadForecastings3) {
//                                com.example.vvpweb.demand.model.Data data = new Data();
//                                data.setValue(Double.parseDouble(format.format(Double.parseDouble(aiLoadForecasting.getPredictAdjustableAmount()))));
//                                data.setTimestamp(sdf1.format(aiLoadForecasting.getCountDataTime()));
//                                data.setQuality(DataQuality.good);
//                                values.add(data);
//                            }
//                            irregular.setValues(values);
//                        }
//                        pointCurveData1.setIrregular(irregular);
//                        pointCurveData.add(pointCurveData1);
//                        break;
//                    case "日前申报下调能力(KW)":
//                        List<AiLoadForecasting> aiLoadForecastings1 = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, systemId, calendar.getTime(), calendar1.getTime());
//                        if (aiLoadForecastings1 != null && aiLoadForecastings1.size() > 0) {
//                            List<com.example.vvpweb.demand.model.Data> values = new ArrayList<>();
//                            for (AiLoadForecasting aiLoadForecasting : aiLoadForecastings1) {
//                                com.example.vvpweb.demand.model.Data data = new Data();
//                                data.setValue(Double.parseDouble(format.format(Double.parseDouble(aiLoadForecasting.getPredictAdjustableAmount()))));
//                                data.setTimestamp(sdf1.format(aiLoadForecasting.getCountDataTime()));
//                                data.setQuality(DataQuality.good);
//                                values.add(data);
//                            }
//                            irregular.setValues(values);
//                        }
//                        pointCurveData1.setIrregular(irregular);
//                        pointCurveData.add(pointCurveData1);
//                        break;
//                    case "日前申报爬坡速度(KW/min)":
//                        List<AiLoadForecasting> aiLoadForecastings2 = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, systemId, calendar.getTime(), calendar1.getTime());
//                        if (aiLoadForecastings2 != null && aiLoadForecastings2.size() > 0) {
//                            List<com.example.vvpweb.demand.model.Data> values = new ArrayList<>();
//                            for (AiLoadForecasting aiLoadForecasting : aiLoadForecastings2) {
//                                Calendar calendar2 = Calendar.getInstance();
//                                calendar2.setTime(aiLoadForecasting.getCountDataTime());
//                                calendar2.add(Calendar.MINUTE, -30);
//                                com.example.vvpweb.demand.model.Data data = new Data();
//                                //获取到前30分钟的数据
//                                List<AiLoadForecasting> aiLoadForecastings5 = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, systemId, calendar2.getTime(), calendar2.getTime());
//                                if (aiLoadForecastings5 != null && aiLoadForecastings5.size() > 0) {
//                                    data.setValue(Double.parseDouble(format.format((Double.parseDouble(aiLoadForecasting.getPredictAdjustableAmount())-Double.parseDouble(aiLoadForecastings5.get(0).getPredictAdjustableAmount()))/30)));
//                                } else {
//                                    data.setValue(0d);
//                                }
//                                data.setTimestamp(sdf1.format(aiLoadForecasting.getCountDataTime()));
//                                data.setQuality(DataQuality.good);
//                                values.add(data);
//                            }
//                            irregular.setValues(values);
//                        }
//                        pointCurveData1.setIrregular(irregular);
//                        pointCurveData.add(pointCurveData1);
//                        break;
//                    case "日前申报响应时间(s)":
//                        List<AiLoadForecasting> aiLoadForecastings8 = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, systemId, calendar.getTime(), calendar1.getTime());
//                        if (aiLoadForecastings8 != null && aiLoadForecastings8.size() > 0) {
//                            List<com.example.vvpweb.demand.model.Data> values = new ArrayList<>();
//                            for (AiLoadForecasting aiLoadForecasting : aiLoadForecastings8) {
//                                com.example.vvpweb.demand.model.Data data = new Data();
//                                data.setValue(5d);
//                                data.setTimestamp(sdf1.format(aiLoadForecasting.getCountDataTime()));
//                                data.setQuality(DataQuality.good);
//                                values.add(data);
//                            }
//                            irregular.setValues(values);
//                        }
//                        pointCurveData1.setIrregular(irregular);
//                        pointCurveData.add(pointCurveData1);
//                        break;
//                    case "日前申报持续时间(min)":
//                        List<AiLoadForecasting> aiLoadForecastings5 = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, systemId, calendar.getTime(), calendar1.getTime());
//                        if (aiLoadForecastings5 != null && aiLoadForecastings5.size() > 0) {
//                            List<com.example.vvpweb.demand.model.Data> values = new ArrayList<>();
//                            for (AiLoadForecasting aiLoadForecasting : aiLoadForecastings5) {
//                                com.example.vvpweb.demand.model.Data data = new Data();
//                                data.setValue(15d);
//                                data.setTimestamp(sdf1.format(aiLoadForecasting.getCountDataTime()));
//                                data.setQuality(DataQuality.good);
//                                values.add(data);
//                            }
//                            irregular.setValues(values);
//                        }
//                        pointCurveData1.setIrregular(irregular);
//                        pointCurveData.add(pointCurveData1);
//                        break;
//                }
//            }
//
//            request.setPointCurveData(pointCurveData);
//
//            logger.info("IntervalDataReportRequest====================>" + JSON.toJSONString(request));
//            String result = okHttpPostToken(parameterValue + "IntervalDataReportRequest", JSON.toJSONString(request), token);
//            logger.info("IntervalDataReportResponse====================>" + result);
//            ObjectMapper om = new ObjectMapper();
//            DataReportResponse dataReportResponse = om.readValue(result, DataReportResponse.class);
//            return ResponseResult.success(dataReportResponse);
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseResult.error("曲线数据上报失败");
//        }
//    }
//
//    /**
//     * 虚拟电厂预测数据上报
//     * @param
//     * @return
//     */
//    @ApiOperation("负荷预测数据报告请求")
//    @UserLoginToken
//    @RequestMapping(value = "/LoadForecastReportRequest", method = {RequestMethod.POST})
//    @Scheduled(cron="0 40 5 * * *")
//    public ResponseResult<DataReportResponse> LoadForecastReportRequest() {
//        try {
//            LoadForecastReportRequest request = new LoadForecastReportRequest();
//            //获取token
//            String token = TokenRequest().getData().getToken();
//            UUID uuid = UUID.randomUUID();
//
//            DecimalFormat format = new DecimalFormat("#.00");
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
//            sdf1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            Date date = new Date();
//            String dateStr= sdf1.format(date);
//            List<String> nodeList = new ArrayList<>();
//            nodeList.add("488067feec453899dcbe8d2660e39c7c");
//
//            //获取查询时间范围
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            calendar.set(Calendar.HOUR_OF_DAY, 0);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
//
//            Calendar calendar1 = Calendar.getInstance();
//            calendar1.add(Calendar.DAY_OF_MONTH, 2);
//            calendar1.set(Calendar.HOUR_OF_DAY, 0);
//            calendar1.set(Calendar.MINUTE, 0);
//            calendar1.set(Calendar.SECOND, 0);
//            calendar1.set(Calendar.MILLISECOND, 0);
//
//            request.setRoot("LoadForecastReportRequest");
//            request.setVersion(1);
//            request.setRequestID(uuid.toString());
//            request.setDnID(DN_ID);
//            request.setReportRequestID("LoadForecastReport");
//            request.setCreatedDateTime(dateStr);
//
//            List<ResourceCurveData> resourceCurveData = new ArrayList<>();
//            List<AiLoadForecasting> aiLoadForecastings = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, "nengyuanzongbiao", calendar.getTime(), calendar1.getTime());
//
//            aiLoadForecastings.remove(96);
//            ResourceCurveData resourceCurveData1 = new ResourceCurveData();
//            RegularCurve regular = new RegularCurve();
//            regular.setDtstart(dateStr);
//            regular.setPeriod("PT15M");
//            List<Double> list = new ArrayList<>();
//            for (int i = 0; i < aiLoadForecastings.size(); i++) {
//                list.add(Double.parseDouble(format.format(Double.parseDouble(aiLoadForecastings.get(i).getPredictValue()))));
//            }
//            regular.setArray(list);
//            resourceCurveData1.setResourceID("10010104053622");
//            resourceCurveData1.setRegular(regular);
//            resourceCurveData.add(resourceCurveData1);
//            request.setResourceCurveData(resourceCurveData);
//
//            logger.info("LoadForecastReportRequest====================>" + JSON.toJSONString(request));
//            String result = okHttpPostToken(parameterValue + "LoadForecastReportRequest", JSON.toJSONString(request), token);
//            logger.info("LoadForecastReportResponse====================>" + result);
//            ObjectMapper om = new ObjectMapper();
//            DataReportResponse dataReportResponse = om.readValue(result, DataReportResponse.class);
//            return ResponseResult.success(dataReportResponse);
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseResult.error("失败");
//        }
//    }
//
//    @ApiOperation("可调节能力负荷预测报告上报")
//    @UserLoginToken
//    @Scheduled(cron="0 45 5 * * *")
//    @RequestMapping(value = "/RegulateForecastReportRequest", method = {RequestMethod.POST})
//    public ResponseResult<DataReportResponse> RegulateForecastReportRequest() {
//        try {
//            //获取token
//            String token = TokenRequest().getData().getToken();
//            UUID uuid = UUID.randomUUID();
//            RegulateForecastReportRequest request = new RegulateForecastReportRequest();
//
//            DecimalFormat format = new DecimalFormat("#.00");
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
//            sdf1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//            Date date = new Date();
//            String dateStr= sdf1.format(date);
//
//            List<String> nodeList = new ArrayList<>();
//            nodeList.add("488067feec453899dcbe8d2660e39c7c");
//
//            //获取查询时间范围
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            calendar.set(Calendar.HOUR_OF_DAY, 0);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
//
//            Calendar calendar1 = Calendar.getInstance();
//            calendar1.add(Calendar.DAY_OF_MONTH, 2);
//            calendar1.set(Calendar.HOUR_OF_DAY, 0);
//            calendar1.set(Calendar.MINUTE, 0);
//            calendar1.set(Calendar.SECOND, 0);
//            calendar1.set(Calendar.MILLISECOND, 0);
//
//            request.setRoot("RegulateForecastReportRequest");
//            request.setVersion(1);
//            request.setRequestID(uuid.toString());
//            request.setDnID(DN_ID);
//            request.setReportRequestID("RegulateForecastReportRequest");
//            request.setCreatedDateTime(dateStr);
//
//            List<ResourceCurveData> resourceCurveData = new ArrayList<>();
//            List<AiLoadForecasting> aiLoadForecastings = aiLoadRepository.findByDateNodeIdsSystemId(nodeList, "nengyuanzongbiao", calendar.getTime(), calendar1.getTime());
//
//            aiLoadForecastings.remove(96);
//            ResourceCurveData resourceCurveData1 = new ResourceCurveData();
//            RegularCurve regular = new RegularCurve();
//            regular.setDtstart(dateStr);
//            regular.setPeriod("PT15M");
//            List<Double> list = new ArrayList<>();
//            for (int i = 0; i < aiLoadForecastings.size(); i++) {
//                list.add(Double.parseDouble(format.format(Double.parseDouble(aiLoadForecastings.get(i).getPredictValue()))));
//            }
//            regular.setArray(list);
//            resourceCurveData1.setResourceID("10010104053622");
//            resourceCurveData1.setRegular(regular);
//            resourceCurveData.add(resourceCurveData1);
//            request.setResourceCurveData(resourceCurveData);
//
//            logger.info("RegulateForecastReportRequest====================>" + JSON.toJSONString(request));
//            String result = okHttpPostToken(parameterValue + "RegulateForecastReportRequest", JSON.toJSONString(request), token);
//            logger.info("RegulateForecastReportResponse====================>" + result);
//            ObjectMapper om = new ObjectMapper();
//            DataReportResponse dataReportResponse = om.readValue(result, DataReportResponse.class);
//            return ResponseResult.success(dataReportResponse);
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseResult.error("失败");
//        }
//    }
//
//    //拉取部分
//    @Scheduled(initialDelay = 1000 * 5, fixedDelay = 10 * 1000)
//    @Async
//    public void pollMonitoringPlatform() throws JsonProcessingException {
//        String result = new PlatformUtils().Poll();
//        String root = JSON.parseObject(result).get("root").toString();
//        ObjectMapper objectMapper = new ObjectMapper();
//        ReportModel model = objectMapper.readValue(result, ReportModel.class);
//        if (Objects.equals(root, "DistributeEventRequest")) {
//            log.info("DistributeEventRequest");
//            DistributeEventRequest(model);
//        } else if (Objects.equals(root, "ReregistrationRequest")) {
//            log.info("ReregistrationRequest");
//            CreateRegistrationRequest();
//        } else if (Objects.equals(root, "CreateReportRequest")) {
//            log.info("CreateReportRequest");
//            CreateReportRequest(model);
//        } else if(Objects.equals(root, "PollResponse")) {
//            log.info("PollResponse:{}",result);
//        } else if (Objects.equals(root,"MetaDataReport")) {
//            log.info("MetaDataReport");
//            RegisterReportRequest();
//        }
//        else if (Objects.equals(root,"RegisterReportResponse")) {
//            log.info("RegisterReportResponse");
//            RegisterReportRequest();
//        } else if (Objects.equals(root, "LoadForecastReport")) {
//            log.info("LoadForecastReport");
//            LoadForecastReportRequest();
//        } else if (Objects.equals(root, "RegulateForecastReport")) {
//            log.info("RegulateForecastReport");
//            RegulateForecastReportRequest();
//        }
//    }
//    public void DistributeEventRequest(ReportModel model) {
//        try {
//            model.getEvents().forEach(v -> {
//                Map<String, Object> descriptorMap = (Map<String, Object>) v.get("descriptor");
//                new PlatformUtils()
//                        .CreateEventResponse(model.getRequestID(), (String) descriptorMap.get("eventID"));
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//            log.info("DistributeEventRequest失败:{}",JSON.toJSONString(model));
//        }
//    }
//    public void CreateReportRequest(ReportModel model) {
//        try {
//            Map<String,Object> map = model.getReportRequest().get(0);
//            Object reportRequestID = map.get("reportRequestID");
//            String requestID = (String) map.get("requestID");
//            PlatformUtils platformUtils = new PlatformUtils();
//            platformUtils.CreateReportResponse(requestID,Arrays.asList((String) reportRequestID));
//            if (reportRequestID.equals("MetaDataReport")) {
//                RegisterReportRequest();
//            } else if (reportRequestID.equals("LoadForecastReport")) {
//                log.info("LoadForecastReport");
//                LoadForecastReportRequest();
//            } else if (reportRequestID.equals("RegulateForecastReport")) {
//                log.info("RegulateForecastReport");
//                RegulateForecastReportRequest();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            log.info("CreateReportRequest失败:{}",JSON.toJSONString(model));
//        }
//    }
//    public static String okHttpTokenPost(String reqUrl, String json) {
//        try {
//            // 创建一个信任所有证书的 TrustManager
//            TrustManager[] trustAllCerts = new TrustManager[]{
//                    new X509TrustManager() {
//                        public X509Certificate[] getAcceptedIssuers() {
//                            return new X509Certificate[0];
//                        }
//
//                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                        }
//
//                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                        }
//                    }
//            };
//
//            // 创建 SSL 上下文，使用信任所有证书的 TrustManager
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//
//            // 设置 OkHttpClient 使用我们创建的 SSL 上下文
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
//                    .hostnameVerifier((hostname, session) -> true)
//                    .build();
//
//            MediaType mediaType = MediaType.parse("application/json");
//            RequestBody body = RequestBody.create(mediaType, json);
//            Request request = new Request.Builder()
//                    .url(reqUrl)
//                    .method("POST", body)
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("Accept", "application/json")
//                    .build();
//
//            // 发送请求
//            Response response = client.newCall(request).execute();
//            return response.body().string();
//        } catch (Exception e) {
//            throw new RuntimeException("HTTP POST同步请求失败 URL:" + reqUrl, e);
//        }
//    }
//
//    public static String okHttpPostToken(String reqUrl, String json, String token) {
//        try {
//            // 创建一个信任所有证书的 TrustManager
//            TrustManager[] trustAllCerts = new TrustManager[]{
//                    new X509TrustManager() {
//                        public X509Certificate[] getAcceptedIssuers() {
//                            return new X509Certificate[0];
//                        }
//
//                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                        }
//                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                        }
//                    }
//            };
//
//            // 创建 SSL 上下文，使用信任所有证书的 TrustManager
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//
//            // 设置 OkHttpClient 使用我们创建的 SSL 上下文
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
//                    .hostnameVerifier((hostname, session) -> true)
//                    .build();
//
//            MediaType mediaType = MediaType.parse("application/json");
//            RequestBody body = RequestBody.create(mediaType, json);
//            Request request = new Request.Builder()
//                    .url(reqUrl)
//                    .method("POST", body)
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("Accept", "application/json")
//                    .addHeader("token", token)
//                    .build();
//
//            // 发送请求
//            Response response = client.newCall(request).execute();
//            return response.body().string();
//        } catch (Exception e) {
//            throw new RuntimeException("HTTP POST同步请求失败 URL:" + reqUrl, e);
//        }
//    }
//}
