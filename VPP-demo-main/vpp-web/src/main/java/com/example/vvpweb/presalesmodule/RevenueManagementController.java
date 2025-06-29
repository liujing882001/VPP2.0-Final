package com.example.vvpweb.presalesmodule;

import com.alibaba.fastjson.JSON;
import com.example.vvpcommom.RequestHeaderContext;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpdomain.ChatPromptRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.entity.ChatPrompt;
import com.example.vvpdomain.entity.Node;
import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.globalapi.service.GlobalApiService;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.prouser.service.UserServiceImpl;
import com.example.vvpservice.revenue.RevenueCalculationService;
import com.example.vvpservice.revenue.model.*;
import com.example.vvpweb.presalesmodule.model.QueryHintCommand;
import com.example.vvpweb.systemmanagement.nodemodel.model.NodeNameResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@EnableAsync
@Slf4j
@RestController
@RequestMapping("/revenueManage")
@CrossOrigin
@Api(value = "收益管理", tags = {"收益管理"})
public class RevenueManagementController {

	@Resource
	ChatPromptRepository chatPromptRepository;
	@Resource
	GlobalApiService globalApiService;

	@ApiOperation(value = "查提示词")
	@RequestMapping(value = "/queryHint", method = {RequestMethod.POST})
	public ResponseResult queryHint(@RequestBody QueryHintCommand command) {
		try {
			LocalDate currentDate = LocalDate.now();
			LocalDate lastMonthDate = currentDate.minusMonths(1);
			LocalDate lLastMonthDate = currentDate.minusMonths(2);
			LocalDate lLLastMonthDate = currentDate.minusMonths(3);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月");
			String lastMonthDateFormatted = lastMonthDate.format(formatter);
			String lLastMonthDateFormatted = lLastMonthDate.format(formatter);
			String lLLastMonthDateFormatted = lLLastMonthDate.format(formatter);

			List<String> names = globalApiService.getPermissionNodeNames();
			int namesSize = names.size();
			if (namesSize == 0) {
				return ResponseResult.success(new ArrayList<>());
			}

			long count = chatPromptRepository.count();
//			if (command.getQuery() == null) {
//				command.setQuery("1");
//			}
//			if (command.getQueryCount() == null) {
//				command.setQueryCount(3);
//			}

			List<String> randomNumbers = getRandomNumbers(count, 3);
//			chatPromptRepository.findByQueryTypeAndLimit(command.getQuery(), command.getQueryCount())
			List<ChatPrompt> chatPrompts = chatPromptRepository.findAllById(randomNumbers);
			chatPrompts.forEach(v -> {
				if(v.getQueryType().equals("2")) {
					Random random = new Random();
					String nodeName = names.get(random.nextInt(namesSize));
					v.setPrompt(names.get(random.nextInt(namesSize)) + v.getPrompt());
				}
				if (v.getDynamicDate().equals("1")) {
					v.setPrompt(lastMonthDateFormatted + v.getPrompt());
				}
				if (v.getDynamicDate().equals("2")) {
					v.setPrompt(lastMonthDateFormatted + "相对于" + lLastMonthDateFormatted + v.getPrompt());
				}
				if (v.getDynamicDate().equals("3")) {
					v.setPrompt(lLLastMonthDateFormatted + "-" + lastMonthDateFormatted + v.getPrompt());
				}
			});
			return ResponseResult.success(chatPrompts);
		} catch (Exception e) {
			return ResponseResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
		}
	}
	public static List<String> getRandomNumbers(long count, int n) {
		if (n > count) {
			throw new IllegalArgumentException("随机数个数不能大于总数");
		}
		List<Long> numbers = new ArrayList<>();
		for (long i = 0; i < count; i++) {
			numbers.add(i);
		}
		Collections.shuffle(numbers, new Random());
		List<String> result = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			result.add(String.valueOf(numbers.get(i)));
		}

		return result;
	}

	@ApiOperation(value = "收益管理-查询项目")
	@RequestMapping(value = "/queryAvailableProjects", method = {RequestMethod.POST})
	public ResponseResult<List<NodeNameResponse>> queryAvailableProjects(@RequestBody QueryAvailableProjectRequest request) {
		// 临时的，参考接口/nodeNameList
		try {
			List<NodeNameResponse> list = new ArrayList<>();
			String user_id = request.getUserId();
			GlobalApiService globalApiService = SpringBeanHelper.getBeanOrThrow(GlobalApiService.class);
			IUserService userService = SpringBeanHelper.getBeanOrThrow(UserServiceImpl.class);
			NodeRepository nodeRepository = SpringBeanHelper.getBeanOrThrow(NodeRepository.class);
			List<Node> nodes = nodeRepository.findAllByNodeIdIn(userService.getAllowNodeIds(user_id));
			List<String> activeSN = globalApiService.listActiveSN().stream().map(StationNode::getStationId).collect(Collectors.toList());
			if (!activeSN.isEmpty()) {
				nodes = nodes.stream().filter(o -> activeSN.contains(o.getNodeId())).collect(Collectors.toList());
			}
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
		} catch (Exception e) {
			return ResponseResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
		}

	}

	@ApiOperation(value = "查询项目收益")
	@RequestMapping(value = "/queryProjectProfit", method = {RequestMethod.POST})
	public ResponseResult<List<ProjectRevenueResponse>> queryProjectProfit(@RequestBody QueryProjectRequest request) {
		try {
			RevenueCalculationService revenueCalculationService = SpringBeanHelper.getBeanOrThrow(RevenueCalculationService.class);
			List<ProjectRevenueResponse> responses = revenueCalculationService.queryProjectRevenue(request);
			responses.forEach(v -> v.getProfits().forEach(v1 -> {
				if (v1.getProfitTotal() != null) {
					v1.setProfitTotal(v1.getProfitTotal().setScale(2, RoundingMode.HALF_UP));
				}
				if (v1.getProfitActual() != null) {
					v1.setProfitActual(v1.getProfitActual().setScale(2, RoundingMode.HALF_UP));
				}
				if (v1.getProfitElectricity() != null) {
					v1.setProfitElectricity(v1.getProfitElectricity().setScale(2, RoundingMode.HALF_UP));
				}
				if (v1.getProfitOperator() != null) {
					v1.setProfitOperator(v1.getProfitOperator().setScale(2, RoundingMode.HALF_UP));
				}

			}));
			return ResponseResult.success(responses);
		} catch (Exception e) {
			return ResponseResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
		}

	}

	@ApiOperation(value = "查询项目收益细节")
	@RequestMapping(value = "/queryProjectProfitDetail", method = {RequestMethod.POST})
	public ResponseResult<List<ProjectRevenueDetailResponse>> queryProjectProfitDetail(@RequestBody QueryProjectRequest request) {
		try {
			RevenueCalculationService revenueCalculationService = SpringBeanHelper.getBeanOrThrow(RevenueCalculationService.class);
			List<ProjectRevenueDetailResponse> responses = revenueCalculationService.queryProjectProfitDetail(request);
			DecimalFormat df = new DecimalFormat("0.00");

			responses.forEach(v1 -> {
				v1.getEsDetail().forEach(v2 -> {
					if (v2.getTotalPower() != null) {
						v2.setTotalPower(Double.valueOf(df.format(v2.getTotalPower())));
					}
					if (v2.getTotalAmount() != null) {
						v2.setTotalAmount(Double.valueOf(df.format(v2.getTotalAmount())));
					}
					v2.getData().forEach(v3 -> {
						if (v3.getAmount() != null) {
							v3.setAmount(Double.valueOf(df.format(v3.getAmount())));
						}
						if (v3.getPower() != null) {
							v3.setPower(Double.valueOf(df.format(v3.getPower())));
						}
						if (v3.getPrice() != null) {
							v3.setPrice(Double.valueOf(df.format(v3.getPrice())));
						}
					});
				});
				v1.getPvDetail().forEach(v2 -> {
					if (v2.getTotalPower() != null) {
						v2.setTotalPower(Double.valueOf(df.format(v2.getTotalPower())));
					}
					if (v2.getTotalAmount() != null) {
						v2.setTotalAmount(Double.valueOf(df.format(v2.getTotalAmount())));
					}
					v2.getData().forEach(v3 -> {
						if (v3.getAmount() != null) {
							v3.setAmount(Double.valueOf(df.format(v3.getAmount())));
						}
						if (v3.getPower() != null) {
							v3.setPower(Double.valueOf(df.format(v3.getPower())));
						}
						if (v3.getPrice() != null) {
							v3.setPrice(Double.valueOf(df.format(v3.getPrice())));
						}
					});
				});
			});
			return ResponseResult.success(responses);
		} catch (Exception e) {
			return ResponseResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
		}
	}

	@ApiOperation(value = "查询项目电量")
	@RequestMapping(value = "/queryProjectPower", method = {RequestMethod.POST})
	public ResponseResult<List<ProjectPowerResponse>> queryProjectPower(@RequestBody QueryProjectRequest request) {
		try {
			RevenueCalculationService revenueCalculationService = SpringBeanHelper.getBeanOrThrow(RevenueCalculationService.class);
			List<ProjectPowerResponse> responses = revenueCalculationService.queryProjectPower(request);
			DecimalFormat df = new DecimalFormat("0.00");

			responses.forEach(v1 -> v1.getInfos().forEach(v2 -> {
                if (v2.getEsChargeVolume() != null) {
                    v2.setEsChargeVolume(Double.valueOf(df.format(v2.getEsChargeVolume())));
                }
                if (v2.getPvVolume() != null) {
                    v2.setPvVolume(Double.valueOf(df.format(v2.getPvVolume())));
                }
                if (v2.getEsDischargeVolume() != null) {
                    v2.setEsDischargeVolume(Double.valueOf(df.format(v2.getEsDischargeVolume())));
                }
            }));
			return ResponseResult.success(responses);
		} catch (Exception e) {
			return ResponseResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
		}
	}

	@ApiOperation(value = "查询项目电量")
	@RequestMapping(value = "/queryProjectPowerDetail", method = {RequestMethod.POST})
	public ResponseResult<List<ProjectPowerDetailResponse>> queryProjectPowerDetail(@RequestBody QueryProjectRequest request) {
		try {
			RevenueCalculationService revenueCalculationService = SpringBeanHelper.getBeanOrThrow(RevenueCalculationService.class);
			List<ProjectPowerDetailResponse> responses = revenueCalculationService.queryProjectPowerDetail(request);
			DecimalFormat df = new DecimalFormat("0.00");

			responses.forEach(v1 -> {
				v1.getInfos().forEach(v2 -> v2.getDeviceInfo().forEach(v3 -> {
                    if (v3.getLoss() != null) {
                        v3.setLoss(Double.valueOf(df.format(v3.getLoss())));
                    }
                    if (v3.getEnd() != null) {
                        v3.setEnd(Double.valueOf(df.format(v3.getEnd())));
                    }
                    if (v3.getStart() != null) {
                        v3.setStart(Double.valueOf(df.format(v3.getStart())));
                    }
                    if (v3.getBillingConsumption() != null) {
                        v3.setBillingConsumption(Double.valueOf(df.format(v3.getBillingConsumption())));
                    }
                    if (v3.getMeteredConsumption() != null) {
                        v3.setMeteredConsumption(Double.valueOf(df.format(v3.getMeteredConsumption())));
                    }
                }));
			});
			return ResponseResult.success(responses);
		} catch (Exception e) {
			return ResponseResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
		}
	}

	@ApiOperation(value = "收益管理-查询项目")
	@RequestMapping(value = "/getProjectCount", method = {RequestMethod.GET})
	public ResponseResult<Integer> getProjectCount() {
		// 临时的，参考接口/nodeNameList
		try {
			String userId = RequestHeaderContext.getInstance().getUserId();
			GlobalApiService globalApiService = SpringBeanHelper.getBeanOrThrow(GlobalApiService.class);
			IUserService userService = SpringBeanHelper.getBeanOrThrow(UserServiceImpl.class);
			NodeRepository nodeRepository = SpringBeanHelper.getBeanOrThrow(NodeRepository.class);
			List<Node> nodes = nodeRepository.findAllByNodeIdIn(userService.getAllowNodeIds(userId));
			List<String> activeSN = globalApiService.listActiveSN().stream().map(StationNode::getStationId).collect(Collectors.toList());
			if (!activeSN.isEmpty()) {
				nodes = nodes.stream().filter(o -> activeSN.contains(o.getNodeId())).collect(Collectors.toList());
			}
			return ResponseResult.success(nodes.size());
		} catch (Exception e) {
			return ResponseResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
		}
	}

}
