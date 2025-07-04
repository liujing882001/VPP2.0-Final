package com.example.vvpservice.revenue;
import java.util.Objects;
import java.util.Date;
import java.time.Instant;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.HttpUtil;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpcommom.StringUtils;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.i18n.i18nUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.globalapi.model.ListProjSubEnergyAndPvVo;
import com.example.vvpservice.globalapi.service.GlobalApiService;
import com.example.vvpservice.nodeprofit.model.BillNodeProfit;
import com.example.vvpservice.nodeprofit.service.NodeProfitServiceImpl;
import com.example.vvpservice.point.service.mappingStrategy.impl.IOTMappingStrategy;
import com.example.vvpservice.revenue.enums.EstimationParameterEnum;
import com.example.vvpservice.revenue.model.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.stream.Stream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RevenueCalculationService {

	// 手动添加log变量以确保编译通过
	private static final Logger log = LoggerFactory.getLogger(RevenueCalculationService.class);

	private RevenueParameterRepository parameterRepository;

	private RevenueProjectRepository projectRepository;

	private RevenueLoadDateRepository loadDateRepository;

	private Map<String, String> fileNameMap = new HashMap<>();

	private static final String DIRECTORY_NAME = "uploadFile";

	@Autowired
	RevenueCalculationService(RevenueParameterRepository parameterRepository, RevenueProjectRepository projectRepository,
	                          RevenueLoadDateRepository loadDateRepository) {
		this.parameterRepository = parameterRepository;
		this.projectRepository = projectRepository;
		this.loadDateRepository = loadDateRepository;
	}

	@Value("${xiaoda.url}")
	private String url;

	public List<ExcelData> readExcel(String projectId) {
		List<ExcelData> data = new ArrayList<>();
		String fileName = projectId + ".xlsx";
		String path = getFilePath(fileName);
		validateExcel(path);
		log.info("save excel to {}", path);
		EasyExcel.read(path, ExcelData.class, new ExcelListener(projectId, data, false)).sheet().headRowNumber(2).doRead();
		return data;
	}

	private void validateExcel(String file) {
		EasyExcel.read(file, new ValidateListener()).sheet().doRead();
	}

	public String saveExcel(String originalName, InputStream inputStream, String projectId) {
		String uuid = StringUtils.isEmpty(projectId) ? UUID.randomUUID().toString() : projectId;
		String fileName = uuid + ".xlsx";
		fileNameMap.put(uuid, originalName);

		String filePath = getFilePath(fileName);
		log.info("save excel to {}", filePath);

		try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
			byte[] buffer = new byte[1024]; // 1KB buffer
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			log.error("io exception", e);
		} finally {
			try {
				inputStream.close(); // 关闭输入流
			} catch (IOException e) {
				log.error("io exception", e);
			}
		}
		validateExcel(filePath);
		if (!StringUtils.isEmpty(projectId)) {
			readExcel(projectId);
		}
		return uuid;
	}

	private static String getFilePath(String fileName) {
		String currDir = System.getProperty("user.dir");
		Path path = Paths.get(currDir, DIRECTORY_NAME);
		try {
			Files.createDirectories(path);
		} catch (Exception e) {
			log.error("创建文件夹失败", e);
		}
		return path.getFileName() + File.separator + fileName;
	}

	public RevenueProjectInfo findProjectById(String projectId) {
		return projectRepository.findById(projectId).orElse(new RevenueProjectInfo());
	}

	public RevenueProjectInfo save(RevenueProjectInfo info) {
		return projectRepository.save(info);
	}

	public long getProjectCount() {
		return projectRepository.count();
	}

	public List<ExcelData> findLoadDataByProjectId(String projectId) {
		List<ExcelData> res = readExcel(projectId);
		res.sort(Comparator.comparing(ExcelData::getDate));
		return res;
	}

	public List<ExcelData> findLoadDataByProjectIdAndTime(String projectId, String st, String et) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startTime = fmt.parse(st);
		Date endTime = fmt.parse(et);
		List<ExcelData> res = readExcel(projectId);
		for (Iterator<ExcelData> iterator = res.listIterator(); iterator.hasNext(); ) {
			ExcelData data = iterator.next();
			if (data.getDate().before(startTime) || data.getDate().after(endTime)) {
				iterator.remove();
			}
		}
		res.sort(Comparator.comparing(ExcelData::getDate));
		return res;
	}


	public RevenueProjectInfo createNewProject() {
		RevenueProjectInfo projectInfo = new RevenueProjectInfo();
		projectInfo.setId(UUID.randomUUID().toString());
		projectRepository.save(projectInfo);
		return projectInfo;
	}

	public RevenueProjectInfo updateProject(UpdateProjectReq req) {
		RevenueProjectInfo projectInfo = projectRepository.findById(req.getProjectId()).get();
		JSONObject object = JSON.parseObject(projectInfo.getFundamentalParameter());

		JSONObject eleParam = JSON.parseObject(projectInfo.getElectricityParameter());

		List<String> percentageParams = getPercentageParameter();
		for (Map.Entry<String, String> entry : req.getBaseInfo().entrySet()) {
			if (object.containsKey(entry.getKey())) {
				if (percentageParams.contains(entry.getKey())) {
					entry.setValue(String.valueOf(Double.parseDouble(entry.getValue()) / 100));
				}
				object.put(entry.getKey(), entry.getValue());
			}
			if (eleParam.containsKey(entry.getKey())) {
				eleParam.put(entry.getKey(), entry.getValue());
			}
			if (entry.getKey().equals("projectName")) {
				projectInfo.setName(entry.getValue());
			}
			if (entry.getKey().equals("area")) {
				projectInfo.setArea(entry.getValue());
			}
		}
		projectInfo.setFundamentalParameter(JSON.toJSONString(object));
		projectInfo.setElectricityParameter(JSON.toJSONString(eleParam));
		projectRepository.save(projectInfo);

		return projectInfo;
	}

	public Boolean checkReGenerateReport(UpdateProjectReq req) {
		AtomicReference<Boolean> result = new AtomicReference<>(false);
		List<String> params = Arrays.asList(EstimationParameterEnum.designCapacity.toString(), EstimationParameterEnum.designPower.toString(),
				EstimationParameterEnum.usableDepth.toString(), EstimationParameterEnum.systemEfficiency.toString());
		List<String> eleParams = Arrays.asList("type1", "type2", "vol1", "vol2");
		RevenueProjectInfo projectInfo = projectRepository.findById(req.getProjectId()).get();
		JSONObject object = JSON.parseObject(projectInfo.getFundamentalParameter());
		checkParamUpdated(req, result, params, object);

		JSONObject object1 = JSON.parseObject(projectInfo.getElectricityParameter());
		checkParamUpdated(req, result, eleParams, object1);
		return result.get();
	}

	private void checkParamUpdated(UpdateProjectReq req, AtomicReference<Boolean> result, List<String> eleParams, JSONObject object) {
		List<String> percentageParams = getPercentageParameter();

		eleParams.forEach(o -> {
			if (result.get().equals(true)) {
				return;
			}
			if (req.getBaseInfo().get(o) != null && object.get(o) != null) {
				if (percentageParams.contains(o)) {
					if (Double.parseDouble((String) object.get(o)) * 100 != Double.parseDouble(req.getBaseInfo().get(o))) {
						result.set(true);
					}
				} else {
					result.set(!object.get(o).equals(req.getBaseInfo().get(o)));
				}
			}
		});
	}

	public RevenueProjectInfo createNewProject(String projectId, String name, String area, Double volume, Double power) {
		RevenueProjectInfo projectInfo = projectRepository.findById(projectId).orElse(new RevenueProjectInfo());
		projectInfo.setId(projectId);
		projectInfo.setFileName(fileNameMap.getOrDefault(projectId, ""));
		projectInfo.setName(name);
		projectInfo.setArea(area);
		projectInfo.setFundamentalParameter(getBasicParameter(volume, power));
		projectInfo.setElectricityParameter(getDefaultElectricityParams(area));
		projectRepository.save(projectInfo);
		return projectInfo;
	}

	private String getDefaultElectricityParams(String area) {
		JSONObject response = JSON.parseObject(getCityInfo());
		JSONArray jsonArray = (JSONArray) response.get("data");
		List<EleNodeInfo> res = jsonArray.toJavaList(EleNodeInfo.class);
		JSONObject eleParam = new JSONObject();
		for (EleNodeInfo tmp : res) {
			if (tmp.getValue().equals(area)) {
				while (tmp.getChildren() != null && !tmp.getChildren().isEmpty()) {
					tmp = tmp.getChildren().get(tmp.getChildren().size() - 1);
					eleParam.put(tmp.getField(), tmp.getValue());
				}
			}
		}
		return JSON.toJSONString(eleParam);
	}

	public String getBasicParameter(Double vol, Double power) {
		List<RevenueParameterDto> basicParams = getDefaultBasicParameter();
		Map<String, RevenueParameterDto> map = basicParams.stream().collect(Collectors.toMap(RevenueParameterDto::getParamName,
				Function.identity()));
		map.get(EstimationParameterEnum.designPower.getChineseName()).setDefaultValue(String.valueOf(power));
		map.get(EstimationParameterEnum.designCapacity.getChineseName()).setDefaultValue(String.valueOf(vol));
		JSONObject object = new JSONObject();
		basicParams.forEach(o -> object.put(o.getParamId(), o.getDefaultValue()));
		return JSON.toJSONString(object);
	}

	public List<String> getPercentageParameter() {
		List<String> params = new ArrayList<>();
		List<RevenueParameterDto> basicParams = getDefaultBasicParameter();
		basicParams.forEach(o -> {
			if (o.getUnit().equals("%")) {
				params.add(o.getParamId());
			}
		});
		return params;
	}

	public List<RevenueParameterDto> getDefaultBasicParameter() {
		return parameterRepository.findAll();
	}

	public List<JSONObject> getProjectIds() {
		List<RevenueProjectInfo> list = projectRepository.findAll();
		List<JSONObject> res = new ArrayList<>();
		list.forEach(o -> {
			JSONObject object = new JSONObject();
			object.put("id", o.getId());
			object.put("name", o.getName());
			object.put("area", o.getArea());
			res.add(object);
		});
		return res;
	}

	public List<EleNodeInfo> queryCityInfo(String city, String type1, String type2) {
		List<String> params = new ArrayList<>();
		params.add(city);
		params.add(type1);
		params.add(type2);
		JSONObject result = JSON.parseObject(HttpUtil.okHttpGet(url + "/charge_info/queryCityEleSystem"));
		List<EleNodeInfo> infos = JSON.parseArray(JSON.toJSONString(result.get("data")), EleNodeInfo.class);
		if (StringUtils.isEmpty(type1) || StringUtils.isEmpty(type2)) {
			List<EleNodeInfo> res = getChild(infos, city);
			for (EleNodeInfo re : res) {
				for (EleNodeInfo child : re.getChildren()) {
					child.setChildren(null);
				}
			}
			return res;
		} else {
			int index = 0;
			while (!CollectionUtils.isEmpty(infos) && !Objects.equals(infos.get(0).getField(), "vol1")) {
				infos = getChild(infos, params.get(index));
				index++;
			}
			return infos;
		}

	}

	private List<EleNodeInfo> getChild(List<EleNodeInfo> infos, String value) {
		for (EleNodeInfo info : infos) {
			if (info.getValue().equals(value)) {
				return info.getChildren();
			}
		}
		return new ArrayList<>();
	}

	public String getCityInfo() {

		return HttpUtil.okHttpGet(url + "/charge_info/queryCityEleSystem");
	}

	public String getElectricityPrice(String city, String type1, String type2, String vol1, String vol2) {

		String queryCityInfoUrl = url + "/charge_info/queryYearly";
		queryCityInfoUrl += "?";
		queryCityInfoUrl =
				queryCityInfoUrl + "city=" + city + "&type1=" + type1 + "&type2=" + type2 + "&voltage1=" + vol1 + "&voltage2=" + (StringUtils.isEmpty(vol2) ? "" : vol2);
		log.info("查询电价：{}", queryCityInfoUrl);
		return HttpUtil.okHttpGet(queryCityInfoUrl);

	}

	public static boolean isLeapYear(int year) {
		return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
	}

	public void exportExcel(ExcelWriter writer, String projectId, String estResult) throws IOException {
		RevenueProjectInfo info = findProjectById(projectId);
		List<RevenueParameterDto> defaultParams = getDefaultBasicParameter();
		List<ReportData> excelData = new ArrayList<>();
		JSONObject parameter = JSON.parseObject(info.getFundamentalParameter());
		AtomicLong index = new AtomicLong(1);
		Map<String, RevenueParameterDto> parameterDtoMap = defaultParams.stream().collect(Collectors.toMap(RevenueParameterDto::getParamId,
				Function.identity()));

		List<String> basicParam = Arrays.asList(EstimationParameterEnum.designPower.toString(), EstimationParameterEnum.backupHours.toString(),
				EstimationParameterEnum.designCapacity.toString());

		List<String> economicParam = Arrays.asList(EstimationParameterEnum.purchasePrice.toString(),
				EstimationParameterEnum.totalInvestment.toString(), EstimationParameterEnum.equipmentCost.toString(),
				EstimationParameterEnum.engineeringCost.toString(), EstimationParameterEnum.assessPeriod.toString());

		List<String> profitParam = Arrays.asList("storageTotalRev", "storageTotalProfit", "invTotalRevXYears", "invAvgAnnualRev", "customerShare",
				"usrTotalRevXYears", "usrAvgAnnualRev");

		List<String> analyzeParam = Arrays.asList("storageIRRPreTax", "storagePayback");

		HashMap<String, String> remarkMap = new HashMap<String, String>() {
			{
				put(EstimationParameterEnum.equipmentCost.toString(), "占投资总额90%");
				put(EstimationParameterEnum.engineeringCost.toString(), "占投资总额10%");
				put(EstimationParameterEnum.storageIRRPreTax.toString(), "扣除运营方及用户收益;\n不计入税费");
			}
		};

		JSONObject estResultJson = JSON.parseObject(estResult);

		buildExcelInfo(excelData, parameter, index, parameterDtoMap, basicParam, "基本信息", remarkMap);

		buildExcelInfo(excelData, parameter, index, parameterDtoMap, economicParam, "经济参数", remarkMap);

		buildExcelInfo(excelData, index, profitParam, estResultJson, "储能收益", remarkMap);

		buildExcelInfo(excelData, index, analyzeParam, estResultJson, "投资分析", remarkMap);

		WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
		WriteFont font = new WriteFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 12);
		contentWriteCellStyle.setWriteFont(font);
		// 设置内容单元格边框样式
		contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
		contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
		contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
		contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
		// 设置内容单元格边框颜色
		contentWriteCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		contentWriteCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		contentWriteCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		contentWriteCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

		// 设置字体
		WriteFont contentWriteFont = new WriteFont();
		contentWriteFont.setFontName("Arial");
		contentWriteFont.setFontHeightInPoints((short) 12);
		contentWriteCellStyle.setWriteFont(contentWriteFont);
		contentWriteCellStyle.setWrapped(true);

		HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(contentWriteCellStyle, contentWriteCellStyle);

		WriteSheet writeSheet =
				EasyExcel.writerSheet("收益报告").head(ReportData.class).registerWriteHandler(horizontalCellStyleStrategy).registerWriteHandler(new ExcelColumnWidth()).registerWriteHandler(new ExcelFillCellMergeStrategy(1, new int[]{1})).build();
		writer.write(excelData, writeSheet);

	}

	private void buildExcelInfo(List<ReportData> excelData, AtomicLong index, List<String> analyzeParam, JSONObject estResultJson, String typeName,
	                            HashMap<String, String> remarkMap) {
		analyzeParam.forEach(o -> {
			if (estResultJson.keySet().contains(o)) {
				ReportData reportData = new ReportData();
				reportData.setId(index.get());
				reportData.setType(typeName);
				reportData.setName(i18nUtil.getMessage(o));
				reportData.setValue(Double.valueOf(estResultJson.get(o).toString()));
				if (o.equals("customerShare") || o.equals("storageIRRPreTax")) {
					reportData.setUnit("%");
				} else if (o.equals("storagePayback")) {
					reportData.setUnit("年");
				} else {
					reportData.setUnit("万元");
				}
				reportData.setRemark(remarkMap.getOrDefault(o, null));
				excelData.add(reportData);
				index.addAndGet(1);
			}
		});
	}

	private void buildExcelInfo(List<ReportData> excelData, JSONObject parameter, AtomicLong index, Map<String, RevenueParameterDto> parameterDtoMap
			, List<String> basicParam, String typeName, HashMap<String, String> remarkMap) {
		basicParam.forEach(o -> {
			if (parameter.keySet().contains(o)) {
				JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(parameterDtoMap.get(o)));
				if (jsonObject.get("unit").equals("%")) {
					jsonObject.put("default_value", Double.parseDouble((String) jsonObject.get("default_value")) * 100);
				}
				excelData.add(buildExcelData(parameterDtoMap.get(o), (String) parameter.get(o), index.get(), typeName, remarkMap.getOrDefault(o,
						null)));
				index.addAndGet(1);
			}
		});
	}

	private ReportData buildExcelData(RevenueParameterDto o, String value, Long index, String typeName, String remark) {
		ReportData data = new ReportData();
		data.setId(index);
		if (o.getParamId().equals("designPower")) {
			data.setName("储能功率");
		} else if (o.getParamId().equals("designCapacity")) {
			data.setName("储能容量");
		} else {
			data.setName(o.getParamName());
		}
		data.setType(typeName);
		data.setValue(Double.valueOf(value));
		data.setUnit(o.getUnit());
		data.setRemark(remark);
		return data;
	}

	public List<ProjectRevenueResponse> queryProjectRevenue(QueryProjectRequest request) {
		List<ProjectRevenueResponse> res = new ArrayList<>();

		request.getInfos().forEach(projectInfo -> {
			ProjectRevenueResponse revenueResponse = new ProjectRevenueResponse();
			revenueResponse.setNodeId(projectInfo.getProjectId());
			List<Pair<LocalDate, LocalDate>> timeSplitByMonths = splitByMonth(projectInfo.getSt(), projectInfo.getEt());
			timeSplitByMonths.forEach(time -> {
				try {
					LocalDate st = time.getKey();
					LocalDate et = time.getValue();
					NodeProfitServiceImpl nodeProfitService = SpringBeanHelper.getBeanOrThrow(NodeProfitServiceImpl.class);
					GlobalApiService globalApiService = SpringBeanHelper.getBeanOrThrow(GlobalApiService.class);
					CfgStorageEnergyShareProportionRepository cfgStorageEnergyShareProportionRepository =
							SpringBeanHelper.getBeanOrThrow(CfgStorageEnergyShareProportionRepository.class);
					CfgPhotovoltaicDiscountRateRepository cfgPhotovoltaicDiscountRateRepository =
							SpringBeanHelper.getBeanOrThrow(CfgPhotovoltaicDiscountRateRepository.class);
					ListProjSubEnergyAndPvVo vo = globalApiService.stationTreeEnergyAndPvNoM(projectInfo.getProjectId());
					ProjectRevenueResponse.Profit esProfit = new ProjectRevenueResponse.Profit();
					ProjectRevenueResponse.Profit pvProfit = new ProjectRevenueResponse.Profit();
					vo.getEnergy().forEach(energyNode -> {
						Date date = Date.from(st.atStartOfDay(ZoneId.systemDefault()).toInstant());
						BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(energyNode.getNodeId(), date,
								Date.from(et.atStartOfDay(ZoneId.systemDefault()).toInstant()));

						esProfit.setType("energy");
						esProfit.setMonth(st.getYear() + "." + st.getMonthValue());
						CfgStorageEnergyShareProportion share =
								cfgStorageEnergyShareProportionRepository.findByNodeIdAndSystemIdAndOrder(energyNode.getNodeId(), "nengyuanzongbiao",
										Integer.parseInt(Objects.requireNonNull(TimeUtil.toYmNumberStr(date))));
						BigDecimal profitTotal = BigDecimal.valueOf(billNodeProfit.getOutElectricityPeakPrice())
								.add(BigDecimal.valueOf(billNodeProfit.getOutElectricityLowPrice()))
								.add(BigDecimal.valueOf(billNodeProfit.getOutElectricityStablePrice()))
								.add(BigDecimal.valueOf(billNodeProfit.getOutElectricityHighPrice()))
//								.add(BigDecimal.valueOf(billNodeProfit.getOutElectricityRavinePrice()))
								.subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityHighPrice()))
								.subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityPeakPrice()))
								.subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityStablePrice()))
								.subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityLowPrice()));
//								.subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityRavinePrice()));
						esProfit.setProfitTotal(esProfit.getProfitTotal().add(profitTotal));
						esProfit.setProfitActual(esProfit.getProfitActual().add(BigDecimal.valueOf(share.getLoadProp()).multiply(profitTotal)));
						esProfit.setProfitElectricity(esProfit.getProfitElectricity().add(BigDecimal.valueOf(share.getPowerUserProp()).multiply(profitTotal)));
						esProfit.setProfitOperator(esProfit.getProfitOperator().add(BigDecimal.valueOf(share.getOperatorProp()).multiply(BigDecimal.valueOf(share.getLoadProp()).multiply(profitTotal))));
					});
					if (!vo.getEnergy().isEmpty() && (request.getEnergyType() == null ||
							request.getEnergyType().equals("energy") || request.getEnergyType().isEmpty())) {
						revenueResponse.getProfits().add(esProfit);
					}

					vo.getPhotovoltaic().forEach(pvNode -> {
						Date date = Date.from(st.atStartOfDay(ZoneId.systemDefault()).toInstant());
						BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(pvNode.getNodeId(), date,
								Date.from(et.atStartOfDay(ZoneId.systemDefault()).toInstant()));
						pvProfit.setType("pv");
						pvProfit.setMonth(st.getYear() + "." + st.getMonthValue());
						CfgPhotovoltaicDiscountRate share =
								cfgPhotovoltaicDiscountRateRepository.findByNodeIdAndSystemIdAndOrder(pvNode.getNodeId(), "nengyuanzongbiao",
										Integer.parseInt(Objects.requireNonNull(TimeUtil.toYmNumberStr(date))));
						BigDecimal profitTotal = BigDecimal.valueOf(billNodeProfit.getOutElectricityPeakPrice())
								.add(BigDecimal.valueOf(billNodeProfit.getOutElectricityLowPrice()))
								.add(BigDecimal.valueOf(billNodeProfit.getOutElectricityStablePrice()))
								.add(BigDecimal.valueOf(billNodeProfit.getOutElectricityHighPrice()))
//								.add(BigDecimal.valueOf(billNodeProfit.getOutElectricityRavinePrice()))
								.subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityHighPrice()))
								.subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityPeakPrice()))
								.subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityStablePrice()))
								.subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityLowPrice()));
//								.subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityRavinePrice()));
						pvProfit.setProfitTotal(pvProfit.getProfitTotal().add(profitTotal));
						pvProfit.setProfitActual(pvProfit.getProfitActual().add(BigDecimal.valueOf(share.getLoadProp()).multiply(profitTotal)));
						pvProfit.setProfitElectricity(pvProfit.getProfitElectricity().add(BigDecimal.valueOf(share.getPowerUserProp()).multiply(profitTotal)));
						pvProfit.setProfitOperator(pvProfit.getProfitOperator().add(BigDecimal.valueOf(share.getOperatorProp()).multiply(BigDecimal.valueOf(share.getLoadProp()).multiply(profitTotal))));
					});
					if (!vo.getPhotovoltaic().isEmpty() && (request.getEnergyType() == null || request.getEnergyType().equals("pv") || request.getEnergyType().isEmpty())) {
						revenueResponse.getProfits().add(pvProfit);
					}
				} catch (Exception e) {
					log.info("queryProjectProfit错误:{}", e.getMessage());
				}

			});
			res.add(revenueResponse);
		});
		return res;
	}

	private List<Pair<LocalDate, LocalDate>> splitByMonth(LocalDate st, LocalDate et) {
		List<Pair<LocalDate, LocalDate>> res = new ArrayList<>();
		LocalDate currentStartDate = st;
		while (!currentStartDate.isAfter(et)) {
			// 获取当前月的结束日期
			LocalDate currentEndDate = currentStartDate.withDayOfMonth(currentStartDate.lengthOfMonth());

			// 如果当前月的结束日期超出终止日期，则以终止日期作为结束日期
			if (currentEndDate.isAfter(et)) {
				currentEndDate = et;
			}

			res.add(new MutablePair<>(currentStartDate, currentEndDate));
			currentStartDate = currentStartDate.plusMonths(1).withDayOfMonth(1);

		}
		return res;
	}

	public List<ProjectRevenueDetailResponse> queryProjectProfitDetail(QueryProjectRequest request) {
		List<ProjectRevenueDetailResponse> res = new ArrayList<>();

		request.getInfos().forEach(projectInfo -> {
			ProjectRevenueDetailResponse revenueResponse = new ProjectRevenueDetailResponse();
			revenueResponse.setNodeId(projectInfo.getProjectId());
			List<Pair<LocalDate, LocalDate>> timeSplitByMonths = splitByMonth(projectInfo.getSt(), projectInfo.getEt());
			timeSplitByMonths.forEach(time -> {
				LocalDate st = time.getLeft();
				LocalDate et = time.getRight();
				NodeProfitServiceImpl nodeProfitService = SpringBeanHelper.getBeanOrThrow(NodeProfitServiceImpl.class);
				GlobalApiService globalApiService = SpringBeanHelper.getBeanOrThrow(GlobalApiService.class);
				ListProjSubEnergyAndPvVo vo = globalApiService.stationTreeEnergyAndPvNoM(projectInfo.getProjectId());
				ProjectRevenueDetailResponse.Detail esChargeDetail = new ProjectRevenueDetailResponse.Detail();
				ProjectRevenueDetailResponse.Detail esDisChargeDetail = new ProjectRevenueDetailResponse.Detail();

				ProjectRevenueDetailResponse.Detail pvDetail = new ProjectRevenueDetailResponse.Detail();
				esChargeDetail.setChargingType("充电");
				esDisChargeDetail.setChargingType("放电");
				pvDetail.setChargingType("放电");
				esChargeDetail.setMonth(st.getYear() + "." + st.getMonthValue());
				esDisChargeDetail.setMonth(st.getYear() + "." + st.getMonthValue());
				pvDetail.setMonth(st.getYear() + "." + st.getMonthValue());

				ProjectRevenueDetailResponse.EleInfo esChargeSharp = new ProjectRevenueDetailResponse.EleInfo("尖");
				ProjectRevenueDetailResponse.EleInfo esChargePeak = new ProjectRevenueDetailResponse.EleInfo("峰");
				ProjectRevenueDetailResponse.EleInfo esChargeShoulder = new ProjectRevenueDetailResponse.EleInfo("平");
				ProjectRevenueDetailResponse.EleInfo esChargeOffPeak = new ProjectRevenueDetailResponse.EleInfo("谷");
//				ProjectRevenueDetailResponse.EleInfo esChargeRavine = new ProjectRevenueDetailResponse.EleInfo("深谷");
				ProjectRevenueDetailResponse.EleInfo esDisChargeSharp = new ProjectRevenueDetailResponse.EleInfo("尖");
				ProjectRevenueDetailResponse.EleInfo esDisChargePeak = new ProjectRevenueDetailResponse.EleInfo("峰");
				ProjectRevenueDetailResponse.EleInfo esDisChargeShoulder = new ProjectRevenueDetailResponse.EleInfo("平");
				ProjectRevenueDetailResponse.EleInfo esDisChargeOffPeak = new ProjectRevenueDetailResponse.EleInfo("谷");
//				ProjectRevenueDetailResponse.EleInfo esDisChargeRavine = new ProjectRevenueDetailResponse.EleInfo("深谷");
				ProjectRevenueDetailResponse.EleInfo pvDisChargeSharp = new ProjectRevenueDetailResponse.EleInfo("尖");
				ProjectRevenueDetailResponse.EleInfo pvDisChargePeak = new ProjectRevenueDetailResponse.EleInfo("峰");
				ProjectRevenueDetailResponse.EleInfo pvDisChargeShoulder = new ProjectRevenueDetailResponse.EleInfo("平");
				ProjectRevenueDetailResponse.EleInfo pvDisChargeOffPeak = new ProjectRevenueDetailResponse.EleInfo("谷");
//				ProjectRevenueDetailResponse.EleInfo pvChargeRavine = new ProjectRevenueDetailResponse.EleInfo("深谷");

				vo.getEnergy().forEach(energyNode -> {
					Date date = Date.from(st.atStartOfDay(ZoneId.systemDefault()).toInstant());
					BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(energyNode.getNodeId(), date,
							Date.from(et.atStartOfDay(ZoneId.systemDefault()).toInstant()));

					// 更新充电电量和金额
					setElectricityDetail(esChargeSharp, billNodeProfit.getPriceHigh(), billNodeProfit.getInElectricityHigh(),
							billNodeProfit.getInElectricityHighPrice(), esChargeDetail);
					setElectricityDetail(esChargePeak, billNodeProfit.getPricePeak(), billNodeProfit.getInElectricityPeak(),
							billNodeProfit.getInElectricityPeakPrice(), esChargeDetail);
					setElectricityDetail(esChargeShoulder, billNodeProfit.getPriceStable(), billNodeProfit.getInElectricityStable(),
							billNodeProfit.getInElectricityStablePrice(), esChargeDetail);
					setElectricityDetail(esChargeOffPeak, billNodeProfit.getPriceLow(), billNodeProfit.getInElectricityLow(),
							billNodeProfit.getInElectricityLowPrice(), esChargeDetail);
//					setElectricityDetail(esChargeRavine, billNodeProfit.getPriceRavine(), billNodeProfit.getInElectricityRavine(),
//							billNodeProfit.getInElectricityRavinePrice(), esChargeDetail);
					// 更新放电电量和金额
					setElectricityDetail(esDisChargeSharp, billNodeProfit.getPriceHigh(), billNodeProfit.getOutElectricityHigh(),
							billNodeProfit.getOutElectricityHighPrice(), esDisChargeDetail);
					setElectricityDetail(esDisChargePeak, billNodeProfit.getPricePeak(), billNodeProfit.getOutElectricityPeak(),
							billNodeProfit.getOutElectricityPeakPrice(), esDisChargeDetail);
					setElectricityDetail(esDisChargeShoulder, billNodeProfit.getPriceStable(), billNodeProfit.getOutElectricityStable(),
							billNodeProfit.getOutElectricityStablePrice(), esDisChargeDetail);
					setElectricityDetail(esDisChargeOffPeak, billNodeProfit.getPriceLow(), billNodeProfit.getOutElectricityLow(),
							billNodeProfit.getOutElectricityLowPrice(), esDisChargeDetail);
//					setElectricityDetail(esDisChargeRavine, billNodeProfit.getPriceRavine(), billNodeProfit.getOutElectricityRavine(),
//							billNodeProfit.getOutElectricityRavinePrice(), esDisChargeDetail);

				});
				if (!vo.getEnergy().isEmpty() && (request.getEnergyType() == null || request.getEnergyType().equals("energy") || request.getEnergyType().isEmpty())) {
					esChargeDetail.getData().add(esChargeSharp);
					esChargeDetail.getData().add(esChargePeak);
					esChargeDetail.getData().add(esChargeShoulder);
					esChargeDetail.getData().add(esChargeOffPeak);

					revenueResponse.getEsDetail().add(esChargeDetail);

					esDisChargeDetail.getData().add(esDisChargeSharp);
					esDisChargeDetail.getData().add(esDisChargePeak);
					esDisChargeDetail.getData().add(esDisChargeShoulder);
					esDisChargeDetail.getData().add(esDisChargeOffPeak);
					revenueResponse.getEsDetail().add(esDisChargeDetail);
				}

				vo.getPhotovoltaic().forEach(pvNode -> {
					Date date = Date.from(st.atStartOfDay(ZoneId.systemDefault()).toInstant());
					BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(pvNode.getNodeId(), date,
							Date.from(et.atStartOfDay(ZoneId.systemDefault()).toInstant()));

					// 更新放电电量和金额
					setElectricityDetail(pvDisChargeSharp, billNodeProfit.getPriceHigh(), billNodeProfit.getOutElectricityHigh(),
							billNodeProfit.getOutElectricityHighPrice(), pvDetail);
					setElectricityDetail(pvDisChargePeak, billNodeProfit.getPricePeak(), billNodeProfit.getOutElectricityPeak(),
							billNodeProfit.getOutElectricityPeakPrice(), pvDetail);
					setElectricityDetail(pvDisChargeShoulder, billNodeProfit.getPriceStable(), billNodeProfit.getOutElectricityStable(),
							billNodeProfit.getOutElectricityStablePrice(), pvDetail);
					setElectricityDetail(pvDisChargeOffPeak, billNodeProfit.getPriceLow(), billNodeProfit.getOutElectricityLow(),
							billNodeProfit.getOutElectricityLowPrice(), pvDetail);
//					setElectricityDetail(pvChargeRavine, billNodeProfit.getPriceRavine(), billNodeProfit.getOutElectricityRavine(),
//							billNodeProfit.getOutElectricityRavinePrice(), pvDetail);
				});
				if (!vo.getPhotovoltaic().isEmpty() && (request.getEnergyType() == null || request.getEnergyType().equals("pv") || request.getEnergyType().isEmpty())) {
					pvDetail.getData().add(pvDisChargeSharp);
					pvDetail.getData().add(pvDisChargePeak);
					pvDetail.getData().add(pvDisChargeShoulder);
					pvDetail.getData().add(pvDisChargeOffPeak);
					revenueResponse.getPvDetail().add(pvDetail);
				}
			});
			res.add(revenueResponse);

		});
		return res;
	}

	// 定义一个辅助方法来设置电价、电量和金额
	private void setElectricityDetail(ProjectRevenueDetailResponse.EleInfo detail,
	                                  double price, double power, double amount,
	                                  ProjectRevenueDetailResponse.Detail summary) {
		detail.setPrice(price);
		detail.setPower(detail.getPower() + power);
		detail.setAmount(detail.getAmount() + amount);
		summary.setTotalPower(summary.getTotalPower() + power);
		summary.setTotalAmount(summary.getTotalAmount() + amount);
	}

	public List<ProjectPowerResponse> queryProjectPower(QueryProjectRequest request) {
		List<ProjectPowerResponse> responses = new ArrayList<>();
		request.getInfos().forEach(projectInfo -> {
			ProjectPowerResponse powerResponse = new ProjectPowerResponse();
			powerResponse.setNodeId(projectInfo.getProjectId());
			List<Pair<LocalDate, LocalDate>> timeSplitByMonths = splitByMonth(projectInfo.getSt(), projectInfo.getEt());
			timeSplitByMonths.forEach(time -> {
				ProjectPowerResponse.PowerInfo info = new ProjectPowerResponse.PowerInfo();
				LocalDate st = time.getLeft();
				LocalDate et = time.getRight();
				GlobalApiService globalApiService = SpringBeanHelper.getBeanOrThrow(GlobalApiService.class);
				SpringBeanHelper.getBeanOrThrow(CfgPhotovoltaicDiscountRateRepository.class);
				PointModelMappingRepository modelMappingRepository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);
				IOTMappingStrategy iotMappingStrategy = SpringBeanHelper.getBeanOrThrow(IOTMappingStrategy.class);
				ListProjSubEnergyAndPvVo vo = globalApiService.stationTreeEnergyAndPvNoM(projectInfo.getProjectId());
				Map<String, Double> resMap = new HashMap<>();
				vo.getEnergy().forEach(energyNode -> {
					List<PointModelMapping> mappings =
							modelMappingRepository.findAllByStation_StationId(energyNode.getNodeId())
									.stream().filter(o -> o.getPointModel().getKey().equals("forward_active_energy") || o.getPointModel().getKey().equals("backward_active_energy"))
									.collect(Collectors.toList());
					mappings.forEach(o -> {
						IotTsKvMeteringDevice96 firstValue = (IotTsKvMeteringDevice96) iotMappingStrategy.getFirstValue(o,
								Date.from(st.atStartOfDay(ZoneId.systemDefault()).toInstant()));
						IotTsKvMeteringDevice96 lastValue = (IotTsKvMeteringDevice96) iotMappingStrategy.getLastValue(o,
								Date.from(et.atTime(23, 59, 59, 999000000).atZone(ZoneId.systemDefault()).toInstant()));
						if (firstValue == null || lastValue == null) {
							return;
						}
						Double sum = lastValue.getTotalPowerEnergy() - firstValue.getTotalPowerEnergy();
						resMap.put(o.getPointModel().getKey(), resMap.getOrDefault(o.getPointModel().getKey(), 0.0) + sum);
					});
				});
				vo.getPhotovoltaic().forEach(pvNode -> {
					List<PointModelMapping> mappings =
							modelMappingRepository.findAllByStation_StationId(pvNode.getNodeId())
									.stream().filter(o -> o.getPointModel().getKey().equals("energy"))
									.collect(Collectors.toList());
					mappings.forEach(o -> {
						IotTsKvMeteringDevice96 firstValue = (IotTsKvMeteringDevice96) iotMappingStrategy.getFirstValue(o,
								Date.from(st.atStartOfDay(ZoneId.systemDefault()).toInstant()));
						IotTsKvMeteringDevice96 lastValue = (IotTsKvMeteringDevice96) iotMappingStrategy.getLastValue(o,
								Date.from(et.atTime(23, 59, 59, 999000000).atZone(ZoneId.systemDefault()).toInstant()));
						if (firstValue == null || lastValue == null) {
							return;
						}
						Double sum = lastValue.getTotalPowerEnergy() - firstValue.getTotalPowerEnergy();

						resMap.put(o.getPointModel().getKey(), resMap.getOrDefault(o.getPointModel().getKey(), 0.0) + sum);
					});
				});

				info.setMonth(st.getYear() + "." + st.getMonthValue());

				if (resMap.get("forward_active_energy") != null && resMap.get("backward_active_energy") != null) {
					info.setEsChargeVolume(resMap.get("forward_active_energy"));
					info.setEsDischargeVolume(resMap.get("backward_active_energy"));
				}
				if (resMap.get("energy") != null) {
					info.setPvVolume(resMap.get("energy"));
				}

				boolean tag = false;
				if (request.getEnergyType() == null || request.getEnergyType().equals("energy") || request.getEnergyType().isEmpty()) {
					if (!vo.getEnergy().isEmpty()) {
						tag = true;
					}
				}
				if (request.getEnergyType() == null || request.getEnergyType().equals("pv") || request.getEnergyType().isEmpty()) {
					if (!vo.getPhotovoltaic().isEmpty()) {
						tag = true;
					}
				}
				if (tag) {
					powerResponse.getInfos().add(info);
				}

			});
			responses.add(powerResponse);
		});
		return responses;
	}

	public List<ProjectPowerDetailResponse> queryProjectPowerDetail(QueryProjectRequest request) {
		GlobalApiService globalApiService = SpringBeanHelper.getBeanOrThrow(GlobalApiService.class);
		PointModelMappingRepository modelMappingRepository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);
		IOTMappingStrategy iotMappingStrategy = SpringBeanHelper.getBeanOrThrow(IOTMappingStrategy.class);
		NodeProfitServiceImpl nodeProfitService = SpringBeanHelper.getBeanOrThrow(NodeProfitServiceImpl.class);

		List<ProjectPowerDetailResponse> responses = new ArrayList<>();
		request.getInfos().forEach(projectInfo -> {
			ProjectPowerDetailResponse powerResponse = new ProjectPowerDetailResponse();
			powerResponse.setNodeId(projectInfo.getProjectId());
			List<Pair<LocalDate, LocalDate>> timeSplitByMonths = splitByMonth(projectInfo.getSt(), projectInfo.getEt());
			ListProjSubEnergyAndPvVo vo = globalApiService.stationTreeEnergyAndPvNoM(projectInfo.getProjectId());
			timeSplitByMonths.forEach(time -> {
				ProjectPowerDetailResponse.DetailInfo info = new ProjectPowerDetailResponse.DetailInfo();
				LocalDate st = time.getLeft();
				LocalDate et = time.getRight();

				if (request.getEnergyType() == null || request.getEnergyType().equals("energy") || request.getEnergyType().isEmpty()) {
					vo.getEnergy().forEach(energyNode -> {
						ProjectPowerDetailResponse.DeviceInfo chargeDevice = new ProjectPowerDetailResponse.DeviceInfo();

						ProjectPowerDetailResponse.DeviceInfo disChargeDevice = new ProjectPowerDetailResponse.DeviceInfo();
						BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(energyNode.getNodeId(),
								Date.from(st.atStartOfDay(ZoneId.systemDefault()).toInstant()),
								Date.from(et.atStartOfDay(ZoneId.systemDefault()).toInstant()));
						List<PointModelMapping> mappings =
								modelMappingRepository.findAllByStation_StationId(energyNode.getNodeId())
										.stream().filter(o -> o.getPointModel().getKey().equals("forward_active_energy") || o.getPointModel().getKey().equals("backward_active_energy"))
										.collect(Collectors.toList());
						mappings.forEach(o -> {
							chargeDevice.setDeviceName(o.getStation().getStationName() + "充电设备");
							chargeDevice.setType("charge");
							chargeDevice.setDeviceType("energy");
							disChargeDevice.setDeviceName(o.getStation().getStationName() + "放电设备");
							disChargeDevice.setType("discharge");
							disChargeDevice.setDeviceType("energy");

							IotTsKvMeteringDevice96 firstValue = (IotTsKvMeteringDevice96) iotMappingStrategy.getFirstValue(o,
									Date.from(st.atStartOfDay(ZoneId.systemDefault()).toInstant()));
							IotTsKvMeteringDevice96 lastValue = (IotTsKvMeteringDevice96) iotMappingStrategy.getLastValue(o,
									Date.from(et.atTime(23, 59, 59, 999000000).atZone(ZoneId.systemDefault()).toInstant()));
							if (firstValue == null || lastValue == null) {
								return;
							}
							if (o.getPointModel().getKey().equals("forward_active_energy")) {
								chargeDevice.setStart(firstValue.getTotalPowerEnergy());
								chargeDevice.setEnd(lastValue.getTotalPowerEnergy());
								chargeDevice.setMeteredConsumption(lastValue.getTotalPowerEnergy() - firstValue.getTotalPowerEnergy());
								chargeDevice.setBillingConsumption(billNodeProfit.getInElectricityLow() + billNodeProfit.getInElectricityHigh() + billNodeProfit.getInElectricityPeak() + billNodeProfit.getInElectricityStable());
							} else {
								disChargeDevice.setStart(firstValue.getTotalPowerEnergy());
								disChargeDevice.setEnd(lastValue.getTotalPowerEnergy());
								disChargeDevice.setMeteredConsumption(lastValue.getTotalPowerEnergy() - firstValue.getTotalPowerEnergy());
								disChargeDevice.setBillingConsumption(billNodeProfit.getOutElectricityLow() + billNodeProfit.getOutElectricityHigh() + billNodeProfit.getOutElectricityPeak() + billNodeProfit.getOutElectricityStable());
							}
						});
						info.getDeviceInfo().add(chargeDevice);
						info.getDeviceInfo().add(disChargeDevice);
					});
				}
				if (request.getEnergyType() == null || request.getEnergyType().equals("pv") || request.getEnergyType().isEmpty()) {
					vo.getPhotovoltaic().forEach(pvNode -> {
						BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(pvNode.getNodeId(),
								Date.from(st.atStartOfDay(ZoneId.systemDefault()).toInstant()),
								Date.from(et.atStartOfDay(ZoneId.systemDefault()).toInstant()));
						ProjectPowerDetailResponse.DeviceInfo disChargeDevice = new ProjectPowerDetailResponse.DeviceInfo();
						List<PointModelMapping> mappings =
								modelMappingRepository.findAllByStation_StationId(pvNode.getNodeId())
										.stream().filter(o -> o.getPointModel().getKey().equals("energy"))
										.collect(Collectors.toList());
						mappings.forEach(o -> {
							IotTsKvMeteringDevice96 firstValue = (IotTsKvMeteringDevice96) iotMappingStrategy.getFirstValue(o,
									Date.from(st.atStartOfDay(ZoneId.systemDefault()).toInstant()));
							IotTsKvMeteringDevice96 lastValue = (IotTsKvMeteringDevice96) iotMappingStrategy.getLastValue(o,
									Date.from(et.atTime(23, 59, 59, 999000000).atZone(ZoneId.systemDefault()).toInstant()));
							if (firstValue == null || lastValue == null) {
								return;
							}
							disChargeDevice.setStart(firstValue.getTotalPowerEnergy());
							disChargeDevice.setEnd(lastValue.getTotalPowerEnergy());
							disChargeDevice.setDeviceName(o.getStation().getStationName() + "光伏逆变器");
							disChargeDevice.setLoss(0.0);
							disChargeDevice.setBillingConsumption(lastValue.getTotalPowerEnergy() - firstValue.getTotalPowerEnergy());
							disChargeDevice.setMeteredConsumption(billNodeProfit.getOutElectricityLow() + billNodeProfit.getOutElectricityHigh() + billNodeProfit.getOutElectricityPeak() + billNodeProfit.getOutElectricityStable());
							disChargeDevice.setType("discharge");
							disChargeDevice.setDeviceType("pv");
						});
						info.getDeviceInfo().add(disChargeDevice);
					});
				}


				info.setMonth(st.getYear() + "." + st.getMonthValue());
				powerResponse.getInfos().add(info);
			});
			responses.add(powerResponse);
		});
		return responses;
	}
}
