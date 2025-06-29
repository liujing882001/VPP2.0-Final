package com.example.vvpweb.aliyun.message;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpdomain.alarm.info.AlarmInfoRepository;
import com.example.vvpservice.alarm.message.AliyunSmsThrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/aliyun003")
public class AliyunSmsThrController {
    @Autowired
    private AliyunSmsThrService aliyunSmsThrService;



    @PostMapping("/sendSmsLog003")
    public ResponseResult<String> sendSmsLog() {

        try {
            String phoneNumber = "15736889715";
            String timestamp = String.valueOf(LocalDateTime.now());
            String stationName = "stationName";
            String nodeName = "nodeName";
            String alarmInformation = "alarmInformation";
            String templateParam = aliyunSmsThrService.generateTemplateParam(timestamp, stationName, nodeName, alarmInformation);
            aliyunSmsThrService.sendSms(phoneNumber, templateParam);
            System.out.println("短信发送成功"+phoneNumber);
            return ResponseResult.success("告警通知发送成功");
        } catch (RuntimeException e) {
            return ResponseResult.error("短信发送失败，请联系管理员");
        }
    }


    /**
     * 最新的方法发送短信
     */
    @PostMapping("/sendAlarm")
    public ResponseResult<String> sendAlarm() {
        String nodeName = "储能电站节点";
        return aliyunSmsThrService.sendAlarmSms(nodeName);
    }


    /**
     * 最新的方法发送短信
     */
    @PostMapping("/sendAlarm008")
    public ResponseResult<String> sendAlarm003() {
        aliyunSmsThrService.newSendSms();
        return ResponseResult.success("短信");

    }
    /**
     * 最新的方法发送短信
     */
    @PostMapping("/sendAlarm009")
    public ResponseResult<String> sendAlarm009() {
        aliyunSmsThrService.newSendSms0003();
        return ResponseResult.success("短信");

    }
}
