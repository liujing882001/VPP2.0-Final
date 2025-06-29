package com.example.gateway;

import com.example.gateway.model.PointInfo;
import com.example.gateway.model.RPCModel;
import com.example.gateway.model.Strategy96Model;

import java.util.List;

public interface IotControlService {

    /**
     * 通用IOT设备控制
     * methodTag  自定义一个标签  方便指令追踪来自哪个方法调用 例如 需求响应，空调控制，冷机控制
     * List<RPCModel> models 多个设备控制点集合
     * deviceSn  设备sn
     * List<PointInfo> 点位sn集合  属性sn，设置值
     * timestamp  当前时间   yyyy-MM-dd HH:mm:ss
     * */
     void CommonRPCRequestToDevice(String methodTag,List<RPCModel> models);
}
