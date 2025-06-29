//package com.example.vvpscheduling;
//
//
//import com.alibaba.fastjson.JSON;
//import com.example.vvpdomain.AiLoadRepository;
//import com.example.vvpdomain.entity.AiLoadForecasting;
//import com.example.vvpscheduling.util.monitoringPlatform.PlatformUtils;
//import com.example.vvpscheduling.util.monitoringPlatform.ReportModel;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import javax.persistence.criteria.Predicate;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Component
//@EnableAsync
//@Slf4j
//public class MonitoringPlatformJob {
//
//    @Resource
//    private AiLoadRepository aiLoadRepository;
//    /**定时poll上海平台*/
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
//            ReregistrationRequest(model);
//        } else if (Objects.equals(root, "CreateReportRequest")) {
//            log.info("CreateReportRequest");
//            CreateReportRequest(model);
//        } else if(Objects.equals(root, "PollResponse")) {
//            log.info("PollResponse:{}",result);
//        } else if (Objects.equals(root,"MetaDataReport")) {
//            log.info("MetaDataReport");
//            String requestID = JSON.parseObject(result).get("requestID").toString();
//            new PlatformUtils().RegisterReportRequest(requestID,"linshi", Arrays.asList("10010104052953","10030000009569"));
//        }
//        else if (Objects.equals(root,"RegisterReportResponse")) {
//            log.info("RegisterReportResponse");
//            String requestID = JSON.parseObject(result).get("requestID").toString();
//            new PlatformUtils().RegisterReportRequest(requestID,"linshi", Arrays.asList("10010104052953","10030000009569"));
//        }
//        else if (Objects.equals(root, "LoadForecastReport")) {
//            log.info("LoadForecastReport");
//            String requestID = JSON.parseObject(result).get("requestID").toString();
//            List<String> forecastingList = ForecastingList("488067feec453899dcbe8d2660e39c7c");
//            Map<String,List<String>> loadForecast = new HashMap<>();
//            Arrays.asList("10010104052953","10030000009569").forEach(v -> loadForecast.put(v,forecastingList));
//            new PlatformUtils().LoadForecastReportRequest(requestID,loadForecast, LocalDateTime.now());
//        } else if (Objects.equals(root, "RegulateForecastReport")) {
//            log.info("RegulateForecastReport");
//            String requestID = JSON.parseObject(result).get("requestID").toString();
//            List<String>forecastingList = ForecastingList("488067feec453899dcbe8d2660e39c7c");
//            Map<String,List<String>> RegulateForecast = new HashMap<>();
//            Arrays.asList("10010104052953","10030000009569").forEach(v -> RegulateForecast.put(v,forecastingList));
//            new PlatformUtils().RegulateForecastReportRequest(requestID,RegulateForecast,LocalDateTime.now());
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
//    public void ReregistrationRequest(ReportModel model) {
//        try {
//            new PlatformUtils().CreateRegistrationRequest(model.getRequestID());
//        }catch (Exception e){
//            e.printStackTrace();
//            log.info("ReregistrationRequest失败:{}",JSON.toJSONString(model));
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
//                platformUtils.RegisterReportRequest(requestID,String.valueOf(reportRequestID), Arrays.asList("10010104052953","10030000009569"));
//            } else if (reportRequestID.equals("LoadForecastReport")) {
//                List<String> forecastingList = ForecastingList("488067feec453899dcbe8d2660e39c7c");
//                Map<String,List<String>> loadForecast = new HashMap<>();
//                Arrays.asList("10010104052953","10030000009569").forEach(v -> loadForecast.put(v,forecastingList));
//                platformUtils.LoadForecastReportRequest(requestID,loadForecast, LocalDateTime.now());
//            } else if (reportRequestID.equals("RegulateForecastReport")) {
//                List<String>forecastingList = RegulateForecastList("488067feec453899dcbe8d2660e39c7c");
//                Map<String,List<String>> RegulateForecast = new HashMap<>();
//                Arrays.asList("10010104052953","10030000009569").forEach(v -> RegulateForecast.put(v,forecastingList));
//                platformUtils.RegulateForecastReportRequest(requestID,RegulateForecast,LocalDateTime.now());
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            log.info("CreateReportRequest失败:{}",JSON.toJSONString(model));
//        }
//    }
//    public List<String> ForecastingList(String nodeId) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
//        Specification<AiLoadForecasting> spec1 = (root, criteriaQuery, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
//            predicates.add(cb.in(root.get("nodeId")).value(Arrays.asList(nodeId)));
//            predicates.add(cb.equal(root.get("systemId"), "kongtiao"));
//            // 获取今天的日期
//            LocalDate today = LocalDate.now();
//
//            // 将今天的日期转换为当天的0点和23:59:59
//            LocalDateTime startOfDay = today.atStartOfDay();
//            LocalDateTime endOfDay = today.atTime(23, 59, 59);
//
//            // 将LocalDateTime转换为Date
//            Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
//            Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
//            predicates.add(cb.between(root.get("createdTime"), startDate, endDate));
//            criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
//            return criteriaQuery.getRestriction();
//        };
//        List<String> forecastingList = aiLoadRepository.findAll(spec1)
//                .stream()
//                .sorted(Comparator.comparing(AiLoadForecasting::getCreatedTime))
//                .distinct()
//                .filter(v -> {
//                    Date createdDate = v.getCountDataTime();
//                    LocalDateTime createdTime = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());
//                    int minute = createdTime.getMinute();
//                    return minute == 0 || minute == 15 || minute == 30 || minute == 45;
//                })
//                .map(AiLoadForecasting::getPredictValue)
//                .collect(Collectors.toList());
//        return forecastingList;
//    }
//    public List<String> RegulateForecastList(String nodeId) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Specification<AiLoadForecasting> spec1 = (root, criteriaQuery, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
//            predicates.add(cb.in(root.get("nodeId")).value(Arrays.asList(nodeId)));
//            predicates.add(cb.equal(root.get("systemId"), "kongtiao"));
//            // 获取今天的日期
//            LocalDate today = LocalDate.now();
//
//            // 将今天的日期转换为当天的0点和23:59:59
//            LocalDateTime startOfDay = today.atStartOfDay();
//            LocalDateTime endOfDay = today.atTime(23, 59, 59);
//
//            // 将LocalDateTime转换为Date
//            Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
//            Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
//            predicates.add(cb.between(root.get("createdTime"), startDate, endDate));
//            criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
//            return criteriaQuery.getRestriction();
//        };
//        List<String> forecastingList = aiLoadRepository.findAll(spec1)
//                .stream()
//                .sorted(Comparator.comparing(AiLoadForecasting::getCreatedTime))
//                .distinct()
//                .filter(v -> {
//                    Date createdDate = v.getCountDataTime();
//                    LocalDateTime createdTime = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());
//                    int minute = createdTime.getMinute();
//                    return minute == 0 || minute == 15 || minute == 30 || minute == 45;
//                })
//                .map(AiLoadForecasting::getPredictAdjustableAmount)
//                .collect(Collectors.toList());
//        return forecastingList;
//    }
//}
