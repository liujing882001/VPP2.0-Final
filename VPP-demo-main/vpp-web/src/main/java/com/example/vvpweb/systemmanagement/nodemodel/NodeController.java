package com.example.vvpweb.systemmanagement.nodemodel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.Enum.NodePostTypeEnum;
import com.example.vvpcommom.*;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.globalapi.service.GlobalApiService;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.usernode.service.IPageableService;
import com.example.vvpweb.systemmanagement.nodemodel.model.*;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zph
 * @description 节点管理
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/system_management/node_model")
@CrossOrigin
@Api(value = "系统管理-节点模型类", tags = {"系统管理-节点模型"})
@Slf4j
public class NodeController {

    private static final String NODEMODEFILEPATH = System.getProperty("user.dir") + File.separator + "MODELS" + File.separator;
    @Resource
    CfgStorageEnergyStrategyRepository cfgStorageEnergyStrategyRepository;
    @Resource
    CfgStorageEnergyBaseInfoRepository cfgStorageEnergyBaseInfoRepository;
    @Resource
    CfgStorageEnergyShareProportionRepository cfgStorageEnergyShareProportionRepository;
    @Resource
    BiStorageEnergyResourcesRepository biStorageEnergyResourcesRepository;
    @Resource
    CfgPhotovoltaicDiscountRateRepository cfgPhotovoltaicDiscountRateRepository;
    @Resource
    CfgPhotovoltaicTouPriceRepository cfgPhotovoltaicTouPriceRepository;
    @Resource
    CfgPhotovoltaicBaseInfoRepository cfgPhotovoltaicBaseInfoRepository;
    @Resource
    BiPvResourcesRepository biPvResourcesRepository;
    @Resource
    UserNodeRepository userNodeRepository;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private SysDictNodeRepository nodeTypeRepository;
    @Autowired
    private SysDictTypeRepository systemRepository;
    @Autowired
    private SysDictTypeRepository sysDictTypeRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private IPageableService userNodeService;

    @Value("${os.type}")
    private String osType;
    /**
     * 节点类型字典表
     */
    @UserLoginToken
    @RequestMapping(value = "nodeTypeList", method = {RequestMethod.POST})
    public ResponseResult<List<NodeTypeResponse>> nodeTypeList() {

        List<NodeTypeResponse> list = new ArrayList<>();
        List<SysDictNode> nodeTypes = nodeTypeRepository.findAllByOrderByNodeOrderAsc();
        if (nodeTypes != null && nodeTypes.size() > 0) {
            if ("loadType".equals(osType)) {

                // 移除nodePostType为 pv，storageEnergy
                nodeTypes = nodeTypes.stream()
                        .filter(node -> !"storageEnergy".equals(node.getNodePostType())
                                && !"pv".equals(node.getNodePostType()))
                        .collect(Collectors.toList());
            }
            if (nodeTypes != null && nodeTypes.size() > 0) {
                nodeTypes.stream().forEach(nodeType -> {
                    NodeTypeResponse response = new NodeTypeResponse();
                    response.setNodeTypeKey(nodeType.getNodeTypeId());
                    response.setNodeTypeName(nodeType.getNodeTypeName());
                    response.setConfigType(nodeType.getConfigType());
                    list.add(response);
                });
            }
        }
        return ResponseResult.success(list);
    }

    /**
     * 节点名列表
     */
    @UserLoginToken
    @RequestMapping(value = "nodeNameList", method = {RequestMethod.POST})
    public ResponseResult<List<NodeNameResponse>> nodeNameList() {
        List<NodeNameResponse> list = new ArrayList<>();

        List<Node> nodes = userNodeService.getPermissionNodes();

        if (nodes != null && nodes.size() > 0) {
            if ("loadType".equals(nodes)) {

                // 移除nodePostType为 pv，storageEnergy
                nodes = nodes.stream()
                        .filter(node -> !"storageEnergy".equals(node.getNodePostType())
                                && !"pv".equals(node.getNodePostType()))
                        .collect(Collectors.toList());
            }
            if (nodes != null && nodes.size() > 0) {
                nodes = nodes
                        .stream()
                        .sorted(Comparator.comparing(Node::getCreatedTime))
                        .collect(Collectors.toList());
                nodes.forEach(node -> {
                        NodeNameResponse response = new NodeNameResponse();
                        response.setId(node.getNodeId());
                        response.setNodeName(node.getNodeName());
                        response.setNodePostType(node.getNodePostType());
                        list.add(response);
                });
            }
        }
        return ResponseResult.success(list);
    }

    @UserLoginToken
    @RequestMapping(value = "nodeNameListNew", method = {RequestMethod.POST})
    public ResponseResult<List<NodeNameResponse>> nodeNameListNew() {
        List<NodeNameResponse> list = new ArrayList<>();
        GlobalApiService globalApiService = SpringBeanHelper.getBeanOrThrow(GlobalApiService.class);
        List<String> activeSN = globalApiService.listActiveSN().stream().map(StationNode::getStationId).collect(Collectors.toList());
        List<Node> nodes = userNodeService.getPermissionNodes();
        if (!activeSN.isEmpty()) {
            nodes = nodes.stream().filter(o -> activeSN.contains(o.getNodeId())).collect(Collectors.toList());
        }
        if (nodes != null && !nodes.isEmpty()) {
	        nodes = nodes
			        .stream()
			        .sorted(Comparator.comparing(Node::getCreatedTime))
			        .collect(Collectors.toList());
	        nodes.forEach(node -> {
	            NodeNameResponse response = new NodeNameResponse();
	            response.setId(node.getNodeId());
	            response.setNodeName(node.getNodeName());
	            response.setNodePostType(node.getNodePostType());
	            list.add(response);
	        });
        }
        return ResponseResult.success(list);
    }

    /**
     * 指定节点下系统列表
     */
    @UserLoginToken
    @RequestMapping(value = "nodeSystemList", method = {RequestMethod.POST})
    public ResponseResult<List<SystemNameResponse>> nodeSystemList(@RequestParam("nodeId") String nodeId) {
        List<SystemNameResponse> list = new ArrayList<>();

        Node node = nodeRepository.findById(nodeId).orElse(null);
        if (node != null) {
            List<String> sysIds = JSON.parseArray(node.getSystemIds(), String.class);
            if (sysIds != null & sysIds.size() > 0) {
                List<SysDictType> systemTypes = systemRepository.findAllBySystemIdIn(sysIds);
                if (systemTypes != null && systemTypes.size() > 0) {
                    systemTypes = systemTypes
                            .stream()
                            .sorted(Comparator.comparing(p -> p.getCreatedTime()))
                            .collect(Collectors.toList());
                    systemTypes.stream().forEach(p -> {
                        SystemNameResponse response = new SystemNameResponse();
                        response.setId(p.getSystemId());
                        response.setSystemName(p.getSystemName());
                        list.add(response);
                    });
                }
            }
        }
        return ResponseResult.success(list);
    }

    /**
     * 光伏节点查询
     */
    @UserLoginToken
    @RequestMapping(value = "pvNodeNameList", method = {RequestMethod.POST})
    public ResponseResult<List<NodeNameResponse>> pvNodeNameList() {
        List<NodeNameResponse> list = new ArrayList<>();

        List<String> sysIds = sysDictTypeRepository.findAllBySystemIdIn(Arrays.asList("guangfu")).stream().map(SysDictType::getSystemId).collect(Collectors.toList());
        List<Node> all = nodeRepository.findAllByNodeIdIn(userService.getAllowNodeIds());
        all.forEach(e -> {
            List<String> syss = JSONObject.parseArray(e.getSystemIds(), String.class);
            syss.retainAll(sysIds);

            if (!syss.isEmpty()) {
                NodeNameResponse response = new NodeNameResponse();
                response.setId(e.getNodeId());
                response.setNodeName(e.getNodeName());
                list.add(response);
            }
        });
        return ResponseResult.success(list);
    }

    /**
     * 可调负荷点查询
     */
    @UserLoginToken
    @RequestMapping(value = "loadNodeNameList", method = {RequestMethod.POST})
    public ResponseResult<List<NodeNameResponse>> loadNodeNameList() {
        List<NodeNameResponse> list = new ArrayList<>();

        List<Node> all = nodeRepository.findAllByNodeIdIn(userService.getAllowLoadNodeIds());
        all.forEach(e -> {
            NodeNameResponse response = new NodeNameResponse();
            response.setId(e.getNodeId());
            response.setNodeName(e.getNodeName());
            list.add(response);
        });
        return ResponseResult.success(list);
    }

    /**
     * 储能节点查询
     */
    @UserLoginToken
    @RequestMapping(value = "storageEnergyNodeNameList", method = {RequestMethod.POST})
    public ResponseResult<List<NodeNameResponse>> storageEnergyNodeNameList() {
        List<NodeNameResponse> list = new ArrayList<>();

        List<String> sysIds = sysDictTypeRepository.findAllBySystemIdIn(Arrays.asList("chuneng")).stream().map(SysDictType::getSystemId).collect(Collectors.toList());
        List<Node> all = nodeRepository.findAllByNodeIdIn(userService.getAllowNodeIds());
        all.forEach(e -> {
            List<String> syss = JSONObject.parseArray(e.getSystemIds(), String.class);
            syss.retainAll(sysIds);

            if (!syss.isEmpty()) {
                NodeNameResponse response = new NodeNameResponse();
                response.setId(e.getNodeId());
                response.setNodeName(e.getNodeName());
                list.add(response);
            }
        });
        return ResponseResult.success(list);
    }

    /**
     * 节点添加
     */
    @UserLoginToken
    @RequestMapping(value = "nodeAdd", method = {RequestMethod.POST})
    @Transactional
    public synchronized ResponseResult nodeAdd(@RequestBody NodeModelData nodeModelData) {

        if (nodeModelData == null) {
            return ResponseResult.error("参数异常，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getNodeTypeId())) {

            return ResponseResult.error("节点类型编号为空，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getNodeName())) {

            return ResponseResult.error("节点类名称为空，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getAddress())) {
            return ResponseResult.error("地址为空，请检查！");
        }
        if (nodeModelData.getSysIds() == null || nodeModelData.getSysIds().size() == 0) {

            return ResponseResult.error("选择系统为空，请检查！");
        }
        if (nodeModelData.getNodeArea() < 0) {
            return ResponseResult.error("建筑面积小于0，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getProvinceRegionId()) || StringUtils.isEmpty(nodeModelData.getProvinceRegionName())) {
            return ResponseResult.error("省所在区域为空，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getCityRegionId()) || StringUtils.isEmpty(nodeModelData.getCityRegionName())) {
            return ResponseResult.error("市所在区域为空，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getCountyRegionId()) || StringUtils.isEmpty(nodeModelData.getCountyRegionName())) {
            return ResponseResult.error("县/区所在区域为空，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getNoHouseholds())) {
            return ResponseResult.error("户号为空，请检查！");
        }
        boolean isMatch = Pattern.matches(RegexUtils.patternName, nodeModelData.getNodeName());
        if (!isMatch) {
            return ResponseResult.error("系统名称不符合 6～12个汉字、字母或数字、_ 要求，请修改！");
        }

        if (nodeModelData != null) {

            String noHouseholds = nodeModelData.getNoHouseholds();

            Node nodes = nodeRepository.findByNoHouseholds(noHouseholds);
            if (nodes != null) {
                return ResponseResult.error("节点添加失败！该户号已被其他节点绑定！");

            }
            List<String> sysIds = nodeModelData.getSysIds();
            if (sysIds == null || sysIds.size() == 0) {
                return ResponseResult.error("系统不能为空,节点添加失败！");
            }
            if (!sysIds.contains("nengyuanzongbiao")) {
                return ResponseResult.error("能源总表为必选项，节点添加失败！");
            }
            if (sysIds.size() == 1) {
                return ResponseResult.error("除能源总表为必选项外，至少需要再选择一种系统，节点添加失败！");
            }
            if (sysIds.contains("guangfu") && sysIds.contains("chuneng")) {
                return ResponseResult.error("光伏系统或者储能系统，不能同时存在，节点添加失败！");
            }
            if (sysIds != null & sysIds.size() > 0) {
                List<SysDictType> systemTypes = systemRepository.findAllBySystemIdIn(sysIds);

                List<SysDictType> collect = systemTypes.stream().filter(e -> "guangfu".equals(e.getSystemId()) || "chuneng".equals(e.getSystemId())).collect(Collectors.toList());
                if (!collect.isEmpty()) {
                    if (collect.size() > 1) {
                        return ResponseResult.error("节点更新失败！系统只能为光伏系统或者储能系统，不能同时存在");
                    }
                    systemTypes.removeAll(collect);
                    if (systemTypes.size() > 1) {
                        return ResponseResult.error("节点更新失败！系统只能能源总表与光伏系统或者储能系统并存,不能超过两个系统");
                    }
                    if (systemTypes.size() == 1) {
                        SysDictType sysDictType = systemTypes.get(0);
                        if (!"nengyuanzongbiao".equals(sysDictType.getSystemId())) {
                            return ResponseResult.error("节点更新失败！系统只能能源总表与光伏系统或者储能系统并存");
                        }
                    }
                }
            }
            List<SysDictType> systemTypes = systemRepository.findAllBySystemIdIn(sysIds);
            if (systemTypes == null || systemTypes.size() == 0) {
                return ResponseResult.error("系统类型不存在，请重新输入");
            }


            Node byNodeName = nodeRepository.findByNodeName(nodeModelData.getNodeName());
            if (byNodeName != null) {
                return ResponseResult.error("节点名称已经存在，请重新输入");
            }
            String systemIds = JSONObject.toJSONString(nodeModelData.getSysIds());
            SysDictNode sysDictNode = nodeTypeRepository.findById(nodeModelData.getNodeTypeId()).orElse(null);
            if (sysDictNode == null) {
                return ResponseResult.error("节点所属类型不存在，请重新输入");
            }

            Node node = new Node();
            node.setNodeId(MD5Utils.generateMD5(nodeModelData.getNodeName()));

            node.setSystemIds(systemIds);
            node.setNodeName(nodeModelData.getNodeName());
            node.setAddress(nodeModelData.getAddress());
            node.setNodeType(sysDictNode);
            node.setLatitude(nodeModelData.getLatitude());
            node.setLongitude(nodeModelData.getLongitude());
            node.setNodePostType(NodePostTypeEnum.load.getNodePostType());
            if (systemIds.contains("guangfu")) {
                node.setNodePostType(NodePostTypeEnum.pv.getNodePostType());
            }
            if (systemIds.contains("chuneng")) {
                node.setNodePostType(NodePostTypeEnum.storageEnergy.getNodePostType());
            }
            node.setIsEnabled(false);
            node.setOnline(false);
            node.setNodeArea(nodeModelData.getNodeArea());
            node.setProvinceRegionId(nodeModelData.getProvinceRegionId());
            node.setProvinceRegionName(nodeModelData.getProvinceRegionName());
            node.setCityRegionId(nodeModelData.getCityRegionId());
            node.setCityRegionName(nodeModelData.getCityRegionName());
            node.setCountyRegionId(nodeModelData.getCountyRegionId());
            node.setCountyRegionName(nodeModelData.getCountyRegionName());

            node.setNoHouseholds(noHouseholds);
            node.setProvince(nodeModelData.getProvinceRegionName());//2.0碳资产加的省份名称
            nodeRepository.save(node);
            return ResponseResult.success();
        }

        return ResponseResult.error("节点添加失败！");
    }

    /**
     * 节点删除
     */
    @UserLoginToken
    @RequestMapping(value = "nodeDelete", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult nodeDelete(@RequestParam("nodeId") String nodeId) {

        if (!userService.isManger()) {
            return ResponseResult.error("只有管理员和系统管理员能删除节点！");
        }

        if (StringUtils.isEmpty(nodeId)) {
            return ResponseResult.error("节点编号为空，请检查！");
        }
        if (StringUtils.isNotEmpty(nodeId)) {
            Node node = nodeRepository.findById(nodeId).orElse(null);
            if (node != null) {

                List<Device> deviceList = node.getDeviceList();
                if (FieldCheckUtil.checkListNotEmpty(deviceList)) {
                    return ResponseResult.error("节点删除失败!需要先删除关联的设备");
                }

                userNodeRepository.deleteByNodeId(nodeId);

                cfgStorageEnergyStrategyRepository.deleteAllByNodeId(nodeId);
                cfgStorageEnergyBaseInfoRepository.deleteAllByNodeId(nodeId);
                cfgStorageEnergyShareProportionRepository.deleteAllByNodeId(nodeId);

                cfgPhotovoltaicDiscountRateRepository.deleteAllByNodeId(nodeId);
                cfgPhotovoltaicTouPriceRepository.deleteAllByNodeId(nodeId);
                cfgPhotovoltaicBaseInfoRepository.deleteAllByNodeId(nodeId);

                biStorageEnergyResourcesRepository.deleteAllByNodeId(nodeId);
                biPvResourcesRepository.deleteAllByNodeId(nodeId);

                nodeRepository.deleteById(nodeId);

                return ResponseResult.success();
            }
            return ResponseResult.success();
        }
        return ResponseResult.error("节点删除失败!");
    }


    /**
     * 节点更新所有字段
     */
    @UserLoginToken
    @RequestMapping(value = "nodeUpdateAll", method = {RequestMethod.POST})
    @Transactional
    public synchronized ResponseResult nodeUpdateAll(@RequestBody NodeModelData nodeModelData) {
        if (nodeModelData == null) {
            return ResponseResult.error("参数异常，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getNodeId())) {

            return ResponseResult.error("节点编号为空，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getNodeTypeId())) {

            return ResponseResult.error("节点类型编号为空，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getNodeName())) {

            return ResponseResult.error("节点类名称为空，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getAddress())) {
            return ResponseResult.error("地址为空，请检查！");
        }
        if (nodeModelData.getSysIds() == null || nodeModelData.getSysIds().size() == 0) {

            return ResponseResult.error("选择系统为空，请检查！");
        }
        if (nodeModelData.getNodeArea() < 0) {
            return ResponseResult.error("建筑面积小于0，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getProvinceRegionId()) || StringUtils.isEmpty(nodeModelData.getProvinceRegionName())) {
            return ResponseResult.error("省所在区域为空，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getCityRegionId()) || StringUtils.isEmpty(nodeModelData.getCityRegionName())) {
            return ResponseResult.error("市所在区域为空，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getCountyRegionId()) || StringUtils.isEmpty(nodeModelData.getCountyRegionName())) {
            return ResponseResult.error("县/区所在区域为空，请检查！");
        }
        if (StringUtils.isEmpty(nodeModelData.getNoHouseholds())) {
            return ResponseResult.error("户号为空，请检查！");
        }

        boolean isMatch = Pattern.matches(RegexUtils.patternName, nodeModelData.getNodeName());
        if (!isMatch) {
            return ResponseResult.error("系统名称不符合 6～12个汉字、字母或数字、_ 要求，请修改！");
        }

        if (nodeModelData != null) {
            Node node = nodeRepository.findByNodeId(nodeModelData.getNodeId());
            if (node == null) {
                return ResponseResult.error("更新的节点不存在");
            }
            Node systemNode = nodeRepository.findByNodeName(nodeModelData.getNodeName());
            if (systemNode != null && !systemNode.getNodeId().equals(node.getNodeId())) {
                return ResponseResult.error("节点名称已经存在，请重新输入");
            }

            Node node_ = nodeRepository.findByNoHouseholds(nodeModelData.getNoHouseholds());
            if (node_ != null && !node_.getNodeId().equals(node.getNodeId())) {
                return ResponseResult.error("节点添加失败！该户号已被其他节点绑定！");

            }

            List<String> sysIds = nodeModelData.getSysIds();

            if (sysIds == null || sysIds.size() == 0) {
                return ResponseResult.error("系统不能为空,节点添加失败！");
            }
            if (!sysIds.contains("nengyuanzongbiao")) {
                return ResponseResult.error("能源总表为必选项，节点添加失败！");
            }
            if (sysIds.size() == 1) {
                return ResponseResult.error("除能源总表为必选项外，至少需要再选择一种系统，节点添加失败！");
            }
            if (sysIds.contains("guangfu") && sysIds.contains("chuneng")) {
                return ResponseResult.error("光伏系统或者储能系统，不能同时存在，节点添加失败！");
            }
            if (sysIds != null & sysIds.size() > 0) {
                List<SysDictType> systemTypes = systemRepository.findAllBySystemIdIn(sysIds);

                List<SysDictType> collect = systemTypes.stream().filter(e -> "guangfu".equals(e.getSystemId()) || "chuneng".equals(e.getSystemId())).collect(Collectors.toList());
                if (!collect.isEmpty()) {
                    if (collect.size() > 1) {
                        return ResponseResult.error("节点更新失败！系统只能为光伏系统或者储能系统，不能同时存在");
                    }
                    systemTypes.removeAll(collect);
                    if (systemTypes.size() > 1) {
                        return ResponseResult.error("节点更新失败！系统只能能源总表与光伏系统或者储能系统并存,不能超过两个系统");
                    }
                    if (systemTypes.size() == 1) {
                        SysDictType sysDictType = systemTypes.get(0);
                        if (!"nengyuanzongbiao".equals(sysDictType.getSystemId())) {
                            return ResponseResult.error("节点更新失败！系统只能能源总表与光伏系统或者储能系统并存");
                        }
                    }
                }
            }

            SysDictNode sysDictNode = nodeTypeRepository.findById(nodeModelData.getNodeTypeId()).orElse(null);
            if (sysDictNode == null) {
                return ResponseResult.error("节点所属类型不存在，请重新输入");
            }

            String systemIds = JSONObject.toJSONString(nodeModelData.getSysIds());


            node.setSystemIds(systemIds);
            node.setNodeName(nodeModelData.getNodeName());
            node.setAddress(nodeModelData.getAddress());
            node.setProvince(nodeModelData.getProvinceRegionName());
            node.setNodeType(sysDictNode);
            node.setLatitude(nodeModelData.getLatitude());
            node.setLongitude(nodeModelData.getLongitude());

            node.setNodePostType(NodePostTypeEnum.load.getNodePostType());
            if (systemIds.contains("guangfu")) {
                node.setNodePostType(NodePostTypeEnum.pv.getNodePostType());
            }
            if (systemIds.contains("chuneng")) {
                node.setNodePostType(NodePostTypeEnum.storageEnergy.getNodePostType());
            }

            node.setNodeArea(nodeModelData.getNodeArea());
            node.setProvinceRegionId(nodeModelData.getProvinceRegionId());
            node.setProvinceRegionName(nodeModelData.getProvinceRegionName());
            node.setCityRegionId(nodeModelData.getCityRegionId());
            node.setCityRegionName(nodeModelData.getCityRegionName());
            node.setCountyRegionId(nodeModelData.getCountyRegionId());
            node.setCountyRegionName(nodeModelData.getCountyRegionName());

            node.setNoHouseholds(nodeModelData.getNoHouseholds());

            nodeRepository.save(node);
            return ResponseResult.success();
        }
        return ResponseResult.error("更新节点失败!");
    }


    /**
     *
     */
    @UserLoginToken
    @RequestMapping(value = "nodeListPageable", method = {RequestMethod.POST})
    public ResponseResult<PageModel> nodeNameContainList(
            @RequestParam("number") int number,
            @RequestParam("pageSize") int pageSize,
            @RequestParam(value = "nodeName", required = false) String nodeName) {

        List<NodeResponse> list = new ArrayList<>();

        Page<Node> nodeLikeNodeName = userNodeService.getNodeLikeNodeName(nodeName, number, pageSize);

        List<Node> nodes = nodeLikeNodeName.getContent();
        if (nodes != null && nodes.size() > 0) {

            List<SysDictType> allSystemTypes = systemRepository.findAll();

            nodes.stream().forEach(node -> {

                NodeResponse nodeRep = new NodeResponse();
                nodeRep.setId(node.getNodeId());
                nodeRep.setName(node.getNodeName());
                SysDictNode nodeType = node.getNodeType();
                if (nodeType != null) {
                    nodeRep.setNodeTypeName(nodeType.getNodeTypeName());
                }
                nodeRep.setLatitude(node.getLatitude());
                nodeRep.setLongitude(node.getLongitude());
                nodeRep.setNodePostType(node.getNodePostType());

                SysDictNode nt = node.getNodeType();
                if (nt != null) {
                    nodeRep.setNodeTypeId(nt.getNodeTypeId());
                }

                nodeRep.setSystemIds(JSON.parseArray(node.getSystemIds(), String.class));

                nodeRep.setOnline(node.getOnline());
                nodeRep.setIsEnabled(node.getIsEnabled());
                nodeRep.setNodeArea(node.getNodeArea());
                nodeRep.setProvinceRegionId(node.getProvinceRegionId());
                nodeRep.setProvinceRegionName(node.getProvinceRegionName());
                nodeRep.setCityRegionId(node.getCityRegionId());
                nodeRep.setCityRegionName(node.getCityRegionName());
                nodeRep.setCountyRegionId(node.getCountyRegionId());
                nodeRep.setCountyRegionName(node.getCountyRegionName());
                nodeRep.setAddress(node.getAddress());

                nodeRep.setNoHouseholds(node.getNoHouseholds());

                List<String> sysIds = JSON.parseArray(node.getSystemIds(), String.class);
                if (sysIds != null & sysIds.size() > 0) {
                    List<SysDictType> systemTypes = new ArrayList<>();
                    allSystemTypes.forEach(l -> {
                        if (sysIds.contains(l.getSystemId())) {
                            systemTypes.add(l);
                        }
                    });

                    if (systemTypes != null && systemTypes.size() > 0) {
                        nodeRep.setSystemNames(systemTypes.stream().map(u -> u.getSystemName()).collect(Collectors.joining(",")));
                    }
                }
                list.add(nodeRep);
            });
        }

        PageModel pageModel = new PageModel();
        pageModel.setPageSize(pageSize);
        pageModel.setContent(list);
        pageModel.setTotalPages(nodeLikeNodeName.getTotalPages());
        pageModel.setTotalElements((int) nodeLikeNodeName.getTotalElements());
        pageModel.setNumber(nodeLikeNodeName.getNumber() + 1);

        return ResponseResult.success(pageModel);

    }
}