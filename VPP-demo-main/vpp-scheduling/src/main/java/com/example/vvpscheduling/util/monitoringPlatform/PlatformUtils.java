package com.example.vvpscheduling.util.monitoringPlatform;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class PlatformUtils {

    //测试
    private String parameterValue = "https://203.110.167.251:9001/";
    //生产
//    private String parameterValue = "https://203.110.167.251:9001/";

    private String registrationID = "b2e9b0d4-a8c7-45ce-9fe6-e151ccfe18b7";

    private String dnID = "10010103000078";

    /**查询token*/
    public String TokenRequest(String requestID) {
        Map<String,Object> json = new HashMap<>();
        json.put("root","TokenRequest");
        json.put("version",1);
        json.put("requestID",requestID);
        json.put("dnID",dnID);
//        log.info("TokenRequest:{}",JSON.toJSONString(json));
        return JSON.parseObject(okHttpTokenPost(parameterValue + "TokenRequest", JSON.toJSONString(json))).get("token").toString();
    }
    /**询问请求*/
    public String Poll() {
        Map<String,Object> json = new HashMap<>();
        String requestId = String.valueOf(UUID.randomUUID());
        json.put("root","Poll");
        json.put("version",1);
        json.put("requestID",requestId);
        json.put("dnID",dnID);
        String result = okHttpPost(parameterValue + "Poll", JSON.toJSONString(json),requestId);
        log.info("PollJson:{}",json);
        log.info("Poll:{}",result);
        return result;
    }
    /**创建注册请求*/
    /**【特别说明！！】首次注册返回的响应中，registrationID记得保存，
     * 后续重新注册需要带上该ID，否则会报错WrongRegistrationID。
     * 首次注册可以用postman之类的工具，registrationID记得保存*/
    /**重新注册请求也用这个*/
    public String CreateRegistrationRequest(String requestID) {
        Map<String,Object> json = new HashMap<>();
        json.put("root","CreateRegistrationRequest");
        json.put("version",1);
        json.put("requestID",requestID);
        json.put("dnID",dnID);
        json.put("dnName","达卯测试");
        json.put("registrationID",registrationID);
        json.put("reportOnly",false);
        json.put("pullMode",true);
        json.put("signature",false);
        json.put("transport","REST");
        json.put("transportAddress","https://52107s0i69.yicp.fun");
        String result = okHttpPost(parameterValue + "CreateRegistrationRequest", JSON.toJSONString(json),requestID);
        log.info("CreateRegistrationRequestJson:{}",json);
        log.info("CreateRegistrationRequest:{}",result);
        return result;
    }

    /**查询注册请求*/
    public String QueryRegistrationRequest(String requestID) {
        Map<String,Object> json = new HashMap<>();
        json.put("root","QueryRegistrationRequest");
        json.put("version",1);
        json.put("requestID",requestID);
        json.put("dnID",dnID);
        String result = okHttpPost(parameterValue + "QueryRegistrationRequest", JSON.toJSONString(json),requestID);
        log.info("QueryRegistrationRequest:{}",result);
        return result;
    }

    /**创建事件请求*/
    public String CreateEventResponse(String requestID,String eventID) {
        Map<String,Object> json = new HashMap<>();
        json.put("root","QueryRegistrationRequest");
        json.put("version",1);
        json.put("code",200);
        json.put("description","ok");
        json.put("requestID",requestID);
        json.put("dnID",dnID);
        Map<String,Object> eventResponses = new HashMap<>();
        eventResponses.put("optType","optIn");
        eventResponses.put("code",200);
        eventResponses.put("description","ok");
        eventResponses.put("requestID",requestID);
        Map<String,Object> qualifiedEventID = new HashMap<>();
        qualifiedEventID.put("eventID",eventID);
        qualifiedEventID.put("modificationNumber",0);
        eventResponses.put("qualifiedEventID",qualifiedEventID);
        json.put("eventResponses", Arrays.asList(eventResponses));
        String result = okHttpPost(parameterValue + "CreateEventResponse", JSON.toJSONString(json),requestID);
        log.info("CreateEventResponseJson:{}",json);
        log.info("CreateEventResponse:{}",result);
        return result;
    }

    /**创建报告响应*/
    public String CreateReportResponse(String requestID,List<String> reportRequestID) {
        ReportModel json = new ReportModel();
        json.setRoot("CreateReportResponse");
        json.setVersion(1);
        json.setCode(200);
        json.setDescription("ok");
        json.setRequestID(requestID);
        json.setDnID(dnID);
        Map<String,Object> map = new HashMap<>();
        map.put("reportRequestID",reportRequestID);
        json.setPendingReports(map);
        String result = okHttpPost(parameterValue + "CreateReportResponse", JSON.toJSONString(json),requestID);
        log.info("CreateReportResponseJson:{}",json);
        log.info("CreateReportResponse:{}",result);
        return result;
    }

    /**拉取元数据报告*/
    public String RegisterReportRequest(String requestID, String reportRequestID, List<String> resourceID) {
        Map<String,Object> json = new HashMap<>();
        json.put("root","RegisterReportRequest");
        json.put("version",1);
        json.put("requestID",requestID);
        json.put("dnID","dnID");
        json.put("reportRequestID","MetaDataReport");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createdDateTime = dateFormat.format(new Date());
        //todo 元数据报告需要三个维度虚拟电厂运营商维度、聚合用户维度、分路可调节资源维度
        List<Map<String,Object>> reportList = new ArrayList<>();

        //第一
        Map<String,Object> report1 = new HashMap<>();

        Map<String,Object> reportDescription1 = new HashMap<>();
        reportDescription1.put("rID",0);

        Map<String,Object> metric1 = new HashMap<>();
        metric1.put("metricName","RESPONSE_TIME");
        metric1.put("multiplier","k");
        metric1.put("symbol","w");
        reportDescription1.put("metric",metric1);
        reportDescription1.put("readingType","Summed");

        Map<String,Object> reportDataSource1 = new HashMap<>();
        reportDataSource1.put("resourceID", resourceID);
        reportDescription1.put("reportDataSource",reportDataSource1);

        Map<String,Object> reportSubject1 = new HashMap<>();
        Map<String,Object> endDeviceAsset1 = new HashMap<>();
        endDeviceAsset1.put("mrid","Vpp");
        reportSubject1.put("endDeviceAsset",endDeviceAsset1);
        reportDescription1.put("reportSubject",reportSubject1);

        Map<String,Object> samplingRate1 = new HashMap<>();
        samplingRate1.put("onChange",false);
        samplingRate1.put("minPeriod","PT15M");
        samplingRate1.put("maxPeriod","PT15M");
        reportDescription1.put("samplingRate",samplingRate1);

        report1.put("createdDateTime",createdDateTime);
        report1.put("reportDescription",reportDescription1);
        reportList.add(report1);

        //第二
        Map<String,Object> report2 = new HashMap<>();

        Map<String,Object> reportDescription2 = new HashMap<>();
        reportDescription2.put("rID",1);

        Map<String,Object> metric2 = new HashMap<>();
        metric2.put("metricName","AP_E");
        metric2.put("multiplier","k");
        metric2.put("symbol","w");
        reportDescription2.put("metric",metric2);
        reportDescription2.put("readingType","Summed");

        Map<String,Object> reportDataSource2 = new HashMap<>();
        reportDataSource2.put("resourceID", Arrays.asList("10030000009569"));
        reportDescription2.put("reportDataSource",reportDataSource2);

        Map<String,Object> reportSubject2 = new HashMap<>();
        Map<String,Object> endDeviceAsset2 = new HashMap<>();
        endDeviceAsset2.put("mrid","Resource_IndustrialPlant");
        reportSubject2.put("endDeviceAsset",endDeviceAsset2);
        reportDescription2.put("reportSubject",reportSubject2);

        Map<String,Object> samplingRate2 = new HashMap<>();
        samplingRate2.put("onChange",false);
        samplingRate2.put("minPeriod","PT15M");
        samplingRate2.put("maxPeriod","PT15M");
        reportDescription2.put("samplingRate",samplingRate2);

        report2.put("createdDateTime",createdDateTime);
        report2.put("reportDescription",reportDescription2);
        reportList.add(report2);
        //第三
        Map<String,Object> report3 = new HashMap<>();

        Map<String,Object> reportDescription3 = new HashMap<>();
        reportDescription3.put("rID",2);

        Map<String,Object> metric3 = new HashMap<>();
        metric3.put("metricName","AP");
        metric3.put("multiplier","k");
        metric3.put("symbol","w");
        reportDescription3.put("metric",metric3);
        reportDescription3.put("readingType","Direct_Read");

        Map<String,Object> reportDataSource3 = new HashMap<>();
        reportDataSource3.put("resourceID", resourceID);
        reportDescription3.put("reportDataSource",reportDataSource3);

        Map<String,Object> reportSubject3 = new HashMap<>();
        Map<String,Object> endDeviceAsset3 = new HashMap<>();
        endDeviceAsset3.put("mrid","Office_Air_Conditioner");
        reportSubject3.put("endDeviceAsset",endDeviceAsset3);
        reportDescription3.put("reportSubject",reportSubject3);

        Map<String,Object> samplingRate3 = new HashMap<>();
        samplingRate3.put("onChange",false);
        samplingRate3.put("minPeriod","PT15M");
        samplingRate3.put("maxPeriod","PT15M");
        reportDescription3.put("samplingRate",samplingRate3);

        report3.put("createdDateTime",createdDateTime);
        report3.put("reportDescription",reportDescription3);
        reportList.add(report3);


        json.put("report", reportList);
        String result = okHttpPost(parameterValue + "RegisterReportRequest", JSON.toJSONString(json),requestID);
        log.info("RegisterReportRequestJson:{}",JSON.toJSONString(json));
        log.info("RegisterReportRequest:{}",result);
        return result;
    }

    /**拉取负荷预测数据报告*/
    public String LoadForecastReportRequest(String requestID, Map<String,List<String>> resourceIDList,LocalDateTime dtstart) {
        Map<String,Object> json = new HashMap<>();
        json.put("root","LoadForecastReportRequest");
        json.put("version",1);
        json.put("requestID",requestID);
        json.put("dnID",dnID);
        json.put("reportRequestID","LoadForecastReport");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        json.put("createdDateTime",dateFormat.format(new Date()));
        List<Map<String,Object>> resourceCurveData = new ArrayList<>();
        resourceIDList.forEach((k,v) -> {
            Map<String,Object> resourceCurve = new HashMap<>();
            resourceCurve.put("resourceID",k);//"DistributeEventRequest接口获取"
            Map<String,Object> regular = new HashMap<>();
            regular.put("dtstart",dtstart);
            regular.put("period","PT15M");
            regular.put("array",v);
            resourceCurve.put("regular",regular);
            resourceCurveData.add(resourceCurve);///96点每十五分钟的预测数据
        });
        json.put("resourceCurveData",resourceCurveData);
        String result = okHttpPost(parameterValue + "LoadForecastReportRequest", JSON.toJSONString(json),requestID);
        log.info("LoadForecastReportRequestJson:{}",json);
        log.info("LoadForecastReportRequest:{}",result);
        return result;
    }
    /**拉取可调节能力负荷预测数据报告*/
    public String RegulateForecastReportRequest(String requestID, Map<String,List<String>> resourceIDList,LocalDateTime dtstart) {
        Map<String,Object> json = new HashMap<>();
        json.put("root","RegulateForecastReportRequest");
        json.put("version",1);
        json.put("requestID",requestID);
        json.put("dnID",dnID);
        json.put("reportRequestID","RegulateForecastReportRequest");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        json.put("createdDateTime",dateFormat.format(new Date()));
        List<Map<String,Object>> resourceCurveData = new ArrayList<>();
        resourceIDList.forEach((k,v) -> {
            Map<String,Object> resourceCurve = new HashMap<>();
            resourceCurve.put("resourceID",k);//"DistributeEventRequest接口获取"
            Map<String,Object> regular = new HashMap<>();
            regular.put("dtstart",dtstart);//开始时间
            regular.put("period","PT15M");
            regular.put("array",v);//十五分钟的预测调节
            resourceCurve.put("regular",regular);
            resourceCurveData.add(resourceCurve);///96点每十五分钟的预测数据
        });
        json.put("resourceCurveData",resourceCurveData);
        String result = okHttpPost(parameterValue + "RegulateForecastReportRequest", JSON.toJSONString(json),requestID);
        log.info("RegulateForecastReportRequestJson:{}",json);
        log.info("RegulateForecastReportRequest:{}",result);
        return result;
    }
    public static String okHttpTokenPost(String reqUrl, String json) {
        try {
            // 创建一个信任所有证书的 TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // 创建 SSL 上下文，使用信任所有证书的 TrustManager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // 设置 OkHttpClient 使用我们创建的 SSL 上下文
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(reqUrl)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();

            // 发送请求
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException("HTTP POST同步请求失败 URL:" + reqUrl, e);
        }
    }
    public static String okHttpPost(String reqUrl, String json,String requestID) {
        try {
            // 创建一个信任所有证书的 TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // 创建 SSL 上下文，使用信任所有证书的 TrustManager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // 设置 OkHttpClient 使用我们创建的 SSL 上下文
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(reqUrl)
                    .method("POST", body)
                    .addHeader("token",new PlatformUtils().TokenRequest(requestID))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();

            // 发送请求
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException("HTTP POST同步请求失败 URL:" + reqUrl, e);
        }
    }
}
