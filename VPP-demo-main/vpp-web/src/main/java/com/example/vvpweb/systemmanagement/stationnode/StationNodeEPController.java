package com.example.vvpweb.systemmanagement.stationnode;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.ElectricityPriceRepository;
import com.example.vvpdomain.StationNodeRepository;
import com.example.vvpdomain.entity.ElectricityPrice;
import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.nodeep.model.*;
import com.example.vvpservice.nodeep.service.NodeEpService;
import com.example.vvpservice.revenue.model.CopyNodeEpRequest;
import com.example.vvpservice.revenue.model.EleNodeInfo;
import com.example.vvpweb.systemmanagement.stationnode.model.AllNodeEPVO;
import com.example.vvpweb.systemmanagement.stationnode.model.NodeEPVO;
import com.example.vvpweb.systemmanagement.stationnode.model.StationNodeEPCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stationNodeEP")
@CrossOrigin
@Api(value = "场站节点模型-电价", tags = {"场站节点模型-电价"})
public class StationNodeEPController {

	@Resource
	private ElectricityPriceRepository electricityPriceRepository;
	@Resource
	private StationNodeRepository stationNodeRepository;

	@ApiOperation("查看项目电价")
	@UserLoginToken
	@RequestMapping(value = "queryNodeEP", method = {RequestMethod.POST})
	public ResponseResult queryNodeEP(@RequestBody StationNodeEPCommand command) {
		String nodeId = command.getNodeId();
		List<StationNode> stationNodes = stationNodeRepository.findAllInfoByStationId(nodeId);
		Map<String, String> nodes = stationNodes.stream().collect(Collectors.toMap(StationNode::getStationId, StationNode::getStationName));
		List<ElectricityPrice> electricityPrices = electricityPriceRepository.findAllByNodeId(nodeId);
		Map<String, AllNodeEPVO> result = electricityPrices.stream()
				.collect(Collectors.groupingBy(ep -> ep.getPriceUse() + ep.getDateType() + ep.getProperty() + ep.getPrice()))
				.values().stream()
				.map(prices -> {
					ElectricityPrice minIdEP = prices.stream()
							.min(Comparator.comparing(e -> e.getId().split("-")[0]))
							.orElse(null);
					if (minIdEP != null) {
						String key = minIdEP.getId().split("-")[0];
						NodeEPVO nodeEPVO = new NodeEPVO(
								minIdEP.getPriceUse(),
								minIdEP.getDateType(),
								minIdEP.getProperty(),
								minIdEP.getPrice().toString()
						);
						List<NodeEPVO> epVoList = new ArrayList<>();
						List<NodeEPVO> eppVoList = new ArrayList<>();

						if ("1".equals(nodeEPVO.getDateType())) {
							eppVoList.add(nodeEPVO);
						} else {
							epVoList.add(nodeEPVO);
						}

						String nodeName = nodes.getOrDefault(key, "Unknown Name");
						AllNodeEPVO allNodeEPVO = new AllNodeEPVO(key, nodeName, epVoList, eppVoList);
						return new AbstractMap.SimpleEntry<>(key, allNodeEPVO);
					}
					return null;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(existing, replacement) -> {
							existing.getEpVoList().addAll(replacement.getEpVoList());
							existing.getSepVoList().addAll(replacement.getSepVoList());
							return existing;
						}
				));
		return ResponseResult.success(result);
	}


	@ApiOperation("查询项目电价")
	@UserLoginToken
	@RequestMapping(value = "queryNodeEPrice", method = {RequestMethod.POST})
	public ResponseResult<QueryNodeEPriceResult> queryNodeEPrice(@RequestBody QueryNodeEPriceRequest request) {
		NodeEpService service = SpringBeanHelper.getBeanOrThrow(NodeEpService.class);
		try {
			QueryNodeEPriceResult res = service.queryEPrice(request);
			return ResponseResult.success(res);
		} catch (Exception e) {
			return ResponseResult.error(e.getMessage());
		}
	}

	@ApiOperation("批量更新电价")
	@UserLoginToken
	@RequestMapping(value = "updateNodeEpBatch", method = {RequestMethod.POST})
	public ResponseResult<String> updateNodeEpBatch(@RequestBody UpdateNodeEPBatchRequest request) {
		NodeEpService service = SpringBeanHelper.getBeanOrThrow(NodeEpService.class);
		try {
			service.updateEpBatch(request);
		} catch (Exception e) {
			log.error("update electricity price error:", e);
			return ResponseResult.error(e.getMessage());
		}

		return ResponseResult.success("success");
	}

	@ApiOperation("复制上月电价")
	@UserLoginToken
	@RequestMapping(value = "copyEpLastMonth", method = {RequestMethod.POST})
	public ResponseResult<String> updateNodeEpBatch(@RequestBody CopyNodeEpRequest request) {
		NodeEpService service = SpringBeanHelper.getBeanOrThrow(NodeEpService.class);
		try {
			service.copyEpLastMonth(request);
		} catch (Exception e) {
			log.error("update electricity price error:", e);
			return ResponseResult.error(e.getMessage());
		}
		return ResponseResult.success("success");
	}

	@ApiOperation("复制项目电价")
	@UserLoginToken
	@RequestMapping(value = "copyEpFromProject", method = {RequestMethod.POST})
	public ResponseResult<String> copyEpFromProject(@RequestBody CopyNodeEpRequest request) {
		NodeEpService service = SpringBeanHelper.getBeanOrThrow(NodeEpService.class);
		try {
			service.copyEpFromProject(request);
		} catch (Exception e) {
			log.error("copy electricity price error:", e);
			return ResponseResult.error(e.getMessage());
		}
		return ResponseResult.success("success");
	}


	@ApiOperation("从电价库获取电价")
	@UserLoginToken
	@RequestMapping(value = "generateEp", method = {RequestMethod.POST})
	public ResponseResult<String> generateEp(@RequestBody GenerateEPriceRequest request) {
		NodeEpService service = SpringBeanHelper.getBeanOrThrow(NodeEpService.class);
		try {
			service.generateEPrice(request);
		} catch (Exception e) {
			log.error("generate electricity price error:", e);
			return ResponseResult.error(e.getMessage());
		}
		return ResponseResult.success("success");
	}

	@ApiOperation("查询城市电价制度")
	@UserLoginToken
	@RequestMapping(value = "queryCityInfo", method = {RequestMethod.GET})
	public ResponseResult<List<EleNodeInfo>> queryCityInfo(@RequestParam String city, @RequestParam(required = false) String province) {
		NodeEpService service = SpringBeanHelper.getBeanOrThrow(NodeEpService.class);
		try {
			return ResponseResult.success(service.queryCityInfo(city, province));
		} catch (Exception e) {
			log.error("query city info error:", e);
			return ResponseResult.error("query city info failed.");
		}
	}

	@ApiOperation("查询城市电力公司")
	@UserLoginToken
	@RequestMapping(value = "queryElectricityCompany", method = {RequestMethod.GET})
	public String queryElectricityCompany(@RequestParam String province) {
		NodeEpService service = SpringBeanHelper.getBeanOrThrow(NodeEpService.class);
		return service.queryElectricityCompany(province);
	}

}
