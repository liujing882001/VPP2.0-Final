package com.example.vvpservice.controlservice;

import java.util.List;

public interface IDeviceControl {

    /*
     * 任务调度反射
     * 发送设备控制命令消息 控制空调和灯光设备等
     * @param strategyId 策略ID
     */
    boolean sendDeviceControlCommandMessage(String strategyId);
    void sendDeviceControlCommandMessageDemand(List<String> strategyId, int respType, int dStatus,String respId);

}
