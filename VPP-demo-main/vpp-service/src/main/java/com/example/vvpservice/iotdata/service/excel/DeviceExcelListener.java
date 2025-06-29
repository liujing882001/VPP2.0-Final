package com.example.vvpservice.iotdata.service.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.vvpcommom.FieldCheckUtil;
import com.example.vvpdomain.DevicePointRepository;
import com.example.vvpdomain.DeviceRepository;
import com.example.vvpdomain.SysDictDataRepository;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.iotdata.model.IotDevice;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * excel 导入设备信息
 */
public class DeviceExcelListener extends AnalysisEventListener<IotDevice> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceExcelListener.class);
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;
    private final Map<String, Device> deviceMap = new HashMap<>();
    private final List<DevicePoint> pointList = new ArrayList<>();

    private final List<String> deviceSnList = new ArrayList<>();

    private final DeviceRepository deviceRepository;
    private final Node node;
    private final SysDictType systemType;

    private final String loadProperties;
    private final String loadType;
    private final String deviceConfigKey;
    private final DevicePointRepository devicePointRepository;

    private final SysDictDataRepository sysDictDataRepository;
    private final double device_rated_power;


    private final StringBuffer errorMessage;

    public DeviceExcelListener(String loadType,String loadProperties,String configKey, Node node, SysDictType systemType, double device_rated_power,
                               DeviceRepository deviceRepository,
                               DevicePointRepository devicePointRepository,
                               SysDictDataRepository sysDictDataRepository,
                               StringBuffer errorMessage) {
        this.deviceConfigKey = configKey;
        this.loadType = loadType;
        this.loadProperties = loadProperties;
        this.node = node;
        this.systemType = systemType;
        this.deviceRepository = deviceRepository;
        this.devicePointRepository = devicePointRepository;
        this.device_rated_power = device_rated_power;
        this.sysDictDataRepository = sysDictDataRepository;
        this.errorMessage = errorMessage;
    }


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param device  one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(IotDevice device, AnalysisContext context) {

        if (device == null
                || StringUtils.isEmpty(device.getDeviceId())
                || StringUtils.isEmpty(device.getDeviceName())
                || StringUtils.isEmpty(device.getPropId())
                || StringUtils.isEmpty(device.getPropName())
                || StringUtils.isEmpty(device.getPropUnit())
                || StringUtils.isEmpty(device.getPropValue())) {

            return;
        }


        if (!FieldCheckUtil.checkObjAllFieldsIsNull(device)) {
            Device byDeviceSn = deviceRepository.findByDeviceSn(device.getDeviceId());

            if (byDeviceSn == null) {
                byDeviceSn = deviceMap.get(device.getDeviceId());
                if (byDeviceSn == null) {
                    byDeviceSn = new Device();
                    String id = device.getDeviceId();

                    byDeviceSn.setLoadType(loadType);
                    byDeviceSn.setLoadProperties(loadProperties);
                    byDeviceSn.setMecId(device.getMecId());
                    byDeviceSn.setDeviceId(id);
                    byDeviceSn.setDeviceSn(device.getDeviceId());
                    byDeviceSn.setDeviceBrand(device.getBrand());
                    byDeviceSn.setConfigKey(deviceConfigKey);
                    byDeviceSn.setDeviceName(device.getDeviceName());
                    byDeviceSn.setDeviceModel(device.getModel());
                    byDeviceSn.setDeviceLabel(device.getLocation());
                    byDeviceSn.setDeviceProtocol(device.getDeviceProtocol());
                    byDeviceSn.setNode(node);
                    byDeviceSn.setSystemType(systemType);
                    byDeviceSn.setOnline(false);
                    // byDeviceSn.setIsEnabled(false);
                    byDeviceSn.setCreatedTime(new Date());
                    byDeviceSn.setDeviceRatedPower(device_rated_power);

                    byDeviceSn.setMecOnline(false);

                    deviceMap.put(device.getDeviceId(), byDeviceSn);
                }

                DevicePoint dp = new DevicePoint();

                String point_id = byDeviceSn.getDeviceSn() + "_" + device.getPropId();

                dp.setPointId(point_id);
                dp.setDevice(byDeviceSn);
                dp.setDeviceSn(byDeviceSn.getDeviceSn());
                dp.setDeviceConfigKey(byDeviceSn.getConfigKey());
                dp.setPointSn(device.getPropId());
                dp.setPointName(device.getPropName());
                dp.setCreatedTime(new Date());
                dp.setPointUnit(device.getPropUnit());
                //点位打标签，写入参数唯一标识
                dp.setPointKey(StringUtils.isEmpty(device.getPropKey()) ? null : device.getPropKey());
                //点位打标签，写入参数键名
                dp.setPointDesc(device.getPropValue());
                pointList.add(dp);
            } else {
                byDeviceSn.setLoadType(loadType);
                byDeviceSn.setLoadProperties(loadProperties);
                byDeviceSn.setMecId(device.getMecId());
                byDeviceSn.setDeviceSn(device.getDeviceId());
                byDeviceSn.setDeviceBrand(device.getBrand());
                byDeviceSn.setConfigKey(deviceConfigKey);
                byDeviceSn.setDeviceName(device.getDeviceName());
                byDeviceSn.setDeviceModel(device.getModel());
                byDeviceSn.setDeviceLabel(device.getLocation());
                byDeviceSn.setDeviceProtocol(device.getDeviceProtocol());
                byDeviceSn.setNode(node);
                byDeviceSn.setSystemType(systemType);
                byDeviceSn.setOnline(false);
                // byDeviceSn.setIsEnabled(false);
                byDeviceSn.setCreatedTime(new Date());
                byDeviceSn.setDeviceRatedPower(device_rated_power);
                byDeviceSn.setMecOnline(false);
                deviceMap.put(device.getDeviceId(), byDeviceSn);
//                for (DevicePoint dp : byDeviceSn.getDevicePointList()) {
//                    if (dp.getPointId().equals(point_id)) {
//                        dp.setDevice(byDeviceSn);
//                        dp.setDeviceSn(byDeviceSn.getDeviceSn());
//                        dp.setDeviceConfigKey(byDeviceSn.getConfigKey());
//                        dp.setPointSn(device.getPropId());
//                        dp.setPointName(device.getPropName());
//                        dp.setCreatedTime(new Date());
//                        dp.setPointUnit(device.getPropUnit());
//                        //点位打标签，写入参数唯一标识
//                        dp.setPointKey(StringUtils.isEmpty(device.getPropKey()) ? device.getPropValue() : device.getPropKey());
//                        //点位打标签，写入参数键名
//                        dp.setPointDesc(device.getPropValue());
//                        pointList.add(dp);
//                    }
//                }
//                if (!deviceSnList.contains(byDeviceSn.getDeviceSn())) {
//                    deviceSnList.add(byDeviceSn.getDeviceSn());
//                }
//
                DevicePoint dp = new DevicePoint();
                String point_id = byDeviceSn.getDeviceSn() + "_" + device.getPropId();

                dp.setPointId(point_id);
                dp.setDevice(byDeviceSn);
                dp.setDeviceSn(byDeviceSn.getDeviceSn());
                dp.setDeviceConfigKey(byDeviceSn.getConfigKey());
                //点位打标签，写入参数唯一标识
                dp.setPointKey(StringUtils.isEmpty(device.getPropKey()) ? device.getPropValue() : device.getPropKey());
                dp.setPointSn(device.getPropId());
                dp.setPointName(device.getPropName());
                //点位打标签，写入参数键名
                dp.setPointDesc(device.getPropValue());
                dp.setPointUnit(device.getPropUnit());
                dp.setCreatedTime(new Date());
                pointList.add(dp);


            }

        }

        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (deviceMap.keySet().size() >= BATCH_COUNT) {
            doService();
            // 存储完成清理 list
            deviceMap.clear();
            pointList.clear();
        }
    }


    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        doService();
        LOGGER.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void doService() {

        if (pointList == null || pointList.isEmpty()) {
            errorMessage.append("请检查模板,不能从模板得到设备数据");
            return;
        }
        List<String> sysDictDataList = sysDictDataRepository.findAllModelKey();
        pointList.forEach(l -> {
            if (!sysDictDataList.contains(l.getPointDesc())) {
                errorMessage.append("请检查模板,deviceId(" + l.getDeviceSn() + ")的(" + l.getPointDesc() + ")未选择合法的参数键名;");
            }
        });


//        if("metering_device".equals(deviceConfigKey) && deviceMap.keySet().size()>1){
//            errorMessage.append("导入的计量设备不能超过一个");
//        }
        deviceRepository.saveAll(deviceMap.values());
        devicePointRepository.saveAll(pointList);
//        if (deviceSnList != null && !deviceSnList.isEmpty()) {
//            String collect = deviceSnList.stream().collect(Collectors.joining(","));
//            errorMessage.append("存在以下重复的deviceId (" + collect + "),请修改后重新导入;");
//        } else {
//            List<String> pointSn = new ArrayList<>();
//            pointList.stream().forEach(p -> {
//                pointSn.add(p.getPointSn());
//            });
//            deviceRepository.saveAll(deviceMap.values());
//            devicePointRepository.saveAll(pointList);
//
//            List<DevicePoint> points = devicePointRepository.findAllByPointSnIn(pointSn);
//            List<String> existPointSns = new ArrayList<>();
//            if (points != null && points.size() > 0) {
//                points.stream().forEach(e -> existPointSns.add(e.getPointSn()));
//            }
//            if (existPointSns != null && !existPointSns.isEmpty()) {
//                String collect = existPointSns.stream().collect(Collectors.joining(","));
//                errorMessage.append("存在以下重复的pointId (" + collect + "),请修改后重新导入;");
//            } else {
//                deviceRepository.saveAll(deviceMap.values());
//                devicePointRepository.saveAll(pointList);
//            }
//        }

    }

}