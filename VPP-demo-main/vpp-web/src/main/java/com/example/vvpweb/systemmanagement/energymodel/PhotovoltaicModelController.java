package com.example.vvpweb.systemmanagement.energymodel;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.CfgPhotovoltaicBaseInfoRepository;
import com.example.vvpdomain.CfgPhotovoltaicDiscountRateRepository;
import com.example.vvpdomain.CfgPhotovoltaicTouPriceRepository;
import com.example.vvpdomain.entity.CfgPhotovoltaicBaseInfo;
import com.example.vvpdomain.entity.CfgPhotovoltaicDiscountRate;
import com.example.vvpdomain.entity.CfgPhotovoltaicTouPrice;
import com.example.vvpservice.energymodel.ProfitChartService;
import com.example.vvpservice.energymodel.model.BlackProfitRequest;
import com.example.vvpservice.energymodel.model.ProfitRequest;
import com.example.vvpservice.energymodel.model.ProfitResponse;
import com.example.vvpservice.globalapi.model.ListProjSubEnergyAndPvVo;
import com.example.vvpservice.globalapi.service.GlobalApiService;
import com.example.vvpweb.systemmanagement.energymodel.model.*;
import com.example.vvpweb.tradepower.model.TradeEnvironmentConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

/**
 * 光伏模型
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/system_management/energy_model/photovoltaic_model")
@Api(value = "系统管理-能源模型", tags = {"系统管理-能源模型"})
public class PhotovoltaicModelController {

	//分时电价
	@Resource
	CfgPhotovoltaicTouPriceRepository touPriceRepository;
	//折扣比例
	@Resource
	CfgPhotovoltaicDiscountRateRepository discountRateRepository;

	@Resource
	private ProfitChartService profitChartService;

	@Autowired
	private CfgPhotovoltaicBaseInfoRepository cfgPhotovoltaicBaseInfoRepository;
	private static TradeEnvironmentConfig config;
	@Resource
	private GlobalApiService globalApiService;

	@Autowired
	public PhotovoltaicModelController(TradeEnvironmentConfig environmentConfig) {
		config = environmentConfig;
	}

	@ApiOperation("查询光伏基本信息")
	@UserLoginToken
	@RequestMapping(value = "findPhotovoltaicBaseInfo", method = {RequestMethod.POST})
	public ResponseResult<PvBaseInfoResponse> findPhotovoltaicBaseInfo(@RequestParam(value = "nodeId") String nodeId
			, @RequestParam(value = "systemId") String systemId) {
		try {
			if (StringUtils.isNotEmpty(nodeId) && StringUtils.isNotEmpty(systemId)) {

				String type = "pv_baseInfo";
				//String id = nodeId + "_" + systemId + "_" + type;
				String id = nodeId + "_" + systemId;
				CfgPhotovoltaicBaseInfo baseInfo = cfgPhotovoltaicBaseInfoRepository.findById(id).orElse(null);
				if (baseInfo != null) {
					PvBaseInfoResponse pvBaseInfoResponse = new PvBaseInfoResponse();
					pvBaseInfoResponse.setNodeId(nodeId);
					pvBaseInfoResponse.setSystemId(systemId);

					pvBaseInfoResponse.setPhotovoltaicInstalledCapacity(baseInfo.getPhotovoltaicInstalledCapacity());

					return ResponseResult.success(pvBaseInfoResponse);
				}
			}

		} catch (Exception ex) {
			return ResponseResult.error("当前节点，系统下无数据，请先初始化数据。");
		}
		return ResponseResult.success(null);
	}


	@ApiOperation("编辑光伏基本信息")
	@UserLoginToken
	@RequestMapping(value = "savePhotovoltaicBaseInfo", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult savePhotovoltaicBaseInfo(@RequestBody PvBaseInfo info) {
		try {
			if (info != null
					&& StringUtils.isNotEmpty(info.getNodeId())
					&& StringUtils.isNotEmpty(info.getSystemId())) {

				String nodeId = info.getNodeId();
				String systemId = info.getSystemId();
				//基本信息保存
				{
					String id = nodeId + "_" + systemId;
					CfgPhotovoltaicBaseInfo baseInfo = cfgPhotovoltaicBaseInfoRepository.findById(id).orElse(null);
					if (baseInfo == null) {
						baseInfo = new CfgPhotovoltaicBaseInfo();
						baseInfo.setId(id);
					}
					baseInfo.setNodeId(nodeId);
					baseInfo.setSystemId(systemId);
					baseInfo.setDataType("photovoltaic");
					baseInfo.setPhotovoltaicInstalledCapacity(info.getPhotovoltaicInstalledCapacity());

					cfgPhotovoltaicBaseInfoRepository.save(baseInfo);
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


	@ApiOperation("查询光伏基本信息-分时电价")
	@UserLoginToken
	@RequestMapping(value = "findPhotovoltaicTimeDivisionBaseInfo", method = {RequestMethod.POST})
	public ResponseResult<PvTimeDivisionBaseInfoResponse> findPhotovoltaicTimeDivisionBaseInfo(@RequestParam(value = "nodeId") String nodeId
			, @RequestParam(value = "systemId") String systemId) {
		try {
			if (StringUtils.isNotEmpty(nodeId) && StringUtils.isNotEmpty(systemId)) {

				String type = "timeDivision";
				//String id = nodeId + "_" + systemId + "_" + type;
				String id = nodeId + "_" + systemId;
				CfgPhotovoltaicBaseInfo baseInfo = cfgPhotovoltaicBaseInfoRepository.findById(id).orElse(null);
				if (baseInfo != null) {
					PvTimeDivisionBaseInfoResponse pvBaseInfoResponse = new PvTimeDivisionBaseInfoResponse();
					pvBaseInfoResponse.setNodeId(nodeId);
					pvBaseInfoResponse.setSystemId(systemId);
					pvBaseInfoResponse.setTimeDivisionExpiryDate(baseInfo.getTimeDivisionExpiryDate());
					pvBaseInfoResponse.setTimeDivisionStartTime(baseInfo.getTimeDivisionStartTime());


					return ResponseResult.success(pvBaseInfoResponse);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("请选择节点和系统后查询。");
		}
		return ResponseResult.success(null);
	}


	@ApiOperation("编辑光伏基本信息-分时电价")
	@UserLoginToken
	@RequestMapping(value = "savePhotovoltaicBaseTimeDivisionInfo", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult savePhotovoltaicBaseTimeDivisionInfo(@RequestBody PvTimeDivisionBaseInfo info) {

		try {
			if (info.getTimeDivisionExpiryDate() < 0 || info.getTimeDivisionExpiryDate() > 30) {
				return ResponseResult.error("分时电价年限应在1-30年之间！");
			}
			if (info != null
					&& StringUtils.isNotEmpty(info.getNodeId())
					&& StringUtils.isNotEmpty(info.getSystemId())) {

				String nodeId = info.getNodeId();
				String systemId = info.getSystemId();
				//分时电价
				{
					String id = nodeId + "_" + systemId;
					CfgPhotovoltaicBaseInfo baseInfo = cfgPhotovoltaicBaseInfoRepository.findById(id).orElse(null);
					if (baseInfo == null) {
						baseInfo = new CfgPhotovoltaicBaseInfo();
						baseInfo.setId(id);
					}
					baseInfo.setNodeId(nodeId);
					baseInfo.setSystemId(systemId);
					baseInfo.setDataType("photovoltaic");
					baseInfo.setTimeDivisionExpiryDate(info.getTimeDivisionExpiryDate());
					baseInfo.setTimeDivisionStartTime(info.getTimeDivisionStartTime());
					cfgPhotovoltaicBaseInfoRepository.save(baseInfo);
				}

				//分时电价 数据初始化
				FutureTask<Boolean> futureTask = new FutureTask(() -> {
					try {
						SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM");
						ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));

						List<CfgPhotovoltaicTouPrice> touPrices = new ArrayList<>();

						int year = info.getTimeDivisionExpiryDate();
						Date s_dt = info.getTimeDivisionStartTime();
						Date e_dt = TimeUtil.dateAddYears(s_dt, year);

						List<String> months = TimeUtil.findDates("M", s_dt, e_dt, 1);
						if (months != null && months.size() > 0) {
							for (int i = 0; i < months.size(); i++) {

								Date effectiveDate = ym.parse(months.get(i));
								for (int j = 0; j < 24; j++) {

									int order = j + 1;
									String sTime = j < 10 ? ("0" + j + ":00") : (j + ":00");
									String eTime = j < 10 ? ("0" + j + ":59") : (j + ":59");

									String touPrice_id = nodeId + "_" + systemId + "_" + ym.format(effectiveDate) + "_" + order;

									CfgPhotovoltaicTouPrice pvTouPrice = new CfgPhotovoltaicTouPrice();

									pvTouPrice.setId(touPrice_id);
									pvTouPrice.setNodeId(nodeId);
									pvTouPrice.setSystemId(systemId);
									pvTouPrice.setEffectiveDate(effectiveDate);
									pvTouPrice.setOrder(order);

									pvTouPrice.setProperty("尖");
									pvTouPrice.setTimeFrame(sTime + "-" + eTime);
									pvTouPrice.setSTime(sTime);
									pvTouPrice.setETime(eTime);
									pvTouPrice.setPriceHour(BigDecimal.valueOf(0.3765));

									//价格表标签
									String priceTag = nodeId
											+ "_" + systemId
											+ "_" + ym.format(effectiveDate)
											+ "_" + pvTouPrice.getTimeFrame();
									pvTouPrice.setPriceTag(priceTag);

									touPrices.add(pvTouPrice);
								}
							}
						}

						if (touPrices != null && touPrices.size() > 0) {
							touPriceRepository.deleteAllByNodeIdAndSystemId(nodeId, systemId);
							Thread.sleep(500);
							touPriceRepository.saveAll(touPrices);
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
			return ResponseResult.error("没有找到保存对象");
		}
	}


	@ApiOperation("查询光伏基本信息-电力用户购电折扣比例")
	@UserLoginToken
	@RequestMapping(value = "findPhotovoltaicPowerUserBaseInfo", method = {RequestMethod.POST})
	public ResponseResult<PvPowerUserBaseInfoResponse> findPhotovoltaicPowerUserBaseInfo(@RequestParam(value = "nodeId") String nodeId
			, @RequestParam(value = "systemId") String systemId) {
		try {
			if (StringUtils.isNotEmpty(nodeId) && StringUtils.isNotEmpty(systemId)) {

				String id = nodeId + "_" + systemId;
				CfgPhotovoltaicBaseInfo baseInfo = cfgPhotovoltaicBaseInfoRepository.findById(id).orElse(null);
				if (baseInfo != null) {
					PvPowerUserBaseInfoResponse pvBaseInfoResponse = new PvPowerUserBaseInfoResponse();
					pvBaseInfoResponse.setNodeId(nodeId);
					pvBaseInfoResponse.setSystemId(systemId);
					pvBaseInfoResponse.setPowerUserStartTime(baseInfo.getPowerUserStartTime());
					pvBaseInfoResponse.setPowerUserExpiryDate(baseInfo.getPowerUserExpiryDate());

					return ResponseResult.success(pvBaseInfoResponse);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("请选择节点和系统后查询。");
		}
		return ResponseResult.success(null);
	}


	@ApiOperation("编辑光伏基本信息-电力用户购电折扣比例")
	@UserLoginToken
	@RequestMapping(value = "savePhotovoltaicBasePowerUserInfo", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult savePhotovoltaicBasePowerUserInfo(@RequestBody PvPowerUserBaseInfo info) {

		try {
			if (info.getPowerUserExpiryDate() < 0 || info.getPowerUserExpiryDate() > 30) {
				return ResponseResult.error("电力用户购电折扣比例年限应在1-30年之间！");
			}
			if (info != null
					&& StringUtils.isNotEmpty(info.getNodeId())
					&& StringUtils.isNotEmpty(info.getSystemId())) {
				String nodeId = info.getNodeId();
				String systemId = info.getSystemId();
				//电力用户购电折扣比例
				{
					String id = nodeId + "_" + systemId;
					CfgPhotovoltaicBaseInfo baseInfo = cfgPhotovoltaicBaseInfoRepository.findById(id).orElse(null);
					if (baseInfo == null) {
						baseInfo = new CfgPhotovoltaicBaseInfo();
						baseInfo.setId(id);
					}
					baseInfo.setNodeId(nodeId);
					baseInfo.setSystemId(systemId);
					baseInfo.setDataType("photovoltaic");
					baseInfo.setPowerUserExpiryDate(info.getPowerUserExpiryDate());
					baseInfo.setPowerUserStartTime(info.getPowerUserStartTime());
					cfgPhotovoltaicBaseInfoRepository.save(baseInfo);
				}

				//电力用户购电折扣比例 数据初始化
				FutureTask<Boolean> futureTask = new FutureTask(() -> {
					try {

						SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM");
						ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));

						SimpleDateFormat ym_ = new SimpleDateFormat("yyyyMM");
						ym_.setTimeZone(TimeZone.getTimeZone("GMT+8"));

						List<CfgPhotovoltaicDiscountRate> discountRates = new ArrayList<>();

						int year = info.getPowerUserExpiryDate();
						Date s_dt = info.getPowerUserStartTime();
						Date e_dt = TimeUtil.dateAddYears(s_dt, year);

						List<String> months = TimeUtil.findDates("M", s_dt, e_dt, 1);

						if (months != null && months.size() > 0) {

							for (int i = 0; i < months.size(); i++) {

								String effectiveDate = ym.format(ym.parse(months.get(i)));
								String shareProp_id = nodeId + "_" + systemId + "_" + effectiveDate;
								String effectiveDate_ = ym_.format(ym.parse(months.get(i)));

								CfgPhotovoltaicDiscountRate prop = new CfgPhotovoltaicDiscountRate();
								prop.setId(shareProp_id);
								prop.setPowerUserProp(0.9);
								prop.setLoadProp(0.1);
								prop.setOrder(Integer.parseInt(effectiveDate_));
								prop.setNodeId(nodeId);
								prop.setSystemId(systemId);

								discountRates.add(prop);
							}
						}

						if (discountRates != null && discountRates.size() > 0) {
							discountRateRepository.deleteAllByNodeIdAndSystemId(nodeId, systemId);
							Thread.sleep(500);
							discountRateRepository.saveAll(discountRates);
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
			return ResponseResult.error("没有找到保存对象");
		}
	}


	@ApiOperation("查询分时电价")
	@UserLoginToken
	@RequestMapping(value = "findPvTimeDivision", method = {RequestMethod.POST})
	public ResponseResult<PageModel> findPvTimeDivision(@RequestBody PvTimeDivisionModel model) {
		try {
			if (model != null
					&& StringUtils.isNotEmpty(model.getNodeId())
					&& StringUtils.isNotEmpty(model.getSystemId())
					&& model.getEffectiveDate() != null
					&& model.getNumber() >= 1
					&& model.getPageSize() >= 1) {
				Specification<CfgPhotovoltaicTouPrice> spec = new Specification<CfgPhotovoltaicTouPrice>() {
					@Override
					public Predicate toPredicate(Root<CfgPhotovoltaicTouPrice> root
							, CriteriaQuery<?> criteriaQuery
							, CriteriaBuilder cb) {
						List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况

						predicates.add(cb.equal(root.get("nodeId"), model.getNodeId()));//对应SQL语句：select * from ### where username= code
						predicates.add(cb.equal(root.get("systemId"), model.getSystemId()));
						predicates.add(cb.equal(root.get("effectiveDate"), model.getEffectiveDate()));


						criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
						criteriaQuery.orderBy(cb.asc(root.get("order"))); //按照createTime升序排列
						return criteriaQuery.getRestriction();
					}
				};

				//当前页为第几页 默认 1开始
				int page = model.getNumber();
				int size = model.getPageSize();

				Pageable pageable = PageRequest.of(page - 1, size);

				Page<CfgPhotovoltaicTouPrice> datas = touPriceRepository.findAll(spec, pageable);

				PageModel pageModel = new PageModel();
				//封装到pageUtil
				pageModel.setContent(datas.getContent());
				pageModel.setTotalPages(datas.getTotalPages());
				pageModel.setTotalElements((int) datas.getTotalElements());
				pageModel.setNumber(datas.getNumber() + 1);

				return ResponseResult.success(pageModel);
			} else {
				return ResponseResult.error("请检查接入参数");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("没有找到保存对象");
		}
	}

	@ApiOperation("复制光伏其它月份分时电价")
	@UserLoginToken
	@Transactional
	@RequestMapping(value = "copyPvTimeDivision", method = {RequestMethod.POST})
	public ResponseResult copyPvTimeDivision(@RequestBody PvTimeDivisionCopyModel model) {
		try {
			if (model != null
					&& StringUtils.isNotEmpty(model.getNodeId())
					&& StringUtils.isNotEmpty(model.getSystemId())
					&& model.getFromEffectiveDate() != null
					&& model.getToEffectiveDate() != null
			) {

				List<CfgPhotovoltaicTouPrice> cfgPhotovoltaicTouPrices = touPriceRepository.findAllByNodeIdAndSystemIdAndEffectiveDate(
						model.getNodeId(),
						model.getSystemId(),
						model.getFromEffectiveDate());

				if (cfgPhotovoltaicTouPrices == null || cfgPhotovoltaicTouPrices.isEmpty()) {
					return ResponseResult.error("请检查复制月份电价是否存在");
				}

				List<CfgPhotovoltaicTouPrice> exits = touPriceRepository.findAllByNodeIdAndSystemIdAndEffectiveDate(
						model.getToNodeId(),
						model.getToSystemId(),
						model.getToEffectiveDate());

				if (exits == null || exits.isEmpty()) {
					return ResponseResult.error("当月的分时电价超过运营年限,不能复制");
				}

				List<CfgPhotovoltaicTouPrice> toCfgPhotovoltaicTouPrices = new ArrayList<>();

				SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM");

				cfgPhotovoltaicTouPrices.forEach(e -> {
					CfgPhotovoltaicTouPrice to = new CfgPhotovoltaicTouPrice();

					to.setId(e.getId().replace("_" + ym.format(model.getFromEffectiveDate()) + "_",
									"_" + ym.format(model.getToEffectiveDate()) + "_").
							replace(model.getNodeId() + "_" + model.getSystemId(),
									model.getToNodeId() + "_" + model.getToSystemId()));
					to.setNodeId(model.getToNodeId());
					to.setSystemId(model.getToSystemId());
					to.setEffectiveDate(model.getToEffectiveDate());
					to.setOrder(e.getOrder());

					to.setProperty(e.getProperty());
					to.setTimeFrame(e.getTimeFrame());
					to.setSTime(e.getSTime());
					to.setETime(e.getETime());
					to.setPriceHour(e.getPriceHour());

					//价格表标签
					to.setPriceTag(e.getPriceTag().replace("_" + ym.format(model.getFromEffectiveDate()) + "_",
									"_" + ym.format(model.getToEffectiveDate()) + "_").
							replace(model.getNodeId() + "_" + model.getSystemId(),
									model.getToNodeId() + "_" + model.getToSystemId()));

					toCfgPhotovoltaicTouPrices.add(to);
				});


				touPriceRepository.saveAll(toCfgPhotovoltaicTouPrices);
				return ResponseResult.success();
			} else {
				return ResponseResult.error("请检查接入参数");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("没有找到保存对象");
		}
	}


	@ApiOperation("编辑分时电价")
	@UserLoginToken
	@RequestMapping(value = "updatePvTimeDivision", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult updatePvTimeDivision(@RequestBody UpdatePvTimeDivisionModel model) {
		try {
			if (model != null) {
				CfgPhotovoltaicTouPrice energyStrategy = touPriceRepository.findById(model.getId()).orElse(null);
				if (energyStrategy != null) {
					energyStrategy.setProperty(model.getProperty());
					energyStrategy.setPriceHour(model.getPriceHour());
					touPriceRepository.save(energyStrategy);
					return ResponseResult.success();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("编辑分时电价失败！");
		}
		return ResponseResult.success(null);
	}


	@ApiOperation("查询电力用户购电折扣比例")
	@UserLoginToken
	@RequestMapping(value = "findPvPowerUser", method = {RequestMethod.POST})
	public ResponseResult<PageModel> findPvPowerUser(@RequestBody PvPowerUserModel model) {
		try {
			Specification<CfgPhotovoltaicDiscountRate> spec = new Specification<CfgPhotovoltaicDiscountRate>() {
				@Override
				public Predicate toPredicate(Root<CfgPhotovoltaicDiscountRate> root
						, CriteriaQuery<?> criteriaQuery
						, CriteriaBuilder cb) {
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

			Page<CfgPhotovoltaicDiscountRate> datas = discountRateRepository.findAll(spec, pageable);

			PageModel pageModel = new PageModel();
			//封装到pageUtil
			pageModel.setContent(datas.getContent());
			pageModel.setTotalPages(datas.getTotalPages());
			pageModel.setTotalElements((int) datas.getTotalElements());
			pageModel.setNumber(datas.getNumber() + 1);

			return ResponseResult.success(pageModel);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("查询电力用户购电折扣比例失败！");
		}
	}


	@ApiOperation("编辑电力用户购电折扣比例")
	@UserLoginToken
	@RequestMapping(value = "updatePvPowerUser", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult updatePvPowerUser(@RequestBody UpdatePvPowerUserModel model) {
		try {
			if (model != null) {
				CfgPhotovoltaicDiscountRate shareProportion = discountRateRepository.findById(model.getId()).orElse(null);
				if (shareProportion != null) {
					shareProportion.setPowerUserProp(model.getPowerUserProp());
					shareProportion.setLoadProp(model.getLoadProp());
					shareProportion.setOperatorProp(model.getOperatorProp());
					discountRateRepository.save(shareProportion);
					return ResponseResult.success();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("编辑电力用户购电折扣比例失败！");
		}
		return ResponseResult.success(null);
	}

	@ApiOperation("编辑电力用户购电折扣比例")
	@UserLoginToken
	@RequestMapping(value = "updatePvPowerUserBatch", method = {RequestMethod.POST})
	@Transactional
	public ResponseResult updatePvPowerUserBatch(@RequestBody UpdatePvPowerUserBatchModel model) {
		try {
			if (model != null) {

				String st = String.valueOf(model.getSt().getYear()) + model.getSt().getMonthValue();
				String et = String.valueOf(model.getEt().getYear()) + model.getEt().getMonthValue();
				List<CfgPhotovoltaicDiscountRate> shareProportionList =
						discountRateRepository.findAllByNodeIdAndOrderBetween(model.getNodeId(), Integer.valueOf(st), Integer.valueOf(et));
				shareProportionList.forEach(o -> {
					o.setLoadProp(model.getLoadProp());
					o.setPowerUserProp(model.getPowerUserProp());
					o.setOperatorProp(model.getOperatorProp());
				});
				discountRateRepository.saveAll(shareProportionList);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseResult.error("编辑电力用户购电折扣比例失败！");
		}
		return ResponseResult.success(null);
	}

	@ApiOperation("光伏-预测收益")
	@UserLoginToken
	@RequestMapping(value = "profit", method = {RequestMethod.POST})
	public ResponseResult<List<ProfitResponse>> findProfit(@RequestBody ProfitRequest request) {
		List<String> nodeIds = new ArrayList<>();
		nodeIds.add(config.getPvNode1());
		Map<String, String> energyNodeMap = new HashMap<>();
		energyNodeMap.put(config.getPvNode1(),"光伏001");
		request.setNodeId(nodeIds);
		List<ProfitResponse> responseList = profitChartService.getPvProfitChart(request,energyNodeMap);
		return ResponseResult.success(responseList);
	}

	@ApiOperation("全量-预测收益")
	@UserLoginToken
	@RequestMapping(value = "profitAll", method = {RequestMethod.POST})
	public ResponseResult<List<ProfitResponse>> findProfitAll(@RequestBody ProfitRequest request) {
		List<String> pvNodeList = Arrays.asList(config.getPvNode1());
		List<String> enNodeList = Arrays.asList(config.getEnergyNode1(), config.getEnergyNode2());
		Map<String, String> energyNodeMap = new HashMap<>();
		energyNodeMap.put(config.getEnergyNode1(),"储能001");
		energyNodeMap.put(config.getEnergyNode2(),"储能002");
		Map<String, String> loadNodeMap = new HashMap<>();
		loadNodeMap.put(config.getPvNode1(),"光伏001");

		List<ProfitResponse> PvResponseList = profitChartService.getProfitChartAll(loadNodeMap,energyNodeMap,pvNodeList, enNodeList, request.getSystemId(), request.getStartDate(), request.getEndDate());
		return ResponseResult.success(PvResponseList);
	}
	@ApiOperation("光伏-预测收益")
	@UserLoginToken
	@RequestMapping(value = "profitNew", method = {RequestMethod.POST})
	public ResponseResult<List<ProfitResponse>> profitNew(@RequestBody BlackProfitRequest request) {
		ListProjSubEnergyAndPvVo energyBlockList = globalApiService.stationTreeEnergyAndPv(request.getNodeId());
		Map<String, String> loadNodeMap = energyBlockList.getPhotovoltaic().stream()
				.collect(Collectors.toMap(
						ListProjSubEnergyAndPvVo::getNodeId,
						ListProjSubEnergyAndPvVo::getStationName,
						(oldValue, newValue) -> oldValue
				));
		ProfitRequest request1 = new ProfitRequest();
		request1.setNodeId(new ArrayList<>(loadNodeMap.keySet()));
		request1.setSystemId(request.getSystemId());
		request1.setStartDate(request.getStartDate());
		request1.setEndDate(request.getEndDate());
		List<ProfitResponse> responseList = profitChartService.getPvProfitChart(request1,loadNodeMap);
		return ResponseResult.success(responseList);
	}
	@ApiOperation("全量-预测收益")
	@UserLoginToken
	@RequestMapping(value = "profitAllNew", method = {RequestMethod.POST})
	public ResponseResult<List<ProfitResponse>> profitAllNew(@RequestBody BlackProfitRequest request) {
		ListProjSubEnergyAndPvVo energyBlockList = globalApiService.stationTreeEnergyAndPv(request.getNodeId());
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
		List<ProfitResponse> PvResponseList = profitChartService.getProfitChartAll(loadNodeMap,energyNodeMap,pvNodeList, enNodeList, request.getSystemId(), request.getStartDate(), request.getEndDate());
		return ResponseResult.success(PvResponseList);
	}
}
