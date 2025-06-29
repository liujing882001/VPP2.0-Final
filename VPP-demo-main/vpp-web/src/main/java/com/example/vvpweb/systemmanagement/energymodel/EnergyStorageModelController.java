package com.example.vvpweb.systemmanagement.energymodel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.gateway.IotControlService;
import com.example.gateway.model.*;
import com.example.vvpcommom.*;
import com.example.vvpcommom.Enum.ModuleNameEnum;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpscheduling.StorageEnergyStrategyPower96Job;
import com.example.vvpservice.energymodel.ProfitChartService;
import com.example.vvpservice.energymodel.model.BlackProfitRequest;
import com.example.vvpservice.energymodel.model.PriceData;
import com.example.vvpservice.energymodel.model.ProfitRequest;
import com.example.vvpservice.energymodel.model.ProfitResponse;
import com.example.vvpservice.globalapi.model.ListProjSubEnergyAndPvVo;
import com.example.vvpservice.globalapi.service.GlobalApiService;
import com.example.vvpservice.point.service.PointService;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.demand.model.CopilotRequest;
import com.example.vvpweb.demand.model.CopilotResponse;
import com.example.vvpweb.demand.model.EnergyStorageCopilotNode;
import com.example.vvpweb.systemmanagement.energymodel.model.*;
import com.example.vvpweb.systemmanagement.energymodel.model.factory.EnergyExportData;
import com.example.vvpweb.tradepower.model.TradeEnvironmentConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 储能模型
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/system_management/energy_model/energy_storage_model")
@Api(value = "系统管理-能源模型", tags = {"系统管理-能源模型"})
public class EnergyStorageModelController {

	@Resource
	BiStorageEnergyLogRepository biStorageEnergyLogRepository;
	@Resource
	CfgStorageEnergyStrategyRepository strategyRepository;
	@Resource
	CfgStorageEnergyShareProportionRepository shareProportionRepository;
	@Resource
	NodeRepository nodeRepository;
	@Resource
	DeviceRepository deviceRepository;
	@Resource
	DevicePointRepository devicePointRepository;
	@Resource
	private RedisUtils redisUtils;
	@Resource
	private CfgStorageEnergyBaseInfoRepository baseInfoRepository;

	@Resource
	private CfgStorageEnergyStrategyPower96Repository cfgStorageEnergyStrategyPower96Repository;

	@Resource
	private IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;

	@Resource
	private AiLoadRepository aiLoadRepository;
	@Resource
	private IotTsKvRepository iotTsKvRepository;

	@Resource
	private CfgStorageEnergyStrategyRepository cfgStorageEnergyStrategyRepository;

	@Resource
	private ProfitChartService profitChartService;


	@Autowired
	private IUserService userService;

	@Resource
	CfgStorageEnergyStrategyPower96LogRepository cfgStorageEnergyStrategyPower96LogRepository;
	@Resource
	CfgStorageEnergyStrategyPower96AiRepository cfgStorageEnergyStrategyPower96AiRepository;
	@Resource
	private StationNodeRepository stationNodeRepository;
	@Autowired
	private IotControlService iotControlService;
	private static TradeEnvironmentConfig config;
	@Resource
	private StorageEnergyStrategyPower96Job storageEnergyStrategyPower96Job;
	@Resource
	private GlobalApiService globalApiService;
	@Resource
	private
	CfgPhotovoltaicTouPriceRepository cfgPhotovoltaicTouPriceRepository;

	@Autowired
	public EnergyStorageModelController(TradeEnvironmentConfig environmentConfig) {
		config = environmentConfig;
	}

	@ApiOperation("编辑储能基本信息")
	@UserLoginToken
	@Transactional
	@RequestMapping(value = "saveStorageEnergyBaseInfo", method = {RequestMethod.POST})
	public ResponseResult saveStorageEnergyBaseInfo(@RequestBody StorageEnergyBaseInfo info) {
		try {
			if (info.getMaxChargePercent() < 0.0 || info.getMaxChargePercent() > 100.0
			|| info.getMinDischargePercent() < 0.0 || info.getMinDischargePercent() > 100.0
			|| info.getMinDischargePercent() > info.getMaxChargePercent()) {
				return ResponseResult.error("最大可充，最小放电填写有误");
			}
			if (info != null
					&& StringUtils.isNotEmpty(info.getNodeId())
					&& StringUtils.isNotEmpty(info.getSystemId())) {

				String nodeId = info.getNodeId();
				String systemId = info.getSystemId();
				//基本信息保存
				{
					String id = nodeId + "_" + systemId;
//                    List<CfgStorageEnergyStrategyPower96> cfgStorageEnergyStrategyPower96List = cfgStorageEnergyStrategyPower96Repository
//                    .findAllByNodeIdAndSystemId(nodeId, systemId);
//                    if (cfgStorageEnergyStrategyPower96List.size() > 0) {
//                        for (CfgStorageEnergyStrategyPower96 cfgStorageEnergyStrategyPower96 : cfgStorageEnergyStrategyPower96List) {
//                            cfgStorageEnergyStrategyPower96.setPower(info.getStorageEnergyLoad());
//                        }
//                    }
//                    cfgStorageEnergyStrategyPower96Repository.saveAll(cfgStorageEnergyStrategyPower96List);
					CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
					if (baseInfo == null) {
						baseInfo = new CfgStorageEnergyBaseInfo();
						baseInfo.setId(id);
					}

					baseInfo.setNodeId(nodeId);
					baseInfo.setSystemId(systemId);
					baseInfo.setDataType("storageEnergy");
					baseInfo.setStorageEnergyCapacity(info.getStorageEnergyCapacity());
					baseInfo.setStorageEnergyLoad(info.getStorageEnergyLoad());
					if (StringUtils.isNotEmpty(info.getChargingDeviceSn())) {
						baseInfo.setChargingDeviceSn(info.getChargingDeviceSn());
					}
					if (StringUtils.isNotEmpty(info.getDischargingDeviceSn())) {
						baseInfo.setChargingDeviceSn(info.getDischargingDeviceSn());
					}
					if (StringUtils.isNotEmpty(info.getBatteryStatusDeviceSn())) {
						baseInfo.setChargingDeviceSn(info.getBatteryStatusDeviceSn());
					}
					baseInfo.setMaxChargePercent(info.getMaxChargePercent());
					baseInfo.setMinDischargePercent(info.getMinDischargePercent());
					baseInfoRepository.save(baseInfo);
				}

				return ResponseResult.success();
			} else {
				return ResponseResult.error("请检查要保存的参数是否正确！");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("没有找到保存对象");
		}
	}

	@ApiOperation("查询储能基本信息")
	@UserLoginToken
	@RequestMapping(value = "findStorageEnergyBaseInfo", method = {RequestMethod.POST})
	public ResponseResult<StorageEnergyBaseInfoResponse> findStorageEnergyBaseInfo(@RequestParam(value = "nodeId") String nodeId,
	 @RequestParam(value = "systemId") String systemId) {
		try {
			List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
			if (!allowStorageEnergyNodeIds.contains(nodeId)) {
				return ResponseResult.error("储能节点不存在或者没有该节点权限");
			}
			if (StringUtils.isNotEmpty(nodeId) && StringUtils.isNotEmpty(systemId)) {
				String id = nodeId + "_" + systemId;
				CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
				if (baseInfo != null) {
					StorageEnergyBaseInfoResponse energyBaseInfoResponse = new StorageEnergyBaseInfoResponse();
					energyBaseInfoResponse.setNodeId(nodeId);
					energyBaseInfoResponse.setSystemId(systemId);
					energyBaseInfoResponse.setStorageEnergyCapacity(baseInfo.getStorageEnergyCapacity());
					energyBaseInfoResponse.setStorageEnergyLoad(baseInfo.getStorageEnergyLoad());
					energyBaseInfoResponse.setMaxChargePercent(baseInfo.getMaxChargePercent());
					energyBaseInfoResponse.setMinDischargePercent(baseInfo.getMinDischargePercent());
					return ResponseResult.success(energyBaseInfoResponse);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("当前节点，系统下无数据，请先初始化数据。");
		}
		return ResponseResult.success(null);
	}

	@ApiOperation("查询储能策略列表")
	@UserLoginToken
	@RequestMapping(value = "findStorageEnergyPowerInfo", method = {RequestMethod.POST})
	public ResponseResult<EnergyStorageOverviewResp> findStorageEnergyBaseInfo(@RequestBody @Valid StorageEnergyBaseInfoReq storageEnergyBaseInfoReq) {
		try {
//            if (StringUtils.isBlank(storageEnergyBaseInfoReq.getNodeId())) {
//                return ResponseResult.error("节点id不能为空");
//            }
//            if (StringUtils.isBlank(storageEnergyBaseInfoReq.getSystemId())) {
//                return ResponseResult.error("系统id不能为空");
//            }
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = sdf.parse(storageEnergyBaseInfoReq.getStartDate());
			Date endDate = sdf.parse(storageEnergyBaseInfoReq.getEndDate());
			Date startDateM = TimeUtil.getMonthStart(startDate);
			Date endDateM = TimeUtil.getMonthEnd(endDate);
//			BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(storageEnergyBaseInfoReq.getNodeId(), startDateM, endDateM);
			PropertyTotal propertyTotal = new PropertyTotal();
//            propertyTotal.setPriceHigh(BigDecimal.valueOf(billNodeProfit.getPriceHigh()));
//            propertyTotal.setPricePeak(BigDecimal.valueOf(billNodeProfit.getPricePeak()));
//            propertyTotal.setPriceStable(BigDecimal.valueOf(billNodeProfit.getPriceStable()));
//            propertyTotal.setPriceLow(BigDecimal.valueOf(billNodeProfit.getPriceLow()));

			if (!TimeUtil.isLegalDate(storageEnergyBaseInfoReq.getStartDate().length(), storageEnergyBaseInfoReq.getStartDate(), "yyyy-MM-dd")) {
				return ResponseResult.error("开始日期格式不正确");
			}
			if (!TimeUtil.isLegalDate(storageEnergyBaseInfoReq.getEndDate().length(), storageEnergyBaseInfoReq.getEndDate(), "yyyy-MM-dd")) {
				return ResponseResult.error("结束日期格式不正确");
			}
//            Date date = new Date();
//            Date rsdate = TimeUtil.strDDToDate(respTaskReq.getRsTime() + ":00", "yyyy-MM-dd HH:mm:ss");
//            if (rsdate.before(date)) {
//                return ResponseResult.error("响应开始时段不能小于当前时间");
//            }
			String nodeId = storageEnergyBaseInfoReq.getNodeId();
			String systemId = storageEnergyBaseInfoReq.getSystemId();
			//得到用户权限下储能的节点列表
			List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
			if (!allowStorageEnergyNodeIds.contains(nodeId)) {
				return ResponseResult.error("储能节点不存在或者没有该节点权限");
			}
			if (StringUtils.isNotEmpty(nodeId) && StringUtils.isNotEmpty(systemId)) {
				CfgStorageEnergyBaseInfo cfgStorageEnergyBaseInfo = baseInfoRepository.findCfgStorageEnergyBaseInfoByNodeId(nodeId);
				List<CfgStorageEnergyStrategyPower96> cfgStorageEnergyStrategyPower96List =
				 cfgStorageEnergyStrategyPower96Repository.findAllBySystemIdAndNodeIde(nodeId, systemId, startDate, endDate);
				List<Date> dateList = getDateRangeList(storageEnergyBaseInfoReq.getStartDate(), storageEnergyBaseInfoReq.getEndDate());
				List<EnergyStorageSubView> energyStorageSubViewList = new ArrayList<>();
				EnergyStorageOverviewResp energyStorageOverviewResp = new EnergyStorageOverviewResp();
				List<EnergyStorageProperty> energyStoragePropertyList = new ArrayList<>();
				List<CfgStorageEnergyStrategy> energyStrategyList =
				 cfgStorageEnergyStrategyRepository.findCfgStorageEnergyStrategyByNodeIdAndSystemId(nodeId, systemId, startDateM, startDateM);
				for (CfgStorageEnergyStrategy cfgStorageEnergyStrategy : energyStrategyList) {
					EnergyStorageProperty energyStorageProperty = new EnergyStorageProperty();

					//把00:59改成01:00
					SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
					Date parse = dateFormat.parse(cfgStorageEnergyStrategy.getETime());
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(parse);
					calendar.add(Calendar.MINUTE, 1);
					Date newtime = calendar.getTime();
					SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
					String dateString2 = formatter2.format(newtime);

					energyStorageProperty.setTimeFrame(cfgStorageEnergyStrategy.getSTime() + '-' + dateString2);
					energyStorageProperty.setPriceHour(cfgStorageEnergyStrategy.getPriceHour());
					energyStorageProperty.setProperty(cfgStorageEnergyStrategy.getProperty());
					energyStoragePropertyList.add(energyStorageProperty);

					if (cfgStorageEnergyStrategy.getProperty().equals("尖")) {
						propertyTotal.setPriceHigh(cfgStorageEnergyStrategy.getPriceHour().setScale(8, RoundingMode.HALF_UP));
					}
					if (cfgStorageEnergyStrategy.getProperty().equals("峰")) {
						propertyTotal.setPricePeak(cfgStorageEnergyStrategy.getPriceHour().setScale(8, RoundingMode.HALF_UP));
					}
					if (cfgStorageEnergyStrategy.getProperty().equals("平")) {
						propertyTotal.setPriceStable(cfgStorageEnergyStrategy.getPriceHour().setScale(8, RoundingMode.HALF_UP));
					}
					if (cfgStorageEnergyStrategy.getProperty().equals("谷")) {
						propertyTotal.setPriceLow(cfgStorageEnergyStrategy.getPriceHour().setScale(8, RoundingMode.HALF_UP));
					}
				}
				if (cfgStorageEnergyStrategyPower96List.isEmpty()) {
					if (!energyStrategyList.isEmpty()) {
						//这是老数据，老数据只有每月一号的数据，24条
						for (Date countDate : dateList) {
							EnergyStorageSubView energyStorageSubView = new EnergyStorageSubView();
							SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
							String countDateStr = sdf1.format(countDate);
							energyStorageSubView.setDate(countDateStr);
							List<NodeChargeDischargeInfo> nodeChargeDischargeInfos = new ArrayList<>();
							for (CfgStorageEnergyStrategy cfgStorageEnergyStrategy : energyStrategyList) {
								SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
								String dateString2 = sdf2.format(cfgStorageEnergyStrategy.getEffectiveDate());
//                            if (countDateStr.equals(dateString2)) {
								//一个小时的数据
								List<String> fifteenMinutes = new ArrayList<>();
								String hour = cfgStorageEnergyStrategy.getSTime().substring(0, 2);
								int hourInt = Integer.parseInt(hour) + 1;
								String hour1 = hourInt >= 10 ? String.valueOf(hourInt) : "0" + String.valueOf(hourInt);
								fifteenMinutes.add(hour + ":00");
								fifteenMinutes.add(hour + ":15");
								fifteenMinutes.add(hour + ":30");
								fifteenMinutes.add(hour + ":45");
								fifteenMinutes.add(hour1 + ":00");

								for (int m = 0; m < 4; m++) {
									NodeChargeDischargeInfo nodeChargeDischargeInfo = new NodeChargeDischargeInfo();
									nodeChargeDischargeInfo.setPower(cfgStorageEnergyBaseInfo.getStorageEnergyLoad());
									nodeChargeDischargeInfo.setTime(fifteenMinutes.get(m) + "-" + fifteenMinutes.get(m + 1));
									if (cfgStorageEnergyStrategy.getStrategy() == null) {
										nodeChargeDischargeInfo.setType("待机");
									} else {
										nodeChargeDischargeInfo.setType(cfgStorageEnergyStrategy.getStrategy());
									}
									nodeChargeDischargeInfo.setPolicyModel(0);
									nodeChargeDischargeInfos.add(nodeChargeDischargeInfo);
								}
//                            }
							}
							energyStorageSubView.setNodeChargeDischargeInfos(nodeChargeDischargeInfos);
							energyStorageSubViewList.add(energyStorageSubView);
						}
					}
				} else {
					for (Date countDate : dateList) {
						EnergyStorageSubView energyStorageSubView = new EnergyStorageSubView();
						SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
						String countDateStr = sdf1.format(countDate);
						energyStorageSubView.setDate(countDateStr);
						List<NodeChargeDischargeInfo> nodeChargeDischargeInfos = new ArrayList<>();
						for (CfgStorageEnergyStrategyPower96 cfgStorageEnergyStrategyPower96 : cfgStorageEnergyStrategyPower96List) {
							SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
							String dateString2 = sdf2.format(cfgStorageEnergyStrategyPower96.getEffectiveDate());
							if (countDateStr.equals(dateString2)) {
								NodeChargeDischargeInfo nodeChargeDischargeInfo = new NodeChargeDischargeInfo();
								nodeChargeDischargeInfo.setPower(cfgStorageEnergyStrategyPower96.getPower());
								nodeChargeDischargeInfo.setTime(cfgStorageEnergyStrategyPower96.getTimeScope());
								if (cfgStorageEnergyStrategyPower96.getStrategy() == null) {
									nodeChargeDischargeInfo.setType("待机");
								} else {
									nodeChargeDischargeInfo.setType(cfgStorageEnergyStrategyPower96.getStrategy());
								}
								nodeChargeDischargeInfo.setPolicyModel(cfgStorageEnergyStrategyPower96.getPolicyModel());
								nodeChargeDischargeInfos.add(nodeChargeDischargeInfo);
							}
						}
						energyStorageSubView.setNodeChargeDischargeInfos(nodeChargeDischargeInfos);
						energyStorageSubViewList.add(energyStorageSubView);
					}
				}
				energyStorageOverviewResp.setEnergyStoragePropertyList(energyStoragePropertyList);
				energyStorageOverviewResp.setEnergyStorageSubViews(energyStorageSubViewList);
				energyStorageOverviewResp.setPropertyTotal(propertyTotal);
				return ResponseResult.success(energyStorageOverviewResp);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("当前节点，系统下无数据，请先初始化数据。");
		}
		return ResponseResult.success(null);
	}

	public static List<Date> getDateRangeList(String start, String end) {
		//先把String转成LocalDate
		List<Date> dateList = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate startDate = LocalDate.parse(start, formatter);
		LocalDate endDate = LocalDate.parse(end, formatter);
		while (!startDate.isAfter(endDate)) {
			Instant instant = startDate.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
			Date date = Date.from(instant);
			dateList.add(date);
			startDate = startDate.plusDays(1);
		}
		return dateList;
	}

	@ApiOperation("查询储能基本信息-充放电策略")
	@UserLoginToken
	@RequestMapping(value = "findStorageEnergyStrategyBaseInfo", method = {RequestMethod.POST})
	public ResponseResult<StorageEnergyStrategyBaseInfoResponse> findStorageEnergyStrategyBaseInfo(@RequestParam(value = "nodeId") String nodeId
			, @RequestParam(value = "systemId") String systemId) {
		try {
			List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
			if (!allowStorageEnergyNodeIds.contains(nodeId)) {
				return ResponseResult.error("储能节点不存在或者没有该节点权限");
			}
			if (StringUtils.isNotEmpty(nodeId) && StringUtils.isNotEmpty(systemId)) {
				String id = nodeId + "_" + systemId;
				CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
				if (baseInfo != null) {
					StorageEnergyStrategyBaseInfoResponse energyBaseInfoResponse = new StorageEnergyStrategyBaseInfoResponse();
					energyBaseInfoResponse.setNodeId(nodeId);
					energyBaseInfoResponse.setSystemId(systemId);
					energyBaseInfoResponse.setStrategyExpiryDate(baseInfo.getStrategyExpiryDate());
					energyBaseInfoResponse.setStrategyStartTime(baseInfo.getStrategyStartTime());
					return ResponseResult.success(energyBaseInfoResponse);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("当前节点，系统下无数据，请先初始化数据。");
		}
		return ResponseResult.success(null);
	}

	@ApiOperation("编辑储能基本信息-充放电策略")
	@UserLoginToken
	@Transactional
	@RequestMapping(value = "saveStorageEnergyStrategyBaseInfo", method = {RequestMethod.POST})
	public ResponseResult saveStorageEnergyStrategyBaseInfo(@RequestBody StorageEnergyStrategyBaseInfo info) {
		try {
			if (info.getStrategyExpiryDate() < 0 || info.getStrategyExpiryDate() > 30) {
				return ResponseResult.error("充放电策略年限应在1-30年之间！");
			}

			if (info != null && StringUtils.isNotEmpty(info.getNodeId())
					&& StringUtils.isNotEmpty(info.getSystemId())) {

				String nodeId = info.getNodeId();
				String systemId = info.getSystemId();
				//基本信息保存
				{
					String id = nodeId + "_" + systemId;
					CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
					if (baseInfo == null) {
						baseInfo = new CfgStorageEnergyBaseInfo();
						baseInfo.setId(id);
					}
					baseInfo.setNodeId(nodeId);
					baseInfo.setSystemId(systemId);
					baseInfo.setDataType("storageEnergy");
					baseInfo.setStrategyExpiryDate(info.getStrategyExpiryDate());
					baseInfo.setStrategyStartTime(info.getStrategyStartTime());

					baseInfoRepository.save(baseInfo);
				}

				Node node = nodeRepository.findById(nodeId).orElse(null);
				if (node != null) {
					//数据初始化
					FutureTask<Boolean> futureTask = new FutureTask(() -> {
						try {
							SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM");
							ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));
							SimpleDateFormat cf = new SimpleDateFormat("yyyy-MM-dd");
							cf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

							List<CfgStorageEnergyStrategy> strategies = new ArrayList<>();

							String id = nodeId + "_" + systemId;
							CfgStorageEnergyBaseInfo cfgStorageEnergyBaseInfo = baseInfoRepository.findById(id).orElse(null);
							double power;
							if (cfgStorageEnergyBaseInfo != null) {
								power = cfgStorageEnergyBaseInfo.getStorageEnergyLoad();
							} else {
								power = 0.0;
							}
							cfgStorageEnergyStrategyPower96Repository.deleteAllByNodeIdAndSystemId(nodeId, systemId);
							int year = info.getStrategyExpiryDate();
							Date s_dt = info.getStrategyStartTime();
							Date e_dt = TimeUtil.dateAddYears(s_dt, year);
//                            cfgStorageEnergyStrategyPower96Repository.deleteAllByNodeIdAndSystemId(nodeId, systemId);
							List<String> months = TimeUtil.findDates("M", s_dt, e_dt, 1);
							if (months != null && months.size() > 0) {
								for (int i = 0; i < months.size(); i++) {
									//现在这是时间范围内的每个月的一号
									Date effectiveDate = ym.parse(months.get(i));
									String effectiveDateStr = cf.format(effectiveDate);
									LocalDate localDate = LocalDate.parse(effectiveDateStr);

									YearMonth yearMonth = YearMonth.from(localDate);
									int totalDays = yearMonth.lengthOfMonth();

									for (int day = 1; day <= totalDays; day++) {
										List<CfgStorageEnergyStrategyPower96> strategyPowers = new ArrayList<>();
										//获取到每一天的日期
										LocalDate day1 = LocalDate.of(localDate.getYear(), localDate.getMonth(), day);
										Instant instant1 = Timestamp.valueOf(day1.atTime(LocalTime.MIDNIGHT)).toInstant();
										Date effective = Date.from(instant1);

										//每个小时
										for (int o = 0; o < 24; o++) {
											String fiftime = o < 10 ? ("0" + o + ":00") : (o + ":00");
											String efiftime = o < 10 ? ("0" + o + ":59") : (o + ":59");
											//每十五分钟的list
											List<String> fifteenMinutes = new ArrayList<>();
											String hour = fiftime.substring(0, 2);
											int hourInt = Integer.parseInt(hour) + 1;
											String hour1 = hourInt >= 10 ? String.valueOf(hourInt) : "0" + String.valueOf(hourInt);
											fifteenMinutes.add(hour + ":00");
											fifteenMinutes.add(hour + ":15");
											fifteenMinutes.add(hour + ":30");
											fifteenMinutes.add(hour + ":45");
											fifteenMinutes.add(hour1 + ":00");
											//每十五分钟
											for (int m = 0; m < 4; m++) {
												String strategy_power_id =
												 nodeId + "_" + systemId + "_" + cf.format(effective) + fifteenMinutes.get(m) + "-" + fifteenMinutes.get(m + 1);
												CfgStorageEnergyStrategyPower96 energyStrategyPower96 = new CfgStorageEnergyStrategyPower96();
												energyStrategyPower96.setId(strategy_power_id);
												energyStrategyPower96.setNodeId(nodeId);
												energyStrategyPower96.setSystemId(systemId);
												energyStrategyPower96.setEffectiveDate(effective);
												if (0 <= Integer.parseInt(fifteenMinutes.get(m).substring(0, 2)) && Integer.parseInt(fifteenMinutes.get(m).substring(0, 2)) <= 7) {
													energyStrategyPower96.setPower(100.0);
													energyStrategyPower96.setStrategy("充电");
												}
												if (8 <= Integer.parseInt(fifteenMinutes.get(m).substring(0, 2)) && Integer.parseInt(fifteenMinutes.get(m).substring(0, 2)) <= 9) {
													energyStrategyPower96.setPower(0.0);
													energyStrategyPower96.setStrategy("待机");
												}
												if (10 <= Integer.parseInt(fifteenMinutes.get(m).substring(0, 2)) && Integer.parseInt(fifteenMinutes.get(m).substring(0, 2)) <= 11) {
													energyStrategyPower96.setPower(100.0);
													energyStrategyPower96.setStrategy("放电");
												}
												if (12 == Integer.parseInt(fifteenMinutes.get(m).substring(0, 2))) {
													energyStrategyPower96.setPower(0.0);
													energyStrategyPower96.setStrategy("待机");
												}
												if (13 == Integer.parseInt(fifteenMinutes.get(m).substring(0, 2))) {
													energyStrategyPower96.setPower(50.0);
													energyStrategyPower96.setStrategy("充电");
												}
												if (14 == Integer.parseInt(fifteenMinutes.get(m).substring(0, 2))) {
													energyStrategyPower96.setPower(0.0);
													energyStrategyPower96.setStrategy("待机");
												}
												if (15 <= Integer.parseInt(fifteenMinutes.get(m).substring(0, 2)) && Integer.parseInt(fifteenMinutes.get(m).substring(0, 2)) <= 19) {
													energyStrategyPower96.setPower(100.0);
													energyStrategyPower96.setStrategy("放电");
												}
												if (20 == Integer.parseInt(fifteenMinutes.get(m).substring(0, 2))) {
													energyStrategyPower96.setPower(0.0);
													energyStrategyPower96.setStrategy("待机");
												}
												if (21 == Integer.parseInt(fifteenMinutes.get(m).substring(0, 2))) {
													energyStrategyPower96.setPower(100.0);
													energyStrategyPower96.setStrategy("放电");
												}
												if (22 <= Integer.parseInt(fifteenMinutes.get(m).substring(0, 2)) && Integer.parseInt(fifteenMinutes.get(m).substring(0, 2)) <= 23) {
													energyStrategyPower96.setPower(0.0);
													energyStrategyPower96.setStrategy("待机");
												}
												energyStrategyPower96.setSTime(fifteenMinutes.get(m));
												energyStrategyPower96.setETime(fifteenMinutes.get(m + 1));
												energyStrategyPower96.setTimeScope(fifteenMinutes.get(m) + "-" + fifteenMinutes.get(m + 1));
												strategyPowers.add(energyStrategyPower96);
											}
										}
										cfgStorageEnergyStrategyPower96Repository.saveAll(strategyPowers);
									}

									for (int j = 0; j < 24; j++) {
										int order = j + 1;
										String sTime = j < 10 ? ("0" + j + ":00") : (j + ":00");
										String eTime = j < 10 ? ("0" + j + ":59") : (j + ":59");

										String strategy_id = nodeId + "_" + systemId + "_" + ym.format(effectiveDate) + "_" + order;

										CfgStorageEnergyStrategy energyStrategy = new CfgStorageEnergyStrategy();
										energyStrategy.setId(strategy_id);
										energyStrategy.setNodeId(nodeId);
										energyStrategy.setSystemId(systemId);
										energyStrategy.setEffectiveDate(effectiveDate);
										energyStrategy.setOrder(order);

										energyStrategy.setStrategy("");
										energyStrategy.setStrategyForecasting("");
										energyStrategy.setStrategyHour(3);
										energyStrategy.setMultiplyingPower((double) 0);
										energyStrategy.setProperty("尖");
										energyStrategy.setTimeFrame(sTime + "-" + eTime);
										energyStrategy.setSTime(sTime);
										energyStrategy.setETime(eTime);
										energyStrategy.setPriceHour(BigDecimal.valueOf(0.3765));

										energyStrategy.setLatitude(node.getLatitude());
										energyStrategy.setLongitude(node.getLongitude());

										//价格表标签
										String priceTag =
										 nodeId + "_" + systemId + "_" + ym.format(effectiveDate) + "_" + energyStrategy.getTimeFrame();
										energyStrategy.setPriceTag(priceTag);

										strategies.add(energyStrategy);
									}
								}
							}

							if (strategies != null && strategies.size() > 0) {
								strategyRepository.deleteAllByNodeIdAndSystemId(nodeId, systemId);
								Thread.sleep(500);
								strategyRepository.saveAll(strategies);
							}
						} catch (Exception ex) {
						}
						return true;
					});
					new Thread(futureTask).start();
				}
				return ResponseResult.success();
			} else {
				return ResponseResult.error("请检查要保存的参数是否正确！");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("当前节点，系统下无数据，请先初始化数据。");
		}
	}


	@ApiOperation("查询储能基本信息-分成比例")
	@UserLoginToken
	@RequestMapping(value = "findStorageEnergyShareProportionBaseInfo", method = {RequestMethod.POST})
	public ResponseResult<StorageEnergyShareProportionBaseInfoResponse> findStorageEnergyShareProportionBaseInfo(@RequestParam(value = "nodeId") String nodeId, @RequestParam(value = "systemId") String systemId) {
		try {
			List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
			if (!allowStorageEnergyNodeIds.contains(nodeId)) {
				return ResponseResult.error("储能节点不存在或者没有该节点权限");
			}

			if (StringUtils.isNotEmpty(nodeId) && StringUtils.isNotEmpty(systemId)) {
				String id = nodeId + "_" + systemId;
				CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
				if (baseInfo != null) {
					StorageEnergyShareProportionBaseInfoResponse energyBaseInfoResponse = new StorageEnergyShareProportionBaseInfoResponse();
					energyBaseInfoResponse.setNodeId(nodeId);
					energyBaseInfoResponse.setSystemId(systemId);
					energyBaseInfoResponse.setShareProportionExpiryDate(baseInfo.getShareProportionExpiryDate());
					energyBaseInfoResponse.setShareProportionStartTime(baseInfo.getShareProportionStartTime());

					return ResponseResult.success(energyBaseInfoResponse);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("没有找到保存对象");
		}
		return ResponseResult.success(null);
	}


	@ApiOperation("编辑储能基本信息-分成比例")
	@UserLoginToken
	@Transactional
	@RequestMapping(value = "saveStorageEnergyShareProportionBaseInfo", method = {RequestMethod.POST})
	public ResponseResult saveStorageEnergyShareProportionBaseInfo(@RequestBody StorageEnergyShareProportionBaseInfo info) {
		try {
			if (info.getShareProportionExpiryDate() < 0 || info.getShareProportionExpiryDate() > 30) {
				return ResponseResult.error("分成比例年限应在1-30年之间！");
			}
			if (info != null && StringUtils.isNotEmpty(info.getNodeId()) && StringUtils.isNotEmpty(info.getSystemId())) {

				String nodeId = info.getNodeId();
				String systemId = info.getSystemId();
				//基本信息保存
				{
					String id = nodeId + "_" + systemId;
					CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
					if (baseInfo == null) {
						baseInfo = new CfgStorageEnergyBaseInfo();
						baseInfo.setId(id);
					}
					baseInfo.setNodeId(nodeId);
					baseInfo.setSystemId(systemId);
					baseInfo.setDataType("storageEnergy");
					baseInfo.setShareProportionExpiryDate(info.getShareProportionExpiryDate());
					baseInfo.setShareProportionStartTime(info.getShareProportionStartTime());
					baseInfoRepository.save(baseInfo);
				}

				//分成比例表数据初始化
				FutureTask<Boolean> futureTask = new FutureTask(() -> {
					try {
						SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM");
						ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));

						SimpleDateFormat ym_ = new SimpleDateFormat("yyyyMM");
						ym_.setTimeZone(TimeZone.getTimeZone("GMT+8"));

						List<CfgStorageEnergyShareProportion> shareProportions = new ArrayList<>();

						int year = info.getShareProportionExpiryDate();
						Date s_dt = info.getShareProportionStartTime();
						Date e_dt = TimeUtil.dateAddYears(s_dt, year);

						List<String> months = TimeUtil.findDates("M", s_dt, e_dt, 1);

						if (months != null && months.size() > 0) {

							for (int i = 0; i < months.size(); i++) {

								String effectiveDate = ym.format(ym.parse(months.get(i)));
								String shareProp_id = nodeId + "_" + systemId + "_" + effectiveDate;
								String effectiveDate_ = ym_.format(ym.parse(months.get(i)));

								CfgStorageEnergyShareProportion prop = new CfgStorageEnergyShareProportion();
								prop.setId(shareProp_id);
								prop.setLoadProp(0.9);
								prop.setPowerUserProp(0.1);
								prop.setOrder(Integer.parseInt(effectiveDate_));
								prop.setNodeId(nodeId);
								prop.setSystemId(systemId);

								shareProportions.add(prop);
							}
						}
						if (shareProportions != null && shareProportions.size() > 0) {
							shareProportionRepository.deleteAllByNodeIdAndSystemId(nodeId, systemId);
							Thread.sleep(500);
							shareProportionRepository.saveAll(shareProportions);
						}
					} catch (Exception ex) {
					}
					return true;
				});
				new Thread(futureTask).start();
				return ResponseResult.success();
			} else {
				return ResponseResult.error("请检查要保存的参数是否正确！");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("编辑储能基本信息失败!");
		}
	}

	@ApiOperation("查询电价")
	@UserLoginToken
	@RequestMapping(value = "findElectricityPrices", method = {RequestMethod.POST})
	public ResponseResult<Map<String, Object>> findElectricityPrices(@RequestBody StorageEnergyStrategyModel model) throws ParseException {
		// 创建一个 SimpleDateFormat 对象，指定目标日期格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01");
		// 将日期对象格式化为指定格式的字符串
		Date formattedDate = sdf.parse(sdf.format(model.getTs()));
		String nodeId = model.getNodeId();
		String systemId = model.getSystemId();
		Specification<CfgStorageEnergyStrategy> spec2 = (root, criteriaQuery, cb) -> {
			List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
			predicates.add(cb.equal(root.get("effectiveDate"), formattedDate));
			predicates.add(cb.equal(root.get("nodeId"), nodeId));
			predicates.add(cb.equal(root.get("systemId"), systemId));
			criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
			criteriaQuery.orderBy(cb.asc(root.get("order"))); //按照createTime升序排列
			return criteriaQuery.getRestriction();
		};
		List<CfgStorageEnergyStrategy> datas = strategyRepository.findAll(spec2);
		Map<String, Object> end = new HashMap<>();
		end.put("content", datas);
		end.put("totalPages", 1);
		end.put("totalElements", datas.size());
		end.put("number", 1);
		return ResponseResult.success(end);
	}

	@ApiOperation("查询储能充放电策略")
	@UserLoginToken
	@RequestMapping(value = "findStorageEnergyStrategy", method = {RequestMethod.POST})
	public ResponseResult<Map<String, Object>> findStorageEnergyStrategy(@RequestBody StorageEnergyStrategyModel model) {
		try {

			List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
			if (!allowStorageEnergyNodeIds.contains(model.getNodeId())) {
				return ResponseResult.error("储能节点不存在或者没有该节点权限");
			}

			SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM");
			ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));

			Specification<BiStorageEnergyLog> spec1 = (root, criteriaQuery, cb) -> {
				List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
				predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
				predicates.add(cb.equal(cb.function("DATE", Date.class, root.get("ts")), new Date(model.getTs().getTime())));
				criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
				criteriaQuery.orderBy(cb.asc(root.get("ts"))); //按照createTime升序排列
				return criteriaQuery.getRestriction();
			};
			List<BiStorageEnergyLog> data = biStorageEnergyLogRepository.findAll(spec1).stream().filter(v -> {
				Date createdDate = v.getTs();
				LocalDateTime createdTime = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());
				int minute = createdTime.getMinute();
				return minute == 0 || minute == 15 || minute == 30 || minute == 45;
			}).collect(Collectors.collectingAndThen(Collectors.toMap(BiStorageEnergyLog::getTs, Function.identity(),
			 (existing, replacement) -> existing), map -> new ArrayList<>(map.values())));

			data = data.stream().sorted(Comparator.comparing(BiStorageEnergyLog::getTs)).collect(Collectors.toList());
			List<IotTsKv> iotTsKvs = iotTsKvRepository.findAllByNodeIdAndPointNameAndTsBetweenOrderByTsAsc(model.getNodeId(), "SOC", model.getTs(),
					TimeUtil.dateAddSeconds(TimeUtil.getPreDay(model.getTs(), 1), -1));
			Map<Long, IotTsKv> map = new HashMap<>();
			for (IotTsKv iotTsKv : iotTsKvs) {
				Calendar c = Calendar.getInstance();
				c.setTime(iotTsKv.getTs());
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				map.put(c.getTime().getTime(), iotTsKv);
			}
			data.forEach(o -> {
				Double soc = map.get(o.getTs().getTime()) == null ? null : Double.parseDouble(map.get(o.getTs().getTime()).getPointValue()) / 100;
				o.setSoc(soc);
			});

			// 创建一个 SimpleDateFormat 对象，指定目标日期格式
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01");
			// 将日期对象格式化为指定格式的字符串
			Date formattedDate = sdf.parse(sdf.format(model.getTs()));
			Specification<CfgStorageEnergyStrategy> spec2 = (root, criteriaQuery, cb) -> {
				List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
				predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
				predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
				predicates.add(cb.equal(root.get("effectiveDate"), formattedDate));
				criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
				criteriaQuery.orderBy(cb.asc(root.get("order"))); //按照createTime升序排列
				return criteriaQuery.getRestriction();
			};

			List<CfgStorageEnergyStrategy> datas = strategyRepository.findAll(spec2);
			Map<String, Object> end = new HashMap<>();
			end.put("line", data);
			end.put("content", datas);
			end.put("totalPages", 1);
			end.put("totalElements", data.size() + datas.size());
			end.put("number", 1);

			return ResponseResult.success(end);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("查询储能充放电策略失败!");
		}
	}

	@ApiOperation("导出")
	@RequestMapping(value = "/bisExport", method = {RequestMethod.POST})
	public void downFile(@RequestBody StorageEnergyStrategyModel model, HttpServletResponse response) {
		try {
			Specification<BiStorageEnergyLog> spec1 = (root, criteriaQuery, cb) -> {
				List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
				predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
				predicates.add(cb.equal(cb.function("DATE", Date.class, root.get("ts")), new Date(model.getTs().getTime())));
				criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
				criteriaQuery.orderBy(cb.asc(root.get("ts"))); //按照createTime升序排列
				return criteriaQuery.getRestriction();
			};

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01");
			Date formattedDate = sdf.parse(sdf.format(model.getTs()));
			Specification<CfgStorageEnergyStrategy> spec2 = (root, criteriaQuery, cb) -> {
				List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
				predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
				predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
				predicates.add(cb.equal(root.get("effectiveDate"), formattedDate));
				criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
				criteriaQuery.orderBy(cb.asc(root.get("order"))); //按照createTime升序排列
				return criteriaQuery.getRestriction();
			};
			SimpleDateFormat formatter3 = new SimpleDateFormat("HH:mm");
			List<CfgStorageEnergyStrategy> datas =
			 new ArrayList<>(strategyRepository.findAll(spec2).stream().collect(Collectors.toMap(CfgStorageEnergyStrategy::getTimeFrame,
					 Function.identity(), (existing, replacement) -> existing)).values());
			Map<Long, List<CfgStorageEnergyStrategy>> groupedByDateTime = datas.stream().collect(Collectors.groupingBy((strategy) -> {
				// 使用 Calendar 进行日期时间的拼接
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(model.getTs());
				Calendar timeCalendar = Calendar.getInstance();
				try {
					timeCalendar.setTime(formatter3.parse(strategy.getETime()));
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
				// 设置时间部分到日期对象中
				calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
				calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
				calendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
				return calendar.getTime().getTime() + 60000;
			}));
			List<BiStorageEnergyLog> data = biStorageEnergyLogRepository.findAll(spec1).stream().filter(v -> {
				Date createdDate = v.getCreatedTime();
				LocalDateTime createdTime = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());
				int minute = createdTime.getMinute();
				return minute == 0 || minute == 15 || minute == 30 || minute == 45;
			}).collect(Collectors.toList());
			List<EnergyExportData> dataList = new ArrayList<>();
			groupedByDateTime.forEach((key, valueList) -> data.forEach(v -> {
				if (v.getCreatedTime().getTime() >= key - 3600000 && v.getCreatedTime().getTime() <= key) {
					EnergyExportData exportData = new EnergyExportData(v, valueList.get(0));
					dataList.add(exportData);
				}
			}));


			// 设置响应头
			response.setContentType("application/vnd.ms-excel");
			response.setCharacterEncoding("utf-8");
			String fileName = URLEncoder.encode("bisExport", "UTF-8");
			response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
			// 导出Excel
			EasyExcel.write(response.getOutputStream(), EnergyExportData.class).sheet("数据").doWrite(dataList);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
//	public MergeStrategiesResult mergeStrategies(List<StrategyDistributionModel> models) throws ParseException {
//		Set<EnergyStrategyDistributionModel> result = new LinkedHashSet<>();
//		LocalDateTime startDateTime = null;
//		LocalDateTime endDateTime = null;
//		for (StrategyDistributionModel model : models) {
//			Set<EnergyStrategyDistributionModel> add = new LinkedHashSet<>();
//			if (result.isEmpty()) {
//				EnergyStrategyDistributionModel other = new EnergyStrategyDistributionModel(model);
//				add.add(other);
//				startDateTime = other.getStartDateTime();
//				endDateTime = other.getEndDateTime();
//			} else {
//				EnergyStrategyDistributionModel other = new EnergyStrategyDistributionModel(model);
//				if (other.getStartDateTime().isBefore(startDateTime)) {
//					startDateTime = other.getStartDateTime();
//				}
//				if (other.getEndDateTime().isAfter(endDateTime)) {
//					endDateTime = other.getEndDateTime();
//				}
//				for (EnergyStrategyDistributionModel current : result) {
//					if (other.getIndex() > current.getIndex()) {
//						if (other.getStartDateTime().isBefore(current.getStartDateTime()) && other.getEndDateTime().isBefore(current
//						.getStartDateTime())) {
//							add.add(other);
////							log.info("结果1");
//                        } else if (!other.getStartDateTime().isAfter(current.getStartDateTime()) && other.getEndDateTime().isBefore(current
//                        .getEndDateTime())
//								&& !other.getEndDateTime().isBefore(current.getStartDateTime())) {
//							current.setStartDateTime(other.getEndDateTime().plusSeconds(1));//是否要加时间
//							add.add(other);
////							log.info("结果2");
//						} else if (other.getStartDateTime().isBefore(current.getStartDateTime()) && other.getEndDateTime().isAfter(current
//						.getEndDateTime())) {
//							current.setStartDateTime(other.getStartDateTime());
//							current.setEndDateTime(other.getEndDateTime());
//							current.setStrategy(other.getStrategy());
//							current.setPower(other.getPower());
//							current.setIndex(other.getIndex());
////							log.info("结果3");
//						} else if (other.getStartDateTime().equals(current.getEndDateTime()) && other.getEndDateTime().equals(current.getEndDateTime
//						())) {
//							current.setStrategy(other.getStrategy());
//							current.setPower(current.getPower());
//							current.setIndex(other.getIndex());
////							log.info("结果4");
//						} else if (other.getStartDateTime().isAfter(current.getStartDateTime()) && other.getEndDateTime().isBefore(current
	//						.getEndDateTime())) {
//							EnergyStrategyDistributionModel otherNew = new EnergyStrategyDistributionModel();
//							otherNew.setNodeId(current.getNodeId());
//							otherNew.setSystemId(current.getSystemId());
//							otherNew.setStrategy(current.getStrategy());
//							otherNew.setPower(current.getPower());
//							otherNew.setStartDateTime(current.getStartDateTime());
//							otherNew.setEndDateTime(other.getStartDateTime().minusSeconds(1));//是否要减时间
//							otherNew.setIndex(current.getIndex());
//							add.add(otherNew);
//							add.add(other);
//							current.setStartDateTime(other.getEndDateTime().plusSeconds(1));//是否要加时间
////							log.info("结果5");
//						} else if (other.getStartDateTime().isAfter(current.getStartDateTime()) && other.getStartDateTime().isBefore(current
//						.getEndDateTime())
//								&& !other.getEndDateTime().isBefore(current.getEndDateTime())) {
//							current.setEndDateTime(other.getStartDateTime().minusSeconds(1));//是否要减时间
//							add.add(other);
////							log.info("结果6");
//						} else if (other.getStartDateTime().isAfter(current.getEndDateTime())) {
//							add.add(other);
////							log.info("结果7");
//						}
//					}
//				}
//			}
//			result.addAll(add);
//		}
//		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//
//		String startDate = startDateTime.format(dateFormatter);
//		String startTime = startDateTime.format(timeFormatter);
//		String endDate = endDateTime.format(dateFormatter);
//		String endTime = endDateTime.format(timeFormatter);
//		return new MergeStrategiesResult(startDate,startTime,endDate,endTime,new ArrayList<>(new LinkedHashSet<>(result)));
//	}


	@ApiOperation("储能Copilot节点查询")
	@UserLoginToken
	@RequestMapping(value = "findEnergyStorageCopilotNode", method = {RequestMethod.POST})
	public ResponseResult<List<EnergyStorageCopilotNode>> findEnergyStorageCopilotNode() throws ParseException {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String dateStr = sdf.format(date);
		Date effectiveDate = sdf.parse(dateStr);
		Calendar calendar = Calendar.getInstance(); //获取当前日期
		calendar.setTime(effectiveDate);
		calendar.add(Calendar.DAY_OF_YEAR, 1); //加一天
		Date nextDay = calendar.getTime();
		List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
		List<String> nodeList = cfgStorageEnergyStrategyPower96Repository.findNode();
		nodeList.retainAll(allowStorageEnergyNodeIds);
		List<String> nodes = cfgStorageEnergyStrategyPower96Repository.findNoDistributionNode(nextDay);
		nodes.retainAll(allowStorageEnergyNodeIds);
		List<String> list = stationNodeRepository.findAll().stream().filter(v -> v.getSystemIds().contains("chuneng") && v.getStationState().equals(
				"运营中"))
				.map(StationNode::getStationId).collect(Collectors.toList());
		nodeList.retainAll(list);
		nodes.retainAll(list);
		List<EnergyStorageCopilotNode> energyStorageCopilotNodeList = new ArrayList<>();
		EnergyStorageCopilotNode energyStorageCopilotNode = new EnergyStorageCopilotNode();
		energyStorageCopilotNode.setNodeType("总节点数");
		energyStorageCopilotNode.setCnt(nodeList.size());
		EnergyStorageCopilotNode energyStorageCopilotNode1 = new EnergyStorageCopilotNode();
		energyStorageCopilotNode1.setNodeType("待生成节点数");
		energyStorageCopilotNode1.setCnt(nodeList.size() - nodes.size());
		energyStorageCopilotNodeList.add(energyStorageCopilotNode);
		energyStorageCopilotNodeList.add(energyStorageCopilotNode1);

		return ResponseResult.success(energyStorageCopilotNodeList);
	}

	@ApiOperation("能量块趋势图")
	@UserLoginToken
	@RequestMapping(value = "energyBlockTrend", method = {RequestMethod.POST})
	public ResponseResult<List<CopilotBlockResponse>> findEnergyStorageCopilotNode(@RequestBody CopilotRequest request) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = sdf.parse(request.getStartDate());
		Date endDate = sdf.parse(request.getEndDate());

		ExecutorService executorService = Executors.newFixedThreadPool(10);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);

		Date st = new Date();
		String pvNode1 = config.getPvNode1();
		String energyNode1 = config.getEnergyNode1();
		String energyNode2 = config.getEnergyNode2();
		String loadNode1 = config.getLoadNode1();

		Future<Map<Date, Double>> pvReal1Map_future = ConcurrentUtils.doJob(executorService,
				() -> iotTsKvMeteringDevice96Repository.findHTotalUseAndTimeBySystemIdAndNodeIde(pvNode1,
								"nengyuanzongbiao", "JL-001-load",
								"load",
								startDate, calendar.getTime())
						.stream().filter(device -> device[0] != null && device[1] != null)
						.collect(Collectors.toMap(o -> (Date) o[1], o -> (Double) o[0],
								(existing, replacement) -> replacement)));

		Future<Map<Date, String>> pvFore1Map_future = ConcurrentUtils.doJob(executorService,
				() -> aiLoadRepository.findPredictValueAndTimeByDateNodeIdSystemId(pvNode1, "nengyuanzongbiao", startDate,
								calendar.getTime())
						.stream().filter(device -> device[1] != null && device[0] != null)
						.collect(Collectors.toMap(o -> (Date) o[1], o -> (String) o[0],
								(existing, replacement) -> replacement)));

		Future<Map<Date, Double>> energyReal1Map_future = ConcurrentUtils.doJob(executorService,
				() -> iotTsKvMeteringDevice96Repository.findHTotalUseAndTimeBySystemIdAndNodeIde(energyNode1,
								"chuneng",
								"load001", "load", startDate, calendar.getTime())
						.stream().filter(device -> device[0] != null && device[1] != null)
						.collect(Collectors.toMap(o -> (Date) o[1], o -> (Double) o[0],
								(existing, replacement) -> replacement)));

		Future<Map<Date, Double>> energyReal2Map_future = ConcurrentUtils.doJob(executorService,
				() -> iotTsKvMeteringDevice96Repository.findHTotalUseAndTimeBySystemIdAndNodeIde(energyNode2, "chuneng",
								"load002", "load", startDate,
								calendar.getTime())
						.stream().filter(device -> device[0] != null && device[1] != null)
						.collect(Collectors.toMap(o -> (Date) o[1], o -> (Double) o[0],
								(existing, replacement) -> replacement)));


		Future<Map<Date, Double>> energySoc1Map_future = ConcurrentUtils.doJob(executorService,
				() -> biStorageEnergyLogRepository.findSocByNodeId(energyNode1,
								startDate,
								calendar.getTime())
						.stream().filter(device -> device[0] != null)
						.filter(v -> {
							Date createdDate = (Date) v[1];
							LocalDateTime createdTime = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());
							int minute = createdTime.getMinute();
							return minute == 0 || minute == 15 || minute == 30 || minute == 45;
						})
						.collect(Collectors.toMap(o -> (Date) o[1], o -> (Double) o[0],
								(existing, replacement) -> replacement)));


		Future<Map<Date, Double>> energySoc2Map_future = ConcurrentUtils.doJob(executorService,
				() -> biStorageEnergyLogRepository.findSocByNodeId(energyNode2, startDate, calendar.getTime()).stream().filter(device -> device[0] != null)
						.filter(v -> {
							Date createdDate = (Date) v[1];
							LocalDateTime createdTime = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());
							int minute = createdTime.getMinute();
							return minute == 0 || minute == 15 || minute == 30 || minute == 45;
						})
						.collect(Collectors.toMap(o -> (Date) o[1], o -> (Double) o[0],
								(existing, replacement) -> replacement)));


		Future<Map<Date, Double>> gateReal1Map_future = ConcurrentUtils.doJob(executorService,
				() -> iotTsKvMeteringDevice96Repository.findHTotalUseAndTimeBySystemIdAndNodeIde(loadNode1, "nengyuanzongbiao"
								, "GKB-load", "load",
								startDate, calendar.getTime()).stream()
						.filter(device -> device[0] != null && device[1] != null)
						.collect(Collectors.toMap(o -> (Date) o[1], o -> (Double) o[0],
								(existing, replacement) -> replacement)));

		Future<Map<Date, String>> gateFore1Map_future = ConcurrentUtils.doJob(executorService,
				() -> aiLoadRepository.findPredictValueAndTimeByDateNodeIdSystemId(loadNode1,
								"nengyuanzongbiao",
								startDate,
								calendar.getTime())
						.stream().filter(device -> device[1] != null && device[0] != null)
						.collect(Collectors.toMap(o -> (Date) o[1], o -> (String) o[0], (existing, replacement) -> replacement)));


		Map<Date, Double> pvReal1Map = ConcurrentUtils.futureGet(pvReal1Map_future);
		Map<Date, String> pvFore1Map = ConcurrentUtils.futureGet(pvFore1Map_future);
		Map<Date, Double> energyReal1Map = ConcurrentUtils.futureGet(energyReal1Map_future);
		Map<Date, Double> energyReal2Map = ConcurrentUtils.futureGet(energyReal2Map_future);
		Map<Date, Double> energySoc1Map = ConcurrentUtils.futureGet(energySoc1Map_future);
		Map<Date, Double> energySoc2Map = ConcurrentUtils.futureGet(energySoc2Map_future);
		Map<Date, Double> gateReal1Map = ConcurrentUtils.futureGet(gateReal1Map_future);
		Map<Date, String> gateFore1Map = ConcurrentUtils.futureGet(gateFore1Map_future);

		//储能预测
		CopilotBlockResponse energyFore1 = null;
		CopilotBlockResponse energyFore2 = null;

		if (request.getNodeId().equals(energyNode1)) {

			energyFore1 = new CopilotBlockResponse("储能001预测功率", false, request.getEnergyFore());

			List<CopilotResponse> energyFore2list = generateResponses(startDate, calendar.getTime());
			Map<Date, Double> energyFore2Map = cfgStorageEnergyStrategyPower96Repository.findAllBySystemIdAndNodeIdeBlock(energyNode2,
			"nengyuanzongbiao", startDate, calendar.getTime())
					.stream().filter(device -> device.getPower() != null).collect(Collectors.toMap(v1 -> {
						try {
							return sdf1.parse(v1.getEffectiveDate().toString().split(" ")[0] + " " + v1.getSTime() + ":00");
						} catch (ParseException e) {
							throw new RuntimeException(e);
						}
					}, CfgStorageEnergyStrategyPower96::getPower, (existing, replacement) -> replacement));
			energyFore2 = new CopilotBlockResponse("储能002预测功率", false,
					energyFore2list.stream().peek(response -> {
						Double value = energyFore2Map.get(response.getDate());
						if (value != null) {
							value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
						}
						response.setValue(value);
					}).collect(Collectors.toList()));
		} else if (request.getNodeId().equals(energyNode2)) {
			energyFore1 = new CopilotBlockResponse("储能002预测功率", false, request.getEnergyFore());

			List<CopilotResponse> energyFore2list = generateResponses(startDate, calendar.getTime());
			Map<Date, Double> energyFore2Map = cfgStorageEnergyStrategyPower96Repository.findAllBySystemIdAndNodeIdeBlock(energyNode1,
			"nengyuanzongbiao", startDate, calendar.getTime())
					.stream().filter(device -> device.getPower() != null).collect(Collectors.toMap(v1 -> {
						try {
							return sdf1.parse(v1.getEffectiveDate().toString().split(" ")[0] + " " + v1.getSTime() + ":00");
						} catch (ParseException e) {
							throw new RuntimeException(e);
						}
					}, CfgStorageEnergyStrategyPower96::getPower, (existing, replacement) -> replacement));
			energyFore2 = new CopilotBlockResponse("储能001预测功率", false,
					energyFore2list.stream().peek(response -> {
						Double value = energyFore2Map.get(response.getDate());
						if (value != null) {
							value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;

						}
						response.setValue(value);
					}).collect(Collectors.toList()));
		}

		//光伏实际
		List<CopilotResponse> pvReal1list = generateResponses(startDate, calendar.getTime());
		CopilotBlockResponse pvReal1 = new CopilotBlockResponse("光伏001实际功率", true,
				pvReal1list.stream().peek(response -> {
					Double value = pvReal1Map.get(response.getDate());
					if (value != null) {
						value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
					}
					response.setValue(value);
				}).collect(Collectors.toList()));

		//光伏预测
		List<CopilotResponse> pvFore1list = generateResponses(startDate, calendar.getTime());
		CopilotBlockResponse pvFore1 = new CopilotBlockResponse("光伏001预测功率", true,
				pvFore1list.stream().peek(response -> {
					Double value = pvFore1Map.get(response.getDate()) == null ? null : Double.valueOf(pvFore1Map.get(response.getDate()));
					if (value != null) {
						value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
					}
					response.setValue(value);
				}).collect(Collectors.toList()));


		//储能实际
		List<CopilotResponse> energyReal1list = generateResponses(startDate, calendar.getTime());
		CopilotBlockResponse energyReal1 = new CopilotBlockResponse("储能001实际功率", false,
				energyReal1list.stream().peek(response -> {
					Double value = energyReal1Map.get(response.getDate());
					if (value != null) {
						value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;

					}
					response.setValue(value);
				}).collect(Collectors.toList()));

		//储能实际
		List<CopilotResponse> energyReal2list = generateResponses(startDate, calendar.getTime());
		CopilotBlockResponse energyReal2 = new CopilotBlockResponse("储能002实际功率", false,
				energyReal2list.stream().peek(response -> {
					Double value = energyReal2Map.get(response.getDate());
					if (value != null) {
						value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;

					}
					response.setValue(value);
				}).collect(Collectors.toList()));


		//储能soc
		List<CopilotResponse> energySoc1list = generateResponses(startDate, calendar.getTime());
		CopilotBlockResponse energySoc1 = new CopilotBlockResponse("储能001 SOC", true,
				energySoc1list.stream().peek(response -> {
					Double value = energySoc1Map.get(response.getDate()) == null ? null : energySoc1Map.get(response.getDate()) * 100;
					if (value != null) {
						value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
					}
					response.setValue(value);
				}).collect(Collectors.toList()));

		List<CopilotResponse> energySoc2list = generateResponses(startDate, calendar.getTime());
		CopilotBlockResponse energySoc2 = new CopilotBlockResponse("储能002 SOC", true, energySoc2list.stream().peek(response -> {
			Double value = energySoc2Map.get(response.getDate()) == null ? null : energySoc2Map.get(response.getDate()) * 100;
			if (value != null) {
				value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
			}
			response.setValue(value);
		}).collect(Collectors.toList()));

		//关口表实际
		List<CopilotResponse> gateReal1list = generateResponses(startDate, calendar.getTime());
		CopilotBlockResponse gateReal1 = new CopilotBlockResponse("实际负荷", true, gateReal1list.stream().peek(response -> {
			Double gateValue = gateReal1Map.get(response.getDate());
			Double energy1Value = energyReal1Map.get(response.getDate());
			Double energy2Value = energyReal2Map.get(response.getDate());
			if (gateValue == null && energy1Value == null && energy2Value == null) {
				response.setValue(null);
			} else {
				Double finalGateValue = Optional.ofNullable(gateValue).orElse(0.0);
				Double finalEnergy1Value = Optional.ofNullable(energy1Value).orElse(0.0);
				Double finalEnergy2Value = Optional.ofNullable(energy2Value).orElse(0.0);

				Double value = finalGateValue + finalEnergy1Value + finalEnergy2Value;
				value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;

				response.setValue(value);
			}
		}).collect(Collectors.toList()));

		//关口表预测
		List<CopilotResponse> gateFore1list = generateResponses(startDate, calendar.getTime());
		CopilotBlockResponse gateFore1 = new CopilotBlockResponse("预测负荷", true,
				gateFore1list.stream().peek(response -> {
					Double value = gateFore1Map.get(response.getDate()) == null ? null : Double.valueOf(gateFore1Map.get(response.getDate()));
					if (value != null) {
						value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
					}
					response.setValue(value);
				}).collect(Collectors.toList()));


		List<CopilotBlockResponse> copilotResponseList = new ArrayList<>();
		copilotResponseList.add(energySoc1);
		copilotResponseList.add(energySoc2);
		copilotResponseList.add(gateReal1);
		copilotResponseList.add(gateFore1);
		copilotResponseList.add(pvReal1);
		copilotResponseList.add(pvFore1);
		copilotResponseList.add(energyReal1);
		copilotResponseList.add(energyFore1);
		copilotResponseList.add(energyReal2);
		copilotResponseList.add(energyFore2);

		Date et = new Date();
		log.info("能量块查询总耗时：{}", et.getTime() - st.getTime());

		executorService.shutdown();
		return ResponseResult.success(copilotResponseList);
	}

	public static List<CopilotResponse> generateResponses(Date startDate, Date endDate) throws ParseException {
		List<CopilotResponse> responses = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);

		while (!calendar.getTime().equals(endDate)) {
			responses.add(new CopilotResponse(sdf.parse(sdf.format(calendar.getTime())), null));
			calendar.add(Calendar.MINUTE, 15);
		}

		return responses;
	}

	@ApiOperation("能量块趋势图新")
	@UserLoginToken
	@RequestMapping(value = "energyBlockTrendNew", method = {RequestMethod.POST})
	public ResponseResult<List<CopilotBlockResponseNew>> energyBlockTrendNew(@RequestBody CopilotRequest request) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		LocalDate startDateLocal = LocalDate.parse(request.getStartDate(), dateFormatter);
		LocalDate endDateLocal = LocalDate.parse(request.getEndDate(), dateFormatter).plusDays(1);

		Date startDate = Date.from(startDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

		Date st = new Date();
		String nodeId = request.getNodeId();
		ListProjSubEnergyAndPvVo energyBlockList = globalApiService.stationTreeEnergyAndPv(nodeId);
		String mNId = energyBlockList.getNodeId();
		Map<String, String> energyNodeMap = energyBlockList.getEnergy().stream()
				.collect(Collectors.toMap(
						ListProjSubEnergyAndPvVo::getNodeId,
						ListProjSubEnergyAndPvVo::getStationName,
						(oldValue, newValue) -> oldValue
				));
		List<String> energyNodeList = new ArrayList<>(energyNodeMap.keySet());
		Map<String, String> pvNodeMap = energyBlockList.getPhotovoltaic().stream()
				.collect(Collectors.toMap(
						ListProjSubEnergyAndPvVo::getNodeId,
						ListProjSubEnergyAndPvVo::getStationName,
						(oldValue, newValue) -> oldValue
				));
		List<String> loadNodeList = new ArrayList<>(pvNodeMap.keySet());

		List<CopilotBlockResponseNew> copilotResponseList = new ArrayList<>();

		PointModelMappingRepository pointModelMappingRepository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);
		PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);
		List<PointModelMapping> mappings =
				pointModelMappingRepository.findAllByStation_StationId(mNId).stream().filter(o -> o.getPointModel().getKey().equals("predict_value") || o.getPointModel().getKey().equals("total_load")).collect(Collectors.toList());

		for (PointModelMapping mapping : mappings) {
			Map<Date, ?> res = pointService.getDValuesByMTime(mapping, startDate, endDate);
			List<CopilotResponse> dataList = generateResponseNew(startDate, endDate);
			dataList.forEach(response -> {
				Double value = (Double) res.get(response.getDate());
				if (value != null) {
					value = value >= 0 ? BigDecimal.valueOf(value)
							.setScale(2, RoundingMode.HALF_UP)
							.doubleValue() : null;
				}
				response.setValue(value);
			});
			CopilotBlockResponseNew response = new CopilotBlockResponseNew(mapping.getStation().getStationName(),
					mapping.getStation().getStationId(), mapping.getPointModel().getKey().equals("predict_value") ? "预测负荷" : "实际负荷",
					"load", true,
					dataList);
			copilotResponseList.add(response);
		}

		energyNodeList.forEach(energyNode -> {
			List<PointModelMapping> energyMappings =
					pointModelMappingRepository.findAllByStation_StationId(energyNode).stream().filter(o -> o.getPointModel().getKey().equals(
							"energy_predict_value") || o.getPointModel().getKey().equals("power") || o.getPointModel().getKey().equals("soc")).collect(Collectors.toList());
			for (PointModelMapping mapping : energyMappings) {
				Map<Date, ?> res = pointService.getValuesByTime(mapping, startDate, endDate);
				List<CopilotResponse> dataList = generateResponseNew(startDate, endDate);
				dataList.forEach(response -> {
					Double value = (Double) res.get(response.getDate());
					if (value != null && mapping.getPointModel().equals("soc")) {
						value = value >= 0 ? BigDecimal.valueOf(value * 100.0)
								.setScale(2, RoundingMode.HALF_UP)
								.doubleValue() : null;
					}
					response.setValue(value);
				});
				CopilotBlockResponseNew response = new CopilotBlockResponseNew(mapping.getStation().getStationName(),
						mapping.getStation().getStationId(), mapping.getPointModel().getPointNameZh(),
						mapping.getPointModel().getKey().equals("soc") ? "soc" : "energy", false,
						dataList);
				copilotResponseList.add(response);
			}

		});

		loadNodeList.forEach(energyNode -> {
			List<PointModelMapping> pvMappings =
					pointModelMappingRepository.findAllByStation_StationId(energyNode).stream().filter(o -> o.getPointModel().getKey().equals(
							"predict_value") || o.getPointModel().getKey().equals("power")).collect(Collectors.toList());
			for (PointModelMapping mapping : pvMappings) {
				Map<Date, ?> res = pointService.getValuesByTime(mapping, startDate, endDate);
				List<CopilotResponse> dataList = generateResponseNew(startDate, endDate);
				dataList.forEach(response -> {
					Double value = (Double) res.get(response.getDate());
					if (value != null) {
						value = value >= 0 ? BigDecimal.valueOf(value)
								.setScale(2, RoundingMode.HALF_UP)
								.doubleValue() : null;
					}
					response.setValue(value);
				});
				CopilotBlockResponseNew response = new CopilotBlockResponseNew(mapping.getStation().getStationName(),
						mapping.getStation().getStationId(), mapping.getPointModel().getPointNameZh(),
						"pv", false,
						dataList);
				copilotResponseList.add(response);
			}

		});

		Date et = new Date();
		log.info("能量块查询总耗时：{}", et.getTime() - st.getTime());

		return ResponseResult.success(copilotResponseList);
	}

	public static List<CopilotResponse> generateResponseNew(Date startDate, Date endDate) {
		if (startDate == null || endDate == null || startDate.after(endDate)) {
			throw new IllegalArgumentException("起始日期必须早于结束日期，且两者不能为空。");
		}
		final long intervalMillis = 15 * 60 * 1000;
		long startMillis = startDate.getTime();
		long endMillis = endDate.getTime();
		int estimatedSize = (int) ((endMillis - startMillis) / intervalMillis) + 1;
		List<CopilotResponse> responses = new ArrayList<>(estimatedSize);
		Date currentDate = new Date(startMillis);
		for (long currentTimeMillis = startMillis; currentTimeMillis < endMillis; currentTimeMillis += intervalMillis) {
			currentDate.setTime(currentTimeMillis);
			responses.add(new CopilotResponse(new Date(currentDate.getTime()), null));
		}
		return responses;
	}

	@ApiOperation("资源调度-预测收益")
	@UserLoginToken
	@RequestMapping(value = "blackProfitAll", method = {RequestMethod.POST})
	public ResponseResult<List<CopilotBlockResponse>> findBlackProfitAll(@RequestBody BlackProfitRequest request) {
		String mNId = request.getNodeId();
		ListProjSubEnergyAndPvVo energyBlockList = globalApiService.stationTreeEnergyAndPv(mNId);
		Map<String, String> energyNodeMap = energyBlockList.getEnergy().stream()
				.collect(Collectors.toMap(
						ListProjSubEnergyAndPvVo::getNodeId,
						ListProjSubEnergyAndPvVo::getStationName,
						(oldValue, newValue) -> oldValue
				));
		List<String> enNodeList = new ArrayList<>(energyNodeMap.keySet());
		Map<String, String> loadNodeMap = energyBlockList.getPhotovoltaic().stream()
				.collect(Collectors.toMap(
						ListProjSubEnergyAndPvVo::getNodeId,
						ListProjSubEnergyAndPvVo::getStationName,
						(oldValue, newValue) -> oldValue
				));
		List<String> pvNodeList = new ArrayList<>(loadNodeMap.keySet());
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		LocalDate startDateLocal = LocalDate.parse(request.getStartDate(), dateFormatter);
		LocalDate endDateLocal = LocalDate.parse(request.getEndDate(), dateFormatter);

		Date startDate = Date.from(startDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<CopilotBlockResponse> responseList = new ArrayList<>();
		Date startDateM = TimeUtil.getMonthStart(startDate);
		List<CopilotResponse> enFValue = new ArrayList<>();
		List<CopilotResponse> enRValue = new ArrayList<>();
		for (String nodeId : enNodeList) {
			String nodeName = energyNodeMap.get(nodeId);
			List<CfgStorageEnergyStrategy> energyStrategyList = cfgStorageEnergyStrategyRepository.findCfgStorageEnergyStrategyByNodeId(nodeId,
			 startDate, endDate);
			List<PriceData> prices =
			 energyStrategyList.stream().map(o -> new PriceData(o.getTimeFrame(), o.getPriceHour())).collect(Collectors.toList());
			// 储能预测收益 此处获取为功率，需要/4 = 每15分钟消耗电量
			List<Double> energyStoragePredicateList =
					cfgStorageEnergyStrategyPower96Repository.findAllByNodeIde(nodeId, startDate, endDate)
							.stream().map(CfgStorageEnergyStrategyPower96::getPower).map(o -> o / 4).collect(Collectors.toList());

			enFValue = calculateProfitChart(energyStoragePredicateList, prices, startDate, endDate);

			CopilotBlockResponse enFResponse = new CopilotBlockResponse(nodeName + "预测收益", false, enFValue);
			// 储能实际收益
			try {
				List<Double> energyStorageActualList = getEnergyStorageActualData(nodeId, startDate, endDate);
				enRValue = calculateProfitChart(energyStorageActualList, prices, startDate, endDate);
			} catch (Exception e) {
				log.error("获取储能实际收益失败，设为空值", e);
			}
			CopilotBlockResponse enRResponse = new CopilotBlockResponse(nodeName + "实际收益", false, enRValue);
			responseList.add(enFResponse);
			responseList.add(enRResponse);
		}
		List<CopilotResponse> pvFValue = new ArrayList<>();
		List<CopilotResponse> pvRValue = new ArrayList<>();
		for (String nodeId : pvNodeList) {
			String nodeName = energyNodeMap.get(nodeId);

			// 光伏价格
			List<CfgPhotovoltaicTouPrice> pvPrice = cfgPhotovoltaicTouPriceRepository.findAllByNodeIdAndEffectiveDateOld(nodeId, startDateM);
			List<PriceData> prices = pvPrice.stream().map(o -> new PriceData(o.getTimeFrame(), o.getPriceHour())).collect(Collectors.toList());

			// 光伏预测
			// 此处获取为功率，需要/4 = 每15分钟消耗电量
			List<Object[]> pvForecastData = aiLoadRepository.findAllByNodeIdAndSystemIdAndCountDataTimeBetween(nodeId, startDate, endDate);
			pvForecastData.sort(Comparator.comparing(o -> o[1].toString()));

			List<Double> predicateList = pvForecastData.stream().map(o -> Double.parseDouble((String) o[0]) / 4).collect(Collectors.toList());

			pvFValue = calculateProfitChart(predicateList, prices, startDate, endDate);
			CopilotBlockResponse pvFResponse = new CopilotBlockResponse(nodeName + "预测收益", false, pvFValue);

			// 光伏实际
			List<Object[]> device96s = iotTsKvMeteringDevice96Repository.findAllByNodeIdAndPointDescAndCountDataTime(nodeId, "energy",
					startDate,
					endDate);
			device96s.sort(Comparator.comparing(o -> o[1].toString()));
			List<Double> actualList = device96s.stream().map(o -> (Double) o[0]).collect(Collectors.toList());

			pvRValue = calculateProfitChart(actualList, prices, startDate, endDate);
			CopilotBlockResponse pvRResponse = new CopilotBlockResponse(nodeName + "实际收益", false, pvRValue);

			responseList.add(pvFResponse);
			responseList.add(pvRResponse);
		}
		List<CopilotResponse> sumFValue = new ArrayList<>();
		for (int i = 0; i < pvFValue.size(); i++) {
			Date date = pvFValue.get(i).getDate();
			double sumValue = pvFValue.get(i).getValue() + enFValue.get(i).getValue();
			sumFValue.add(new CopilotResponse(date, sumValue));
		}
		CopilotBlockResponse sumFResponse = new CopilotBlockResponse("总预测收益", false, sumFValue);
		responseList.add(sumFResponse);

		List<CopilotResponse> sumRValue = new ArrayList<>();
		for (int i = 0; i < pvFValue.size(); i++) {
			Date date = pvFValue.get(i).getDate();
			double sumValue = pvRValue.get(i).getValue() + enRValue.get(i).getValue();
			sumFValue.add(new CopilotResponse(date, sumValue));
		}
		CopilotBlockResponse sumRResponse = new CopilotBlockResponse("总实际收益", false, sumRValue);
		responseList.add(sumRResponse);
		return ResponseResult.success(responseList);
	}

	private List<Double> getEnergyStorageActualData(String nodeId, Date startDate, Date endDate) {
		List<Double> energyStorageActualList = new ArrayList<>();

		CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findCfgStorageEnergyBaseInfoByNodeId(nodeId);
		String chargingDeviceSn = baseInfo.getChargingDeviceSn();
		String dischargingDeviceSn = baseInfo.getDischargingDeviceSn();

		DevicePoint inDevicePoint = devicePointRepository.findByDeviceSnAndPointDesc(chargingDeviceSn, "energy");

		DevicePoint outDevicePoint = devicePointRepository.findByDeviceSnAndPointDesc(dischargingDeviceSn, "energy");

		List<Object[]> inDevicePointList = new ArrayList<>();
		List<Object[]> outDevicePointList = new ArrayList<>();
		if (inDevicePoint != null) {
			String chargingPointSn = inDevicePoint.getPointSn();
			inDevicePointList = iotTsKvMeteringDevice96Repository.findHTotalUseAndTimeScope(nodeId, chargingDeviceSn, chargingPointSn, startDate,
			 endDate);
			inDevicePointList.sort(Comparator.comparing(o -> o[1].toString()));
		}

		if (outDevicePoint != null) {
			String chargingPointSn = outDevicePoint.getPointSn();
			outDevicePointList = iotTsKvMeteringDevice96Repository.findHTotalUseAndTimeScope(nodeId, dischargingDeviceSn, chargingPointSn, startDate
			, endDate);
			outDevicePointList.sort(Comparator.comparing(o -> o[1].toString()));
		}

		int len;

		if (inDevicePointList.size() != outDevicePointList.size()) {
			len = Math.min(inDevicePointList.size(), outDevicePointList.size());
		} else {
			len = inDevicePointList.size();
		}

		for (int i = 0; i < len; i++) {
			energyStorageActualList.add(Double.parseDouble(outDevicePointList.get(i)[0].toString()) - Double.parseDouble(inDevicePointList.get(i)[0].toString()));
		}
		return energyStorageActualList;
	}

	private static List<CopilotResponse> calculateProfitChart(List<Double> startegyList, List<PriceData> priceDataList, Date date, Date endDate) {
		BigDecimal sum = BigDecimal.ZERO;
		priceDataList.sort(Comparator.comparing(PriceData::getTimeFrame));
		List<CopilotResponse> values = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		for (int i = 0, j = 0; (i < startegyList.size()) & (j < priceDataList.size()); ) {
			CopilotResponse value = new CopilotResponse();
			j = i / 4;
			while (j >= 24) {
				j -= 24;
			}
			BigDecimal v = BigDecimal.valueOf(startegyList.get(i)).multiply(priceDataList.get(j).getPrice());
			sum = sum.add(v);
			value.setValue(v.doubleValue());
			value.setDate(calendar.getTime());
			values.add(value);
			i++;
			calendar.add(Calendar.MINUTE, 15);
		}
		// 填充无数据部分
		CopilotResponse end = new CopilotResponse();
		if (!values.isEmpty()) {
			end = values.get(values.size() - 1);
		}

		while (calendar.getTime().before(endDate)) {
			CopilotResponse addPart = new CopilotResponse(end);
			addPart.setDate(calendar.getTime());
			addPart.setValue(null);
			values.add(addPart);
			calendar.add(Calendar.MINUTE, 15);
		}

		log.info(String.valueOf(sum));
		return values;
	}

	@ApiOperation("查询储能AI策略")
	@UserLoginToken
	@RequestMapping(value = "findAIStorageEnergystrategy", method = {RequestMethod.POST})
	public ResponseResult<EnergyStorageOverviewResp> findAIStorageEnergystrategy(@RequestBody @Valid StorageEnergyBaseInfoReq storageEnergyBaseInfoReq) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = sdf.parse(storageEnergyBaseInfoReq.getStartDate());
			Date endDate = sdf.parse(storageEnergyBaseInfoReq.getEndDate());
			Date startDateM = TimeUtil.getMonthStart(startDate);
			Date endDateM = TimeUtil.getMonthEnd(endDate);
//			BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(storageEnergyBaseInfoReq.getNodeId(), startDateM, endDateM);
			PropertyTotal propertyTotal = new PropertyTotal();
//            propertyTotal.setPriceHigh(BigDecimal.valueOf(billNodeProfit.getPriceHigh()));
//            propertyTotal.setPricePeak(BigDecimal.valueOf(billNodeProfit.getPricePeak()));
//            propertyTotal.setPriceStable(BigDecimal.valueOf(billNodeProfit.getPriceStable()));
//            propertyTotal.setPriceLow(BigDecimal.valueOf(billNodeProfit.getPriceLow()));

			if (!TimeUtil.isLegalDate(storageEnergyBaseInfoReq.getStartDate().length(), storageEnergyBaseInfoReq.getStartDate(), "yyyy-MM-dd")) {
				return ResponseResult.error("开始日期格式不正确");
			}
			if (!TimeUtil.isLegalDate(storageEnergyBaseInfoReq.getEndDate().length(), storageEnergyBaseInfoReq.getEndDate(), "yyyy-MM-dd")) {
				return ResponseResult.error("结束日期格式不正确");
			}
			String energyNode = config.getFindAIStorageEnergystrategy();
			String nodeId = storageEnergyBaseInfoReq.getNodeId();
			if (!energyNode.contains(nodeId)) {
				return ResponseResult.error("该储能节点暂不支持");
			}
			String systemId = storageEnergyBaseInfoReq.getSystemId();
			//得到用户权限下储能的节点列表
			List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
			if (!allowStorageEnergyNodeIds.contains(nodeId)) {
				return ResponseResult.error("储能节点不存在或者没有该节点权限");
			}
			if (StringUtils.isNotEmpty(nodeId) && StringUtils.isNotEmpty(systemId)) {
				CfgStorageEnergyBaseInfo cfgStorageEnergyBaseInfo = baseInfoRepository.findCfgStorageEnergyBaseInfoByNodeId(nodeId);
				List<CfgStorageEnergyStrategyPower96> cfgStorageEnergyStrategyPower96List =
				 cfgStorageEnergyStrategyPower96Repository.findAllBySystemIdAndNodeIde(nodeId, systemId, startDate, endDate);
				List<Date> dateList = getDateRangeList(storageEnergyBaseInfoReq.getStartDate(), storageEnergyBaseInfoReq.getEndDate());
				List<EnergyStorageSubView> energyStorageSubViewList = new ArrayList<>();
				EnergyStorageOverviewResp energyStorageOverviewResp = new EnergyStorageOverviewResp();
				List<EnergyStorageProperty> energyStoragePropertyList = new ArrayList<>();
				List<CfgStorageEnergyStrategy> energyStrategyList =
				 cfgStorageEnergyStrategyRepository.findCfgStorageEnergyStrategyByNodeIdAndSystemId(nodeId, systemId, startDateM, startDateM);
				for (CfgStorageEnergyStrategy cfgStorageEnergyStrategy : energyStrategyList) {
					EnergyStorageProperty energyStorageProperty = new EnergyStorageProperty();

					//把00:59改成01:00
					SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
					Date parse = dateFormat.parse(cfgStorageEnergyStrategy.getETime());
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(parse);
					calendar.add(Calendar.MINUTE, 1);
					Date newtime = calendar.getTime();
					SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
					String dateString2 = formatter2.format(newtime);

					energyStorageProperty.setTimeFrame(cfgStorageEnergyStrategy.getSTime() + '-' + dateString2);
					energyStorageProperty.setPriceHour(cfgStorageEnergyStrategy.getPriceHour());
					energyStorageProperty.setProperty(cfgStorageEnergyStrategy.getProperty());
					energyStoragePropertyList.add(energyStorageProperty);

					if (cfgStorageEnergyStrategy.getProperty().equals("尖")) {
						propertyTotal.setPriceHigh(cfgStorageEnergyStrategy.getPriceHour().setScale(8, RoundingMode.HALF_UP));
					}
					if (cfgStorageEnergyStrategy.getProperty().equals("峰")) {
						propertyTotal.setPricePeak(cfgStorageEnergyStrategy.getPriceHour().setScale(8, RoundingMode.HALF_UP));
					}
					if (cfgStorageEnergyStrategy.getProperty().equals("平")) {
						propertyTotal.setPriceStable(cfgStorageEnergyStrategy.getPriceHour().setScale(8, RoundingMode.HALF_UP));
					}
					if (cfgStorageEnergyStrategy.getProperty().equals("谷")) {
						propertyTotal.setPriceLow(cfgStorageEnergyStrategy.getPriceHour().setScale(8, RoundingMode.HALF_UP));
					}
				}
				if (cfgStorageEnergyStrategyPower96List.isEmpty()) {
					if (!energyStrategyList.isEmpty()) {
						//这是老数据，老数据只有每月一号的数据，24条
						for (Date countDate : dateList) {
							EnergyStorageSubView energyStorageSubView = new EnergyStorageSubView();
							SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
							String countDateStr = sdf1.format(countDate);
							energyStorageSubView.setDate(countDateStr);
							List<NodeChargeDischargeInfo> nodeChargeDischargeInfos = new ArrayList<>();
							for (CfgStorageEnergyStrategy cfgStorageEnergyStrategy : energyStrategyList) {
								SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
								String dateString2 = sdf2.format(cfgStorageEnergyStrategy.getEffectiveDate());
//                            if (countDateStr.equals(dateString2)) {
								//一个小时的数据
								List<String> fifteenMinutes = new ArrayList<>();
								String hour = cfgStorageEnergyStrategy.getSTime().substring(0, 2);
								int hourInt = Integer.parseInt(hour) + 1;
								String hour1 = hourInt >= 10 ? String.valueOf(hourInt) : "0" + String.valueOf(hourInt);
								fifteenMinutes.add(hour + ":00");
								fifteenMinutes.add(hour + ":15");
								fifteenMinutes.add(hour + ":30");
								fifteenMinutes.add(hour + ":45");
								fifteenMinutes.add(hour1 + ":00");

								for (int m = 0; m < 4; m++) {
									NodeChargeDischargeInfo nodeChargeDischargeInfo = new NodeChargeDischargeInfo();
									nodeChargeDischargeInfo.setPower(cfgStorageEnergyBaseInfo.getStorageEnergyLoad());
									nodeChargeDischargeInfo.setTime(fifteenMinutes.get(m) + "-" + fifteenMinutes.get(m + 1));
									if (cfgStorageEnergyStrategy.getStrategy() == null) {
										nodeChargeDischargeInfo.setType("待机");
									} else {
										nodeChargeDischargeInfo.setType(cfgStorageEnergyStrategy.getStrategy());
									}
									nodeChargeDischargeInfo.setPolicyModel(0);
									nodeChargeDischargeInfos.add(nodeChargeDischargeInfo);
								}
//                            }
							}
							energyStorageSubView.setNodeChargeDischargeInfos(nodeChargeDischargeInfos);
							energyStorageSubViewList.add(energyStorageSubView);
						}
					}
				} else {
					for (Date countDate : dateList) {
						EnergyStorageSubView energyStorageSubView = new EnergyStorageSubView();
						SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
						String countDateStr = sdf1.format(countDate);
						energyStorageSubView.setDate(countDateStr);
						List<NodeChargeDischargeInfo> nodeChargeDischargeInfos = new ArrayList<>();
						for (CfgStorageEnergyStrategyPower96 cfgStorageEnergyStrategyPower96 : cfgStorageEnergyStrategyPower96List) {
							SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
							String dateString2 = sdf2.format(cfgStorageEnergyStrategyPower96.getEffectiveDate());
							if (countDateStr.equals(dateString2)) {
								NodeChargeDischargeInfo nodeChargeDischargeInfo = new NodeChargeDischargeInfo();
								nodeChargeDischargeInfo.setPower(cfgStorageEnergyStrategyPower96.getPower());
								nodeChargeDischargeInfo.setTime(cfgStorageEnergyStrategyPower96.getTimeScope());
								if (cfgStorageEnergyStrategyPower96.getStrategy() == null) {
									nodeChargeDischargeInfo.setType("待机");
								} else {
									nodeChargeDischargeInfo.setType(cfgStorageEnergyStrategyPower96.getStrategy());
								}
								nodeChargeDischargeInfo.setPolicyModel(cfgStorageEnergyStrategyPower96.getPolicyModel());
								nodeChargeDischargeInfos.add(nodeChargeDischargeInfo);
							}
						}
						energyStorageSubView.setNodeChargeDischargeInfos(nodeChargeDischargeInfos);
						energyStorageSubViewList.add(energyStorageSubView);
					}
				}
				energyStorageOverviewResp.setEnergyStoragePropertyList(energyStoragePropertyList);
				energyStorageOverviewResp.setEnergyStorageSubViews(energyStorageSubViewList);
				energyStorageOverviewResp.setPropertyTotal(propertyTotal);
				return ResponseResult.success(energyStorageOverviewResp);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("当前节点，系统下无数据，请先初始化数据。");
		}
		return ResponseResult.success(null);
	}

	private CfgStorageEnergyStrategyPower96Log toInitLogInfo(Date dateId, CfgStorageEnergyStrategyPower96 power96) {
		CfgStorageEnergyStrategyPower96Log log = new CfgStorageEnergyStrategyPower96Log();
		log.setId("Init" + UUID.randomUUID());
		log.setNodeId(power96.getNodeId());
		log.setSystemId(power96.getSystemId());
		log.setEffectiveDate(power96.getEffectiveDate());
		log.setTimeScope(power96.getTimeScope());
		log.setSTime(power96.getSTime());
		log.setETime(power96.getETime());
		log.setPower(power96.getPower());
		log.setStrategy(power96.getStrategy());
		log.setDistributeStatus(power96.getDistributeStatus());
		log.setStrategyType("初始策略");
		log.setCreateTime(dateId);
		return log;
	}

	private void newEnergyStorageChargingAndDischargingStrategyControl(String deviceSn, String pointSn, Strategy96Model strategy96Model) {

		if (StringUtils.isEmpty(deviceSn) || StringUtils.isEmpty(pointSn) || strategy96Model == null || strategy96Model.getCfStrategy() == null || strategy96Model.getCfStrategy().length != 96) {
			return;
		}

		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < strategy96Model.getCfStrategy().length; i++) {

			Every15MinuteModel cfStrategy = strategy96Model.getCfStrategy()[i];
			if (cfStrategy == null) {
				//打印错误
				return;
			}
			int strategyLoad = cfStrategy.getStrategyLoad();
			int strategyType = cfStrategy.getStrategyType();
			sb.append(strategyLoad * strategyType).append(",");
		}
		sb.append(strategy96Model.isImmediateEffectiveness() ? "1" : "0");

		List<PointInfo> pointInfoList = new ArrayList<>();
		pointInfoList.add(new PointInfo(pointSn, sb.toString()));
		List<RPCModel> models = new ArrayList<>();
		models.add(new RPCModel(deviceSn, pointInfoList, new Date()));

		iotControlService.CommonRPCRequestToDevice(ModuleNameEnum.storage_tactics.name(), models);
	}

	@ApiOperation("复制储能其它月份充放电策略")
	@UserLoginToken
	@Transactional
	@RequestMapping(value = "copyStorageEnergyStrategy", method = {RequestMethod.POST})
	public ResponseResult findStorageEnergyStrategy(@RequestBody StorageEnergyStrategyCopyModel model) {
		try {
			List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
			if (!allowStorageEnergyNodeIds.contains(model.getNodeId())) {
				return ResponseResult.error("储能节点不存在或者没有该节点权限");
			}
			SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM");
			ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			Date fromStart = sdf.parse(model.getFromStartDate());
			Date fromEnd = sdf.parse(model.getFromEndDate());
			Date toStart = sdf.parse(model.getToStartDate());
			Date toEnd = sdf.parse(model.getToEndDate());

			LocalDate localDate = LocalDate.parse(model.getFromStartDate());
			LocalDate day1 = LocalDate.of(localDate.getYear(), localDate.getMonth(), 1);
			Instant instant1 = Timestamp.valueOf(day1.atTime(LocalTime.MIDNIGHT)).toInstant();
			Date fromStart1 = Date.from(instant1);

			LocalDate localDate1 = LocalDate.parse(model.getToStartDate());
			LocalDate toStartDate1 = LocalDate.of(localDate1.getYear(), localDate1.getMonth(), 1);
			Instant instant2 = Timestamp.valueOf(toStartDate1.atTime(LocalTime.MIDNIGHT)).toInstant();
			Date toStart1 = Date.from(instant2);

			Date now = new Date();
			String nowStr = ym.format(now);
			Date mouth = ym.parse(nowStr);
			if (mouth.after(ym.parse(model.getToStartDate()))) {
				return ResponseResult.error("不能修改过去节点月份");
			}
			List<CfgStorageEnergyStrategyPower96> power96List = cfgStorageEnergyStrategyPower96Repository.findAllBySystemIdAndNodeIde(
					model.getNodeId(),
					model.getSystemId(),
					fromStart,
					fromEnd);
			List<CfgStorageEnergyStrategyPower96> power96Exits = cfgStorageEnergyStrategyPower96Repository.findAllBySystemIdAndNodeIde(
					model.getNodeId(),
					model.getSystemId(),
					toStart,
					toEnd);
			if (power96List.size() == 0) {
				return ResponseResult.error("请检查复制月份充放电策略是否存在");
			}
			if (power96Exits.size() == 0) {
				return ResponseResult.error("当月的电价策略超过运营年限,不能复制");
			}

			List<Date> formDateList = getDateRangeList(model.getFromStartDate(), model.getFromEndDate());
			List<String> formDateStrList = new ArrayList<>();
			for (Date date : formDateList) {
				formDateStrList.add(sdf.format(date));
			}
			List<Date> toDateList = getDateRangeList(model.getToStartDate(), model.getToEndDate());
			List<String> toDateStrList = new ArrayList<>();
			for (Date date : toDateList) {
				toDateStrList.add(sdf.format(date));
			}

			List<CfgStorageEnergyStrategyPower96> strategyPowers = new ArrayList<>();
			for (CfgStorageEnergyStrategyPower96 cfgStorageEnergyStrategyPower96 : power96List) {
				String formDate = sdf.format(cfgStorageEnergyStrategyPower96.getEffectiveDate());
				for (CfgStorageEnergyStrategyPower96 toCfgStorageEnergyStrategyPower96 : power96Exits) {
					String toDate = sdf.format(toCfgStorageEnergyStrategyPower96.getEffectiveDate());
					if (formDateStrList.indexOf(formDate) == toDateStrList.indexOf(toDate) && cfgStorageEnergyStrategyPower96.getSTime().equals(toCfgStorageEnergyStrategyPower96.getSTime())) {
						toCfgStorageEnergyStrategyPower96.setPower(cfgStorageEnergyStrategyPower96.getPower());
						toCfgStorageEnergyStrategyPower96.setStrategy(cfgStorageEnergyStrategyPower96.getStrategy());
						strategyPowers.add(toCfgStorageEnergyStrategyPower96);
					}
				}
			}
			return ResponseResult.success();
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("查询储能充放电策略失败!");
		}
	}

	@ApiOperation("新老数据迁移")
	@UserLoginToken
	@Transactional
	@RequestMapping(value = "old_new", method = {RequestMethod.POST})
	public ResponseResult oldNew(@RequestBody StorageEnergyStrategyCopyModel info) {
		try {
			if (info != null && StringUtils.isNotEmpty(info.getNodeId()) && StringUtils.isNotEmpty(info.getSystemId())) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
				String nodeId = info.getNodeId();
				String systemId = info.getSystemId();
				String id = nodeId + "_" + systemId;
				CfgStorageEnergyBaseInfo cfgStorageEnergyBaseInfo = baseInfoRepository.findById(id).orElse(null);
				double power;
				if (cfgStorageEnergyBaseInfo != null) {
					power = cfgStorageEnergyBaseInfo.getStorageEnergyLoad();
				} else {
					power = 0.0;
				}
				//查找老数据表
//                List<CfgStorageEnergyStrategy> cfgStorageEnergyStrategies = strategyRepository.findCfgStorageEnergyByNodeIdAndSystemId(
//                        info.getNodeId(),
//                        info.getSystemId());
				List<CfgStorageEnergyStrategy> cfgStorageEnergyStrategies =
				 strategyRepository.findCfgStorageEnergyStrategyByNodeIdAndSystemId(info.getNodeId(), info.getSystemId(),
				  sdf.parse(info.getFromStartDate()), sdf.parse(info.getFromEndDate()));
				for (CfgStorageEnergyStrategy cfgStorageEnergyStrategy : cfgStorageEnergyStrategies) {
					List<String> fifteenMinutes = new ArrayList<>();

					List<CfgStorageEnergyStrategyPower96> strategyPowers = new ArrayList<>();
					LocalDate localDate = LocalDate.parse(sdf.format(cfgStorageEnergyStrategy.getEffectiveDate()));
					YearMonth yearMonth = YearMonth.from(localDate);
					int totalDays = yearMonth.lengthOfMonth();

					for (int day = 1; day <= totalDays; day++) {
						//获取到每一天的日期
						LocalDate day1 = LocalDate.of(localDate.getYear(), localDate.getMonth(), day);
						Instant instant1 = Timestamp.valueOf(day1.atTime(LocalTime.MIDNIGHT)).toInstant();
						Date effective = Date.from(instant1);

						String hour = cfgStorageEnergyStrategy.getSTime().substring(0, 2);
						int hourInt = Integer.parseInt(hour) + 1;
						String hour1 = hourInt >= 10 ? String.valueOf(hourInt) : "0" + String.valueOf(hourInt);
						fifteenMinutes.add(hour + ":00");
						fifteenMinutes.add(hour + ":15");
						fifteenMinutes.add(hour + ":30");
						fifteenMinutes.add(hour + ":45");
						fifteenMinutes.add(hour1 + ":00");
						//每十五分钟
						for (int m = 0; m < 4; m++) {
							String strategy_power_id =
							 nodeId + "_" + systemId + "_" + sdf.format(effective) + fifteenMinutes.get(m) + "-" + fifteenMinutes.get(m + 1);
							CfgStorageEnergyStrategyPower96 energyStrategyPower96 = new CfgStorageEnergyStrategyPower96();
							energyStrategyPower96.setId(strategy_power_id);
							energyStrategyPower96.setNodeId(nodeId);
							energyStrategyPower96.setSystemId(systemId);
							energyStrategyPower96.setEffectiveDate(effective);
							energyStrategyPower96.setPower(power);
							energyStrategyPower96.setStrategy(cfgStorageEnergyStrategy.getStrategy());
							energyStrategyPower96.setSTime(fifteenMinutes.get(m));
							energyStrategyPower96.setETime(fifteenMinutes.get(m + 1));
							energyStrategyPower96.setTimeScope(fifteenMinutes.get(m) + "-" + fifteenMinutes.get(m + 1));
							energyStrategyPower96.setDistributeStatus(0);
							strategyPowers.add(energyStrategyPower96);
						}
					}
					cfgStorageEnergyStrategyPower96Repository.saveAll(strategyPowers);
				}
				return ResponseResult.success();
			} else {
				return ResponseResult.error("请检查参数是否正确！");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("当前节点，系统下无数据，请先初始化数据。");
		}
	}


	@ApiOperation("编辑储能充放电策略")
	@UserLoginToken
	@RequestMapping(value = "updateStorageEnergyStrategy", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult updateStorageEnergyStrategy(@RequestBody UpdateStorageEnergyStrategyModel model) {
		try {
			if (model != null) {
				CfgStorageEnergyStrategy energyStrategy = strategyRepository.findById(model.getId()).orElse(null);
				if (energyStrategy != null) {
					energyStrategy.setProperty(model.getProperty());
					energyStrategy.setPriceHour(model.getPriceHour());
					energyStrategy.setStrategy(model.getStrategy());
					strategyRepository.save(energyStrategy);
					return ResponseResult.success();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("编辑储能充放电策略失败!");
		}
		return ResponseResult.success(null);
	}

	@ApiOperation("查询储能分成比例")
	@UserLoginToken
	@RequestMapping(value = "findStorageEnergyShareProportion", method = {RequestMethod.POST})
	public ResponseResult<PageModel> findStorageEnergyShareProportion(@RequestBody StorageEnergyShareProportionModel model) {
		try {
			List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
			if (!allowStorageEnergyNodeIds.contains(model.getNodeId())) {
				return ResponseResult.error("储能节点不存在或者没有该节点权限");
			}
			Specification<CfgStorageEnergyShareProportion> spec = new Specification<CfgStorageEnergyShareProportion>() {
				@Override
				public Predicate toPredicate(Root<CfgStorageEnergyShareProportion> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
					List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况

					predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
					predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
					criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
					criteriaQuery.orderBy(cb.asc(root.get("order"))); //按照createTime升序排列
					return criteriaQuery.getRestriction();
				}
			};

			//当前页为第几页 默认 1开始
			int page = model.getNumber();
			int size = model.getPageSize();
			Pageable pageable = PageRequest.of(page - 1, size);
			Page<CfgStorageEnergyShareProportion> datas = shareProportionRepository.findAll(spec, pageable);
			PageModel pageModel = new PageModel();
			//封装到pageUtil
			pageModel.setContent(datas.getContent());
			pageModel.setTotalPages(datas.getTotalPages());
			pageModel.setTotalElements((int) datas.getTotalElements());
			pageModel.setNumber(datas.getNumber() + 1);

			return ResponseResult.success(pageModel);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("没有找到储能分成比例");
		}
	}


	@ApiOperation("编辑储能分成比例")
	@UserLoginToken
	@RequestMapping(value = "updateStorageEnergyShareProportion", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult updateStorageEnergyShareProportion(@RequestBody UpdateStorageShareProportionStrategyModel model) {
		try {
			if (model != null) {
				CfgStorageEnergyShareProportion shareProportion = shareProportionRepository.findById(model.getId()).orElse(null);
				if (shareProportion != null) {
					shareProportion.setLoadProp(model.getLoadProp());
					shareProportion.setPowerUserProp(model.getPowerUserProp());
					shareProportionRepository.save(shareProportion);
					return ResponseResult.success();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("没有找到保存对象");
		}
		return ResponseResult.success(null);
	}

	@ApiOperation("编辑储能分成比例")
	@UserLoginToken
	@RequestMapping(value = "updateStorageEnergyShareProportionBatch", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult updateStorageEnergyShareProportionBatch(@RequestBody UpdateStorageShareProportionBatchModel model) {
		try {
			if (model != null) {
				String st = String.valueOf(model.getSt().getYear()) + model.getSt().getMonthValue();
				String et = String.valueOf(model.getEt().getYear()) + model.getEt().getMonthValue();
				List<CfgStorageEnergyShareProportion> shareProportionList =
						shareProportionRepository.findAllByNodeIdAndOrderBetween(model.getNodeId(), Integer.valueOf(st), Integer.valueOf(et));
				shareProportionList.forEach(o -> {
					o.setLoadProp(model.getLoadProp());
					o.setPowerUserProp(model.getPowerUserProp());
					o.setOperatorProp(model.getOperatorProp());
				});
				shareProportionRepository.saveAll(shareProportionList);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("没有找到保存对象");
		}
		return ResponseResult.success("success");
	}


	@ApiOperation("查询储能同步配置")
	@UserLoginToken
	@RequestMapping(value = "findStorageEnergySynCfg", method = {RequestMethod.POST})
	public ResponseResult<StorageEnergySynCfgModel> findStorageEnergySynCfg(@RequestParam(value = "nodeId") String nodeId, @RequestParam(value =
	"systemId") String systemId) {
		try {
			List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
			if (!allowStorageEnergyNodeIds.contains(nodeId)) {
				return ResponseResult.error("储能节点不存在或者没有该节点权限");
			}
			if (StringUtils.isNotEmpty(nodeId) && StringUtils.isNotEmpty(systemId)) {
				String id = nodeId + "_" + systemId;
				CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
				if (baseInfo != null) {
					StorageEnergySynCfgModel model = new StorageEnergySynCfgModel();
					if (baseInfo.getSynStorageEnergyCfg() != null && !"".equals(baseInfo.getSynStorageEnergyCfg().trim())) {
						JSONObject obj = JSONObject.parseObject(baseInfo.getSynStorageEnergyCfg());
						if (obj.get("name") != null) {
							model.setName(String.valueOf(obj.get("name")));
						}
						if (obj.get("requestUrl") != null) {
							model.setRequestUrl(String.valueOf(obj.get("requestUrl")));
						}
						if (obj.get("method") != null) {
							model.setMethod(StorageEnergySynCfgModel.RequestMethod.valueOf(String.valueOf(obj.get("method"))));
						}
						if (obj.get("args") != null) {
							model.setArgs(String.valueOf(obj.get("args")));
						}
						model.setNodeId(nodeId);
						model.setSystemId(systemId);
					}
					return ResponseResult.success(model);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("查询储能同步配置失败。");
		}
		return ResponseResult.success(null);
	}


	@ApiOperation("更新储能同步配置")
	@UserLoginToken
	@RequestMapping(value = "updateStorageEnergySynCfg", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult updateStorageEnergySynCfg(@RequestBody StorageEnergySynCfgModel model) {
		try {
			if (StringUtils.isNotEmpty(model.getNodeId()) && StringUtils.isNotEmpty(model.getSystemId())) {
				String id = model.getNodeId() + "_" + model.getSystemId();
				CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
				if (baseInfo != null) {
					TreeMap<String, Object> map = new TreeMap<>();
					map.put("name", model.getName());
					map.put("requestUrl", model.getRequestUrl());
					map.put("method", model.getMethod());
					map.put("args", model.getArgs());
					baseInfo.setSynStorageEnergyCfg(JSON.toJSONString(map));

					baseInfoRepository.save(baseInfo);
					return ResponseResult.success();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("编辑储能同步配置失败。");
		}
		return ResponseResult.success(null);
	}

	@ApiOperation("查询储能下发配置")
	@UserLoginToken
	@RequestMapping(value = "findStorageEnergyDistributeCfg", method = {RequestMethod.POST})
	public ResponseResult<StorageEnergyDistributeCfgModel> findStorageEnergyDistributeCfg(@RequestParam(value = "nodeId") String nodeId,
	 @RequestParam(value = "systemId") String systemId) {
		try {
			List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
			if (!allowStorageEnergyNodeIds.contains(nodeId)) {
				return ResponseResult.error("储能节点不存在或者没有该节点权限");
			}
			if (StringUtils.isNotEmpty(nodeId) && StringUtils.isNotEmpty(systemId)) {
				String id = nodeId + "_" + systemId;
				CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
				if (baseInfo != null) {
					StorageEnergyDistributeCfgModel model = new StorageEnergyDistributeCfgModel();

					if (baseInfo.getDistributeStorageEnergyCfg() != null && !"".equals(baseInfo.getDistributeStorageEnergyCfg().trim())) {
						JSONObject obj = JSONObject.parseObject(baseInfo.getDistributeStorageEnergyCfg());
						if (obj.get("name") != null) {
							model.setName(String.valueOf(obj.get("name")));
						}
						if (obj.get("requestUrl") != null) {
							model.setRequestUrl(String.valueOf(obj.get("requestUrl")));
						}
						if (obj.get("method") != null) {
							model.setMethod(StorageEnergyDistributeCfgModel.RequestMethod.valueOf(String.valueOf(obj.get("method"))));
						}
						model.setNodeId(nodeId);
						model.setSystemId(systemId);
					}
					return ResponseResult.success(model);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("查询储能同步配置失败。");
		}
		return ResponseResult.success(null);
	}

	@ApiOperation("编辑储能下发配置")
	@UserLoginToken
	@RequestMapping(value = "updateStorageEnergyDistributeCfg", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult updateStorageEnergyDistributeCfg(@RequestBody StorageEnergyDistributeCfgModel model) {
		try {
			if (StringUtils.isNotEmpty(model.getNodeId()) && StringUtils.isNotEmpty(model.getSystemId())) {
				String id = model.getNodeId() + "_" + model.getSystemId();
				CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
				if (baseInfo != null) {
					TreeMap<String, Object> map = new TreeMap<>();
					map.put("name", model.getName());
					map.put("requestUrl", model.getRequestUrl());
					map.put("method", model.getMethod());
					baseInfo.setDistributeStorageEnergyCfg(JSON.toJSONString(map));

					baseInfoRepository.save(baseInfo);
					return ResponseResult.success();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("编辑储能下发配置失败。");
		}
		return ResponseResult.success(null);
	}

	@ApiOperation("获取所有的设备列表-标记 充/放 电设备")
	@UserLoginToken
	@RequestMapping(value = "getAllMeteringDeviceList", method = {RequestMethod.POST})
	public ResponseResult<List<StorageEnergyModel>> getAllMeteringDeviceList(@RequestParam("nodeId") String nodeId,
	 @RequestParam(value = "systemId") String systemId) {

		List<StorageEnergyModel> models = new ArrayList<>();

		List<Device> devices = deviceRepository.findAllByNode_NodeId(nodeId);
		if (devices != null) {
			devices.stream().forEach(p -> {
				StorageEnergyModel model = new StorageEnergyModel();
				model.setDeviceSn(p.getDeviceSn());
				model.setDeviceName(p.getDeviceName());
				models.add(model);
			});
		}
		return ResponseResult.success(models);
	}

	@ApiOperation("获取已经保存 充/放/电池状态监控 设备sn 信息")
	@UserLoginToken
	@RequestMapping(value = "getAllMeteringDeviceOtherList", method = {RequestMethod.POST})
	public ResponseResult<StorageEnergyModelResponse> getAllMeteringDeviceOtherList(@RequestParam("nodeId") String nodeId, @RequestParam(value =
	"systemId") String systemId) {

		StorageEnergyModelResponse response = new StorageEnergyModelResponse();
		String id = nodeId + "_" + systemId;
		CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
		if (baseInfo != null) {
			response.setNodeId(nodeId);
			response.setCharging_device_sn(baseInfo.getChargingDeviceSn());
			response.setDischarging_device_sn(baseInfo.getDischargingDeviceSn());
			response.setBattery_status_device_sn(baseInfo.getBatteryStatusDeviceSn());
			response.setSystemId(systemId);
		}
		return ResponseResult.success(response);
	}

	@ApiOperation("保存 充/放/电池状态监控 设备sn")
	@UserLoginToken
	@RequestMapping(value = "saveMeteringDeviceOtherSn", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult saveMeteringDeviceOtherSn(@RequestBody StorageEnergyModelResponse response) {

		String id = response.getNodeId() + "_" + response.getSystemId();
		CfgStorageEnergyBaseInfo baseInfo = baseInfoRepository.findById(id).orElse(null);
		if (baseInfo == null) {
			baseInfo = new CfgStorageEnergyBaseInfo();
			baseInfo.setId(id);
		}
		baseInfo.setNodeId(response.getNodeId());
		baseInfo.setChargingDeviceSn(response.getCharging_device_sn());
		baseInfo.setDischargingDeviceSn(response.getDischarging_device_sn());
		baseInfo.setBatteryStatusDeviceSn(response.getBattery_status_device_sn());
		baseInfo.setSystemId(response.getSystemId());
		baseInfoRepository.save(baseInfo);
		return ResponseResult.success();
	}


	@ApiOperation("储能AI 调度模型")
	@UserLoginToken
	@RequestMapping(value = "aiScheduling", method = {RequestMethod.POST})
	public ResponseResult aiScheduling(@RequestBody StorageEnergyAiSchedulingModel model) {
		SimpleDateFormat df_ym = new SimpleDateFormat("yyyy-MM");
		df_ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));

		try {
			if (model != null) {
				String nodeId = model.getNodeId();
				String systemId = model.getSystemId();
				Date count_Date = model.getCount_Date();

				Node node = nodeRepository.findById(nodeId).orElse(null);
				if (node != null) {
					//移除商汤接口调用 zph 20240428
				}
			}
			return ResponseResult.success();
		} catch (Exception ex) {
			return ResponseResult.error("储能AI 调度模型 失败!");
		}
	}

	@ApiOperation("储能天气图表")
	@UserLoginToken
	@RequestMapping(value = "weatherChart", method = {RequestMethod.POST})
	public ResponseResult weatherChart(@RequestBody StorageEnergyBaseInfoReq model) {
		try {
//			Node node = nodeRepository.findByNodeId("96a1a8c51194b433025bc8fb677de785");
//            List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
//            if (!allowStorageEnergyNodeIds.contains(node.getNodeId())) {
//                return ResponseResult.error("储能节点不存在或者没有该节点权限");
//            }

			WeatherRequest json = new WeatherRequest();
			json.setKey("9875c3ceb98687f62b75b9639a875b27");
			json.setLongitude(119.295559);
			json.setLatitude(26.066354);
			json.setStartDateTime(model.getStartDate() + " 00:00:00");
			json.setEndDateTime(model.getEndDate() + " 23:59:59");
			log.info("weatherChart天气传参：{}", JSONObject.toJSONString(json));
			String data = HttpUtil.okHttpPost("http://8.153.13.210:58080/datalake/meteorological/QueryMeteorologicalData",
			 JSONObject.toJSONString(json));
			List<MeteorologicalData> meteorologicalData = JSONObject.parseArray(JSONObject.parseObject(data).getString("data"),
			 MeteorologicalData.class);
			List<MeteorologicalDataVo> list = new ArrayList<>();
			meteorologicalData.forEach(v -> {
				MeteorologicalDataVo vo = new MeteorologicalDataVo();
				vo.setTs(v.getTs());
				if (v.getRtTt2() != 0) {
					vo.setRtTt2(Double.parseDouble(String.format("%.2f", v.getRtTt2() - 273.15)));
				} else {
					vo.setRtTt2(null);
				}
				if (v.getPredTt2() != 0) {
					vo.setPredTt2(Double.parseDouble(String.format("%.2f", v.getPredTt2() - 273.15)));
				} else {
					vo.setPredTt2(null);
				}
				list.add(vo);
			});
			return ResponseResult.success(list);
		} catch (Exception ex) {
			return ResponseResult.error("储能天气图表查询失败");
		}
	}


	@ApiOperation("储能策略预测数据入库")
	@UserLoginToken
	@RequestMapping(value = "energyStoragePrediction", method = {RequestMethod.POST})
	public ResponseResult energyStoragePrediction(@RequestBody Map<String, List<Double>> energyStoragePredictions) throws ParseException {
		try {
			log.info("算法预测数据保存至redis");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date date = sdf.parse(sdf.format(calendar.getTime()));
			Set<String> nodeSet = energyStoragePredictions.keySet();
			log.info("");
			for (String nodeID : nodeSet) {
				//先把预测数据保存在redis里面
				log.info("算法预测节点=======>>>>" + nodeID);
				log.info("算法预测节点数据=======>>>>" + energyStoragePredictions.get(nodeID));
				boolean tt = redisUtils.add("AIStorageEnergystrategy-" + nodeID, energyStoragePredictions.get(nodeID), 60 * 30, TimeUnit.SECONDS);
				if (tt == false) {
					return ResponseResult.error("节点" + nodeID + "数据保存失败");
				}
			}
			return ResponseResult.success("数据保存成功");
		} catch (Exception ex) {
			return ResponseResult.error("数据结构异常");
		}
	}


	public static String okHttpPost(String reqUrl, String json) {
		try {
			// 创建一个信任所有证书的 TrustManager
			TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			}};

			// 创建 SSL 上下文，使用信任所有证书的 TrustManager
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new SecureRandom());

			// 设置 OkHttpClient 使用我们创建的 SSL 上下文
			OkHttpClient client =
					new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]).hostnameVerifier((hostname, session) -> true).build();

			MediaType mediaType = MediaType.parse("application/json");
			okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, json);
			Request request = new Request.Builder().url(reqUrl).method("POST", body).addHeader("Content-Type", "application/json").addHeader("Accept"
			, "application/json").build();

			// 发送请求
			Response response = client.newCall(request).execute();
			return response.body().string();
		} catch (Exception e) {
			throw new RuntimeException("HTTP POST同步请求失败 URL:" + reqUrl, e);
		}
	}

	public List<CopilotResponse> getPointValueList(String nodeId, String pointDesc, Date startDate, Date endDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		List<IotTsKv> iotTsKvList = iotTsKvRepository.findAllByPointDesc(nodeId, pointDesc, startDate, endDate);
		//先得到日期包含时间的所有96点数据
		List<Date> dateList = getDateRangeList(sdf.format(startDate), sdf.format(endDate));
		dateList.remove(dateList.size() - 1);
		List<String> countTime = new ArrayList<>();
		for (Date date : dateList) {
			for (int o = 0; o < 24; o++) {
				String fiftime = o < 10 ? ("0" + o + ":00") : (o + ":00");
				//每十五分钟的list
				List<String> fifteenMinutes = new ArrayList<>();
				String hour = fiftime.substring(0, 2);
				int hourInt = Integer.parseInt(hour) + 1;
				String hour1 = hourInt >= 10 ? String.valueOf(hourInt) : "0" + String.valueOf(hourInt);
				fifteenMinutes.add(hour + ":00");
				fifteenMinutes.add(hour + ":15");
				fifteenMinutes.add(hour + ":30");
				fifteenMinutes.add(hour + ":45");
				fifteenMinutes.add(hour1 + ":00");
				for (int m = 0; m < 4; m++) {
					String time = sdf.format(date) + " " + fifteenMinutes.get(m) + ":00";
					Date date1 = sdf1.parse(time);
					countTime.add(time);
				}
			}
		}
		List<CopilotResponse> copilotResponseList = new ArrayList<>();
		for (String dateTime : countTime) {
			CopilotResponse copilotResponse = new CopilotResponse();
//            copilotResponse.setDate(dateTime);
			for (IotTsKv iotTsKv : iotTsKvList) {
				if (dateTime.substring(0, 16).equals(sdf1.format(iotTsKv.getTs()).substring(0, 16))) {
					copilotResponse.setValue(Double.parseDouble(iotTsKv.getPointValue()));
					break;
				}
			}
			copilotResponseList.add(copilotResponse);
		}
		return copilotResponseList;
	}

	@ApiOperation("储能-预测收益")
	@UserLoginToken
	@RequestMapping(value = "profit", method = {RequestMethod.POST})
	public ResponseResult<List<ProfitResponse>> findProfit(@RequestBody ProfitRequest request) {
		List<ProfitResponse> responseList;
		try {
			List<String> nodeIds = new ArrayList<>();
			nodeIds.add(config.getEnergyNode1());
			nodeIds.add(config.getEnergyNode2());
			Map<String, String> energyNodeMap = new HashMap<>();
			energyNodeMap.put(config.getEnergyNode1(), "储能001");
			energyNodeMap.put(config.getEnergyNode2(), "储能002");
			request.setNodeId(nodeIds);
			responseList = profitChartService.getEnergyStorageProfitChart(request, energyNodeMap);
		} catch (Exception e) {
			return ResponseResult.error("获取收益失败");
		}
		return ResponseResult.success(responseList);
	}

	@ApiOperation("储能-预测收益")
	@UserLoginToken
	@RequestMapping(value = "profitNew", method = {RequestMethod.POST})
	public ResponseResult profitNew(@RequestBody BlackProfitRequest request) {
		try {
			ListProjSubEnergyAndPvVo energyBlockList = globalApiService.stationTreeEnergyAndPv(request.getNodeId());
			Map<String, String> energyNodeMap = energyBlockList.getEnergy().stream()
					.collect(Collectors.toMap(
							ListProjSubEnergyAndPvVo::getNodeId,
							ListProjSubEnergyAndPvVo::getStationName,
							(oldValue, newValue) -> oldValue
					));
			List<String> enNodeList = new ArrayList<>(energyNodeMap.keySet());
			List<ProfitResponse> responseList;
			ProfitRequest request1 = new ProfitRequest();
			request1.setNodeId(enNodeList);
			request1.setSystemId(request.getSystemId());
			request1.setStartDate(request.getStartDate());
			request1.setEndDate(request.getEndDate());
			responseList = profitChartService.getEnergyStorageProfitChart(request1, energyNodeMap);
			return ResponseResult.success(responseList);
		} catch (Exception e) {
			return ResponseResult.error("获取收益失败");
		}
	}

	@ApiOperation("保存储能策略")
	@UserLoginToken
	@RequestMapping(value = "saveStorageEnergyStrategy", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult saveStorageEnergyStrategy(@RequestBody StorageEnergyStrategyDistributionModel response) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date todayDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(todayDate);
		int minutes = calendar.get(Calendar.MINUTE);
		int roundedMinutes = ((minutes + 14) / 15) * 15;
		if (minutes % 15 == 0) {
			roundedMinutes = (minutes + 15);
		}
		if (roundedMinutes == 60) {
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			calendar.set(Calendar.MINUTE, 0);
		} else {
			calendar.set(Calendar.MINUTE, roundedMinutes);
		}
		calendar.set(Calendar.SECOND, 0);
		Date nowDateTime = dateTimeFormat.parse(dateTimeFormat.format(calendar.getTime()));

		Map<Date, Date> dateMap = new HashMap<>();
		Calendar calendarStart = Calendar.getInstance();
		Date startDate = dateFormat.parse(response.getStartDate());
		Date endDate = dateFormat.parse(response.getEndDate());
		Date endTime = timeFormat.parse(response.getEndTime());
		calendarStart.setTime(startDate);

		Date endDateTime = dateTimeFormat.parse(response.getEndDate() + " " + response.getEndTime());
		if (nowDateTime.before(endDateTime)) {
			while (!calendarStart.getTime().after(endDate)) {
				String starDate1 = dateFormat.format(calendarStart.getTime());
				Date startDateTime1 = dateTimeFormat.parse(starDate1 + " " + response.getStartTime());
				Date endDateTime1 = dateTimeFormat.parse(starDate1 + " " + response.getEndTime());
				;
				if (nowDateTime.before(endDateTime1)) {
					if (nowDateTime.after(startDateTime1)) {
						dateMap.put(dateFormat.parse(starDate1), nowDateTime);
					} else {
						dateMap.put(dateFormat.parse(starDate1), startDateTime1);
					}
				}
				calendarStart.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
//		log.info("nowDateTime：{}",nowDateTime);
//		log.info("endDateTime：{}",endDateTime);
//		log.info("dateMap:{}",JSON.toJSONString(dateMap));
		List<CfgStorageEnergyStrategyPower96> addList = new ArrayList<>();
		Map<Date, List<CfgStorageEnergyStrategyPower96>> strategyPower96List = cfgStorageEnergyStrategyPower96Repository
				.findAllBySystemIdAndNodeIde(response.getNodeId(), response.getSystemId(), startDate, endDate)
				.stream().collect(Collectors.groupingBy(CfgStorageEnergyStrategyPower96::getEffectiveDate));
		StringBuilder message = new StringBuilder();
		dateMap.forEach((k, v) -> {
			Date startTime = null;
			try {
				startTime = timeFormat.parse(timeFormat.format(v));
			} catch (ParseException ignored) {
			}
			Date finalStartTime = startTime;
			strategyPower96List.get(k).forEach(power96 -> {
				try {
					Date powerTime = timeFormat.parse(power96.getETime());
					if (powerTime.after(finalStartTime) && !powerTime.after(endTime)) {
						power96.setPolicyModel(1);
						addList.add(power96);
					}
				} catch (Exception e) {
					message.append("AI策略不存在:").append(power96.getNodeId());
				}
			});
		});
		cfgStorageEnergyStrategyPower96Repository.saveAll(addList);
		return ResponseResult.success(message);
	}

	@ApiOperation("测试")
	@UserLoginToken
	@RequestMapping(value = "restoreAIModeTest", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult restoreAIModeTest(@RequestBody StorageEnergyStrategyDistributionModel response) {
		// 日期格式化器
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// 初始化起始时间为2024年9月12日00:00
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.set(2024, Calendar.SEPTEMBER, 12, 0, 0, 0);

		// 将一天的每分钟时间点存入列表
		List<Date> minuteList = new ArrayList<>();
		for (int i = 0; i < 1440; i++) { // 一天1440分钟
			minuteList.add(calendarNow.getTime());
			calendarNow.add(Calendar.MINUTE, 1);
		}

		// 用于存储原始时间和舍入后的时间的列表
		List<Map<String, String>> timeList = new ArrayList<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date todayDate = new Date();
		// 遍历minuteList进行舍入操作
		for (Date date : minuteList) {
			// 保存当前时间作为原始时间
			String originalTimeStr = sdf.format(date);

			// 使用改进的逻辑计算四舍五入后的时间
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date); // 设置时间为当前迭代时间
			int minutes = calendar.get(Calendar.MINUTE);
			int roundedMinutes = ((minutes + 14) / 15) * 15;

			if (minutes % 15 == 0) {
				roundedMinutes = (minutes + 15);
			}

			if (roundedMinutes == 60) {
				calendar.add(Calendar.HOUR_OF_DAY, 1);
				calendar.set(Calendar.MINUTE, 0);
			} else {
				calendar.set(Calendar.MINUTE, roundedMinutes);
			}
			calendar.set(Calendar.SECOND, 0);

			// 保存计算后的时间
			String roundedTimeStr = sdf.format(calendar.getTime());

			// 将当前分钟和舍入结果放入map
			Map<String, String> timeMap = new HashMap<>();
			timeMap.put("Original time", originalTimeStr);
			timeMap.put("Rounded time", roundedTimeStr);

			// 将map加入list
			timeList.add(timeMap);
		}

		// 返回结果
		return ResponseResult.success(timeList);
	}

	@ApiOperation("恢复AI模式")
	@UserLoginToken
	@RequestMapping(value = "restoreAIMode", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult restoreAIMode(@RequestBody StorageEnergyStrategyDistributionModel response) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date todayDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(todayDate);
		int minutes = calendar.get(Calendar.MINUTE);
		int roundedMinutes = ((minutes + 14) / 15) * 15;
		if (minutes % 15 == 0) {
			roundedMinutes = (minutes + 15);
		}
		if (roundedMinutes == 60) {
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			calendar.set(Calendar.MINUTE, 0);
		} else {
			calendar.set(Calendar.MINUTE, roundedMinutes);
		}
		calendar.set(Calendar.SECOND, 0);
		Date nowDateTime = dateTimeFormat.parse(dateTimeFormat.format(calendar.getTime()));
		Map<Date, Date> dateMap = new HashMap<>();
		Calendar calendarStart = Calendar.getInstance();
		Date startDate = dateFormat.parse(response.getStartDate());
		Date endDate = dateFormat.parse(response.getEndDate());
		Date endTime = timeFormat.parse(response.getEndTime());
		calendarStart.setTime(startDate);
		Date endDateTime = dateTimeFormat.parse(response.getEndDate() + " " + response.getEndTime());
		if (nowDateTime.before(endDateTime)) {
			while (!calendarStart.getTime().after(endDate)) {
				String starDate1 = dateFormat.format(calendarStart.getTime());
				Date startDateTime1 = dateTimeFormat.parse(starDate1 + " " + response.getStartTime());
				Date endDateTime1 = dateTimeFormat.parse(starDate1 + " " + response.getEndTime());
				;
				if (nowDateTime.before(endDateTime1)) {
					if (nowDateTime.after(startDateTime1)) {
						dateMap.put(dateFormat.parse(starDate1), nowDateTime);
					} else {
						dateMap.put(dateFormat.parse(starDate1), startDateTime1);
					}
				}
				calendarStart.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		Map<Date, List<CfgStorageEnergyStrategyPower96>> strategyPower96Map = cfgStorageEnergyStrategyPower96Repository
				.findAllBySystemIdAndNodeIde(response.getNodeId(), response.getSystemId(), startDate, endDate)
				.stream().collect(Collectors.groupingBy(CfgStorageEnergyStrategyPower96::getEffectiveDate));
		Map<Date, List<CfgStorageEnergyStrategyPower96Ai>> powerAi96Map = cfgStorageEnergyStrategyPower96AiRepository
				.findAllBySystemIdAndNodeIde(response.getNodeId(), response.getSystemId(), startDate, endDate)
				.stream().collect(Collectors.groupingBy(CfgStorageEnergyStrategyPower96Ai::getEffectiveDate));
		List<CfgStorageEnergyStrategyPower96> addList = new ArrayList<>();
		StringBuilder message = new StringBuilder();
		dateMap.forEach((k, v) -> {
			Date startTime = null;
			try {
				startTime = timeFormat.parse(timeFormat.format(v));
			} catch (ParseException ignored) {
			}
			List<CfgStorageEnergyStrategyPower96> strategyPower96List = strategyPower96Map.get(k);
			if (strategyPower96List != null) {
				strategyPower96List.sort(Comparator.comparing(CfgStorageEnergyStrategyPower96::getSTime));
			}
			List<CfgStorageEnergyStrategyPower96Ai> powerAi96List = powerAi96Map.get(k);
			if (powerAi96List != null) {
				powerAi96List.sort(Comparator.comparing(CfgStorageEnergyStrategyPower96Ai::getSTime));
			}
			Date finalStartTime1 = startTime;
			IntStream.range(0, strategyPower96List.size()).forEach(i -> {
				CfgStorageEnergyStrategyPower96 power96 = strategyPower96List.get(i);
				try {
					Date powerTime = timeFormat.parse(power96.getETime());
					if (powerTime.after(finalStartTime1) && !powerTime.after(endTime)) {
						CfgStorageEnergyStrategyPower96Ai powerAi96 = powerAi96List.get(i);
						power96.setPolicyModel(0);
						power96.setStrategy(powerAi96.getStrategy());
						power96.setPower(powerAi96.getPower());
						addList.add(power96);
					}
				} catch (Exception e) {
					message.append("AI策略不存在:").append(power96.getNodeId());
				}
			});
		});
		cfgStorageEnergyStrategyPower96Repository.saveAll(addList);
		return ResponseResult.success(message);
	}

	@ApiOperation("测试")
	@RequestMapping(value = "testIssue", method = {RequestMethod.POST})
	public void distributionStorageEnergyStrategy() {
		storageEnergyStrategyPower96Job.strategy96Issue();
	}

	@ApiOperation("保存并下发储能充放电策略")
	@UserLoginToken
	@RequestMapping(value = "distributionStorageEnergyStrategy", method = {RequestMethod.POST})
//	@Transactional
	public ResponseResult distributionStorageEnergyStrategy(@RequestBody StorageEnergyStrategyDistributionModels models) throws ParseException {
//		try {

		String mainNodeId = models.getStorageEnergyStrategyDistributionModels().get(0).getNodeId();
		String mainSystemId = models.getStorageEnergyStrategyDistributionModels().get(0).getSystemId();
		List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
		String id = mainNodeId + "_" + mainSystemId;
		CfgStorageEnergyBaseInfo cfgStorageEnergyBaseInfo = baseInfoRepository.findById(id).orElse(null);
		double power;
		if (cfgStorageEnergyBaseInfo != null) {
			power = cfgStorageEnergyBaseInfo.getStorageEnergyLoad();
		} else {
			return ResponseResult.error("储能节点储能配置基本信息未配置");
		}
		if (!allowStorageEnergyNodeIds.contains(mainNodeId)) {
			return ResponseResult.error("储能节点不存在或者没有该节点权限");
		}
		String systemId = mainSystemId;
		if (systemId == null || "".equals(systemId.trim())) {
			return ResponseResult.error("储能节点系统不能为空");
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfhm = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdfymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		Calendar calendarNow = Calendar.getInstance();
//		calendarNow.set(2024, Calendar.SEPTEMBER, 12, 18, 0, 1);
		Date date1 = new Date();
		Date dateymd = formatter.parse(formatter.format(date1));
		Calendar calendarymdhm = Calendar.getInstance();
		calendarymdhm.setTime(date1);
		int minutes = calendarymdhm.get(Calendar.MINUTE);
		int roundedMinutes = ((minutes + 14) / 15) * 15;
		if (minutes % 15 == 0) {
			roundedMinutes = (minutes + 15);
		}
		if (roundedMinutes == 60) {
			calendarymdhm.add(Calendar.HOUR_OF_DAY, 1);
			calendarymdhm.set(Calendar.MINUTE, 0);
		} else {
			calendarymdhm.set(Calendar.MINUTE, roundedMinutes);
		}
		calendarymdhm.set(Calendar.SECOND, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date dateTomorrow = formatter.parse(formatter.format(calendar.getTime()));

		Date dateymdhm = sdfymdhm.parse(sdfymdhm.format(calendarymdhm.getTime()));

		List<Date> issueList = new ArrayList<>();
		List<CfgStorageEnergyStrategyPower96Log> logList = new ArrayList<>();

		if (models.isModify()) {
			MergeStrategiesResult result = new MergeStrategiesResult(models.getStorageEnergyStrategyDistributionModels());
			Date starDate = result.getStartDate();
			Date endDate = result.getEndDate();
			Map<Date, List<CfgStorageEnergyStrategyPower96>> power96Map = cfgStorageEnergyStrategyPower96Repository
					.findAllBySystemIdAndNodeId(mainNodeId, mainSystemId, starDate, endDate)
					.stream().collect(Collectors.groupingBy(CfgStorageEnergyStrategyPower96::getEffectiveDate));

			List<CfgStorageEnergyStrategyPower96> addPower96List = new ArrayList<>();
			if (power96Map.isEmpty()) {
				Date startDateM = TimeUtil.getMonthStart(dateymd);
				List<CfgStorageEnergyStrategy> cfgStorageEnergyStrategys = cfgStorageEnergyStrategyRepository.
						findCfgStorageEnergyStrategyByNodeIdAndSystemId(mainNodeId, mainSystemId, startDateM, startDateM);
				if (cfgStorageEnergyStrategys.size() == 0) {
					return ResponseResult.error("储能节点充放电策略未配置");
				}
				power96Map = issueEmpty(mainNodeId, mainSystemId, addPower96List, power, cfgStorageEnergyStrategys, starDate, endDate, formatter);
			}
			List<StrategyDistributionModel> distributionModels = result.getResult();
			distributionModels.sort(Comparator.comparing(StrategyDistributionModel::getIndex));

			power96Map.forEach((date, items) -> {
				if (!dateymd.before(starDate) && !dateymd.after(endDate)) {
					issueList.add(dateymd);
				}
				if (!dateTomorrow.before(starDate) && !dateTomorrow.after(endDate)) {
					issueList.add(dateTomorrow);
				}
				distributionModels.forEach(d -> {
					if (d.getPower() > power) {
						throw new RuntimeException("修改功率不能大于额定功率");
					}
					Date disStartDate = d.getStartDate();
					Date disEndDate = d.getEndDate();
					if (!date.before(disStartDate) && !date.after(disEndDate)) {
						Date startTime = d.getStartTime();
						Date endTime = d.getEndTime();
						items.forEach(item -> {
							try {
								if (!(sdfymdhm.parse(formatter.format(item.getEffectiveDate()) + " " + item.getSTime())).before(dateymdhm)) {
									Date eTime = sdfhm.parse(item.getETime());
									Calendar eTimeCalendar = Calendar.getInstance();
									eTimeCalendar.setTime(eTime);
									eTimeCalendar.add(Calendar.MINUTE, -1);
									Date updatedTime = eTimeCalendar.getTime();
									if (!updatedTime.before(startTime) && !updatedTime.after(endTime)) {
										item.setStrategy(d.getStrategy());
										item.setPower(d.getPower());
										addPower96List.add(item);
									}
								}
							} catch (ParseException e) {
								throw new RuntimeException("下发策略失败，请重新下发");
							}
						});
					}
				});
			});
			cfgStorageEnergyStrategyPower96Repository.saveAll(addPower96List);
			if (!issueList.isEmpty()) {
				List<DevicePoint> devicePoints = devicePointRepository.findAllByDeviceSnInAndPointDesc(
						deviceRepository.findAllByNode_NodeIdAndSystemType_SystemId(mainNodeId, mainSystemId)
								.stream()
								.map(Device::getDeviceSn)
								.collect(Collectors.toList()),
						"storage_strategy"
				);
				if (devicePoints.size() == 0 || devicePoints.size() > 1) {
					return ResponseResult.error("储能节点没有录入控制点位，或者录入多个控制点位");
				}
				DevicePoint devicePoint = devicePoints.get(0);
				Device device = devicePoint.getDevice();
				issue(mainNodeId, mainSystemId, issueList, power96Map, logList, device.getDeviceSn(), devicePoint.getPointSn(), dateymd);
			}
		} else {
			issueList.add(dateymd);
			issueList.add(dateTomorrow);
			List<CfgStorageEnergyStrategyPower96> addPower96List = new ArrayList<>();
			Map<Date, List<CfgStorageEnergyStrategyPower96>> finalPower96Map = cfgStorageEnergyStrategyPower96Repository
					.findAllBySystemIdAndNodeId(mainNodeId, mainSystemId, dateymd, dateTomorrow)
					.stream().collect(Collectors.groupingBy(CfgStorageEnergyStrategyPower96::getEffectiveDate));
			if (finalPower96Map.isEmpty()) {
				Date startDateM = TimeUtil.getMonthStart(dateymd);
				List<CfgStorageEnergyStrategy> cfgStorageEnergyStrategys = cfgStorageEnergyStrategyRepository.
						findCfgStorageEnergyStrategyByNodeIdAndSystemId(mainNodeId, mainSystemId, startDateM, startDateM);
				if (cfgStorageEnergyStrategys.size() == 0) {
					return ResponseResult.error("储能节点充放电策略未配置");
				}
				finalPower96Map = issueEmpty(mainNodeId, mainSystemId, addPower96List, power, cfgStorageEnergyStrategys, dateymd, dateTomorrow,
				 formatter);
			}
			List<DevicePoint> devicePoints = devicePointRepository.findAllByDeviceSnInAndPointDesc(
					deviceRepository.findAllByNode_NodeIdAndSystemType_SystemId(mainNodeId, mainSystemId)
							.stream()
							.map(Device::getDeviceSn)
							.collect(Collectors.toList()),
					"storage_strategy"
			);
			if (devicePoints.size() == 0 || devicePoints.size() > 1) {
				return ResponseResult.error("储能节点没有录入控制点位，或者录入多个控制点位");
			}
			DevicePoint devicePoint = devicePoints.get(0);
			Device device = devicePoint.getDevice();
			issue(mainNodeId, mainSystemId, issueList, finalPower96Map, logList, device.getDeviceSn(), devicePoint.getPointSn(), dateymd);
		}
		cfgStorageEnergyStrategyPower96LogRepository.saveAll(logList);
		Map<String, String> map = new HashMap<>();
		map.put("date", formatter.format(date1));
		return ResponseResult.success(map);
		//	} catch (Exception e) {
//			log.info("e:{}",e.getMessage());
//		}
	}

	private void issue(String mainNodeId, String mainSystemId, List<Date> issueList,
Map<Date, List<CfgStorageEnergyStrategyPower96>> finalPower96Map,
	                   List<CfgStorageEnergyStrategyPower96Log> logList, String deviceSn, String pointSn, Date dateymd) {
		issueList.forEach(issue -> {
			List<CfgStorageEnergyStrategyPower96> cfgStorageEnergyStrategyPower96List = finalPower96Map.get(issue);
			cfgStorageEnergyStrategyPower96List.sort(Comparator.comparing(CfgStorageEnergyStrategyPower96::getSTime));
			Strategy96Model strategy96Model = new Strategy96Model();
			List<Every15MinuteModel> every15MinuteModelList = new ArrayList<>();
			List<Integer> logList1 = new ArrayList<>();
			List<String> logList2 = new ArrayList<>();
			if (cfgStorageEnergyStrategyPower96List.size() == 96) {
				for (CfgStorageEnergyStrategyPower96 cfgStorageEnergyStrategyPower96 : cfgStorageEnergyStrategyPower96List) {
					Every15MinuteModel every15MinuteModel = new Every15MinuteModel();
					if (cfgStorageEnergyStrategyPower96.getStrategy().contains("充")) {
						every15MinuteModel.setStrategyType(-1);
						every15MinuteModel.setStrategyLoad((int) Math.round(cfgStorageEnergyStrategyPower96.getPower()));
					} else if (cfgStorageEnergyStrategyPower96.getStrategy().contains("放")) {
						every15MinuteModel.setStrategyType(1);
						every15MinuteModel.setStrategyLoad((int) Math.round(cfgStorageEnergyStrategyPower96.getPower()));
					} else {
						every15MinuteModel.setStrategyType(0);
						every15MinuteModel.setStrategyLoad(0);
					}
					every15MinuteModelList.add(every15MinuteModel);
					logList1.add((int) Math.round(cfgStorageEnergyStrategyPower96.getPower()));
					logList2.add(cfgStorageEnergyStrategyPower96.getStrategy());
					logList.add(toDistLogInfo(new Date(), cfgStorageEnergyStrategyPower96));
				}
				Every15MinuteModel[] every15MinuteModels = every15MinuteModelList.toArray(new Every15MinuteModel[0]);
				strategy96Model.setCfStrategy(every15MinuteModels);
			}
			if (dateymd.equals(issue)) {
				strategy96Model.setImmediateEffectiveness(true);
			} else {
				strategy96Model.setImmediateEffectiveness(false);
			}
			log.info("日期：{}，下发策略详情:{},个数：{}", issue, logList1, logList1.size());
			log.info("下发策略:{},,个数：{}", logList2, logList2.size());
			log.info("开始下发储能策略,设备序列号:{},设备下点位序列号:{}下发策略:{}", deviceSn, pointSn, strategy96Model);
			newEnergyStorageChargingAndDischargingStrategyControl(deviceSn, pointSn, strategy96Model);
			cfgStorageEnergyStrategyPower96Repository.updateDistributeStatus(mainNodeId, mainSystemId, issue);
		});
	}

	private Map<Date, List<CfgStorageEnergyStrategyPower96>> issueEmpty(String mainNodeId, String mainSystemId,
	                                                                    List<CfgStorageEnergyStrategyPower96> addPower96List, Double power,
	                                                                    List<CfgStorageEnergyStrategy> cfgStorageEnergyStrategys,
	                                                                    Date starDate, Date endDate, SimpleDateFormat formatter) {
		List<Date> dateList = getDateRangeList(formatter.format(starDate), formatter.format(endDate));
		for (Date countDate : dateList) {
			String countDateStr = formatter.format(countDate);
			for (CfgStorageEnergyStrategy cfgStorageEnergyStrategy : cfgStorageEnergyStrategys) {
				//一个小时的数据
				List<String> fifteenMinutes = new ArrayList<>();
				String hour = cfgStorageEnergyStrategy.getSTime().substring(0, 2);
				int hourInt = Integer.parseInt(hour) + 1;
				String hour1 = hourInt >= 10 ? String.valueOf(hourInt) : "0" + String.valueOf(hourInt);
				fifteenMinutes.add(hour + ":00");
				fifteenMinutes.add(hour + ":15");
				fifteenMinutes.add(hour + ":30");
				fifteenMinutes.add(hour + ":45");
				fifteenMinutes.add(hour1 + ":00");
				for (int m = 0; m < 4; m++) {
					CfgStorageEnergyStrategyPower96 cfgStorageEnergyStrategyPower96 = new CfgStorageEnergyStrategyPower96();
					cfgStorageEnergyStrategyPower96.setId(mainNodeId + "_" + mainSystemId + "_" + countDateStr + fifteenMinutes.get(m) + "-" + fifteenMinutes.get(m + 1));
					cfgStorageEnergyStrategyPower96.setNodeId(mainNodeId);
					cfgStorageEnergyStrategyPower96.setSystemId(mainSystemId);
					cfgStorageEnergyStrategyPower96.setEffectiveDate(countDate);
					cfgStorageEnergyStrategyPower96.setSTime(fifteenMinutes.get(m));
					cfgStorageEnergyStrategyPower96.setETime(fifteenMinutes.get(m + 1));
					cfgStorageEnergyStrategyPower96.setPower(power);
					cfgStorageEnergyStrategyPower96.setStrategy(cfgStorageEnergyStrategy.getStrategy());
					cfgStorageEnergyStrategyPower96.setTimeScope(fifteenMinutes.get(m) + "-" + fifteenMinutes.get(m + 1));
					cfgStorageEnergyStrategyPower96.setPolicyModel(0);
					addPower96List.add(cfgStorageEnergyStrategyPower96);
				}
			}
		}
		return addPower96List.stream().collect(Collectors.groupingBy(CfgStorageEnergyStrategyPower96::getEffectiveDate));
	}

	private CfgStorageEnergyStrategyPower96Log toDistLogInfo(Date dateId, CfgStorageEnergyStrategyPower96 power96) {
		CfgStorageEnergyStrategyPower96Log log = new CfgStorageEnergyStrategyPower96Log();
		log.setId("Dist" + UUID.randomUUID());
		log.setNodeId(power96.getNodeId());
		log.setSystemId(power96.getSystemId());
		log.setEffectiveDate(power96.getEffectiveDate());
		log.setTimeScope(power96.getTimeScope());
		log.setSTime(power96.getSTime());
		log.setETime(power96.getETime());
		log.setPower(power96.getPower());
		log.setStrategy(power96.getStrategy());
		log.setDistributeStatus(power96.getDistributeStatus());
		log.setStrategyType("下发策略");
		log.setCreateTime(dateId);
		return log;
	}

	@ApiOperation("储能-次日策略生成并覆盖并下发")
	@UserLoginToken
	@RequestMapping(value = "futureAiEnergyStrategy", method = {RequestMethod.POST})
	public ResponseResult futureAiEnergyStrategy(@RequestBody Map<String, List<Double>> command) {
		try {
			log.info("算法生成策略:{}", JSON.toJSONString(command));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date now = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(now);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date effectiveDate = sdf.parse(sdf.format(calendar.getTime()));
			List<CfgStorageEnergyStrategyPower96Log> logList = new ArrayList<>();
			List<CfgStorageEnergyStrategyPower96Ai> aiList = new ArrayList<>();
			List<CfgStorageEnergyStrategyPower96> updateList = new ArrayList<>();
			for (Map.Entry<String, List<Double>> entry : command.entrySet()) {
				String key = entry.getKey();
				List<Double> value = entry.getValue();
				List<CfgStorageEnergyStrategyPower96> strategyPower96List = cfgStorageEnergyStrategyPower96Repository
						.findAllByNodeId(key, "nengyuanzongbiao", effectiveDate);
				List<CfgStorageEnergyStrategyPower96> addPower96List = new ArrayList<>();
				if (strategyPower96List.isEmpty()) {
					String id = key + "_" + "nengyuanzongbiao";

					CfgStorageEnergyBaseInfo cfgStorageEnergyBaseInfo = baseInfoRepository.findById(id).orElse(null);
					double power;
					if (cfgStorageEnergyBaseInfo != null) {
						power = cfgStorageEnergyBaseInfo.getStorageEnergyLoad();
					} else {
						return ResponseResult.error("储能节点储能配置基本信息未配置");
					}
					Date startDateM = TimeUtil.getMonthStart(effectiveDate);
					List<CfgStorageEnergyStrategy> cfgStorageEnergyStrategys = cfgStorageEnergyStrategyRepository.
							findCfgStorageEnergyStrategyByNodeIdAndSystemId(key, "nengyuanzongbiao", startDateM, startDateM);
					if (cfgStorageEnergyStrategys.size() == 0) {
						return ResponseResult.error("储能节点充放电策略未配置");
					}
					issueEmpty(key, "nengyuanzongbiao", addPower96List, power, cfgStorageEnergyStrategys, effectiveDate, effectiveDate, sdf);
					strategyPower96List = addPower96List;
					strategyPower96List.sort(Comparator.comparing(CfgStorageEnergyStrategyPower96::getSTime));
				}
				List<Integer> logList1 = new ArrayList<>();
				List<String> logList2 = new ArrayList<>();

				List<Every15MinuteModel> every15MinuteModelList = new ArrayList<>();
				if (strategyPower96List.size() == 96 && value.size() == 96) {
					List<CfgStorageEnergyStrategyPower96> finalStrategyPower96List = strategyPower96List;
					IntStream.range(0, strategyPower96List.size()).forEach(i -> {
						CfgStorageEnergyStrategyPower96 strategyPower96 = finalStrategyPower96List.get(i);
						CfgStorageEnergyStrategyPower96 oldStrategyPower96 = newDistInfo(strategyPower96);
						Double prediction = value.get(i);
						Integer policyModel = strategyPower96.getPolicyModel() == null ? 0 : strategyPower96.getPolicyModel();
						strategyPower96.setDistributeStatus(1);
						if (policyModel.equals(0)) {
							updateStrategy(strategyPower96, prediction);
							updateList.add(strategyPower96);
						}
						Every15MinuteModel every15MinuteModel = new Every15MinuteModel();
						if (strategyPower96.getStrategy().contains("充")) {
							every15MinuteModel.setStrategyType(-1);
							every15MinuteModel.setStrategyLoad((int) Math.round(strategyPower96.getPower()));
						} else if (strategyPower96.getStrategy().contains("放")) {
							every15MinuteModel.setStrategyType(1);
							every15MinuteModel.setStrategyLoad((int) Math.round(strategyPower96.getPower()));
						} else {
							every15MinuteModel.setStrategyType(0);
							every15MinuteModel.setStrategyLoad(0);
						}
						every15MinuteModelList.add(every15MinuteModel);
						updateStrategy(oldStrategyPower96, prediction);
						logList.add(toDistLogInfo(now, strategyPower96));
						aiList.add(toDistAiInfo(oldStrategyPower96));
						logList1.add((int) Math.round(strategyPower96.getPower()));
						logList2.add(strategyPower96.getStrategy());
					});

					//策略下发
					Strategy96Model strategy96Model = new Strategy96Model();
					Every15MinuteModel[] every15MinuteModels = every15MinuteModelList.toArray(new Every15MinuteModel[0]);
					strategy96Model.setCfStrategy(every15MinuteModels);
					strategy96Model.setImmediateEffectiveness(false);
					List<Device> allByNode_nodeIdAndSystemType_systemId = deviceRepository.findAllByNode_NodeIdAndSystemType_SystemId(key,
					"nengyuanzongbiao");
					List<String> deviceSns = allByNode_nodeIdAndSystemType_systemId.stream().map(Device::getDeviceSn).collect(Collectors.toList());
					List<DevicePoint> storage_strategy = devicePointRepository.findAllByDeviceSnInAndPointDesc(deviceSns, "storage_strategy");
					if (storage_strategy.size() == 0 || storage_strategy.size() > 1) {
						return ResponseResult.error("储能节点没有录入控制点位，或者录入多个控制点位");
					}
					DevicePoint devicePoint = storage_strategy.get(0);
					Device device = devicePoint.getDevice();

					log.info("日期：{}，下发策略详情:{},个数：{}", effectiveDate, logList1, logList1.size());
					log.info("下发策略:{},,个数：{}", logList2, logList2.size());
					log.info("futureAiEnergyStrategy开始下发储能策略,设备序列号:{},设备下点位序列号:{}下发策略:{}", device.getDeviceSn(), devicePoint.getPointSn(), strategy96Model);
					newEnergyStorageChargingAndDischargingStrategyControl(device.getDeviceSn(), devicePoint.getPointSn(), strategy96Model);
				}
			}
			cfgStorageEnergyStrategyPower96Repository.saveAll(updateList);
			cfgStorageEnergyStrategyPower96AiRepository.saveAll(aiList);
			cfgStorageEnergyStrategyPower96LogRepository.saveAll(logList);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseResult.error("覆盖策略失败: " + e.getMessage());
		}
		return ResponseResult.success("策略下发成功");
	}

	private void updateStrategy(CfgStorageEnergyStrategyPower96 strategyPower96, Double prediction) {
		Double power = Math.abs(prediction);
		if (prediction > 0) {
			strategyPower96.setStrategy("放电");
			strategyPower96.setPower(power);
		} else if (prediction < 0) {
			strategyPower96.setStrategy("充电");
			strategyPower96.setPower(power);
		} else {
			strategyPower96.setStrategy("待机");
			strategyPower96.setPower(0d);
		}
	}

	private CfgStorageEnergyStrategyPower96Ai toDistAiInfo(CfgStorageEnergyStrategyPower96 power96) {
		CfgStorageEnergyStrategyPower96Ai ai = new CfgStorageEnergyStrategyPower96Ai();
		ai.setId(power96.getId());
		ai.setNodeId(power96.getNodeId());
		ai.setSystemId(power96.getSystemId());
		ai.setEffectiveDate(power96.getEffectiveDate());
		ai.setTimeScope(power96.getTimeScope());
		ai.setSTime(power96.getSTime());
		ai.setETime(power96.getETime());
		ai.setPower(power96.getPower());
		ai.setStrategy(power96.getStrategy());
		ai.setDistributeStatus(power96.getDistributeStatus());
		ai.setPolicyModel(power96.getPolicyModel());
		return ai;
	}

	private CfgStorageEnergyStrategyPower96 newDistInfo(CfgStorageEnergyStrategyPower96 power96) {
		CfgStorageEnergyStrategyPower96 newPower96 = new CfgStorageEnergyStrategyPower96();
		newPower96.setId(power96.getId());
		newPower96.setNodeId(power96.getNodeId());
		newPower96.setSystemId(power96.getSystemId());
		newPower96.setEffectiveDate(power96.getEffectiveDate());
		newPower96.setTimeScope(power96.getTimeScope());
		newPower96.setSTime(power96.getSTime());
		newPower96.setETime(power96.getETime());
		newPower96.setPower(power96.getPower());
		newPower96.setStrategy(power96.getStrategy());
		newPower96.setDistributeStatus(power96.getDistributeStatus());
		newPower96.setPolicyModel(power96.getPolicyModel());
		return newPower96;
	}


}