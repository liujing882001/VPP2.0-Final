package com.example.gateway;

import com.alibaba.fastjson.JSONObject;
import com.example.gateway.entity.CommandUnit;
import com.example.gateway.profile.RPCRequestService;
import com.example.gateway.model.*;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpdomain.DeviceRepository;
import com.example.vvpdomain.SysParamRepository;
import com.example.vvpdomain.entity.Device;
import com.example.vvpdomain.entity.SysParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

//@EnableAsync
@Service("iotControlService")
public  class IotControlServiceImpl implements  IotControlService {

    @Resource
    RPCRequestService rpcRequestService;
    @Resource
    private DeviceRepository deviceRepository;

    @Resource
    private SysParamRepository sysParamRepository;
    /**
     * 通用IOT设备控制
     * methodTag  自定义一个标签  方便指令追踪来自哪个方法调用 例如 需求响应，空调控制，冷机控制
     * List<RPCModel> models 多个设备控制点集合
     * deviceSn  设备sn
     * List<PointInfo> 点位sn集合  属性sn，设置值
     * timestamp  当前时间   yyyy-MM-dd HH:mm:ss
     * */
//    @Async
    public void CommonRPCRequestToDevice(String methodTag,List<RPCModel> models) {

        if (models == null || models.size() == 0) {
            return;
        }

        for (RPCModel model : models) {
            try {
                if (model == null
                        || StringUtils.isEmpty(model.getDeviceSn())
                        || new Date().before(model.getTimestamp()) == true
                        || model.getPointInfoList() == null
                        || model.getPointInfoList().size() == 0) {
                    break;
                }
                SysParam sysParam = sysParamRepository.findSysParamBySysParamKey(SysParamEnum.IOTParam.getId());
                if (sysParam == null) {
                    break;
                }
                JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
                if (obj == null) {
                    break;
                }
                Device device = deviceRepository.findByDeviceSn(model.getDeviceSn());
                if (device == null) {
                    break;
                }
                Map<String, Object> pv = new HashMap();
                for (PointInfo point : model.getPointInfoList()) {
                    pv.put(point.getPointSn(), point.getValue());
                }

                //设备的 设备码，点位码，控制值
                List<CommandUnit> commandUnits = new ArrayList<>();

                CommandUnit unit = new CommandUnit();
                unit.setDeviceSn(model.getDeviceSn());
                unit.setPropValues(pv);
                unit.setProtocol(device.getDeviceProtocol());
                commandUnits.add(unit);


                Map<String, Object> sendMsgDto = new HashMap<>();
                sendMsgDto.put("method", methodTag);
                sendMsgDto.put("params", commandUnits);

                String message = JSONObject.toJSONString(sendMsgDto);

                rpcRequestService.pubDeviceCommands(device.getMecId(),  message);
//                Thread.sleep(50);
            } catch (Exception ex) {
            }
        }
    }
}
