package com.example.vvpweb.systemmanagement.stationnode;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.*;
import com.example.vvpcommom.Enum.NodePostTypeEnum;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.point.service.PointService;
import com.example.vvpservice.point.service.model.MappingView;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.systemmanagement.nodemodel.model.NodeTypeResponse;
import com.example.vvpweb.systemmanagement.stationnode.model.*;
import com.example.vvpweb.systemmanagement.systemmodel.model.SystemModelResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.result.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stationNode")
@CrossOrigin
@Api(value = "场站节点模型", tags = {"场站节点模型"})
public class StationNodeController {
	@Resource
	private StationNodeRepository stationNodeRepository;
	@Resource
	private SysDictStationRepository sysDictStationRepository;
	@Resource
	private NodeRepository nodeRepository;
	@Resource
	private SysDictTypeRepository systemRepository;
	@Resource
	private SysDictNodeRepository nodeTypeRepository;
	@Resource
	private IUserService userService;
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
	@Resource
	private SysRegionRepository sysRegionRepository;

	@Value("${os.type}")
	private String osType;


	@ApiOperation("查看功能")
	@UserLoginToken
	@RequestMapping(value = "viewFunction", method = {RequestMethod.POST})
	public ResponseResult viewFunction(@RequestParam("nodeId") String nodeId) {
		StationNode stationNode = stationNodeRepository.findByStationId(nodeId);
		if (stationNode.getStationCategory().contains("项目")) {
			PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);
			List<MappingView> mappings = pointService.queryMappingsByStationId(stationNode.getStationId());
			return ResponseResult.success(new StationPageQueryModel(stationNode, mappings));
		} else {
			PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);
			List<MappingView> mappings = pointService.queryMappingsByStationId(stationNode.getStationId());
			return ResponseResult.success(new StationNodeView(stationNode, mappings));
		}
	}

	@ApiOperation("站点信息分页查询")
	@UserLoginToken
	@RequestMapping(value = "stationPageQuery", method = {RequestMethod.POST})
	public ResponseResult stationPageQuery(@RequestBody StationPageQueryCommand command) {
		return ResponseResult.success(getPagedStationNodes(command));
	}

	public Page<StationPageQueryModel> getPagedStationNodes(StationPageQueryCommand command) {
		int page = command.getPage();
		int size = command.getSize();
		List<String> stationTypeIds = command.getStationTypeIds();
		List<String> stationStates = command.getStationState();
		String stationCategory = command.getStationCategory();
		String query = command.getQuery();
		if (command.getKeyword() == null) {
			return new PageImpl<>(new ArrayList<>(), PageRequest.of(page - 1, size), 0);
		}

		List<StationNode> stationNodes = stationNodeRepository.findAll();
		List<StationPageQueryModel> roots;
		if (stationCategory != null) {
			List<String> filteredIds;
			switch (stationCategory) {
				case "load":
					filteredIds = userService.getAllowRunLoadNodeIds();
					break;
				case "pv":
					filteredIds = userService.getAllowRunPvNodeIds();
					break;
				default:
					filteredIds = new ArrayList<>();
			}
			List<StationNode> filteredSysNodes = stationNodes.stream()
					.filter(node -> filteredIds.contains(node.getStationId()))
					.collect(Collectors.toList());
			roots = new ArrayList<>(stationTree(stationNodes,filteredSysNodes));

		} else {
			List<StationNode> filteredNodes =
					stationNodes.stream()
							.filter(node -> query.isEmpty() || node.getStationName().contains(query))
							.filter(node -> stationTypeIds.isEmpty() || stationTypeIds.contains(node.getStationTypeId()))
							.filter(node -> stationStates.isEmpty() || stationStates.contains(node.getStationState())).collect(Collectors.toList());
			roots = new ArrayList<>(stationTree(filteredNodes).values());
		}

		int start = Math.min((page - 1) * size, roots.size());
		int end = Math.min(page * size, roots.size());
		List<StationPageQueryModel> pagedRoots = roots.subList(start, end);

		return new PageImpl<>(pagedRoots, PageRequest.of(page - 1, size), roots.size());
	}

	public List<StationPageQueryModel> stationTree(List<StationNode> allNodes,List<StationNode> nodes){
		PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);
		Set<StationPageQueryModel> result = new HashSet<>();
		Map<String, StationPageQueryModel> rootMap = new HashMap<>();
		allNodes.forEach(v -> {
			if (v.getParentId().isEmpty()) {
				List<MappingView> mappings = pointService.queryMappingsByStationId(v.getStationId());
				rootMap.put(v.getStationId(), new StationPageQueryModel(v, mappings));
			}
		});
		Map<String, StationNodeView> nodeViewMap = new HashMap<>();
		nodes.stream().filter(o -> !o.getParentId().isEmpty()).forEach(node -> {
			List<MappingView> mappings = pointService.queryMappingsByStationId(node.getStationId());
			nodeViewMap.put(node.getStationId(), new StationNodeView(node, mappings));
		});

		nodes.forEach(node -> {
			if (node.getParentId().isEmpty()) {
				result.add(rootMap.get(node.getStationId()));
			} else {
				StationPageQueryModel model = rootMap.get(node.getParentId());
				if (model != null) {
					result.add(model);
					model.getChildren().add(nodeViewMap.get(node.getStationId()));
				}
				StationNodeView view = nodeViewMap.get(node.getParentId());
				if (view != null) {
					view.getChildren().add(nodeViewMap.get(node.getStationId()));
				}
			}
		});
		return new ArrayList<>(result);
	}

	public Map<String, StationPageQueryModel> stationTree(List<StationNode> stationNodes) {
		PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);
		Map<String, StationPageQueryModel> rootMap = new HashMap<>();

		stationNodes.forEach(v -> {
			if (v.getParentId().isEmpty()) {
				List<MappingView> mappings = pointService.queryMappingsByStationId(v.getStationId());
				rootMap.put(v.getStationId(), new StationPageQueryModel(v, mappings));
			}
		});
		Map<String, StationNodeView> nodeViewMap = new HashMap<>();
		stationNodes.stream().filter(o -> !o.getParentId().isEmpty()).forEach(node -> {
			List<MappingView> mappings = pointService.queryMappingsByStationId(node.getStationId());
			nodeViewMap.put(node.getStationId(), new StationNodeView(node, mappings));
		});

		stationNodes.stream().filter(o -> !o.getParentId().isEmpty()).forEach(node -> {
			StationPageQueryModel model = rootMap.get(node.getParentId());
			if (model != null) {
				model.getChildren().add(nodeViewMap.get(node.getStationId()));
			}
			StationNodeView view = nodeViewMap.get(node.getParentId());
			if (view != null) {
				view.getChildren().add(nodeViewMap.get(node.getStationId()));
			}
		});
		return rootMap;
	}

	@ApiOperation("节点阶段列表")
	@UserLoginToken
	@RequestMapping(value = "nodeStageList", method = {RequestMethod.POST})
	public ResponseResult nodeStageList() {
		List<NodeStageListVo> nodeStageList = new ArrayList<>();
		NodeStageListVo node = new NodeStageListVo();
		node.setKey(1);
		node.setValue("建设中");
		NodeStageListVo node1 = new NodeStageListVo();
		node1.setKey(2);
		node1.setValue("运营中");
		NodeStageListVo node2 = new NodeStageListVo();
		node2.setKey(3);
		node2.setValue("规划中");
		NodeStageListVo node3 = new NodeStageListVo();
		node3.setKey(4);
		node3.setValue("已关闭");
		nodeStageList.add(node);
		nodeStageList.add(node1);
		nodeStageList.add(node2);
		nodeStageList.add(node3);
		return ResponseResult.success(nodeStageList);
	}

	@ApiOperation("新建项目节点")
	@UserLoginToken
	@Transactional
	@RequestMapping(value = "addProjectNode", method = {RequestMethod.POST})
	public ResponseResult addProjectNode(@Valid @RequestBody AddProjectNodeCommand command) {
		return stationNodeAdd(command);
	}

	public StationNodeModel toAddNodeModelData(AddProjectNodeCommand command) {
		StationNodeModel nodeModelData = new StationNodeModel();
		nodeModelData.setNodeTypeId(command.getNodeTypeId());
		nodeModelData.setNodeType(command.getNodeTypeId());
		nodeModelData.setNodeName(command.getNodeName());
		nodeModelData.setAddress(command.getAddress());
		nodeModelData.setSysIds(command.getSysIds());
		nodeModelData.setLongitude(command.getLongitude());
		nodeModelData.setLatitude(command.getLatitude());
		nodeModelData.setNodeArea(command.getNodeArea());
		nodeModelData.setProvinceRegionId(command.getProvinceRegionId());
		nodeModelData.setProvinceRegionName(command.getProvinceRegionName());
		nodeModelData.setCityRegionId(command.getCityRegionId());
		nodeModelData.setCityRegionName(command.getCityRegionName());
		nodeModelData.setCountyRegionId(command.getCountyRegionId());
		nodeModelData.setCountyRegionName(command.getCountyRegionName());
		nodeModelData.setNoHouseholds(command.getNoHouseholds());
		return nodeModelData;
	}

	public ResponseResult stationNodeAdd(AddProjectNodeCommand command) {
		StationNodeModel nodeModelData = toAddNodeModelData(command);
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
			return ResponseResult.error("系统名称不符合 6～16个汉字、字母或数字、_ 要求，请修改！");
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

				List<SysDictType> collect =
						systemTypes.stream().filter(e -> "guangfu".equals(e.getSystemId()) || "chuneng".equals(e.getSystemId())).collect(Collectors.toList());
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
			StringBuilder systemTypeNames = new StringBuilder();
			systemTypes.forEach(v -> systemTypeNames.append(v.getSystemName()).append("、"));
			if (systemTypeNames.length() > 0) {
				systemTypeNames.setLength(systemTypeNames.length() - 1);
			}
			String systemTypeNamesResult = systemTypeNames.toString();
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
			node.setIsEnabled(!command.getStationState().equals("建设中"));
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
//            node.setStationId(stationNodeId);
			nodeRepository.save(node);
			toAddStationNode(node, command, systemTypeNamesResult);
//			addEP(node.getProvince(), command.getEType(), command.getVol());
			return ResponseResult.success();
		}

		return ResponseResult.error("节点添加失败！");
	}

	//	public void addEP(String province,String eType,String vol) {
//		List<ElectricityPrice> prices = epApiService.generateEPrice(province,eType,vol);
//		electricityPriceRepository.saveAll(prices);
//	}
	public void toAddStationNode(Node node, AddProjectNodeCommand command, String systemTypeNamesResult) {
		StationNode stationNode = new StationNode();
		stationNode.setId(node.getNodeId() + command.getNodeTypeId());
		stationNode.setStationId(node.getNodeId());
		stationNode.setStationName(command.getNodeName());
		stationNode.setStationCategory("项目");
		stationNode.setStationType(command.getNodeType());
		stationNode.setCreateTime(node.getCreatedTime());
		stationNode.setUpdateTime(node.getUpdateTime());
		stationNode.setParentId("");
		stationNode.setLongitude(node.getLongitude());
		stationNode.setLatitude(node.getLatitude());
		stationNode.setNodeArea(node.getNodeArea());
		stationNode.setProvinceRegionId(node.getProvinceRegionId());
		stationNode.setProvinceRegionName(node.getProvinceRegionName());
		stationNode.setProvince(node.getProvince());
		stationNode.setCityRegionId(node.getCityRegionId());
		stationNode.setCityRegionName(node.getCityRegionName());
		stationNode.setCountyRegionId(node.getCountyRegionId());
		stationNode.setCountyRegionName(node.getCountyRegionName());
		stationNode.setNoHouseholds(node.getNoHouseholds());
		stationNode.setAddress(node.getAddress());
		stationNode.setSystemIds(node.getSystemIds());
		stationNode.setStationTypeId(command.getNodeTypeId());
		stationNode.setSystemNames(systemTypeNamesResult);
		stationNode.setStationState(command.getStationState());
		stationNode.setEType(command.getEType());
		stationNode.setVoltage(command.getVol());
		stationNode.setBasicBill(command.getBasicBill());
		stationNode.setElectricityCompany(command.getElectricityCompany());
		stationNodeRepository.save(stationNode);
	}

	@ApiOperation("编辑项目节点")
	@UserLoginToken
	@RequestMapping(value = "editProjectNode", method = {RequestMethod.POST})
	public ResponseResult editProjectNode(@Valid @RequestBody EditProjectNodeCommand command) {
		return stationNodeEditAll(command);
	}

	public StationNodeModel toEditNodeModelData(EditProjectNodeCommand command) {
		StationNodeModel nodeModelData = new StationNodeModel();
		nodeModelData.setNodeId(command.getNodeId());
		nodeModelData.setNodeTypeId(command.getNodeTypeId());
		nodeModelData.setNodeType(command.getNodeType());
		nodeModelData.setNodeName(command.getNodeName());
		nodeModelData.setAddress(command.getAddress());
		nodeModelData.setSysIds(command.getSysIds());
		nodeModelData.setLongitude(command.getLongitude());
		nodeModelData.setLatitude(command.getLatitude());
		nodeModelData.setNodeArea(command.getNodeArea());
		nodeModelData.setProvinceRegionId(command.getProvinceRegionId());
		nodeModelData.setProvinceRegionName(command.getProvinceRegionName());
		nodeModelData.setCityRegionId(command.getCityRegionId());
		nodeModelData.setCityRegionName(command.getCityRegionName());
		nodeModelData.setCountyRegionId(command.getCountyRegionId());
		nodeModelData.setCountyRegionName(command.getCountyRegionName());
		nodeModelData.setNoHouseholds(command.getNoHouseholds());
		return nodeModelData;
	}

	public ResponseResult stationNodeEditAll(EditProjectNodeCommand command) {
		StationNodeModel nodeModelData = toEditNodeModelData(command);
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
			return ResponseResult.error("系统名称不符合 6～16个汉字、字母或数字、_ 要求，请修改！");
		}

		String systemTypeNamesResult = "";
		if (nodeModelData != null) {
			Node node = nodeRepository.findByNodeId(nodeModelData.getNodeId());
			if (node == null) {
				return ResponseResult.error("更新的节点不存在");
			}
			Node systemNode = nodeRepository.findByNodeName(nodeModelData.getNodeName());
			if (systemNode != null && !systemNode.getNodeId().equals(node.getNodeId())) {
				return ResponseResult.error("节点名称已经存在，请重新输入");
			}

//            Node node_ = nodeRepository.findByNoHouseholds(nodeModelData.getNoHouseholds());
//            if (node_ != null && !node_.getNodeId().equals(node.getNodeId())) {
//                return ResponseResult.error("节点添加失败！该户号已被其他节点绑定！");
//
//            }

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
				StringBuilder systemTypeNames = new StringBuilder();
				systemTypes.forEach(v -> systemTypeNames.append(v.getSystemName()).append("、"));
				if (systemTypeNames.length() > 0) {
					systemTypeNames.setLength(systemTypeNames.length() - 1);
				}
				systemTypeNamesResult = systemTypeNames.toString();
				List<SysDictType> collect =
						systemTypes.stream().filter(e -> "guangfu".equals(e.getSystemId()) || "chuneng".equals(e.getSystemId())).collect(Collectors.toList());
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
			node.setIsEnabled(!command.getStationState().equals("建设中"));

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
			node.setProvince(nodeModelData.getProvinceRegionName());
			node.setCityRegionId(nodeModelData.getCityRegionId());
			node.setCityRegionName(nodeModelData.getCityRegionName());
			node.setCountyRegionId(nodeModelData.getCountyRegionId());
			node.setCountyRegionName(nodeModelData.getCountyRegionName());

			node.setNoHouseholds(nodeModelData.getNoHouseholds());

			nodeRepository.save(node);
			toEditStationNode(node, command, systemTypeNamesResult);
			return ResponseResult.success();
		}
		return ResponseResult.error("更新节点失败!");
	}

	public void toEditStationNode(Node node, EditProjectNodeCommand command, String systemTypeNamesResult) {
		List<StationNode> stationNodes = stationNodeRepository.findAllInfoByStationId(command.getNodeId());

		stationNodeRepository.saveAll(stationNodes.stream().map(stationNode -> {
			if (stationNode.getParentId().isEmpty()) {
				stationNode.setStationId(node.getNodeId());
				stationNode.setStationName(command.getNodeName());
				stationNode.setStationCategory("项目");
				stationNode.setStationType(command.getNodeType());
				stationNode.setCreateTime(node.getCreatedTime());
				stationNode.setUpdateTime(node.getUpdateTime());
				stationNode.setParentId("");
				stationNode.setLongitude(node.getLongitude());
				stationNode.setLatitude(node.getLatitude());
				stationNode.setNodeArea(node.getNodeArea());
				stationNode.setProvinceRegionId(node.getProvinceRegionId());
				stationNode.setProvinceRegionName(node.getProvinceRegionName());
				stationNode.setCityRegionId(node.getCityRegionId());
				stationNode.setCityRegionName(node.getCityRegionName());
				stationNode.setCountyRegionId(node.getCountyRegionId());
				stationNode.setCountyRegionName(node.getCountyRegionName());
				stationNode.setProvince(node.getProvince());
				stationNode.setNoHouseholds(node.getNoHouseholds());
				stationNode.setAddress(node.getAddress());
				stationNode.setSystemIds(node.getSystemIds());
				stationNode.setStationTypeId(command.getNodeTypeId());
				stationNode.setSystemNames(systemTypeNamesResult);
				stationNode.setStationState(command.getStationState());
				stationNode.setEType(command.getEType());
				stationNode.setVoltage(command.getVol());
				stationNode.setBasicBill(command.getBasicBill());
				stationNode.setElectricityCompany(command.getElectricityCompany());
			} else {
				stationNode.setLongitude(node.getLongitude());
				stationNode.setLatitude(node.getLatitude());
				stationNode.setNodeArea(node.getNodeArea());
				stationNode.setProvinceRegionId(node.getProvinceRegionId());
				stationNode.setProvinceRegionName(node.getProvinceRegionName());
				stationNode.setCityRegionId(node.getCityRegionId());
				stationNode.setCityRegionName(node.getCityRegionName());
				stationNode.setCountyRegionId(node.getCountyRegionId());
				stationNode.setCountyRegionName(node.getCountyRegionName());
				stationNode.setProvince(node.getProvince());
				stationNode.setAddress(node.getAddress());
				stationNode.setNoHouseholds(node.getNoHouseholds());
				stationNode.setEType(command.getEType());
				stationNode.setVoltage(command.getVol());
				stationNode.setBasicBill(command.getBasicBill());
				stationNode.setElectricityCompany(command.getElectricityCompany());
			}
			return stationNode;
		}).collect(Collectors.toList()));
		nodeRepository.saveAll(nodeRepository.findAllByNodeIdIn(stationNodes.stream().filter(v -> !v.getParentId().isEmpty()).map(StationNode::getStationId).collect(Collectors.toList())).stream().map(v -> {
			v.setLongitude(node.getLongitude());
			v.setLatitude(node.getLatitude());
			v.setNodeArea(node.getNodeArea());
			v.setProvinceRegionId(node.getProvinceRegionId());
			v.setProvinceRegionName(node.getProvinceRegionName());
			v.setCityRegionId(node.getCityRegionId());
			v.setCityRegionName(node.getCityRegionName());
			v.setCountyRegionId(node.getCountyRegionId());
			v.setCountyRegionName(node.getCountyRegionName());
			v.setAddress(node.getAddress());
			v.setProvince(node.getProvince());
			v.setNoHouseholds(node.getNoHouseholds());
			return v;
		}).collect(Collectors.toList()));
	}

	@Transactional
	@ApiOperation("删除项目节点")
	@UserLoginToken
	@RequestMapping(value = "deleteProjectNode", method = {RequestMethod.POST})
	public ResponseResult deleteProjectNode(@RequestParam("nodeId") String nodeId) {
		return nodeDelete(nodeId);
	}

	public ResponseResult nodeDelete(String nodeId) {

		if (!userService.isManger()) {
			return ResponseResult.error("只有管理员和系统管理员能删除节点！");
		}

		if (StringUtils.isEmpty(nodeId)) {
			return ResponseResult.error("节点编号为空，请检查！");
		}
		if (StringUtils.isNotEmpty(nodeId)) {
			Node node = nodeRepository.findById(nodeId).orElse(null);
			List<String> childIds =
					stationNodeRepository.findNodeHierarchyChild(nodeId).stream().map(o -> String.valueOf(o[0])).collect(Collectors.toList());
			boolean hasDevice = false;
			for (String childId : childIds) {
				Node child = nodeRepository.findByNodeId(childId);
				if (child != null && child.getDeviceList() != null && !child.getDeviceList().isEmpty()) {
					hasDevice = true;
				}
			}
			if (node != null) {

				if (hasDevice) {
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
				//todo 删除本身和子系统内容
				List<String> nodeIds = stationNodeRepository.findAllInfoByStationIdToString(nodeId);
				log.info("nodeIds:{}", nodeIds);
				nodeRepository.deleteAllByIdIn(nodeIds);

				stationNodeRepository.deleteByStationIdAndParentId(nodeId);
				return ResponseResult.success();
			}
			return ResponseResult.success();
		}
		return ResponseResult.error("节点删除失败!");
	}

	@ApiOperation("项目节点类型查询")
	@UserLoginToken
	@RequestMapping(value = "projectNodeTypeQuery", method = {RequestMethod.POST})
	public ResponseResult projectNodeTypeQuery() {
		return ResponseResult.success(sysDictStationRepository.findAll());

	}

	@ApiOperation("项目设备分类查询")
	@UserLoginToken
	@RequestMapping(value = "projectDeviceCategoryQuery", method = {RequestMethod.POST})
	public ResponseResult projectDeviceCategoryQuery() {
		return ResponseResult.success(systemList());
	}

	public ResponseResult<List<SystemModelResponse>> systemList() {
		try {
			List<SystemModelResponse> list = new ArrayList<>();

			List<SysDictType> systemList = systemRepository.findAll();
			if ("loadType".equals(osType)) {

				// 移除nodePostType为 pv，storageEnergy
				systemList =
						systemList.stream().filter(node -> !"guangfu".equals(node.getSystemId()) && !"chuneng".equals(node.getSystemId())).collect(Collectors.toList());
			}
			if (systemList != null && systemList.size() > 0) {
				systemList = systemList.stream().sorted(Comparator.comparing(p -> p.getCreatedTime())).collect(Collectors.toList());
				systemList.stream().forEach(p -> {
					SystemModelResponse systemModelResponse = new SystemModelResponse();
					systemModelResponse.setSystemKey(p.getSystemId());
					systemModelResponse.setSystemName(p.getSystemName());
					systemModelResponse.setConfigType(p.getConfigType());
					list.add(systemModelResponse);
				});
			}
			return ResponseResult.success(list);
		} catch (Exception ex) {
			return ResponseResult.error("获取所有的系统失败!");
		}
	}

	@ApiOperation("省市区树形结构查询")
	@UserLoginToken
	@RequestMapping(value = "regionalTreeQuery", method = {RequestMethod.POST})
	public ResponseResult regionalTreeQuery() {
		return ResponseResult.success(buildRegionTree());
	}

	public List<RegionalTreeQueryModel> buildRegionTree() {
		List<SysRegion> sysRegions = sysRegionRepository.findAll();

		Map<String, RegionalTreeQueryModel> provinces = new HashMap<>();
		Map<String, List<RegionalTreeQueryModel>> cities = new HashMap<>();
		Map<String, List<RegionalTreeQueryModel>> counties = new HashMap<>();

		for (SysRegion region : sysRegions) {
			RegionalTreeQueryModel model = new RegionalTreeQueryModel(region.getRegionId(), region.getRegionName(), region.getRegionShortName(),
					region.getRegionParentId(), region.getRegionCode(), region.getRegionLevel());

			switch (region.getRegionLevel()) {
				case "1":
					provinces.put(region.getRegionId(), model);
					break;
				case "2":
					cities.computeIfAbsent(region.getRegionParentId(), k -> new ArrayList<>()).add(model);
					break;
				case "3":
					counties.computeIfAbsent(region.getRegionParentId(), k -> new ArrayList<>()).add(model);
					break;
			}
		}

		List<RegionalTreeQueryModel> sortedProvinces = new ArrayList<>(provinces.values());
		sortedProvinces.sort(Comparator.comparing(RegionalTreeQueryModel::getRegionId));

		for (RegionalTreeQueryModel province : sortedProvinces) {
			List<RegionalTreeQueryModel> cityList = cities.get(province.getRegionId());
			if (cityList != null) {
				cityList.sort(Comparator.comparing(RegionalTreeQueryModel::getRegionId));
				province.setChildren(new ArrayList<>(cityList));

				for (RegionalTreeQueryModel city : cityList) {
					List<RegionalTreeQueryModel> countyList = counties.get(city.getRegionId());
					if (countyList != null) {
						countyList.sort(Comparator.comparing(RegionalTreeQueryModel::getRegionId));
						city.setChildren(new ArrayList<>(countyList));
					}
				}
			}
		}

		return sortedProvinces;
	}

	@ApiOperation("新建系统节点")
	@UserLoginToken
	@RequestMapping(value = "addSysNode", method = {RequestMethod.POST})
	public ResponseResult addSysNode(@Valid @RequestBody AddSysNodeCommand command) {
		StationNode stationNode = stationNodeRepository.findByStationId(command.getNodeId());
		if (stationNode == null) {
			return ResponseResult.error("节点不存在");
		}
		return sysNodeAdd(stationNode, command);
	}

	public SysStationNodeModel toAddSysNodeModel(StationNode stationNode, AddSysNodeCommand command) {
		SysStationNodeModel nodeModelData = new SysStationNodeModel();
		nodeModelData.setNodeTypeId(command.getNodeTypeId());
		nodeModelData.setNodeType(command.getNodeType());
		nodeModelData.setNodeName(command.getNodeName());
		nodeModelData.setSysIds(command.getSysIds());

		nodeModelData.setAddress(stationNode.getAddress());
		nodeModelData.setLongitude(stationNode.getLongitude());
		nodeModelData.setLatitude(stationNode.getLatitude());
		nodeModelData.setNodeArea(stationNode.getNodeArea());
		nodeModelData.setProvinceRegionId(stationNode.getProvinceRegionId());
		nodeModelData.setProvinceRegionName(stationNode.getProvinceRegionName());
		nodeModelData.setCityRegionId(stationNode.getCityRegionId());
		nodeModelData.setCityRegionName(stationNode.getCityRegionName());
		nodeModelData.setCountyRegionId(stationNode.getCountyRegionId());
		nodeModelData.setCountyRegionName(stationNode.getCountyRegionName());
		nodeModelData.setNoHouseholds(stationNode.getNoHouseholds());
		return nodeModelData;
	}


	public ResponseResult sysNodeAdd(StationNode stationNode, AddSysNodeCommand command) {
		SysStationNodeModel nodeModelData = toAddSysNodeModel(stationNode, command);
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
			return ResponseResult.error("系统名称不符合 6～16个汉字、字母或数字、_ 要求，请修改！");
		}

		if (nodeModelData != null) {

			String noHouseholds = nodeModelData.getNoHouseholds();

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

				List<SysDictType> collect =
						systemTypes.stream().filter(e -> "guangfu".equals(e.getSystemId()) || "chuneng".equals(e.getSystemId())).collect(Collectors.toList());
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
			StringBuilder systemTypeNames = new StringBuilder();
			systemTypes.forEach(v -> systemTypeNames.append(v.getSystemName()).append("、"));
			if (systemTypeNames.length() > 0) {
				systemTypeNames.setLength(systemTypeNames.length() - 1);
			}
			String systemTypeNamesResult = systemTypeNames.toString();
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
			node.setIsEnabled(!command.getStationState().equals("建设中"));
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
			toAddStationNode(node, command, systemTypeNamesResult);
			return ResponseResult.success();
		}

		return ResponseResult.error("节点添加失败！");
	}

	public void toAddStationNode(Node node, AddSysNodeCommand command, String systemTypeNamesResult) {
		StationNode stationNode = stationNodeRepository.findByStationId(command.getNodeId());
		if (stationNode == null) {
			stationNode = new StationNode();
		}
		stationNode.setId(node.getNodeId() + command.getNodeTypeId());
		stationNode.setStationId(node.getNodeId());
		stationNode.setStationName(node.getNodeName());
		stationNode.setStationCategory(StringUtils.isEmpty(command.getStationCategory()) ? "系统" : command.getStationCategory());
		stationNode.setStationType(command.getNodeType());

		stationNode.setCreateTime(node.getCreatedTime());
		stationNode.setUpdateTime(node.getUpdateTime());
		stationNode.setParentId(command.getNodeId());
		stationNode.setLongitude(node.getLongitude());
		stationNode.setLatitude(node.getLatitude());
		stationNode.setNodeArea(node.getNodeArea());
		stationNode.setProvinceRegionId(node.getProvinceRegionId());
		stationNode.setProvinceRegionName(node.getProvinceRegionName());
		stationNode.setCityRegionId(node.getCityRegionId());
		stationNode.setCityRegionName(node.getCityRegionName());
		stationNode.setCountyRegionId(node.getCountyRegionId());
		stationNode.setCountyRegionName(node.getCountyRegionName());
		stationNode.setNoHouseholds(node.getNoHouseholds());
		stationNode.setAddress(node.getAddress());
		stationNode.setSystemIds(node.getSystemIds());
		stationNode.setStationTypeId(command.getNodeTypeId());
		stationNode.setSystemNames(systemTypeNamesResult);
		stationNode.setStationState(command.getStationState());

		stationNodeRepository.save(stationNode);
	}

	@ApiOperation("编辑系统节点")
	@UserLoginToken
	@RequestMapping(value = "editSysNode", method = {RequestMethod.POST})
	public ResponseResult editSysNode(@Valid @RequestBody EditSysNodeCommand command) {
		return sysNodeEditAll(command);
	}

	public StationNodeModel toEditSysNodeModelData(EditSysNodeCommand command) {
		Node node = nodeRepository.findByNodeId(command.getNodeId());

		StationNodeModel nodeModelData = new StationNodeModel();
		nodeModelData.setNodeId(command.getSysNodeId());
		nodeModelData.setNodeTypeId(command.getNodeTypeId());
		nodeModelData.setNodeType(command.getNodeType());
		nodeModelData.setNodeName(command.getNodeName());
		nodeModelData.setSysIds(command.getSysIds());

		nodeModelData.setAddress(node.getAddress());
		nodeModelData.setLongitude(node.getLongitude());
		nodeModelData.setLatitude(node.getLatitude());
		nodeModelData.setNodeArea(node.getNodeArea());
		nodeModelData.setProvinceRegionId(node.getProvinceRegionId());
		nodeModelData.setProvinceRegionName(node.getProvinceRegionName());
		nodeModelData.setCityRegionId(node.getCityRegionId());
		nodeModelData.setCityRegionName(node.getCityRegionName());
		nodeModelData.setCountyRegionId(node.getCountyRegionId());
		nodeModelData.setCountyRegionName(node.getCountyRegionName());
		nodeModelData.setNoHouseholds(node.getNoHouseholds());
		return nodeModelData;
	}

	public ResponseResult sysNodeEditAll(EditSysNodeCommand command) {
		StationNodeModel nodeModelData = toEditSysNodeModelData(command);
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
			return ResponseResult.error("系统名称不符合 6～16个汉字、字母或数字、_ 要求，请修改！");
		}

		String systemTypeNamesResult = "";
		if (nodeModelData != null) {
			Node node = nodeRepository.findByNodeId(nodeModelData.getNodeId());
			if (node == null) {
				return ResponseResult.error("更新的节点不存在");
			}
			Node systemNode = nodeRepository.findByNodeName(nodeModelData.getNodeName());
			if (systemNode != null && !systemNode.getNodeId().equals(node.getNodeId())) {
				return ResponseResult.error("节点名称已经存在，请重新输入");
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
				StringBuilder systemTypeNames = new StringBuilder();
				systemTypes.forEach(v -> systemTypeNames.append(v.getSystemName()).append("、"));
				if (systemTypeNames.length() > 0) {
					systemTypeNames.setLength(systemTypeNames.length() - 1);
				}
				systemTypeNamesResult = systemTypeNames.toString();
				List<SysDictType> collect =
						systemTypes.stream().filter(e -> "guangfu".equals(e.getSystemId()) || "chuneng".equals(e.getSystemId())).collect(Collectors.toList());
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
			node.setNodeType(sysDictNode);
			node.setNodePostType(NodePostTypeEnum.load.getNodePostType());
			node.setIsEnabled(!command.getStationState().equals("建设中"));

			if (systemIds.contains("guangfu")) {
				node.setNodePostType(NodePostTypeEnum.pv.getNodePostType());
			}
			if (systemIds.contains("chuneng")) {
				node.setNodePostType(NodePostTypeEnum.storageEnergy.getNodePostType());
			}

			node.setNoHouseholds(nodeModelData.getNoHouseholds());

			nodeRepository.save(node);
			toEditSysNode(node, command, systemTypeNamesResult);
			return ResponseResult.success();
		}
		return ResponseResult.error("更新节点失败!");
	}

	public void toEditSysNode(Node node, EditSysNodeCommand command, String systemTypeNamesResult) {
		StationNode stationNode = stationNodeRepository.findByStationId(node.getNodeId());
		if (stationNode == null) {
			stationNode = new StationNode();
		}
		stationNode.setStationId(node.getNodeId());
		stationNode.setStationName(node.getNodeName());
		stationNode.setStationCategory(StringUtils.isEmpty(stationNode.getStationCategory()) ? "系统" : stationNode.getStationCategory());
		stationNode.setStationType(command.getNodeType());
		stationNode.setSystemIds(node.getSystemIds());
		stationNode.setStationTypeId(command.getNodeTypeId());
		stationNode.setSystemNames(systemTypeNamesResult);
		stationNode.setStationState(command.getStationState());
		stationNodeRepository.save(stationNode);
	}

	@Transactional
	@ApiOperation("删除系统节点")
	@UserLoginToken
	@RequestMapping(value = "deleteSysNode", method = {RequestMethod.POST})
	public ResponseResult deleteSysNode(@RequestParam("nodeId") String nodeId) {
		return sysNodeDelete(nodeId);
	}

	public ResponseResult sysNodeDelete(String nodeId) {

		if (!userService.isManger()) {
			return ResponseResult.error("只有管理员和系统管理员能删除节点！");
		}

		if (StringUtils.isEmpty(nodeId)) {
			return ResponseResult.error("节点编号为空，请检查！");
		}
		if (StringUtils.isNotEmpty(nodeId)) {
			Node node = nodeRepository.findById(nodeId).orElse(null);
			List<String> childIds =
					stationNodeRepository.findNodeHierarchyChild(nodeId).stream().map(o -> String.valueOf(o[0])).collect(Collectors.toList());
			boolean hasDevice = false;
			for (String childId : childIds) {
				Node child = nodeRepository.findByNodeId(childId);
				if (child != null && child.getDeviceList() != null && !child.getDeviceList().isEmpty()) {
					hasDevice = true;
				}
			}
			if (node != null) {

				if (hasDevice) {
					return ResponseResult.error("节点删除失败!需要先删除关联的设备");
				}
				for (String childId : childIds) {
					if (!nodeId.equals(childId)) {
						sysNodeDelete(childId);
					}
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

				stationNodeRepository.deleteByStationId(nodeId);

				return ResponseResult.success();
			}
			return ResponseResult.success();
		}
		return ResponseResult.error("节点删除失败!");
	}

	@ApiOperation("系统节点类型查询")
	@UserLoginToken
	@RequestMapping(value = "sysNodeTypeQuery", method = {RequestMethod.POST})
	public ResponseResult sysNodeTypeQuery() {
		return nodeTypeList();
	}

	public ResponseResult<List<NodeTypeResponse>> nodeTypeList() {

		List<NodeTypeResponse> list = new ArrayList<>();
		List<SysDictNode> nodeTypes = nodeTypeRepository.findAllByOrderByNodeOrderAsc();
		if (nodeTypes != null && nodeTypes.size() > 0) {
			if ("loadType".equals(osType)) {

				// 移除nodePostType为 pv，storageEnergy
				nodeTypes =
						nodeTypes.stream().filter(node -> !"storageEnergy".equals(node.getNodePostType()) && !"pv".equals(node.getNodePostType())).collect(Collectors.toList());
			}

			if (nodeTypes != null && nodeTypes.size() > 0) {
				nodeTypes.stream().filter(v -> v.getConfigType().equals("T")).forEach(nodeType -> {
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

	@ApiOperation("系统设备分类查询")
	@UserLoginToken
	@RequestMapping(value = "sysDeviceCategoryQuery", method = {RequestMethod.POST})
	public ResponseResult sysDeviceCategoryQuery() {
		return ResponseResult.success(systemList());
	}
}
