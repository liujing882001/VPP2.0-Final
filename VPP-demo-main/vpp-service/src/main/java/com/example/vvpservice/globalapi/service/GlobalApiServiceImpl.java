package com.example.vvpservice.globalapi.service;

import com.example.vvpdomain.StationNodeRepository;
import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.globalapi.model.*;
import com.example.vvpservice.prouser.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.stream.Stream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GlobalApiServiceImpl implements GlobalApiService {
    @Resource
    private StationNodeRepository stationNodeRepository;

    @Resource
    private UserServiceImpl userService;
    @Override
    public CityATProNodesVO cityATypeProNodes() {
        List<StructTreeDTO> result = new ArrayList<>();
        StructTreeDTO structTreeDTO = new StructTreeDTO();
        structTreeDTO.setId("quanguo");
        structTreeDTO.setTitle("全国");
        structTreeDTO.setKey("quanguo");
        structTreeDTO.setType("全国");
        result.add(structTreeDTO);
        Set<String> nodeTypes = new HashSet<>();
        result.addAll(ofNodeAreaResponse(nodeTypes,userService.getAllowNodeIds()));
        CityATProNodesVO cityATProNodesVO = new CityATProNodesVO();
        cityATProNodesVO.setCityTree(result);
        cityATProNodesVO.setNodeTypes(nodeTypes);
//        if (result != null && result.size() > 0) {
//            Comparator comparator = Collator.getInstance(Locale.CHINA);
//            Collections.sort(result, (p1, p2) -> comparator.compare(
//                    PinyinUtils.converterToFirstSpell(p1.getTitle().substring(0, 1)).toLowerCase(),
//                    PinyinUtils.converterToFirstSpell(p2.getTitle().substring(0, 1)).toLowerCase()));
//
//        }
        return cityATProNodesVO;
    }

    private List<StructTreeDTO> ofNodeAreaResponse(Set<String> nodeTypes,List<String> nodeIds) {
        Map<String, StructTreeDTO> resultMap = new HashMap<>();
        List<StationNode> allByNodeIdIn = stationNodeRepository.findAllByNodeIdsAndSc(nodeIds);
        Map<String,List<StructTreeDTO>> nodeMap = allByNodeIdIn.stream().map(sn -> {
            StructTreeDTO structTreeDTO = new StructTreeDTO();
            structTreeDTO.setId(sn.getStationId());
            structTreeDTO.setTitle(sn.getStationName());
            structTreeDTO.setKey(sn.getStationId());
            structTreeDTO.setType(sn.getStationCategory());
            structTreeDTO.setNodeType(sn.getStationType());
            structTreeDTO.setNodeState(sn.getStationState());
            nodeTypes.add(sn.getStationType());
            return structTreeDTO;
        }).collect(Collectors.groupingBy(StructTreeDTO::getId));
        allByNodeIdIn.forEach(e -> {
            String provinceRegionId = e.getProvinceRegionId();
            String provinceRegionName = e.getProvinceRegionName();
            StructTreeDTO StructTreeDTO = resultMap.get(provinceRegionId);
            if (StructTreeDTO == null) {
                StructTreeDTO = new StructTreeDTO();
                StructTreeDTO.setId(provinceRegionId);
                StructTreeDTO.setKey(provinceRegionId);
                StructTreeDTO.setTitle(provinceRegionName);
                StructTreeDTO.setType("PROVINCE");

                String cityRegionId = e.getCityRegionId();
                String cityRegionName = e.getCityRegionName();
                StructTreeDTO city = new StructTreeDTO();
                city.setId(cityRegionId);
                city.setKey(cityRegionId);
                city.setTitle(cityRegionName);
                city.setType("CITY");
                StructTreeDTO.getChildren().add(city);
                resultMap.put(provinceRegionId, StructTreeDTO);
            } else {
                List<StructTreeDTO> collect = StructTreeDTO.getChildren().stream().filter(l -> l.getId().equals(e.getCityRegionId())).collect(Collectors.toList());
                if (collect.isEmpty()) {
                    String cityRegionId = e.getCityRegionId();
                    String cityRegionName = e.getCityRegionName();
                    StructTreeDTO city = new StructTreeDTO();
                    city.setId(cityRegionId);
                    city.setKey(cityRegionId);
                    city.setTitle(cityRegionName);
                    city.setType("CITY");
                    StructTreeDTO.getChildren().add(city);
                }

            }

            nodeCatchSet(StructTreeDTO, e.getCityRegionId(), nodeMap.get(e.getStationId()));


        });
        return new ArrayList<>(resultMap.values());
    }
    private void nodeCatchSet(StructTreeDTO provinceResponse,String cityRegionId,List<StructTreeDTO> structTreeRespons) {
        for (StructTreeDTO e : structTreeRespons) {
            provinceResponse.getChildren().stream().filter(l -> l.getId().equals(cityRegionId)).forEach(l -> nodeSet(l, e));
        }
    }
    public StructTreeDTO nodeSet(StructTreeDTO pre, StructTreeDTO down) {
        pre.getChildren().add(down);
        return pre;
    }
    @Override
    public List<String> useNodes() {
        return userService.getAllowNodeIds();
    }
    @Override
    public List<StationNode> findAllByParents(List<String> stationIds) {
        return stationNodeRepository.findAllByParents(stationIds);
    }
    @Override
    public StationNode findTopByStationId(String stationId) {
        return stationNodeRepository.findTopByStationId(stationId);
    }
    @Override
    public List<NodeDevCountTreeDTO> findTreeByRole() {
        return getNodesDeviceTree(stationNodeRepository.findTreeByRole(userService.getAllowNodeIds()));
    }
    @Override
    public List<StationNodeTreeDTO> findSubTreeByStationId(String stationId) {
        return getRelatedStationSubNodesTree(stationId,stationNodeRepository.findSubTreeByStationId(stationId));
    }
    @Override
    public List<String> findSubEnergyIdsByStationIds(String stationId) {
        return stationNodeRepository.findSubEnergyIdsByStationIds(stationId,"chuneng");
    }

    @Override
    public List<String> findSubPvIdsByStationIds(String stationId) {
        return stationNodeRepository.findSubEnergyIdsByStationIds(stationId,"guangfu");
    }
    @Override
    public List<String> findSubLoadIdsByStationIds(String stationId) {
        return stationNodeRepository.findSubLoadIdsByStationIds(stationId,"chuneng");
    }
    @Override
    public List<Object[]> findSubTreeByStationIds(List<String> stationIds) {
        return stationNodeRepository.findSubTreeByStationIds(stationIds);
    }
    @Override
    public List<StationNodeTreeDTO> findTreeByStationId(String stationId) {
        return getRelatedStationNodesTree(stationNodeRepository.findTreeByStationId(stationId));
    }

    @Override
    public List<String> getPermissionNodeNames() {
        return stationNodeRepository.findAllNameByNodeIdToString(userService.getAllowNodeIds());
    }

    @Override
    public List<String> findAllStationId() {
        return stationNodeRepository.findAllStationId();
    }
    public List<String> listActSNForUser() {
        return stationNodeRepository.listActSNForUser(userService.getAllowNodeIds());
    }
    public List<StationNode> listActiveSN() {
        return stationNodeRepository.findProjectStationsInOperation();
    }
    public List<ListProjSubVo> listProjAndSub(String query,Map<String,Object> keyword) {
        @SuppressWarnings("unchecked")
        List<String> stationTypeIds = (List<String>) keyword.get("stationTypeId");

        @SuppressWarnings("unchecked")
        List<String> stationStates = (List<String>) keyword.get("stationState");

        if (stationTypeIds == null || stationStates == null) {
            throw new IllegalArgumentException("stationTypeId 或 stationState 不存在或类型不正确");
        }
        List<StationNode> stationNodes = stationNodeRepository.findAll();
        List<ListProjSubVo> roots;
        List<StationNode> filteredNodes = stationNodes.stream()
                .filter(node -> query.isEmpty() || node.getStationName().contains(query))
                .filter(node -> stationTypeIds.isEmpty() || stationTypeIds.contains(node.getStationTypeId()))
                .filter(node -> stationStates.isEmpty() || stationStates.contains(node.getStationState()))
                .collect(Collectors.toList());
        List<String> parentIds = filteredNodes.stream()
                .map(StationNode::getParentId).collect(Collectors.toList());
        filteredNodes.addAll(stationNodes.stream()
                .filter(v2 -> parentIds.contains(v2.getStationId()))
                .filter(node -> stationStates.isEmpty() || stationStates.contains(node.getStationState()))
                .collect(Collectors.toList()));
        roots = new ArrayList<>(stationTree(filteredNodes).values());
        return roots;
    }
    public List<ListProjSubCategoryVo> listProjAndSubCategory(String query,Map<String,Object> keyword) {
        @SuppressWarnings("unchecked")
        List<String> stationTypeIds = (List<String>) keyword.get("stationTypeId");

        @SuppressWarnings("unchecked")
        List<String> stationStates = (List<String>) keyword.get("stationState");

        if (stationTypeIds == null || stationStates == null) {
            throw new IllegalArgumentException("stationTypeId 或 stationState 不存在或类型不正确");
        }
        List<StationNode> stationNodes = stationNodeRepository.findAll();
        List<ListProjSubCategoryVo> roots;
        List<StationNode> filteredNodes = stationNodes.stream()
                .filter(node -> query.isEmpty() || node.getStationName().contains(query))
                .filter(node -> stationTypeIds.isEmpty() || stationTypeIds.contains(node.getStationTypeId()))
                .filter(node -> stationStates.isEmpty() || stationStates.contains(node.getStationState()))
                .collect(Collectors.toList());
        List<String> parentIds = filteredNodes.stream()
                .map(StationNode::getParentId).collect(Collectors.toList());
        filteredNodes.addAll(stationNodes.stream()
                .filter(v2 -> parentIds.contains(v2.getStationId()))
                .filter(node -> stationStates.isEmpty() || stationStates.contains(node.getStationState()))
                .collect(Collectors.toList()));
        roots = new ArrayList<>(stationTreeCategory(filteredNodes).values());
        return roots;
    }
    public List<ListProjSubCategoryVo> listNodeProjAndSubCategory(String nodeId,Map<String,Object> keyword) {
        @SuppressWarnings("unchecked")
        List<String> stationTypeIds = (List<String>) keyword.get("stationTypeId");

        @SuppressWarnings("unchecked")
        List<String> stationStates = (List<String>) keyword.get("stationState");

        if (stationTypeIds == null || stationStates == null) {
            throw new IllegalArgumentException("stationTypeId 或 stationState 不存在或类型不正确");
        }
        List<StationNode> stationNodes = stationNodeRepository.findAllInfoByStationId(nodeId);
        List<ListProjSubCategoryVo> roots;
        List<StationNode> filteredNodes = stationNodes.stream()
                .filter(node -> stationTypeIds.isEmpty() || stationTypeIds.contains(node.getStationTypeId()))
                .filter(node -> stationStates.isEmpty() || stationStates.contains(node.getStationState()))
                .collect(Collectors.toList());
        List<String> parentIds = filteredNodes.stream()
                .map(StationNode::getParentId).collect(Collectors.toList());
        filteredNodes.addAll(stationNodes.stream()
                .filter(v2 -> parentIds.contains(v2.getStationId()))
                .filter(node -> stationStates.isEmpty() || stationStates.contains(node.getStationState()))
                .collect(Collectors.toList()));
        roots = new ArrayList<>(stationTreeCategory(filteredNodes).values());
        return roots;
    }

    public ListProjSubEnergyAndPvVo stationTreeEnergyAndPv(String nodeId) {
        List<StationNode> stationNodes = stationNodeRepository.findNodesByStationId(nodeId)
                .stream()
                .filter(node -> "运营中".contains(node.getStationState()))
                .collect(Collectors.toList());
        ListProjSubEnergyAndPvVo vo = new ListProjSubEnergyAndPvVo();
        List<String> ids = stationNodes.stream().map(StationNode::getStationId).collect(Collectors.toList());
        stationNodes.forEach(node -> {
            if (ids.contains(node.getParentId())) {
                String stationTypeId = node.getStationTypeId();
                if ("chuneng".equals(stationTypeId)) {
                    vo.getEnergy().add(new ListProjSubEnergyAndPvVo(node));
                } else if ("guangfu".equals(stationTypeId)) {
                    vo.getPhotovoltaic().add(new ListProjSubEnergyAndPvVo(node));
                }
            } else {
                vo.setNodeId(node.getStationId());
                vo.setStationName(node.getStationName());
                vo.setStationTypeId(node.getStationTypeId());
            }
        });
        return vo;
    }
    public ListProjSubEnergyAndPvVo stationTreeEnergyAndPvNoM(String nodeId) {
        List<StationNode> stationNodes = new ArrayList<>();
        Queue<StationNode> queue = new ArrayDeque<>();
        StationNode root = stationNodeRepository.findByStationId(nodeId);
        queue.offer(root);
        while (!queue.isEmpty()) {
            StationNode newNode = queue.poll();
            stationNodes.add(newNode);
            List<StationNode> tmp = stationNodeRepository.findAllByParentId(newNode.getStationId());
            for (StationNode s : tmp) {
                queue.offer(s);
            }
        }
        ListProjSubEnergyAndPvVo vo = new ListProjSubEnergyAndPvVo();
        stationNodes.forEach(node -> {
            if (!node.getParentId().isEmpty()) {
                String stationTypeId = node.getStationTypeId();
                if ("chuneng".equals(stationTypeId)) {
                    vo.getEnergy().add(new ListProjSubEnergyAndPvVo(node));
                } else if ("guangfu".equals(stationTypeId)) {
                    vo.getPhotovoltaic().add(new ListProjSubEnergyAndPvVo(node));
                }
            } else {
                vo.setNodeId(node.getStationId());
                vo.setStationName(node.getStationName());
                vo.setStationTypeId(node.getStationTypeId());

            }
        });
        return vo;
    }
    public Map<String,ListProjSubCategoryVo> stationTreeCategory(List<StationNode> stationNodes) {
        Map<String,ListProjSubCategoryVo> stationNodeMap = new HashMap<>();

        stationNodes.forEach(v -> {
            if (v.getParentId().isEmpty()) {
                stationNodeMap.put(v.getStationId(),new ListProjSubCategoryVo(v));
            }
        });

        stationNodes.forEach(node -> {
            if (!node.getParentId().isEmpty()) {
                ListProjSubCategoryVo model = stationNodeMap.get(node.getParentId());
                if (model != null) {
                    String stationTypeId = node.getStationTypeId();
                    if ("chuneng".equals(stationTypeId)) {
                        model.getEnergy().add(new ListProjSubCategoryVo(node));
                    } else if ("guangfu".equals(stationTypeId)) {
                        model.getPhotovoltaic().add(new ListProjSubCategoryVo(node));
                    } else {
                        model.getLoad().add(new ListProjSubCategoryVo(node));
                    }
                }
            }
        });
        return stationNodeMap;
    }
    public Map<String, ListProjSubVo> stationTree(List<StationNode> stationNodes) {
        Map<String,ListProjSubVo> stationNodeMap = new HashMap<>();

        stationNodes.forEach(v -> {
            if (v.getParentId().isEmpty()) {
                stationNodeMap.put(v.getStationId(),new ListProjSubVo(v));
            }
        });

        stationNodes.forEach(node -> {
            if (!node.getParentId().isEmpty()) {
                ListProjSubVo model = stationNodeMap.get(node.getParentId());
                if (model != null) {
                    model.getChildren().add(new ListProjSubVo(node));
                }
            }
        });
        return stationNodeMap;
    }
    public List<NodeDevCountTreeDTO> getNodesDeviceTree(List<Object[]> allNodes) {
        Map<String, NodeDevCountTreeDTO> nodeMap = new HashMap<>();
        for (Object[] node : allNodes) {
            nodeMap.put((String) node[0], new NodeDevCountTreeDTO((String) node[0],(String) node[1],(String) node[2],(BigInteger) node[3]));
        }
        List<NodeDevCountTreeDTO> result = new ArrayList<>();
        for (NodeDevCountTreeDTO node : nodeMap.values()) {
            if (node.getParentId().isEmpty()) {
                result.add(node);
            } else {
                NodeDevCountTreeDTO parentNode = nodeMap.get(node.getParentId());
                if (parentNode != null) {
                    if (parentNode.getChildren() == null) {
                        parentNode.setChildren(new ArrayList<>());
                    }
                    parentNode.getChildren().add(node);
                }
            }
        }
        return result;
    }
    public List<StationNodeTreeDTO> getRelatedStationNodesTree(List<Object[]> allNodes) {
        Map<String, StationNodeTreeDTO> nodeMap = new HashMap<>();
        for (Object[] node : allNodes) {
            nodeMap.put((String) node[0], new StationNodeTreeDTO((String) node[0],(String) node[1],(String) node[2]));
        }
        List<StationNodeTreeDTO> result = new ArrayList<>();
        for (StationNodeTreeDTO node : nodeMap.values()) {
            if (node.getParentId().isEmpty()) {
                result.add(node);
            } else {
                StationNodeTreeDTO parentNode = nodeMap.get(node.getParentId());
                if (parentNode != null) {
                    if (parentNode.getChildren() == null) {
                        parentNode.setChildren(new ArrayList<>());
                    }
                    parentNode.getChildren().add(node);
                }
            }
        }
        return result;
    }
    public List<StationNodeTreeDTO> getRelatedStationSubNodesTree(String stationId,List<Object[]> allNodes) {
        Map<String, StationNodeTreeDTO> nodeMap = new HashMap<>();
        for (Object[] node : allNodes) {
            if (node[0].equals(stationId)) {
                node[1] = "";
            }
            nodeMap.put((String) node[0], new StationNodeTreeDTO((String) node[0],(String) node[1],(String) node[2]));
        }
        List<StationNodeTreeDTO> result = new ArrayList<>();
        for (StationNodeTreeDTO node : nodeMap.values()) {
            if (node.getParentId().isEmpty()) {
                result.add(node);
            } else {
                StationNodeTreeDTO parentNode = nodeMap.get(node.getParentId());
                if (parentNode != null) {
                    if (parentNode.getChildren() == null) {
                        parentNode.setChildren(new ArrayList<>());
                    }
                    parentNode.getChildren().add(node);
                }
            }
        }
        return result;
    }
}
