package com.example.vvpcommom;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.util.UUID;

public class JwtUtil {

    public static String getToken(String name, String pwd) {
        CacheUtils.putTokenArg(name, UUID.randomUUID().toString());
        String token = "";
        token = JWT.create().withAudience(CacheUtils.getTokenArg(name))
                .sign(Algorithm.HMAC256(pwd));
        return token;
    }

    public static void validateToken(String token, String name, String pwd) {
        String strFlag;
        try {
            strFlag = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new AuthException("解析token错误");
        }

        if (!strFlag.equals(CacheUtils.getTokenArg(name))) {
            throw new AuthException("用户token错误");
        }
        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(pwd)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new AuthException("验证token错误");
        }
    }

}
