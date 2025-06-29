package com.example.vvpservice.device;

import com.example.vvpdomain.entity.Device;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DeviceService {

    Page<Device> devicePageByNodeId(String nodeId,Integer number,Integer pageSize);
    Page<Device> devicePageByNodeIds(List<String> nodeIds, Integer number, Integer pageSize);


}
