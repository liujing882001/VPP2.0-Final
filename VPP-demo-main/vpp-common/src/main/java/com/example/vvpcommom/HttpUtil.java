package com.example.vvpcommom;

import com.alibaba.fastjson.JSON;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author Zhaoph
 */
public class HttpUtil {

    private final static String CHARSET = "UTF-8";

    /**
     * OKHTTP  POST 请求
     *
     * @param reqUrl 地址
     * @param json   请求参数
     * @return
     */
    public static String okHttpPost(String reqUrl, String json) {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(reqUrl)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException("HTTP POST同步请求失败 URL:" + reqUrl, e);
        }
    }

    public static String okHttpGet(String reqUrl) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url(reqUrl)
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException("HTTP GET同步请求失败 URL:" + reqUrl, e);
        }
    }

    public static String sign(String s, String key, String method) throws Exception {
        Mac mac = Mac.getInstance(method);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(CHARSET), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(s.getBytes(CHARSET));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static String getRandomString() {
        String str = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
        StringBuffer stringBuffer = new StringBuffer();
        for (int j = 0; j < 10; j++) {
            int index = new Random().nextInt(str.length());
            char c = str.charAt(index);
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    public static String okHttpGetWithHeadParams(String reqUrl, Map<String,Object> headerParams) {

        OkHttpClient client =
                new OkHttpClient().newBuilder()
                        .build();
        Request.Builder url = new Request.Builder()
                .url(reqUrl);
        headerParams.keySet().forEach(l-> url.addHeader(l, String.valueOf(headerParams.get(l))));
        Request request =  url.get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException("HTTP GET URL:" + reqUrl, e);
        }
    }

    public static String okHttpIotLoginPost(String url, String userName, String passWord) {

        TreeMap<String, Object> loginParams = new TreeMap<String, Object>(); // TreeMap可以自动排序
        loginParams.put("username", userName); // 公共参数
        loginParams.put("password", passWord); // 公共参数

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(loginParams));
        Request request = new Request.Builder()
                .url(url + "api/auth/login")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException("HTTP POST同步请求失败 URL:" + url, e);
        }
    }

    public static String okHttpIotRpcPost(String reqUrl, String deviceId, String json, String token) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(reqUrl + "api/plugins/rpc/oneway/" + UUID.fromString(deviceId))
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("X-Authorization", "Bearer  " + token)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            //throw new RuntimeException("HTTP POST同步请求失败 URL:" + reqUrl, e);
            return null;
        }
    }


    public static int okHttpIotRpcTwoWayPost(String reqUrl, String deviceId, String json, String token) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(reqUrl + "api/plugins/rpc/twoway/" + UUID.fromString(deviceId))
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("X-Authorization", "Bearer  " + token)
                    .build();

            Response response = client.newCall(request).execute();
            return response.code();
        } catch (Exception e) {
            //throw new RuntimeException("HTTP POST同步请求失败 URL:" + reqUrl, e);
            return 0;
        }
    }
}
