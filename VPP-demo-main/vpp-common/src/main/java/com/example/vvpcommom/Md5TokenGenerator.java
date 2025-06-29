package com.example.vvpcommom;

import org.springframework.util.DigestUtils;

/**
 * @author zph
 */

public class Md5TokenGenerator {

    public static String generate(String... strings) {
        long timestamp = System.currentTimeMillis();
        String tokenMeta = "";
        for (String s : strings) {
            tokenMeta = tokenMeta + s;
        }
        tokenMeta = tokenMeta + timestamp;
        String token = DigestUtils.md5DigestAsHex(tokenMeta.getBytes());
        return token;
    }
}