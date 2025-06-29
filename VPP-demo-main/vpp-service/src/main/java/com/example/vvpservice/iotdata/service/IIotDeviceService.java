package com.example.vvpservice.iotdata.service;


import com.example.vvpservice.iotdata.model.IotDevicePointDataView;
import com.example.vvpservice.iotdata.model.IotDeviceView;

import java.io.InputStream;
import java.util.List;

public interface IIotDeviceService {

    List<IotDeviceView> devicesOfNodeAndSystem(String nodeId, String systemId);

    void deviceImportByExcel(InputStream inputStream, String configKey, String nodeId, String systemId, double device_rated_power,String loadType,String loadProperties);

    List<IotDevicePointDataView> getPointByNodeIdOrSystemIdOrDeviceId(String nodeId, String systemId, String deviceId);

}
