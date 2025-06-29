package com.example.vvpservice.alarm.message;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpdomain.alarm.info.AlarmInfo;
import com.example.vvpdomain.alarm.info.AlarmInfoRepository;
import com.example.vvpservice.alarm.message.AlarmShortMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AliyunSmsThrService {

    @Autowired
    private AlarmInfoRepository alarmInfoRepository;

    // 您的阿里云访问密钥 ID - 请在生产环境中使用环境变量
    private static final String accessKeyId = System.getenv("ALIYUN_ACCESS_KEY_ID");
    // 您的阿里云访问密钥 Secret - 请在生产环境中使用环境变量
    private static final String accessKeySecret = System.getenv("ALIYUN_ACCESS_KEY_SECRET");

    public static final String ALARM_SMS_CODE = "SMS_472810132";

    public static final String RECOVERY_SMS_CODE = "SMS_472620133";

    public static String generateTemplateParam(String timestamp,String stationName, String nodeName, String alarmInformation) {
        Map<String,String> templateParam = new HashMap<>();
        templateParam.put("timestamp",timestamp);
        templateParam.put("station_name",stationName);
        templateParam.put("node_name",nodeName);
        templateParam.put("alarm_information",alarmInformation);
        return JSON.toJSONString(templateParam);
    }

    public static String generateTemplateParam(AlarmShortMessage message) {
        return JSON.toJSONString(message);
    }


    public void sendSms(String phoneNumber, String templateParam) {
        // 创建 IClientProfile 实例
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumber);
        request.setSignName("达卯智能");
        request.setTemplateCode("SMS_472810132");
        request.setTemplateParam(templateParam);

        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println("短信发送成功，请求 ID: " + response.getRequestId());
            System.out.println("短信发送成功，请求 message: " + response.getMessage());
        } catch (ClientException e) {
            System.out.println("短信发送失败，错误代码: " + e.getErrCode());
            System.out.println("错误消息: " + e.getErrMsg());
        }
    }

    public void sendSmsByCode(String phoneNumber, String templateParam, String TemplateCode){
        // 创建 IClientProfile 实例
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumber);
        request.setSignName("达卯智能");
        request.setTemplateCode(TemplateCode);
        request.setTemplateParam(templateParam);

        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println("短信发送成功，请求 ID: " + response.getRequestId());
            System.out.println("短信发送成功，请求 message: " + response.getMessage());
        } catch (ClientException e) {
            System.out.println("短信发送失败，错误代码: " + e.getErrCode());
            System.out.println("错误消息: " + e.getErrMsg());
        }

    }


    /**
     * 发送短信功能
     * @param nodeName
     * @return
     */
    public ResponseResult<String> sendAlarmSms(String nodeName) {
        try {
            AlarmInfo alarmInfo = alarmInfoRepository.findByNodeName(nodeName);
            String alarmInformation = alarmInfo.getAlarmContext();
            String stationName = alarmInfo.getStationName();
            Date startTime = alarmInfo.getStartTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(startTime);
            String templateParam = generateTemplateParam(timestamp, stationName, nodeName, alarmInformation);
            String phoneNumber = "15736889715";
            sendSms(phoneNumber,templateParam);
            System.out.println("短信发送成功"+phoneNumber);
            return ResponseResult.success("告警通知发送成功");
        } catch (RuntimeException e) {
            return ResponseResult.error("短信发送失败，请联系管理员");
        }
    }


    //第一个：最新模板--告警
    public static String generateTemplate(String timestamp, String stationName, String nodeName, String alarmInformation, String alarmLevel) {
        return "{\"timestamp\":\"" + timestamp + "\",\"station_name\":\"" + stationName + "\",\"node_name\":\"" + nodeName + "\",\"alarm_information\":\"" + alarmInformation + "\",\"alarm_level\":\"" + alarmLevel + "\"}";
    }

    //第2个：最新模板--恢复
    public static String newGenerateTemplate(String timestamp, String stationName, String nodeName, String alarmInformation, String alarmLevel) {
        return "{\"timestamp\":\"" + timestamp + "\",\"station_name\":\"" + stationName + "\",\"node_name\":\"" + nodeName + "\",\"alarm_information\":\"" + alarmInformation + "\",\"alarm_level\":\"" + alarmLevel + "\"}";
    }

    public void sendSms(String regionId, String signName, String templateCode, String phoneNumber, String templateParam) {
        try {
            // 创建 IClientProfile 实例
            DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            IAcsClient client = new DefaultAcsClient(profile);

            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(phoneNumber);
            request.setSignName(signName);
            request.setTemplateCode(templateCode);
            request.setTemplateParam(templateParam);

            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println("短信发送成功，请求 ID: " + response.getRequestId());
            System.out.println("短信发送成功，请求 message: " + response.getMessage());
        } catch (ClientException e) {
            System.out.println("短信发送失败，错误代码: " + e.getErrCode());
            System.out.println("错误消息: " + e.getErrMsg());
        }
    }


    //第3个： 告警
    public void newSendSms() {
        // 设置地域信息，根据实际情况修改
        String regionId = "cn-hangzhou";
        // 短信签名
        String signName = "达卯智能";
        // 短信模板 ID--这个是最新的模板
        String templateCode = "SMS_472810132";
        // 接收短信的手机号码
        String phoneNumber = "15736889715";

        String stationName = "达sdsaas";
        String nodeName = "达fdgdg";
        String alarmInformation = "dfgsfdg能";
        int alarmLevel1 = 1;
        String alarmLevel = String.valueOf(alarmLevel1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = simpleDateFormat.format(new Date());

        String templateParam = generateTemplate(timestamp, stationName, nodeName, alarmInformation, alarmLevel);

        sendSms(regionId, signName, templateCode, phoneNumber, templateParam);
    }


    //第3个：恢复
    public void newSendSms0003() {
        // 设置地域信息，根据实际情况修改
        String regionId = "cn-hangzhou";
        // 短信签名
        String signName = "达卯智能";
        // 短信模板 ID--这个是最新的模板
        String templateCode ="SMS_472620133";
        // 接收短信的手机号码
        String phoneNumber = "15736889715";

        String stationName = "达sdsaas";
        String nodeName = "达fdgdg";
        String alarmInformation = "dfgsfdg能";
        int alarmLevel1 = 1;
        String alarmLevel = String.valueOf(alarmLevel1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = simpleDateFormat.format(new Date());

        String templateParam = newGenerateTemplate(timestamp, stationName, nodeName, alarmInformation, alarmLevel);

        sendSms(regionId, signName, templateCode, phoneNumber, templateParam);
    }
}
