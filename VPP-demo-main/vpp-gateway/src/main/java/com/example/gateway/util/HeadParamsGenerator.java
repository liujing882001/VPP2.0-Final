package com.example.gateway.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HeadParamsGenerator {

    public static Map<String,Object> generate(String token,String password,String subUrl){
        Map<String,Object> params = new HashMap<>();
        long date = xAcDate();
        long nonce = xAcNonce();
        params.put("X-AC-Token",token);
        params.put("X-Date",date);
        params.put("X-AC-Nonce",nonce);
        params.put("X-AC-Signature",signature(password,date,nonce,subUrl));

//        System.out.println("请求数据header信息："+ JSONObject.toJSONString(params));

        return params;
    }

    /**
     * ①　时间戳为30分钟之内有效，取毫秒的格式；
     * @return
     */
    private static long xAcDate(){
        return System.currentTimeMillis();
    }

    /**
     * 是随机数即X-AC-None；
     * @return
     */
    private static int xAcNonce(){
        return new Random().nextInt(10000);
    }

    private static String signature(String password,long date,long nonce,String url){
        return new String(DigestUtils.md5DigestAsHex((password+"\n"+date+"\n"+nonce+"\n"+url).getBytes()));
    }

    public static void main(String[] args) {
        System.out.println(JSONObject.toJSONString(generate("1a9d6d73c1138257e09c6c603c134b19",
                "b2674f8cbecee1f5435d654b04919901",
                "/tob-data-cldas/v1/data/points")));
    }
}
