package com.example.vvpweb.demand;

import com.alibaba.fastjson.JSON;
import com.example.vvpcommom.*;
import com.example.vvpcommom.thread.ThreadLocalUtil;
import com.example.vvpdomain.ComputeNodeRepository;
import com.example.vvpdomain.UserRepository;
import com.example.vvpdomain.UserSecretRepository;
import com.example.vvpdomain.entity.ComputeNode;
import com.example.vvpdomain.entity.UserSecret;
import com.example.vvpweb.demand.model.*;
import com.example.vvpweb.systemmanagement.systemuser.model.DrSouthLogin;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.example.vvpdomain.constants.UserConstant.*;

/**
 * 此类用于对接外部对接需求响应的接口，使用加密解密处理
 * 写接口时请求参数均已@RequestBody接入，返回使用DrsResponse封装
 * 入需截取其他类型，需要测试加解密问题
 */
@Slf4j
@RestController
@RequestMapping("/drSouth")
@CrossOrigin
@Api(value = "国网上海-需求响应南向服务", tags = {"国网上海-需求响应南向服务"})
public class DRSubServiceController {
    @Resource
    ComputeNodeRepository computeNodeRepository;
    @Resource
    private Producer captchaProducer;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserRepository userRepository;
    @Autowired
    private UserSecretRepository userSecretRepository;
    @Value("${mayi.domain.url:null}")
    private String domainUrl;

    @PostMapping(value = "/test")
    @Encrypted
    public DrsResponse test(@RequestBody DrsRCommand command) {
        log.info("request=={}", JSON.toJSONString(command));
        return drsResponse("MomentDataReportRequest",200,"ok",command.getRequestID());
    }

//    /**
//     * 获取登录码
//     * @return
//     */
//    @PostMapping("/getCode")
//    public DrsResponse getCode() {
//        String sessionKey = UUID.randomUUID().toString();
//        String codeText = captchaProducer.createText();
//
//        String base64Code = new BASE64Encoder().encode(codeText.getBytes(StandardCharsets.UTF_8));
//
//        // 保存到验证码到 redis 设置1分钟过期
//        redisUtils.add(Constants.KAPTCHA_SESSION_KEY + "_" + sessionKey, codeText, 1, TimeUnit.MINUTES);
//
//        DrsResponse response = new DrsResponse();
//        response.setCode(200);
//        response.setDescription(base64Code);
//        return response;
//    }

    /**
     * 需求响应用户登录
     * @param authLogin
     * @return
     */
    @PostMapping(value = "/login")
    public DrsResponse login(@RequestBody @Validated DrSouthLogin authLogin) {

        String userId = authLogin.getUserId();
        String passWord = authLogin.getPassWord();

        Optional<UserSecret> user = userSecretRepository.findById(userId);

        if (!user.isPresent()) {
            return drsResponse(404, "未找到用户");
        }
        UserSecret userSecret = user.get();
        if (!userSecret.getUserPassword().equals(passWord)) {
            return drsResponse(400, "密码错误");
        }

        String token = Md5TokenGenerator.generate(userId, passWord);
        String base64Token = Base64.getEncoder().encodeToString(token.getBytes());
        String userKey = USER_TOKEN_KEY + token;

        redisUtils.add(userKey, userId, 7 * 24, TimeUnit.HOURS);

        return drsResponse(200, base64Token);
    }

    @ApiOperation("实时数据上报")
    @UserLoginToken
    @Encrypted
    @RequestMapping(value = "/MomentDataReportRequest", method = {RequestMethod.POST})
    public DrsResponse getDateList(@RequestBody DrsRCommand command) {
        String requestID = command.getRequestID();
        log.info("实时数据上报:{}",JSON.toJSONString(command));
        try {
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<ComputeNode> computeNodeList = new ArrayList<>();
            List<DrsRPointData> pointData = command.getPointData();
            for (DrsRPointData p : pointData) {
                String resourceID = p.getResourceID();
                String timestamp = p.getTimestamp();
                LocalDateTime parsedDateTime = LocalDateTime.parse(timestamp, formatter1);
                List<DrsRData> rData = p.getRData();
                if (rData != null) {
                    for (DrsRData r : rData) {
                        Integer valueType = r.getValueType();
                        ComputeNode computeNode = new ComputeNode();
                        computeNode.setId(resourceID + parsedDateTime + valueType);
                        computeNode.setResourceID(resourceID);
                        computeNode.setTimestamp(parsedDateTime);
                        computeNode.setValueType(valueType);
                        computeNode.setValue(r.getValue());
                        computeNode.setDataType("实际");
                        computeNode.setPeriod("PT15M");
                        computeNode.setUpTime(LocalDateTime.now());
                        computeNodeList.add(computeNode);
                    }
                }
                List<DrsDData> dData = p.getDData();
                if (dData != null) {
                    for (DrsDData d : dData) {
                        String dResourceID = d.getResourceID();
                        for (DrsRData r : d.getRData()) {
                            Integer valueType = r.getValueType();
                            ComputeNode computeNode = new ComputeNode();
                            computeNode.setId(dResourceID + parsedDateTime + valueType);
                            computeNode.setResourceID(dResourceID);
                            computeNode.setTimestamp(parsedDateTime);
                            computeNode.setValueType(valueType);
                            computeNode.setValue(r.getValue());
                            computeNode.setDataType("实际");
                            computeNode.setPeriod("PT15M");
                            computeNode.setUpTime(LocalDateTime.now());
                            computeNodeList.add(computeNode);
                        }
                    }
                }
            }
            computeNodeRepository.saveAll(computeNodeList);
            return drsResponse("MomentDataReportRequest",200,"ok",requestID);
        } catch (Exception e) {
            log.error("实时上报出错:{}",requestID + e.getMessage());
            return drsResponse("MomentDataReportRequest",500,e.getMessage(),requestID);
        }
    }
    @ApiOperation("预测数据上报")
    @UserLoginToken
    @Encrypted
    @RequestMapping(value = "/ForecastReportRequest", method = {RequestMethod.POST})
    public DrsResponse getDateList(@RequestBody DrsRFCommand command) {
        String requestID = command.getRequestID();
        log.info("预测数据上报:{}",JSON.toJSONString(command));
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<DrsRFPointData> pointData = command.getPointData();
            List<ComputeNode> computeNodeList = new ArrayList<>();
            for (DrsRFPointData r : pointData) {
                String resourceID = r.getResourceID();
                DrsRFData rFData = r.getRFData();
                String dtstart = rFData.getDtstart();
                LocalDateTime localDateTime = LocalDateTime.parse(dtstart, formatter);
                String period = rFData.getPeriod();
                for (Double rf : rFData.getArray()) {
                    ComputeNode computeNode = new ComputeNode();
                    computeNode.setId(resourceID + localDateTime);
                    computeNode.setResourceID(resourceID);
                    computeNode.setTimestamp(localDateTime);
                    computeNode.setValueType(1);
                    computeNode.setValue(rf);
                    computeNode.setDataType("预测");
                    computeNode.setPeriod(period);
                    computeNode.setUpTime(LocalDateTime.now());
                    computeNodeList.add(computeNode);
                    localDateTime = localDateTime.plusMinutes(15);
                }
            }
            computeNodeRepository.saveAll(computeNodeList);
            return drsResponse("ForecastReportRequest", 200, "ok", requestID);
        } catch (Exception e) {
            log.error("实时上报出错:{}",requestID + e.getMessage());
            return drsResponse("ForecastReportRequest",500,e.getMessage(),requestID);
        }

    }

    @ApiOperation("邀请资源方参与需求响应")
    @UserLoginToken
    @Encrypted
    @RequestMapping(value = "/DemandResponseInvitation", method = {RequestMethod.POST})
    public String getDateList(@RequestBody DrICommand command) {
        String requestID = String.valueOf(UUID.randomUUID());
        command.setRequestID(requestID);
        String url = domainUrl + "/openapi/DMKJ/energy/invite/publish/notify.do";
        return okHttpPost(url, JSON.toJSONString(command));
    }
    @ApiOperation("资源方申报参与需求响应")
    @UserLoginToken
    @Encrypted
    @RequestMapping(value = "/DemandResponseDeclaration", method = {RequestMethod.POST})
    public DrsResponse getDateList(@RequestBody DrDCommand command) {
        String requestID = command.getRequestID();
        log.info("资源方申报参与需求响应:{}",JSON.toJSONString(command));
        try {
            if (command.getDeclareData() != null) {
                return drsResponse("DemandResponseDeclaration",200,"ok",requestID);
            } else {
                return drsResponse("DemandResponseDeclaration",400,"上报数据为NULL",requestID);
            }
        } catch (Exception e) {
            log.error("实时上报出错:{}",requestID + e.getMessage());
            return drsResponse("DemandResponseDeclaration",500,"Error",requestID);
        }
    }
    @ApiOperation("告知资源方申报结果")
    @UserLoginToken
    @Encrypted
    @RequestMapping(value = "/DemandResponseBidNotification", method = {RequestMethod.POST})
    public String getDateList(@RequestBody DrBCommand command) {
        log.info("告知资源方申报结果:{}",JSON.toJSONString(command));
        String requestID = String.valueOf(UUID.randomUUID());
        command.setRequestID(requestID);
        String url = domainUrl + "/openapi/DMKJ/energy/invite/bid/notify.do";
        return okHttpPost(url, JSON.toJSONString(command));
    }

    public DrsResponse drsResponse(String root,Integer code,String description,String requestID) {
        DrsResponse response = new DrsResponse();
        response.setRoot(root);
        response.setVersion(1);
        response.setCode(code);
        response.setDescription(description);
        response.setRequestID(requestID);
        return response;
    }
    public DrsResponse drsResponse(Integer code,String token) {
        DrsResponse response = new DrsResponse();
        response.setCode(code);
        response.setToken(token);
        return response;
    }
    // 自定义方法用于生成DrsResponse对象
    public DrsResponse createDrsResponse(String root, Integer code, String description, String requestID) {
        DrsResponse response = new DrsResponse();
        response.setRoot(root);
        response.setVersion(1);  // 可以固定版本号
        response.setCode(code);
        response.setDescription(description);
        response.setRequestID(requestID);
        return response;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DrsResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String requestID = request.getHeader("requestID");
        String root = request.getHeader("root");
        log.error(ex.getMessage(), ex);
        DrsResponse errorResponse = createDrsResponse(
                root,
                400,
                "JsonWrong",
                requestID
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    //    @ExceptionHandler(CustomServiceException.class)
//    public ResponseEntity<DrsResponse> handleCustomServiceException(CustomServiceException ex) {
//        DrsResponse errorResponse = createDrsResponse(
//                "root",
//                500,
//                ex.getMessage(),
//                ex.getRequestID()
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
    public static String okHttpPost(String reqUrl, String json) {
        log.info("http request url={}, param={}", reqUrl, json);
        String json1 = RSAUtil.encrypt(json, ThreadLocalUtil.get(CUR_REQUEST_USER_PUBLIC_KEY));
        log.info("加密数据={}", json1);
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
            sslContext.init(null, trustAllCerts, new SecureRandom());

            // 设置 OkHttpClient 使用我们创建的 SSL 上下文
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            MediaType mediaType = MediaType.parse("application/json");
            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, json1);
            Request request = new Request.Builder()
                    .url(reqUrl)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();

            // 发送请求
            Response response = client.newCall(request).execute();
            String resBody = response.body().string();
            log.info("返回数据={}", resBody);
            log.info("key={}", DM_PRIVATE_KEY);
            String cleanResponse = resBody.trim()
                    .replaceAll("^\"|\"$", "") // 移除首尾的引号
                    .replaceAll("\\\\\"", "\"") // 处理转义的引号
                    .replaceAll("\\r", "")
                    .replaceAll("\\n", "")
                    .replaceAll("\\s+", "");
            log.info("cleanResponse={}", cleanResponse);
            String decode = RSAUtil.decode(cleanResponse, DM_PRIVATE_KEY);
            log.info("解密数据={}", decode);
            return decode;
        } catch (Exception e) {
            throw new RuntimeException("HTTP POST同步请求失败 URL:" + reqUrl, e);
        }
    }

//    public static void main(String[] args) {
//        String publicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt/ZsRRI2HHjWqePgJHVCM99Tmfh1Nd8qanUj3vbRVvhX92+if6g/uZ1cU86xnrSfMF0iAuvOAcOhljlkYbPqTNNffc5Jq7z/pV1FKPwDbhHSl0aC64j5SHsjrbmC8oZuhOy7GggmGF60E6r4v0R8ggUmQ7qFz9XMN6esLwDXsDO2knEfLLhw0+5sj7NaEnw1WqWDpPUQlfhLPJVUoighTsiZJ9lWV2VW3Xqi9Mqr3pHNQ02tUxYc+eqEk7rwxp0VcGvxAajCpbHYGWWqbIiRRjwtm6QneVWTeHYQKvWjIAb5CWBNqhEwK5CcV6rh7XVaCx4Tghly+UudmHrLbrJUEwIDAQAB";
//        ThreadLocalUtil.put(CUR_REQUEST_USER_PUBLIC_KEY, publicKeyBase64);
//        String url = "http://127.0.0.1:39090/drSouth/test";
//        String json = "{\"test\":\"testretsfrdgr\"}";
//        String s = okHttpPost(url, json);
//        System.out.println(s);
//    }
}
