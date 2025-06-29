package com.example.vvpservice.tree.service;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.Enum.SystemTypeEnum;
import com.example.vvpcommom.StringUtils;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.tree.model.StructTreeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TreeLabelServiceImpl implements ITreeLabelService {

    private static final Logger logger = LoggerFactory.getLogger(TreeLabelServiceImpl.class);
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SysDictTypeRepository sysDictTypeRepository;

    @Autowired
    private SysDictNodeRepository sysDictNodeRepository;

    @Autowired
    private IUserService userService;
    @Autowired
    private ScheduleStrategyViewRepository scheduleStrategyViewRepository;

    @Override
    public List<StructTreeResponse> deviceShortView() {
        return nodeTreeStructWithSystem(userService.getAllowNodeIds(), false);
    }

    @Override
    public List<StructTreeResponse> loadForestShortView() {
        return nodeTreeStructWithSystem(userService.getAllowLoadNodeIds(), true);
    }

    @Override
    public List<StructTreeResponse> runLoadForestShortView() {
        return nodeTreeStructWithSystem(userService.getAllowRunLoadNodeIds(), true);
    }
    @Override
    public List<StructTreeResponse> pvForestShortView() {
        return nodeTreeStructWithSystem(userService.getAllowPvNodeIds(), true);
    }
    @Override
    public List<StructTreeResponse> runPvForestShortView() {
        return nodeTreeStructWithSystem(userService.getAllowRunPvNodeIds(), true);
    }

    @Override
    public List<StructTreeResponse> loadShortView() {
        return nodeTreeStructWithSystem(userService.getAllowLoadNodeIds(), true);
    }

    @Override
    public List<StructTreeResponse> loadNoSystemId_NY_ShortView() {
        List<StructTreeResponse> responses = new ArrayList<>();
        List<Node> all = nodeRepository.findAllByNodeIdIn(userService.getAllowLoadNodeIds());

        List<SysDictType> sysDictTypeAll = sysDictTypeRepository.findAll();

        all.stream().forEach(e -> {
            List<String> syss = JSONObject.parseArray(e.getSystemIds(), String.class);
            List<SysDictType> sysDictTypes = new ArrayList<>();
            sysDictTypeAll.forEach(l -> {
                if (syss.contains(l.getSystemId()) && !SystemTypeEnum.nengyuanzongbiao.getId().equals(l.getSystemId())) {
                    sysDictTypes.add(l);
                }
            });
            responses.add(getByNode(e, true, sysDictTypes));
        });

        return responses;
    }

    @Override
    public List<StructTreeResponse> runLoadNoSystemId_NY_ShortView() {
        List<StructTreeResponse> responses = new ArrayList<>();
        List<Node> all = nodeRepository.findAllByNodeIdIn(userService.getAllowRunLoadNodeIds());

        List<SysDictType> sysDictTypeAll = sysDictTypeRepository.findAll();

        all.stream().forEach(e -> {
            List<String> syss = JSONObject.parseArray(e.getSystemIds(), String.class);
            List<SysDictType> sysDictTypes = new ArrayList<>();
            sysDictTypeAll.forEach(l -> {
                if (syss.contains(l.getSystemId()) && !SystemTypeEnum.nengyuanzongbiao.getId().equals(l.getSystemId())) {
                    sysDictTypes.add(l);
                }
            });
            responses.add(getByNode(e, true, sysDictTypes));
        });

        return responses;
    }

    private List<StructTreeResponse> ofNodeAreaResponse(List<StructTreeResponse> nodeResponse) {
        Map<String, StructTreeResponse> resultMap = new HashMap<>();

        Map<String, List<StructTreeResponse>> nodeMap = nodeResponse.stream().collect(Collectors.groupingBy(StructTreeResponse::getId));
        List<Node> allByNodeIdIn = nodeRepository.findAllByNodeIdIn(new ArrayList<>(nodeMap.keySet()));

        allByNodeIdIn.forEach(e -> {

            String provinceRegionId = e.getProvinceRegionId();
            String provinceRegionName = e.getProvinceRegionName();

            StructTreeResponse structTreeResponse = resultMap.get(provinceRegionId);

            if (structTreeResponse == null) {
                structTreeResponse = new StructTreeResponse();
                structTreeResponse.setId(provinceRegionId);
                structTreeResponse.setKey(provinceRegionId);
                structTreeResponse.setTitle(provinceRegionName);
                structTreeResponse.setType("PROVINCE");

                String cityRegionId = e.getCityRegionId();
                String cityRegionName = e.getCityRegionName();
                StructTreeResponse city = new StructTreeResponse();
                city.setId(cityRegionId);
                city.setKey(cityRegionId);
                city.setTitle(cityRegionName);
                city.setType("CITY");

                String countyRegionId = e.getCountyRegionId();
                String countyRegionName = e.getCountyRegionName();
                StructTreeResponse county = new StructTreeResponse();
                county.setId(countyRegionId);
                county.setKey(countyRegionId);
                county.setTitle(countyRegionName);
                county.setType("COUNTY");

                structTreeResponse.getChildren().add(city);

                city.getChildren().add(county);

                resultMap.put(provinceRegionId, structTreeResponse);
            } else {

                List<StructTreeResponse> collect = structTreeResponse.getChildren().stream().filter(l -> l.getId().equals(e.getCityRegionId())).collect(Collectors.toList());
                if (collect.isEmpty()) {
                    String cityRegionId = e.getCityRegionId();
                    String cityRegionName = e.getCityRegionName();
                    StructTreeResponse city = new StructTreeResponse();
                    city.setId(cityRegionId);
                    city.setKey(cityRegionId);
                    city.setTitle(cityRegionName);
                    city.setType("CITY");

                    String countyRegionId = e.getCountyRegionId();
                    String countyRegionName = e.getCountyRegionName();
                    StructTreeResponse county = new StructTreeResponse();
                    county.setId(countyRegionId);
                    county.setKey(countyRegionId);
                    county.setTitle(countyRegionName);
                    county.setType("COUNTY");

                    city.getChildren().add(county);

                    structTreeResponse.getChildren().add(city);


                } else {
                    StructTreeResponse city = collect.get(0);

                    List<StructTreeResponse> cc = city.getChildren().stream().filter(l -> l.getId().equals(e.getCountyRegionId())).collect(Collectors.toList());
                    if (cc.isEmpty()) {

                        String countyRegionId = e.getCountyRegionId();
                        String countyRegionName = e.getCountyRegionName();
                        StructTreeResponse county = new StructTreeResponse();
                        county.setId(countyRegionId);
                        county.setKey(countyRegionId);
                        county.setTitle(countyRegionName);
                        county.setType("COUNTY");

                        city.getChildren().add(county);

                    }

                }


            }

            nodeCatchSet(structTreeResponse, e.getCityRegionId(), e.getCountyRegionId(), nodeMap.get(e.getNodeId()));


        });


        return new ArrayList<>(resultMap.values());

    }

    private void nodeCatchSet(StructTreeResponse provinceResponse, String cityRegionId, String countyRegionId, List<StructTreeResponse> structTreeRespons) {

        for (StructTreeResponse e : structTreeRespons) {
            provinceResponse.setDeviceSize(provinceResponse.getDeviceSize() + e.getDeviceSize());

            provinceResponse.getChildren().stream().filter(l -> l.getId().equals(cityRegionId)).forEach(l -> {
                if (countyRegionId == null || "".equals(cityRegionId.trim())) {
                    nodeSet(l, e);
                } else {
                    l.setDeviceSize(l.getDeviceSize() + e.getDeviceSize());
                    l.getChildren().stream().filter(n -> n.getId().equals(countyRegionId)).forEach(n -> nodeSet(n, e));
                }

            });

        }


    }

    private List<StructTreeResponse> ofNodeTypeResponse(List<StructTreeResponse> nodeResponse) {

        Map<String, StructTreeResponse> resultMap = new HashMap<>();

        Map<String, List<StructTreeResponse>> nodeMap = nodeResponse.stream().collect(Collectors.groupingBy(StructTreeResponse::getId));

        List<Node> allByNodeIdIn = nodeRepository.findAllByNodeIdIn(new ArrayList<>(nodeMap.keySet()));

        allByNodeIdIn.forEach(el -> {
            SysDictNode nodeType = el.getNodeType();
            if (nodeType != null) {

                StructTreeResponse structTreeResponse = resultMap.get(nodeType.getNodeTypeId());

                if (structTreeResponse == null) {
                    structTreeResponse = new StructTreeResponse();
                    structTreeResponse.setId(nodeType.getNodeTypeId());
                    structTreeResponse.setKey(nodeType.getNodeTypeId());
                    structTreeResponse.setTitle(nodeType.getNodeTypeName());
                    structTreeResponse.setType("NODETYPE");

                    resultMap.put(nodeType.getNodeTypeId(), structTreeResponse);
                }
                nodeSet(structTreeResponse, nodeMap.get(el.getNodeId()));
            }

        });


        return new ArrayList<>(resultMap.values());

    }

    @Override
    public List<StructTreeResponse> areaDeviceShortView() {
        return ofNodeAreaResponse(deviceShortView());
    }

    @Override
    public List<StructTreeResponse> areaLoadForestShortView() {
        return ofNodeAreaResponse(loadForestShortView());
    }

    public List<StructTreeResponse> runAreaLoadForestShortView() {
        return ofNodeAreaResponse(runLoadForestShortView());
    }

    @Override
    public List<StructTreeResponse> areaPvForestShortView() {
        return ofNodeAreaResponse(pvForestShortView());
    }
    @Override
    public List<StructTreeResponse> runAreaPvForestShortView() {
        return ofNodeAreaResponse(runPvForestShortView());
    }

    @Override
    public List<StructTreeResponse> areaLoadShortView() {
        return ofNodeAreaResponse(loadShortView());
    }

    @Override
    public List<StructTreeResponse> areaLoadNoSystemId_NY_ShortView() {
        return ofNodeAreaResponse(loadNoSystemId_NY_ShortView());
    }

    @Override
    public List<StructTreeResponse> runAreaLoadNoSystemId_NY_ShortView() {
        return ofNodeAreaResponse(runLoadNoSystemId_NY_ShortView());
    }

    @Override
    public List<StructTreeResponse> typeDeviceShortView() {
        return ofNodeTypeResponse(deviceShortView());
    }

    @Override
    public List<StructTreeResponse> typeLoadForestShortView() {
        return ofNodeTypeResponse(loadForestShortView());
    }

    @Override
    public List<StructTreeResponse> typePvForestShortView() {
        return ofNodeTypeResponse(pvForestShortView());
    }

    @Override
    public List<StructTreeResponse> typeLoadShortView() {
        return ofNodeTypeResponse(loadShortView());
    }

    @Override
    public List<StructTreeResponse> typeLoadNoSystemId_NY_ShortView() {
        return ofNodeTypeResponse(loadNoSystemId_NY_ShortView());
    }

    private StructTreeResponse getByNode(Node node, boolean ignoreDeviceSize, List<SysDictType> sysDictTypes) {

        StructTreeResponse res = new StructTreeResponse();
        res.setId(node.getNodeId());
        res.setTitle(node.getNodeName());
        res.setKey(node.getNodeId());
        res.setType("NODE");

        Map<String, List<Device>> collect = new HashMap<>();
        if (!ignoreDeviceSize) {
            List<Device> nodeDevices = node.getDeviceList();
            res.setDeviceSize(nodeDevices.size());
            Map<SysDictType, List<Device>> collectObj = nodeDevices.stream().collect(Collectors.groupingBy(Device::getSystemType));
            collectObj.keySet().forEach(l -> collect.put(l.getSystemId(), collectObj.get(l)));
        }
        Map<String, List<Device>> finalCollect = collect;
        sysDictTypes.stream().forEach(l -> {
            StructTreeResponse tsys = new StructTreeResponse();
            tsys.setId(l.getSystemId());
            tsys.setTitle(l.getSystemName());
            tsys.setType("SYSTEM");
            tsys.setKey(l.getSystemId() + "_" + node.getNodeId());
            if (!ignoreDeviceSize) {
                List<Device> devices = finalCollect.get(l.getSystemId());
                if (devices != null) {
                    tsys.setDeviceSize(devices.size());
                }
            }
            res.getChildren().add(tsys);

        });

        return res;

    }

    private StructTreeResponse getDeviceByNode(Node node, List<Device> nodeDevices) {

        StructTreeResponse res = new StructTreeResponse();
        res.setId(node.getNodeId());
        res.setTitle(node.getNodeName());
        res.setKey(node.getNodeId());
        res.setType("NODE");

        if (nodeDevices != null && !nodeDevices.isEmpty()) {
            List<Device> fed = nodeDevices.stream().filter(r -> !"metering_device".equals(r.getConfigKey())).collect(Collectors.toList());

            res.setDeviceSize(fed.size());
            Map<SysDictType, List<Device>> collectObj = fed.stream().collect(Collectors.groupingBy(Device::getSystemType));
            collectObj.keySet().forEach(l -> {
                StructTreeResponse tsys = new StructTreeResponse();
                tsys.setId(l.getSystemId());
                tsys.setTitle(l.getSystemName());
                tsys.setType("SYSTEM");
                tsys.setKey(l.getSystemId() + "_" + node.getNodeId());

                List<Device> devices = collectObj.get(l);
                if (devices != null && !devices.isEmpty()) {
                    tsys.setDeviceSize(devices.size());
                    devices.forEach((Device d) -> {
                        StructTreeResponse de = new StructTreeResponse();
                        de.setId(d.getDeviceId());
                        de.setKey(d.getDeviceId());
                        de.setTitle(d.getDeviceName());
                        de.setLoad(d.getDeviceRatedPower());
                        de.setType("DEVICE");

                        tsys.getChildren().add(de);
                    });

                    res.getChildren().add(tsys);

                }
            });
        } else {
            return res;
        }

        return res;

    }

    public StructTreeResponse nodeSet(StructTreeResponse pre, StructTreeResponse down) {
        pre.getChildren().add(down);
        pre.setDeviceSize(pre.getDeviceSize() + down.getDeviceSize());

        return pre;
    }

    public StructTreeResponse nodeSet(StructTreeResponse pre, List<StructTreeResponse> downs) {
        downs.forEach(e -> nodeSet(pre, e));
        return pre;
    }

    @Override
    public List<StructTreeResponse> areaDeviceView() {

        List<StructTreeResponse> responses = new ArrayList<>();
        List<String> allowNodeIds = userService.getAllowNodeIds();

        Map<String, List<Device>> collect = deviceRepository.findAllByNode_NodeIdIn(allowNodeIds)
                .stream().collect(Collectors.groupingBy(e -> e.getNode().getNodeId()));

        List<Node> all = nodeRepository.findAllByNodeIdIn(collect.keySet());

        all.stream().forEach(e -> {
            StructTreeResponse deviceByNode = getDeviceByNode(e, collect.get(e.getNodeId()));
            if (deviceByNode.getDeviceSize() > 0) {
                responses.add(deviceByNode);
            }

        });


        return ofNodeAreaResponse(responses);
    }

    @Override
    public List<StructTreeResponse> areaDeviceViewMatch(String strategyId) {

        List<String> allowNodeIds = userService.getAllowNodeIds();

        if (StringUtils.isEmpty(strategyId)) {
            List<String> nodeIds = scheduleStrategyViewRepository.findAllByIsDemandResponse(true).stream()
                    .map(ScheduleStrategyView::getNodeId).collect(Collectors.toList());

            if (!nodeIds.isEmpty()) {
                allowNodeIds.removeAll(nodeIds);
            }
        } else {
            List<String> nodeIds = scheduleStrategyViewRepository.findAllByIsDemandResponse(true)
                    .stream().filter(s -> !s.getStrategyId().equals(strategyId))
                    .map(ScheduleStrategyView::getNodeId).collect(Collectors.toList());

            if (!nodeIds.isEmpty()) {
                allowNodeIds.removeAll(nodeIds);
            }
        }

        List<StructTreeResponse> responses = new ArrayList<>();
        Map<String, List<Device>> collect = deviceRepository.findAllByNode_NodeIdIn(allowNodeIds).stream().collect(Collectors.groupingBy(e -> e.getNode().getNodeId()));

        List<Node> all = nodeRepository.findAllByNodeIdIn(collect.keySet());

        all.stream().forEach(e -> {
            StructTreeResponse deviceByNode = getDeviceByNode(e, collect.get(e.getNodeId()));
            if (deviceByNode.getDeviceSize() > 0) {
                responses.add(deviceByNode);
            }

        });

        return ofNodeAreaResponse(responses);


    }

    @Override
    public List<StructTreeResponse> pvNodeTree() {
        return ofNodeAreaResponse(nodeTreeStruct(userService.getAllowPvNodeIds()));
    }
    @Override
    public List<StructTreeResponse> runPvNodeTree() {
        return ofNodeAreaResponse(nodeTreeStruct(userService.getAllowRunPvNodeIds()));
    }

    @Override
    public List<StructTreeResponse> loadNodeTree() {
        return ofNodeAreaResponse(loadForestShortView());
    }

    @Override
    public List<StructTreeResponse> storageEnergyNodeTree() {
        return ofNodeAreaResponse(nodeTreeStruct(userService.getAllowStorageEnergyNodeIds()));
    }
    @Override
    public List<StructTreeResponse> runStorageEnergyNodeTree() {
        return ofNodeAreaResponse(nodeTreeStruct(userService.getAllowRunStorageEnergyNodeIds()));
    }

    @Override
    public List<StructTreeResponse> nodeTree() {
        return ofNodeAreaResponse(nodeTreeStruct(userService.getAllowNodeIds()));
    }

    @Override
    public List<StructTreeResponse> runNodeTree() {
        return ofNodeAreaResponse(nodeTreeStruct(userService.getRunAllowNodeIds()));
    }

    @Override
    public List<StructTreeResponse> chargingPileNodeTree() {
        return ofNodeAreaResponse(nodeTreeStruct(userService.getAllChargingPileLoadNodeIds()));
    }
    @Override
    public List<StructTreeResponse> runChargingPileNodeTree() {
        return ofNodeAreaResponse(nodeTreeStruct(userService.getAllRunChargingPileLoadNodeIds()));
    }


    @Override
    public List<StructTreeResponse> pvAndStorageEnergyNodeTree() {
        List<String> nodeIds = new ArrayList<>();
        List<String> pvNodeIds = userService.getAllowPvNodeIds();
        if (pvNodeIds != null && !pvNodeIds.isEmpty()) {
            nodeIds.addAll(pvNodeIds);
        }
        List<String> storageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
        if (storageEnergyNodeIds != null && !storageEnergyNodeIds.isEmpty()) {
            nodeIds.addAll(storageEnergyNodeIds);
        }
        return ofNodeAreaResponse(nodeTreeStruct(nodeIds));
    }
    @Override
    public List<StructTreeResponse> runPvAndStorageEnergyNodeTree() {
        List<String> nodeIds = new ArrayList<>();
        List<String> pvNodeIds = userService.getAllowRunPvNodeIds();
        if (pvNodeIds != null && !pvNodeIds.isEmpty()) {
            nodeIds.addAll(pvNodeIds);
        }
        List<String> storageEnergyNodeIds = userService.getAllowRunStorageEnergyNodeIds();
        if (storageEnergyNodeIds != null && !storageEnergyNodeIds.isEmpty()) {
            nodeIds.addAll(storageEnergyNodeIds);
        }
        return ofNodeAreaResponse(nodeTreeStruct(nodeIds));
    }

    private List<StructTreeResponse> nodeTreeStruct(List<String> nodeIds) {
        List<StructTreeResponse> result = new ArrayList<>();
        List<Node> all = nodeRepository.findAllByNodeIdIn(nodeIds);
        all.forEach(e -> {
            StructTreeResponse res = new StructTreeResponse();
            res.setId(e.getNodeId());
            res.setTitle(e.getNodeName());
            res.setKey(e.getNodeId());
            res.setType("NODE");
            result.add(res);
        });

        return result;
    }


    private List<StructTreeResponse> nodeTreeStructWithSystem(List<String> nodeIds, boolean ignoreDeviceSize) {
        List<StructTreeResponse> responses = new ArrayList<>();
        List<Node> all = nodeRepository.findAllByNodeIdIn(nodeIds);
        List<SysDictType> sysDictTypeAll = sysDictTypeRepository.findAll();

        all.stream().forEach(e -> {
            List<String> syss = JSONObject.parseArray(e.getSystemIds(), String.class);
            List<SysDictType> sysDictTypes = new ArrayList<>();
            sysDictTypeAll.forEach(l -> {
                if (syss.contains(l.getSystemId())) {
                    sysDictTypes.add(l);
                }
            });
            responses.add(getByNode(e, ignoreDeviceSize, sysDictTypes));
        });
        return responses;
    }

}
