package com.example.vvpservice.iotdata.service;


import com.alibaba.excel.EasyExcel;
import com.example.vvpcommom.FieldCheckUtil;
import com.example.vvpcommom.FieldConvertUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.Device;
import com.example.vvpdomain.entity.DevicePoint;
import com.example.vvpdomain.entity.Node;
import com.example.vvpdomain.entity.SysDictType;
import com.example.vvpservice.iotdata.model.IotDevice;
import com.example.vvpservice.iotdata.model.IotDevicePointDataView;
import com.example.vvpservice.iotdata.model.IotDeviceView;
import com.example.vvpservice.iotdata.service.excel.DeviceExcelListener;
import com.example.vvpservice.prouser.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IotDeviceServiceImpl implements IIotDeviceService {

    private static Logger logger = LoggerFactory.getLogger(IotDeviceServiceImpl.class);
    @Autowired
    private DeviceRepository deviceRepository;

    @Resource
    private DevicePointRepository devicePointRepository;
    @Resource
    private NodeRepository nodeRepository;

    @Resource
    private SysDictTypeRepository sysDictTypeRepository;

    @Resource
    private SysDictDataRepository sysDictDataRepository;
    @Resource
    private IUserService userService;


    @Override
    public List<IotDeviceView> devicesOfNodeAndSystem(String nodeId, String systemId) {
        List<IotDeviceView> result = new ArrayList<>();

        List<Device> allByNode_nodeIdAndSystemType_systemId = deviceRepository.findAllByNode_NodeIdAndSystemType_SystemId(nodeId, systemId);

        return getIotDeviceViews(result, allByNode_nodeIdAndSystemType_systemId);
    }

    private List<IotDeviceView> getIotDeviceViews(List<IotDeviceView> result, List<Device> allByNode_nodeIdAndSystemType_systemId) {
        allByNode_nodeIdAndSystemType_systemId.forEach(e -> {
            IotDeviceView view = new IotDeviceView();
            view.setDeviceId(e.getDeviceId());
            view.setDeviceSn(e.getDeviceSn());
            view.setDeviceBrand(e.getDeviceBrand());
            view.setDeviceLabel(e.getDeviceLabel());
            view.setDeviceModel(e.getDeviceModel());
            view.setDeviceName(e.getDeviceName());
            view.setNodeName(e.getNode().getNodeName());
            view.setSystemName(e.getSystemType().getSystemName());
            view.setConfigKey(e.getConfigKey());
            view.setSystemId(e.getSystemType().getSystemId());
            view.setDeviceRatedPower(e.getDeviceRatedPower());//add by maoyating
            List<DevicePoint> devicePointList = e.getDevicePointList();
            devicePointList.forEach(l -> {
                IotDeviceView.IotDevicePointView pv = new IotDeviceView.IotDevicePointView();
                pv.setPointId(l.getPointId());
                pv.setPointSn(l.getPointSn());
                pv.setPointDesc(l.getPointDesc());
                pv.setPointName(l.getPointName());

                view.getPointViewList().add(pv);
            });

            result.add(view);
        });

        return result;
    }


    @Override
    public void deviceImportByExcel(InputStream inputStream, String configKey, String nodeId, String systemId, double device_rated_power,String loadType,String loadProperties) {


        Node node = nodeRepository.findById(nodeId).orElse(null);
        SysDictType systemType = sysDictTypeRepository.findById(systemId).orElse(null);
        if (node != null && systemType != null) {
            StringBuffer errorMessage = new StringBuffer();
            EasyExcel.read(inputStream,
                    IotDevice.class,
                    new DeviceExcelListener(loadType,
                            loadProperties,
                            configKey,
                            node,
                            systemType,
                            device_rated_power,
                            deviceRepository,
                            devicePointRepository,
                            sysDictDataRepository,
                            errorMessage))
                    .sheet().doRead();
            if (errorMessage.toString().length() > 0) {
                throw new IllegalArgumentException(errorMessage.toString());
            }
        }

    }

    @Override
    public List<IotDevicePointDataView> getPointByNodeIdOrSystemIdOrDeviceId(String nodeId, String systemId, String deviceId) {
        List<IotDevicePointDataView> ipdv = new ArrayList<>();
        List<DevicePoint> result = new ArrayList<>();

        if (FieldCheckUtil.checkStringNotEmpty(deviceId)) {
            result = devicePointRepository.findAllByDevice_DeviceId(deviceId);
        } else {
            if (FieldCheckUtil.checkStringNotEmpty(nodeId)) {
                Optional<Node> byId = nodeRepository.findById(nodeId);
                boolean present = byId.isPresent();
                if (present) {
                    Node node = byId.get();
                    List<Device> deviceList = node.getDeviceList();
                    List<DevicePoint> finalResult = result;

                    if (FieldCheckUtil.checkStringNotEmpty(systemId)) {
                        deviceList.forEach(e -> {
                            if (e.getSystemType().getSystemId().equals(systemId)) {
                                finalResult.addAll(e.getDevicePointList());
                            }
                        });
                    } else {
                        deviceList.forEach(e -> finalResult.addAll(e.getDevicePointList()));
                    }


                }
            } else {
                // TODO 查询用户权限下的所有节点的所有设备点位信息  现在默认查所有

                List<Device> allByNode_nodeIdIn = deviceRepository.findAllByNode_NodeIdIn(userService.getAllowNodeIds());
                List<String> deviceSns = allByNode_nodeIdIn.stream().map(Device::getDeviceSn).collect(Collectors.toList());
                result = devicePointRepository.findAllByDeviceSnIn(deviceSns);

            }
        }

        if (result != null && !result.isEmpty()) {
            result.forEach(e -> {
                Device device = e.getDevice();
                Node node = device.getNode();
                SysDictType systemType = device.getSystemType();

                IotDevicePointDataView v = new IotDevicePointDataView();
                v.setDeviceName(device.getDeviceName());
                v.setNodeName(node.getNodeName());
                v.setPointName(e.getPointName());
                v.setSystemName(systemType.getSystemName());
                v.setPointSn(e.getPointSn());
                v.setPointUnit(e.getPointUnit());
                v.setProvinceRegionName(node.getProvinceRegionName());
                v.setCountyRegionName(node.getCountyRegionName());
                v.setCityRegionName(node.getCityRegionName());
                v.setPointDesc(e.getPointDesc());

                v.setLoadType(FieldConvertUtil.convertLoadType(device.getLoadType()));
                v.setLoadProperties(FieldConvertUtil.convertLoadProperties(device.getLoadProperties()));

                ipdv.add(v);
            });
        }

        return ipdv;
    }


}
