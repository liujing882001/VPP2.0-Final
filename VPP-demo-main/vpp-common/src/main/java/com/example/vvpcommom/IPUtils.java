package com.example.vvpcommom;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPUtils {
    private static final Logger logger = LoggerFactory.getLogger(IPUtils.class);

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
     *
     * @return ip
     */
    public static String getRealIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                logger.debug("Proxy-Client-IP header is empty or unknown");
            } else {
                logger.debug("Found IP from Proxy-Client-IP: {}", ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
                logger.debug("Found IP from WL-Proxy-Client-IP: {}", ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
                logger.debug("Found IP from HTTP_CLIENT_IP: {}", ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
                logger.debug("Found IP from HTTP_X_FORWARDED_FOR: {}", ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
                logger.debug("Found IP from X-Real-IP: {}", ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            logger.debug("Using RemoteAddr IP: {}", ip);
        }
        return ip;
    }

    public static String getLocalIp() {
        String hostAddress = "";
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("Error getting IP address", e);
        }
        return hostAddress;
    }
}