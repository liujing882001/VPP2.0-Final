//package com.example.vvpscheduling;
//
//
//import com.alibaba.fastjson.JSON;
//import com.example.vvpscheduling.model.tradePowerJob.AIStorageEnergystrategyRequest;
//import com.example.vvpweb.tradepower.model.TradeEnvironmentConfig;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import java.security.SecureRandom;
//import java.security.cert.X509Certificate;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.ZoneOffset;
//import java.util.*;
//
//@Component
//@EnableAsync
//@Slf4j
//public class tradePowerJob {
//
//    private static TradeEnvironmentConfig config;
//
//    @Autowired
//    public tradePowerJob(TradeEnvironmentConfig environmentConfig) {
//        config = environmentConfig;
//    }
//
//    @Scheduled(cron = "0 0 8 * * *")
//    @Async
//    public void tradePower() throws ParseException, JsonProcessingException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        LocalDateTime now = LocalDateTime.now(ZoneOffset.ofHours(8));
//        LocalDateTime sLocalTime = now.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(2);
//        LocalDateTime eLocalTime = sLocalTime.plusDays(2);
//
//        Date sTime = Date.from(sLocalTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
//        Date eTime = Date.from(eLocalTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
//        int startYear = sLocalTime.getYear();
//        int startMonth = sLocalTime.getMonthValue();
//        int startDay = sLocalTime.getDayOfMonth();
//        int endMonth = eLocalTime.getMonthValue();
//        int endDay = eLocalTime.getDayOfMonth();
//        String id = String.format("%04d%02d%02d%02d%02d", startYear, startMonth, startDay, endMonth, endDay);
//
//        //给算法发送请求
//        Map<String,String> aireq = new HashMap<>();
//        aireq.put("node_id",config.getAireqNode());
////        aireq.put("node_id","c20a1ecb5d33539e5334ad85af822252");
////        aireq.put("node_id","e4653aad857c96f4c2ea4fd044bffbea");
//        aireq.put("task_code",id);
//
//        log.info("定时任务交易任务生成给算法发送预测储能策略请求,预测节点为===========>>>>>>>" + JSON.toJSONString(aireq));
//        String result = okHttpPost("http://127.0.0.1:13360/powerTradingStrategy", JSON.toJSONString(aireq));
//        log.info("定时任务交易任务生成算法返回结果=======" + result);
//        ObjectMapper om = new ObjectMapper();
//        AIStorageEnergystrategyRequest responseResult = om.readValue(result, AIStorageEnergystrategyRequest.class);
//        if (responseResult.getCode() != 200) {
//            log.info("定时任务交易任务算法生成失败");
//        }
////        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
////
////        LocalDateTime now = LocalDateTime.now(ZoneOffset.ofHours(8));
////        LocalDateTime sLocalTime = now.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(2);
////        LocalDateTime eLocalTime = sLocalTime.plusDays(2);
////
////        Date sTime = Date.from(sLocalTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
////        Date eTime = Date.from(eLocalTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
////        int startYear = sLocalTime.getYear();
////        int startMonth = sLocalTime.getMonthValue();
////        int startDay = sLocalTime.getDayOfMonth();
////        int endMonth = eLocalTime.getMonthValue();
////        int endDay = eLocalTime.getDayOfMonth();
////        String id = String.format("%04d%02d%02d%02d%02d", startYear, startMonth, startDay, endMonth, endDay);
////        Optional<TradePower> existingRecord = ttradePowerRepository.findById(id);
////        TradePower po;
////        if (existingRecord.isPresent()) {
////            po = existingRecord.get();
////        } else {
////            po = new TradePower();
////            po.setId(id);
////        }
////        //给算法发送请求
////        Map<String,String> aireq = new HashMap<>();
////        aireq.put("node_id","e4653aad857c96f4c2ea4fd044bffbea");
////        aireq.put("task_code",id);
////
////        log.info("给算法发送预测储能策略请求,预测节点为===========>>>>>>>" + aireq.get("node_id"));
////        String result = okHttpPost("http://192.168.110.55:13360/powerTradingStrategy", JSON.toJSONString(aireq));
////        log.info("算法返回结果=======" + result);
////        ObjectMapper om = new ObjectMapper();
////        AIStorageEnergystrategyRequest responseResult = om.readValue(result, AIStorageEnergystrategyRequest.class);
////        if (responseResult.getCode() != 200) {
////            return;
////        }
////        Map<String,String> map = new HashMap<>();
////        map.put("e4653aad857c96f4c2ea4fd044bffbea","产投储能001");
////        map.put("07c3c82df1dd93e9c303644eb79985cb","产投储能002");
////        List<SchedulingStrategyModel> listEnd = new ArrayList<>();
////        long startTime = System.currentTimeMillis();
////        while (true) {
////            ObjectMapper objectMapper = new ObjectMapper();
////            Object schedulingStrategyObject = redisUtils.get("SchedulingStrategy" + id);
////            List<SchedulingStrategyFirstModel> list = null;
////            if (schedulingStrategyObject instanceof String) {
////                String schedulingStrategyJson = JSON.toJSONString(schedulingStrategyObject);
////                list = objectMapper.readValue(
////                        schedulingStrategyJson,
////                        new TypeReference<List<SchedulingStrategyFirstModel>>() {
////                        }
////                );
////            }
////            if (list != null && list.size() > 0) {
////                list.forEach(v1 -> {
////                    SchedulingStrategyModel model = new SchedulingStrategyModel();
////                    model.setDate(v1.getDate());
////                    List<StrategyModel> sModels = new ArrayList<>();
////                    v1.getStrategy().forEach(s -> {
////                        StrategyModel sModel = new StrategyModel();
////                        sModel.setNodeId(s.getNodeId());
////                        sModel.setNodeName(map.get(s.getNodeId()));
////                        sModel.setList(new ArrayList<>());
////                        SimpleDateFormat sft = new SimpleDateFormat("HH:mm");
////
////                        List<StrategyTimeModel> sTModels = new ArrayList<>();
////                        IntStream.range(0, s.getList().size()).forEach(i -> {
////                            int startHour = (i * 15) / 60;
////                            int startMinute = (i * 15) % 60;
////                            int endHour = ((i + 1) * 15) / 60;
////                            int endMinute = ((i + 1) * 15) % 60;
////                            String stime = String.format("%02d:%02d", startHour, startMinute);
////                            String etime = String.format("%02d:%02d", endHour, endMinute);
////                            StrategyTimeModel sTModel = new StrategyTimeModel();
////                            try {
////                                sTModel.setStime(sft.parse(stime));
////                                sTModel.setEtime(sft.parse(etime));
////                            } catch (ParseException e) {
////                                throw new RuntimeException(e);
////                            }
////                            Double power = s.getList().get(i);
////                            sTModel.setPower(power);
////                            sTModel.setType(power > 0 ? "充电" : power == 0 ? "待机" : "放电");
////                            sTModels.add(sTModel);
////                        });
////                        sModel.setList(sTModels);
////                        sModels.add(sModel);
////                    });
////                    model.setStrategy(sModels);
////                    listEnd.add(model);
////                });
////                break;
////            } else {
////                if (System.currentTimeMillis() - startTime > 60000) {
////                    break;
////                }
////                try {
////                    Thread.sleep(1000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }
////        }
////        List<DeclareForOperationModel> list = genList(sdf.format(sTime));
////        po.setSTime(sTime);
////        po.setETime(eTime);
////        po.setTradeType(1);
////        po.setStation("长乐产投大楼、深圳泰伦广场A座、深圳泰伦广场B座、深圳某工业设备新材料股份有限公司");
////        po.setStatus(1);
////        po.setLoadNodes("176c0991f24e30c2b25a9dbf1185b7b9,5eb413037ba16ea6108c12e0d6353be3,3da72e052a0b48759b0f4633df42235a,e238bb37143b82082f695bb5c9cb438f");
////        po.setEnergyNodes("e4653aad857c96f4c2ea4fd044bffbea,07c3c82df1dd93e9c303644eb79985cb");
////        po.setPvNodes("bb05b2b6d467846b9ea2b68de14c6f70");
////        po.setStrategy(JSON.toJSONString(listEnd));
////        po.setOperation(JSON.toJSONString(list));
////        ttradePowerRepository.save(po);
//    }
////    public List<DeclareForOperationModel> genList(String commend) throws ParseException {
////        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
////        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
////        Date startDate = sdf.parse(commend);
////        Calendar calendar = Calendar.getInstance();
////        calendar.setTime(startDate);
////        calendar.set(Calendar.HOUR_OF_DAY, 0);
////        calendar.set(Calendar.MINUTE, 0);
////        calendar.set(Calendar.SECOND, 0);
////        calendar.set(Calendar.MILLISECOND, 0);
////        calendar.add(Calendar.DAY_OF_MONTH, 3);
////        Date endDate = calendar.getTime();
////        //用电
////        List<AiLoadForecasting> allData = aiLoadRepository.findByDateNodeIdsSystemId(
////                Arrays.asList("e238bb37143b82082f695bb5c9cb438f",
////                        "176c0991f24e30c2b25a9dbf1185b7b9",
////                        "5eb413037ba16ea6108c12e0d6353be3",
////                        "3da72e052a0b48759b0f4633df42235a"),
////                "nengyuanzongbiao",
////                startDate,
////                endDate);
////
////        Map<String, Map<String, List<AiLoadForecasting>>> groupedData = allData.stream()
////                .filter(device -> device.getCountDataTime() != null && device.getPredictValue() != null)
////                .collect(Collectors.groupingBy(
////                        device -> sdf.format(device.getCountDataTime()),
////                        Collectors.groupingBy(
////                                AiLoadForecasting::getNodeId
////                        )
////                ));
////        //发电
////        List<AiLoadForecasting> allPvData = aiLoadRepository.findByDateNodeIdSystemId(
////                "bb05b2b6d467846b9ea2b68de14c6f70",
////                "nengyuanzongbiao",
////                startDate,
////                endDate);
////
////        Map<String, Map<String, List<AiLoadForecasting>>> groupedPvData = allPvData.stream()
////                .filter(device -> device.getCountDataTime() != null && device.getPredictValue() != null)
////                .collect(Collectors.groupingBy(
////                        device -> sdf.format(device.getCountDataTime()),
////                        Collectors.groupingBy(
////                                AiLoadForecasting::getNodeId
////                        )
////                ));
////
////        List<DeclareForOperationModel> list1 = generateResponsesForOperation(startDate,endDate);
////        for (DeclareForOperationModel d : list1) {
////            String dDate = sdf.format(d.getDate());
////            Map<String, List<AiLoadForecasting>> forecast = groupedData.get(dDate);
////            Map<String, List<AiLoadForecasting>> pvForecast = groupedPvData.get(dDate);
////            for (OperationModel s : d.getStrategy()) {
////                List<AiLoadForecasting> forecastings = forecast.get(s.getNodeId());
////                List<AiLoadForecasting> pvForecastings = new ArrayList<>();
////                int i = 1;
////                if (s.getNodeId().equals(config.getMasterNode())) {
////                    i = 2;
////                    pvForecastings = pvForecast.get(s.getNodeId());
////                }
////                if (forecastings == null) {
////                    continue;
////                }
////                List<OperationTimeModel> operationTimeModels = s.getList();
////                if (i == 2) {
////                    for (OperationTimeModel operationTime : operationTimeModels) {
////                        String stimeHHMM = timeFormat.format(operationTime.getStime());
////                        if (pvForecastings != null) {
////                            for (AiLoadForecasting pvForecasting : pvForecastings) {
////                                String countDataTimeHHMM = timeFormat.format(pvForecasting.getCountDataTime());
////                                if (stimeHHMM.equals(countDataTimeHHMM)) {
////                                    Double power = operationTime.getPower();
////                                    Double predictValue = Double.parseDouble(pvForecasting.getPredictValue());
////                                    operationTime.setPower(power - predictValue);
////                                }
////                            }
////                        }
////                        for (AiLoadForecasting forecasting : forecastings) {
////                            String countDataTimeHHMM = timeFormat.format(forecasting.getCountDataTime());
////                            if (stimeHHMM.equals(countDataTimeHHMM)) {
////                                Double power = operationTime.getPower();
////                                Double predictValue = Double.parseDouble(forecasting.getPredictValue());
////                                operationTime.setPower(power + predictValue);
////                            }
////                        }
////                    }
////                } else {
////                    for (OperationTimeModel operationTime : operationTimeModels) {
////                        String stimeHHMM = timeFormat.format(operationTime.getStime());
////                        for (AiLoadForecasting forecasting : forecastings) {
////                            String countDataTimeHHMM = timeFormat.format(forecasting.getCountDataTime());
////                            if (stimeHHMM.equals(countDataTimeHHMM)) {
////                                Double power = operationTime.getPower();
////                                Double predictValue = Double.parseDouble(forecasting.getPredictValue());
////                                operationTime.setPower(power + predictValue);
////                            }
////                        }
////                    }
////                }
////
////            }
////        }
////        return list1;
////    }
////    public static List<DeclareForOperationModel> generateResponsesForOperation(Date startDate, Date endDate) throws ParseException {
////        List<DeclareForOperationModel> responses = new ArrayList<>();
////        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
////        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
////
////        Calendar calendar = Calendar.getInstance();
////        calendar.setTime(startDate);
////        while (calendar.getTime().before(endDate)) {
////            DeclareForOperationModel declareForOperationModel = new DeclareForOperationModel();
////            declareForOperationModel.setDate(dateFormat.parse(dateFormat.format(calendar.getTime())));
////
////            List<OperationModel> operationModelList = new ArrayList<>();
////            for (Map.Entry<String, String> entry : config.getNodeMap().entrySet()) {
////                OperationModel operationModel = new OperationModel();
////                operationModel.setNodeId(entry.getKey());
////                operationModel.setNodeName(entry.getValue());
////
////                List<OperationTimeModel> operationTimeModelList = new ArrayList<>();
////                for (int i = 0; i < 96; i++) { // 每15分钟一个数据点
////                    OperationTimeModel operationTimeModel = new OperationTimeModel();
////                    Calendar startTime = (Calendar) calendar.clone();
////                    startTime.add(Calendar.MINUTE, i * 15);
////                    operationTimeModel.setStime(timeFormat.parse(timeFormat.format(startTime.getTime())));
////
////                    Calendar endTime = (Calendar) startTime.clone();
////                    endTime.add(Calendar.MINUTE, 15);
////                    operationTimeModel.setEtime(timeFormat.parse(timeFormat.format(endTime.getTime())));
////
////                    operationTimeModel.setPower(0.0);
////                    operationTimeModel.setType("type");
////                    operationTimeModelList.add(operationTimeModel);
////                }
////                operationModel.setList(operationTimeModelList);
////                operationModelList.add(operationModel);
////            }
////
////            declareForOperationModel.setStrategy(operationModelList);
////            responses.add(declareForOperationModel);
////
////            calendar.add(Calendar.DAY_OF_MONTH, 1);
////        }
////
////        return responses;
////    }
//
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
//
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
//
//}
