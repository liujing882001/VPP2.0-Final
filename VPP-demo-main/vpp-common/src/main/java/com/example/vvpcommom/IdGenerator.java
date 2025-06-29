package com.example.vvpcommom;

import java.util.UUID;

/**
 * @author Zhaoph
 */
public class IdGenerator {

    public static String generateId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String md5Id(String arg) {
        return MD5Utils.generateMD5(arg);
    }

    public static String concatString(String arg1, String arg2) {
        return arg1 + "_" + arg2;
    }
}
