package com.example.gateway.profile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.gateway.entity.CommandContext;
import com.example.gateway.model.IotLoginRoot;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpcommom.HttpUtil;
import com.example.vvpcommom.MD5Utils;
import com.example.vvpcommom.RedisUtils;
import com.example.vvpdomain.SysParamRepository;
import com.example.vvpdomain.entity.SysParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("rpcRequestService")
public class RPCRequestServiceImpl implements RPCRequestService{

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private SysParamRepository sysParamRepository;
    //自定义lock key前缀
    private final static String LOCK_PREFIX = "LOCK:CUSTOMER_BALANCE";

    public boolean pubDeviceCommands(String mecId, String message) {

        String messageId = LOCK_PREFIX + "_" + mecId + "_" + MD5Utils.generateMD5(message);

        if (redisUtils != null && redisUtils.existOrNot(messageId)) {
            System.out.println("redis校验，设备命令已经执行，messageId" + messageId);
            return false;
        }

        SysParam sysParam = sysParamRepository.findSysParamBySysParamKey(SysParamEnum.IOTParam.getId());

        if (sysParam == null) {
            return false;
        }
        JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
        if (obj == null) {
            return false;
        }
        String iotAddress = obj.get("iotAddress").toString();
        String iotUserName = obj.get("iotUserName").toString();
        String iotUserPwd = obj.get("iotUserPwd").toString();

        if (!iotAddress.endsWith("/")) {
            iotAddress = iotAddress + "/";
        }
        String loginJson = HttpUtil.okHttpIotLoginPost(iotAddress, iotUserName, iotUserPwd);
        if (StringUtils.isEmpty(loginJson)) {
            System.out.println("登录失败");
            return false;
        }
        IotLoginRoot iotLoginRoot = JSON.parseObject(loginJson, IotLoginRoot.class);
        if (iotLoginRoot == null || StringUtils.isEmpty(iotLoginRoot.getToken())) {
            System.out.println("登录失败，loginJson:" + loginJson);
            return false;
        }
        int result = 0;
        int maxRetries = 10;
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            result = HttpUtil.okHttpIotRpcTwoWayPost(iotAddress, mecId, message, iotLoginRoot.getToken());
            if (result == 200) {
                log.info("下发成功,"+ mecId +"完成下发:" + message);
                break;
            } else {
                log.info("下发失败尝试重试，第 " + (attempt + 1) + "次" );
            }
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
        }
        if (result != 200) {
            log.info("下发"+ mecId +"失败，已达到最大重试次数" + message);
            return false;
        }
        return true;
    }


}
