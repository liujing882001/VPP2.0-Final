//package com.example.vvpweb.demand;
//
//import com.alibaba.fastjson.JSON;
//import com.example.vvpcommom.UserLoginToken;
//import com.example.vvpdomain.*;
//import com.example.vvpdomain.entity.*;
//import com.example.vvpweb.demand.model.*;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import javax.servlet.http.HttpServletRequest;
//import java.security.SecureRandom;
//import java.security.cert.X509Certificate;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//@RestController
//@RequestMapping("/drSouth")
//@CrossOrigin
//@Api(value = "国网上海-需求响应南向服务", tags = {"国网上海-需求响应南向服务"})
//public class DRSubService1Controller {
//    @Resource
//    IotTsKvMeteringDevice96Repository iot96Repository;
//    @Resource
//    IotTsKvRepository iotTsKvRepository;
//    @Resource
//    DeviceRepository deviceRepository;
//    @Resource
//    DevicePointRepository devicePointRepository;
//    @Resource
//    SouthSourceRepository southSourceRepository;
//    @Resource
//    StationNodeRepository stationNodeRepository;
//    @Resource
//    AiLoadRepository aiLoadRepository;
//    @Resource
//    ComputeNodeRepository computeNodeRepository;
//
//    @ApiOperation("实时数据上报")
//    @UserLoginToken
//    @RequestMapping(value = "/MomentDataReportRequest", method = {RequestMethod.POST})
//    public DrsResponse getDateList(@RequestBody DrsRCommand command) {
//        String requestID = command.getRequestID();
//        try {
//            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            DateTimeFormatter formatter5 = DateTimeFormatter.ofPattern("HH:mm");
//
//            List<IotTsKvMeteringDevice96> iotTsKv96List = new ArrayList<>();
//
//            String singleTime = command.getCreatedDateTime().replace("-", "")
//                    .replace(" ", "")
//                    .replace(":", "");
//            List<String> strings = command.getPointData().stream().map(DrsRPointData::getResourceID).collect(Collectors.toList());
//            Map<String, List<DevicePoint>> devicePointsMap = devicePointRepository.findAllByDevice_DeviceIds(strings).stream()
//                    .collect(Collectors.groupingBy(
//                            devicePoint -> devicePoint.getDevice().getDeviceId()
//                    ));
//            command.getPointData().forEach(p -> {
//                List<DevicePoint> devicePoints = devicePointsMap.get(p.getResourceID());
//                Map<String, DevicePoint> devicePointMap = devicePoints.stream()
//                        .collect(Collectors.toMap(
//                                DevicePoint::getPointName,
//                                devicePoint -> devicePoint,
//                                (existing, replacement) -> existing
//                        ));
//                String timestamp = p.getTimestamp();
//                LocalDateTime parsedDateTime = LocalDateTime.parse(timestamp, formatter1);
//                ZoneId zoneId = ZoneId.of("Asia/Shanghai");
//                LocalDateTime lastTime = parsedDateTime.minusMinutes(15);
//                Date yMdHms = Date.from(parsedDateTime.atZone(zoneId).toInstant());
//                p.getRData().forEach(r -> {
//                    DevicePoint devicePoint;
//                    if (r.getValueType() == 2) {
//                        devicePoint = devicePointMap.get("发电量");
//                    } else if (r.getValueType() == 3) {
//                        devicePoint = devicePointMap.get("放电量");
//                    } else{
//                        devicePoint = devicePointMap.get("有功功率");
//                    }
//                    String id = String.format("%s_%s_%s_%s", devicePoint.getDeviceSn(), devicePoint.getPointSn(), devicePoint.getPointDesc(), singleTime);
//                    IotTsKvMeteringDevice96 iotTs96Kv = new IotTsKvMeteringDevice96();
//                    iotTs96Kv.setId(id);
//                    iotTs96Kv.setNodeId(devicePoint.getDevice().getNode().getNodeId());
//                    iotTs96Kv.setSystemId(devicePoint.getDevice().getSystemType().getSystemId());
//                    iotTs96Kv.setDeviceSn(devicePoint.getDeviceSn());
//                    iotTs96Kv.setPointSn(devicePoint.getPointSn());
//                    iotTs96Kv.setConfigKey(devicePoint.getDeviceConfigKey());
//                    iotTs96Kv.setHTotalUse(r.getValue());
//                    iotTs96Kv.setTotalPowerEnergy(0.0);
//                    iotTs96Kv.setPointDesc(devicePoint.getPointDesc());
//                    iotTs96Kv.setPointUnit(devicePoint.getPointUnit());
//                    iotTs96Kv.setCountDate(yMdHms);
//                    iotTs96Kv.setCountTime(yMdHms);
//                    iotTs96Kv.setCountDataTime(yMdHms);
//                    iotTs96Kv.setTimeScope(formatter5.format(lastTime)+ "-" +formatter5.format(parsedDateTime));
//                    iotTs96Kv.setNodePostType(devicePoint.getDevice().getNode().getNodePostType());
//                    iotTs96Kv.setTs(yMdHms);
//                    iotTsKv96List.add(iotTs96Kv);
//                });
//            });
//            iot96Repository.saveAll(iotTsKv96List);
//            return drsResponse("MomentDataReportRequest",200,"ok",requestID);
//        } catch (Exception e) {
//            log.error("实时上报出错：",e);
//            return drsResponse("MomentDataReportRequest",500,"Error",requestID);
//        }
//    }
//    @ApiOperation("预测数据上报")
//    @UserLoginToken
//    @RequestMapping(value = "/ForecastReportRequest", method = {RequestMethod.POST})
//    public DrsResponse getDateList(@RequestBody DrsRFCommand command) {
//        String requestID = command.getRequestID();
//        try {
//            List<AiLoadForecasting> loadFList = new ArrayList<>();
//            List<String> strings = command.getPointData().stream().map(DrsRFPointData::getResourceID).collect(Collectors.toList());
//            Map<String, StationNode> stationNodes = stationNodeRepository.findAllByStationIdIn(strings).stream()
//                    .collect(Collectors.toMap(
//                            StationNode::getStationId,
//                            stationNode -> stationNode,
//                            (existing, replacement) -> existing
//                    ));
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//            List<DrsRFPointData> pointDataList = command.getPointData();
//            for (DrsRFPointData p : pointDataList) {
//                StationNode stationNode = stationNodes.get(p.getResourceID());
//                DrsRFData rfData = p.getRFData();
//                String dtsStart = rfData.getDtstart();
//                LocalDateTime dtsStartDate = LocalDateTime.parse(dtsStart, formatter);
//
//                List<Double> rfArray = rfData.getArray();
//                if (rfArray.size() != 96) {
//                    return drsResponse("ForecastReportRequest",400,"LoadForecastDataNot96Points",requestID);
//                }
//                for (Double rf : rfArray) {
//                    Date countDateTime = Date.from(dtsStartDate.atZone(ZoneId.systemDefault()).toInstant());
//                    String id = stationNode.getStationId() + "_" + "nengyuanzongbiao" + "_" + formatter.format(dtsStartDate);
//                    AiLoadForecasting forecasting = new AiLoadForecasting();
//                    forecasting.setId(id);
//                    forecasting.setCountDataTime(countDateTime);
//                    forecasting.setPredictValue(String.valueOf(rf));
//                    loadFList.add(forecasting);
//                    dtsStartDate = dtsStartDate.plusMinutes(15);
//                }
//            }
//
//            aiLoadRepository.saveAll(loadFList);
//            return drsResponse("ForecastReportRequest", 200, "ok", requestID);
//        } catch (Exception e) {
//            log.error("实时上报出错：",e);
//            return drsResponse("ForecastReportRequest",500,"Error",requestID);
//        }
//
//    }
//
//    @ApiOperation("邀请资源方参与需求响应")
//    @UserLoginToken
//    @RequestMapping(value = "/DemandResponseInvitation", method = {RequestMethod.POST})
//    public String getDateList(@RequestBody DrICommand command) {
//        String requestID = String.valueOf(UUID.randomUUID());
//        command.setRequestID(requestID);
//        String url = "";
//        return okHttpPost(url, JSON.toJSONString(command));
//    }
//    @ApiOperation("资源方申报参与需求响应")
//    @UserLoginToken
//    @RequestMapping(value = "/DemandResponseDeclaration", method = {RequestMethod.POST})
//    public DrsResponse getDateList(@RequestBody DrDCommand command) {
//        String requestID = command.getRequestID();
//        try {
//            return drsResponse("DemandResponseDeclaration",200,"ok",requestID);
//        } catch (Exception e) {
//            log.error("实时上报出错：",e);
//            return drsResponse("DemandResponseDeclaration",500,"Error",requestID);
//        }
//    }
//    @ApiOperation("告知资源方申报结果")
//    @UserLoginToken
//    @RequestMapping(value = "/DemandResponseBidNotification", method = {RequestMethod.POST})
//    public String getDateList(@RequestBody DrBCommand command) {
//        String requestID = String.valueOf(UUID.randomUUID());
//        command.setRequestID(requestID);
//        String url = "";
//        return okHttpPost(url, JSON.toJSONString(command));
//    }
//
//    public DrsResponse drsResponse(String root,Integer code,String description,String requestID) {
//        DrsResponse response = new DrsResponse();
//        response.setRoot(root);
//        response.setVersion(1);
//        response.setCode(code);
//        response.setDescription(description);
//        response.setRequestID(requestID);
//        return response;
//    }
//    // 自定义方法用于生成DrsResponse对象
//    public DrsResponse createDrsResponse(String root, Integer code, String description, String requestID) {
//        DrsResponse response = new DrsResponse();
//        response.setRoot(root);
//        response.setVersion(1);  // 可以固定版本号
//        response.setCode(code);
//        response.setDescription(description);
//        response.setRequestID(requestID);
//        return response;
//    }
//
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<DrsResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
//        String requestID = request.getHeader("requestID");
//        String root = request.getHeader("root");
//        DrsResponse errorResponse = createDrsResponse(
//                root,
//                400,
//                "JsonWrong",
//                requestID
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//
////    @ExceptionHandler(CustomServiceException.class)
////    public ResponseEntity<DrsResponse> handleCustomServiceException(CustomServiceException ex) {
////        DrsResponse errorResponse = createDrsResponse(
////                "root",
////                500,
////                ex.getMessage(),
////                ex.getRequestID()
////        );
////        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
////    }
//    public static String okHttpPost(String reqUrl, String json) {
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
//            // 创建 SSL 上下文，使用信任所有证书的 TrustManager
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new SecureRandom());
//
//            // 设置 OkHttpClient 使用我们创建的 SSL 上下文
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
//                    .hostnameVerifier((hostname, session) -> true)
//                    .build();
//
//            MediaType mediaType = MediaType.parse("application/json");
//            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, json);
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
//}
