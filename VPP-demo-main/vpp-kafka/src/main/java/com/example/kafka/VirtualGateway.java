package com.example.kafka;

import com.alibaba.fastjson.JSONObject;
import com.example.kafka.model.*;
import com.example.vvpcommom.HttpUtil;
import com.example.vvpcommom.StringUtils;
import com.example.vvpcommom.event.KafkaRegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VirtualGateway implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualGateway.class);
    private static final String demandResponseDayInfo = "demand_response_day_info";
    @Value("${server.port}")
    private int port;


    /**
     * 采集节点下实时可调负荷
     */
    @EventListener(value = KafkaRegisterEvent.class)
    public void registryEventInitAccountHandler(KafkaRegisterEvent registryEvent) {

        String topic = registryEvent.getTopic();
        String message = registryEvent.getMessage();
        String iotDataUrl = "http://localhost:" + port + "/platFrom/dataTransfer";

        if (demandResponseDayInfo.equals(topic) && StringUtils.isEmpty(message) == false) {

            DemandResponseDayInfoModel model = JSONObject.parseObject(message, DemandResponseDayInfoModel.class);

            if (model != null && model.getTotalSize() > 0
                    && model.getData() != null
                    && model.getData().size() > 0) {

                List<DayInfoModel> dataItems = model.getData();

                for (DayInfoModel dayInfo : dataItems) {

                    if (dayInfo != null) {
                        //可响应负荷
                        String deviceId = dayInfo.getMeterAccountNumber();
                        Object availableValue = dayInfo.getAvailableValue();
                        //基站当前总负荷
                        String deviceTotalId = dayInfo.getMeterAccountNumber()+"_NYZB";
                        Object availableTotalValue = dayInfo.getAllValue();
                        //时间戳
                        long time = dayInfo.getTime();

                        sendMsg(iotDataUrl,deviceId,deviceId,availableValue,time);
                        sendMsg(iotDataUrl,deviceTotalId,deviceTotalId,availableTotalValue,time);

                    }
                }
            }
        }
    }


    private  void sendMsg(String iotDataUrl,String deviceId,String propId,Object value,long time){


        IotDataOriginalModel iotDataOriginalModel = new IotDataOriginalModel();

        iotDataOriginalModel.setSystem("");
        iotDataOriginalModel.setType("MSG");
        iotDataOriginalModel.setDatatime(time);
        iotDataOriginalModel.setDeviceId(deviceId);
        iotDataOriginalModel.setDdccode(propId);
        DataIotType dataIotType = new DataIotType();
        dataIotType.setValuetype("3");
        dataIotType.setValue(value);
        iotDataOriginalModel.setData(dataIotType);
        iotDataOriginalModel.setTagcode(iotDataOriginalModel.hashCode());

        Map<String, String> data = new HashMap<>();
        data.put(iotDataOriginalModel.getDeviceId() + "_" + iotDataOriginalModel.getDdccode(), JSONObject.toJSONString(iotDataOriginalModel));
        //发送数据到API接口
        HttpUtil.okHttpPost(iotDataUrl, JSONObject.toJSONString(data));

        LOGGER.info("[接收信息] 发送数据到API接口 基站负荷上报,  data:{}", JSONObject.toJSONString(data));
    }
}
