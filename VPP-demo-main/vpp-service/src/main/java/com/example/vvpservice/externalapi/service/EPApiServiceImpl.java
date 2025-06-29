package com.example.vvpservice.externalapi.service;
import java.util.Date;
import java.time.Instant;

import com.alibaba.fastjson.JSON;
import com.example.vvpdomain.DemandCalendarRepository;
import com.example.vvpdomain.PointModelMappingRepository;
import com.example.vvpdomain.PointModelRepository;
import com.example.vvpdomain.entity.DemandCalendar;
import com.example.vvpdomain.entity.PointModelMapping;
import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.externalapi.model.AvailVppDataDTO;
import com.example.vvpservice.externalapi.model.DataType;
import com.example.vvpservice.externalapi.model.VppData;
import com.example.vvpservice.externalapi.model.VppDataDTO;
import com.example.vvpservice.point.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class EPApiServiceImpl implements EPApiService {

    @Resource
    DemandCalendarRepository demandCalendarRepository;
    @Resource
    PointModelMappingRepository pointModelMappingRepository;
    @Resource
    PointModelRepository pointModelRepository;
    @Resource
    PointService pointService;


    @Override
    public List<DemandCalendar> findByDateBetween(Date sDate, Date eDate) {
        LocalDate localSDate = sDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localEDate = eDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        sDate = Date.from(localSDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        eDate = Date.from(localEDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return demandCalendarRepository.findByDateBetween(sDate, eDate);
    }
    @Override
    public List<AvailVppDataDTO> getAvailVppData(List<String> nodeIds) {
        List<PointModelMapping> mappings = pointModelMappingRepository.findAllByStationIdAndMappingType(nodeIds,"Algorithm");
        Set<StationNode> stationNodes = new HashSet<>();
        Map<String,List<DataType>> mappingMap = new HashMap<>();
        mappings.forEach(v -> {
            StationNode stationNode = v.getStation();
            String stationId = stationNode.getStationId();
            stationNodes.add(stationNode);
            if (mappingMap.get(stationId) == null) {
                mappingMap.put(stationId,new ArrayList<>());
                mappingMap.get(stationId).add(new DataType(v,v.getPointModel()));
            }
        });
//        log.info("mappings:{}", JSON.toJSON(mappings));
//        log.info("mappings:{}", mappings.size());
//        log.info("stationNodes:{}", JSON.toJSON(stationNodes));
//        log.info("stationNodes:{}", stationNodes.size());
//        log.info("mappingMap:{}", JSON.toJSON(mappingMap));
//        log.info("mappingMap:{}", mappingMap.size());

        return buildTree(new ArrayList<>(stationNodes),mappingMap);

    }
    public static List<AvailVppDataDTO> buildTree(List<StationNode> stationNodes,Map<String,List<DataType>> mappingMap) {
        Map<String, AvailVppDataDTO> nodeMap = new HashMap<>();
        List<AvailVppDataDTO> rootNodes = new ArrayList<>();
        for (StationNode stationNode : stationNodes) {
            nodeMap.put(stationNode.getStationId(), new AvailVppDataDTO(stationNode.getStationId(),stationNode.getStationName(),mappingMap.get(stationNode.getStationId())));
        }
        for (StationNode stationNode : stationNodes) {
            AvailVppDataDTO childNode = nodeMap.get(stationNode.getStationId());
            if (stationNode.getParentId() == null || stationNode.getParentId().isEmpty()) {
                rootNodes.add(childNode);
            } else {
                AvailVppDataDTO parentNode = nodeMap.get(stationNode.getParentId());
                parentNode.getChildren().add(childNode);
            }
        }
        return rootNodes;
    }
    @Override
    public VppDataDTO getVppDataList(String dataId, LocalDateTime sLTime, LocalDateTime eLTime) {
        Date sDate = Date.from(sLTime.atZone(ZoneId.systemDefault()).toInstant());
        Date eDate = Date.from(eLTime.atZone(ZoneId.systemDefault()).toInstant());
        VppDataDTO vppDataDTO = new VppDataDTO();
        Map<Date, ?> res = pointService.getDValuesByTime(dataId, sDate, eDate);
        List<VppData> vppDataList = new ArrayList<>();
        for (Map.Entry<Date, ?> entry : res.entrySet()) {
            Date date = entry.getKey();
            Object value = entry.getValue();
            VppData vppData = new VppData();
            vppData.setTime(date);
            vppData.setValue(value);
            vppDataList.add(vppData);
        }
        vppDataDTO.setDataId(dataId);
        vppDataDTO.setDataList(vppDataList);

        return vppDataDTO;
    }
    @Override
    public VppDataDTO getVppDataListNow(String dataId, LocalDateTime sLTime, LocalDateTime eLTime) {
        Date sDate = Date.from(sLTime.atZone(ZoneId.systemDefault()).toInstant());
        Date eDate = Date.from(eLTime.atZone(ZoneId.systemDefault()).toInstant());
        VppDataDTO vppDataDTO = new VppDataDTO();
        Map<Date, ?> res = pointService.getValuesByTime(dataId, sDate, eDate);
        List<VppData> vppDataList = new ArrayList<>();
        for (Map.Entry<Date, ?> entry : res.entrySet()) {
            Date date = entry.getKey();
            Object value = entry.getValue();
            VppData vppData = new VppData();
            vppData.setTime(date);
            vppData.setValue(value);
            vppDataList.add(vppData);
        }
        vppDataDTO.setDataId(dataId);
        vppDataDTO.setDataList(vppDataList);

        return vppDataDTO;
    }
}
