package com.example.vvpweb.presalesmodule;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpcommom.StringUtils;
import com.example.vvpdomain.RevenueLoadDateRepository;
import com.example.vvpdomain.RevenueProjectRepository;
import com.example.vvpdomain.entity.RevenueLoadDto;
import com.example.vvpdomain.entity.RevenueParameterDto;
import com.example.vvpdomain.entity.RevenueProjectInfo;
import com.example.vvpservice.revenue.CashFlow;
import com.example.vvpservice.revenue.ExcelData;
import com.example.vvpservice.revenue.RevenueCalculationService;
import com.example.vvpservice.revenue.model.CustomRowColorHandler;
import com.example.vvpservice.revenue.model.EleNodeInfo;
import com.example.vvpservice.revenue.model.ExcelColumnWidth2;
import com.example.vvpservice.revenue.model.UpdateProjectReq;
import com.example.vvpweb.presalesmodule.model.*;
import com.example.vvpweb.presalesmodule.model.factory.RevenueEstimationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@EnableAsync
@Slf4j
@RestController
@RequestMapping("/revenueEst")
@CrossOrigin
@Api(value = "收益测算", tags = {"收益测算"})
public class RevenueEstimationController {

	@Autowired
	private RevenueCalculationService revenueCalculationService;
	@Resource
	RevenueLoadDateRepository revenueLoadDateRepository;

	private final Map<String, RevenueEstVO> reportMap = new HashMap<>();

	/**
	 * true:等待报告生成
	 * false:旧报告失效 或 未生成
	 */
	private final Map<String, Boolean> reportCacheAvailable = new HashMap<>();

	@PostConstruct
	public void initReportCache() {
		RevenueProjectRepository projectRepository = SpringBeanHelper.getBeanOrThrow(RevenueProjectRepository.class);
		List<RevenueProjectInfo> infosToDelete = new ArrayList<>();
		List<RevenueProjectInfo> projectInfos = projectRepository.findAll();
		projectInfos.forEach(o -> {
			if (StringUtils.isEmpty(o.getIntermediateResult())) {
				infosToDelete.add(o);
			}
		});
		projectRepository.deleteInBatch(infosToDelete);
	}

	@ApiOperation(value = "新建项目")
	@RequestMapping(value = "/createProject", method = {RequestMethod.POST})
	public ResponseResult<ProjectResponse> createProject(@RequestBody CreateProjectRequest request) {
		try {
			RevenueProjectInfo info = revenueCalculationService.createNewProject(request.getProjectId(), request.getProjectName(), request.getArea()
					, request.getVolume(), request.getPower());
			// 负荷数据验证
			List<ExcelData> loadData = revenueCalculationService.readExcel(request.getProjectId());
			LocalDate localDate = loadData.get(0).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if (RevenueCalculationService.isLeapYear(localDate.getYear())) {
				if (loadData.size() != 366 * 96) {
					return ResponseResult.error(HttpStatus.SC_BAD_REQUEST, "负荷数据不足1年(366天)", null);
				}
			} else {
				if (loadData.size() != 365 * 96) {
					return ResponseResult.error(HttpStatus.SC_BAD_REQUEST, "负荷数据不足1年(365天)", null);
				}
			}
			List<RevenueParameterDto> defaultParams = revenueCalculationService.getDefaultBasicParameter();
			ProjectResponse response = new ProjectResponse(info, defaultParams);
			response.setLoadData(loadData);
			// 生成报告
			reportCacheAvailable.put(request.getProjectId(), true);
			return generateReport(info, loadData, response);
		} catch (Exception e) {
			log.error("create project error : ", e);
			return ResponseResult.error(e.getMessage());
		}
	}

	@NotNull
	private ResponseResult<ProjectResponse> generateReport(RevenueProjectInfo info, List<ExcelData> loadData, ProjectResponse response) throws Exception {
		ElectricityParameter eleParam = JSONObject.parseObject(info.getElectricityParameter(), ElectricityParameter.class);
		String eleString = queryDataYearly(info.getArea(), eleParam.getType1(), eleParam.getType2(), eleParam.getVol1(), eleParam.getVol2());
		ResponseResult result = JSONObject.parseObject(eleString, ResponseResult.class);
//		log.info("电价查询结果:{},消息：{}", result.getCode(), result.getMsg());
		if (result.getCode() != 200) {
			log.info("电价查询结果:{},消息：{}", result.getCode(), result.getMsg());
			throw new Exception("电价查询有误");
		}
		ElectricityQueryYearlyResponse electricityData = JSONObject.parseObject(JSON.toJSONString(result.getData()),
				ElectricityQueryYearlyResponse.class);
		int priceSize = 0;
		List<PriceInfo> pricesList = electricityData.getPricesList();
		for (PriceInfo priceInfo : pricesList) {
			priceSize += priceInfo.getPrices().size();

		}
		if (pricesList.size() != 12 || priceSize != 1152) {
			throw new Exception("此地区缺失部分电价数据，请补充电价信息");
		}
		CompletableFuture.runAsync(() -> {
			try {
				RevenueRequestVO requestVO = new RevenueRequestVO();
				requestVO.setProjectId(info.getId());
				FundamentalParameter parameter = JSONObject.parseObject(info.getFundamentalParameter(), FundamentalParameter.class);
				requestVO.setDesignCapacity(parameter.getDesignCapacity());
				requestVO.setDesignPower(parameter.getDesignPower());
				requestVO.setUsableDepth(parameter.getUsableDepth());
				requestVO.setSystemEfficiency(parameter.getSystemEfficiency());
				requestVO.setElectricity(electricityData);
				requestVO.setPowerList(loadData);
				requestTotPredictEnergy(requestVO);
			} catch (Exception e) {
				log.error("开始收益测算失败", e);
			}
		});
		return ResponseResult.success(response);
	}

	@ApiOperation(value = "更新项目")
	@RequestMapping(value = "/updateProject", method = {RequestMethod.POST})
	public ResponseResult<String> updateProject(@RequestBody UpdateProjectReq request) {
		try {
			if (revenueCalculationService.checkReGenerateReport(request)) {
				reportCacheAvailable.put(request.getProjectId(), false);
			}
			revenueCalculationService.updateProject(request);
			return ResponseResult.success("success");
		} catch (Exception e) {
			return ResponseResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
		}
	}


	@ApiOperation(value = "生成报告")
	@RequestMapping(value = "/generateReport", method = {RequestMethod.POST})
	public ResponseResult<String> generateReport(@RequestBody UpdateProjectReq request) {
		try {
			RevenueProjectInfo info = revenueCalculationService.findProjectById(request.getProjectId());
			List<RevenueParameterDto> defaultParams = revenueCalculationService.getDefaultBasicParameter();
			ProjectResponse response = new ProjectResponse(info, defaultParams);
			List<ExcelData> loadData = revenueCalculationService.findLoadDataByProjectId(request.getProjectId());
			response.setLoadData(loadData);
			if (reportCacheAvailable.get(request.getProjectId()) == null) {
				if (info.getIntermediateResult() != null) {
					return ResponseResult.success("success");
				}
				reportCacheAvailable.put(request.getProjectId(), true);
				generateReport(info, loadData, response);
				return ResponseResult.success("success");
			} else if (!reportCacheAvailable.get(request.getProjectId())) {
				reportCacheAvailable.put(request.getProjectId(), true);
				generateReport(info, loadData, response);
				return ResponseResult.success("success");
			} else {
				return ResponseResult.success("success");
			}
		} catch (Exception e) {
			return ResponseResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null);
		}
	}


	@ApiOperation(value = "导入项目数据")
	@RequestMapping(value = "/uploadExcel", method = {RequestMethod.POST})
	public ResponseResult<String> uploadExcel(@RequestParam("file") MultipartFile file,
	                                          @RequestParam(value = "projectId", required = false) String projectId) {
		try {
			String uuid = revenueCalculationService.saveExcel(file.getOriginalFilename(), file.getInputStream(), projectId);
			return ResponseResult.success(uuid);
		} catch (Exception e) {
			return ResponseResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "文件解析失败，请重新上传", e.getMessage());
		}
	}

	@ApiOperation(value = "导出项目数据")
	@RequestMapping(value = "/exportExcel", method = {RequestMethod.GET})
	public ResponseResult<String> exportExcel(HttpServletResponse response, @RequestParam(value = "projectId", required = false) String projectId) {
		try {

			PredictEnergyCommand command = new PredictEnergyCommand();
			command.setProjectId(projectId);
			RevenueEstVO result = predictEnergyQuery(command).getData();

			ExcelWriter writer = EasyExcel.write(response.getOutputStream()).build();

			revenueCalculationService.exportExcel(writer, projectId, JSON.toJSONString(result));
			RevenueProjectInfo info = revenueCalculationService.findProjectById(command.getProjectId());
			FundamentalParameter parameter = JSONObject.parseObject(info.getFundamentalParameter(), FundamentalParameter.class);
			RevenueEstDTO estDTO = new RevenueEstDTO(Double.valueOf(info.getIntermediateResult()), parameter);
			RevenueEstimationUtils utils = new RevenueEstimationUtils();
			RevenueEstVO revenueEstVO = utils.reportEstEasy(estDTO);
			List<CashFlow> data = new ArrayList<>();
			data.add(new CashFlow(1.0, "现金流入(万元)", revenueEstVO.getCashInflow().toArray(new Double[0])));
			data.add(new CashFlow(1.1, "投资方收入(万元)", revenueEstVO.getAssetIncomeAfterShare().toArray(new Double[0])));
			data.add(new CashFlow(2.0, "现金流出(万元)", revenueEstVO.getCashOutflow().toArray(new Double[0])));
			data.add(new CashFlow(2.1, "建设投资(万元)", revenueEstVO.getConstInvest().toArray(new Double[0])));
			data.add(new CashFlow(2.2, "经营成本(万元)", revenueEstVO.getOperatingCost().toArray(new Double[0])));
			data.add(new CashFlow(2.3, "增值税(万元)", revenueEstVO.getVat().toArray(new Double[0])));
			data.add(new CashFlow(3.0, "净现金流量(万元)", revenueEstVO.getNetCashFlowBeforeTax().toArray(new Double[0])));
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
					EasyExcel.writerSheet("现金流量表")
							.head(CashFlow.class)
							.registerWriteHandler(horizontalCellStyleStrategy)
							.registerWriteHandler(new CustomRowColorHandler())
							.registerWriteHandler(new ExcelColumnWidth2())
							.build();
			writer.write(data, writeSheet);
			writer.finish();

			return ResponseResult.success("1");
		} catch (Exception e) {
			log.error("error", e);
			return ResponseResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "文件解析失败，请重新上传", e.getMessage());
		}
	}

	@ApiOperation(value = "获取项目详情")
	@RequestMapping(value = "/getProjectDetail", method = {RequestMethod.GET})
	public ResponseResult<ProjectResponse> getProjectDetail(@RequestParam("id") String projectId) {
		ProjectResponse response = new ProjectResponse();
		try {
		RevenueProjectInfo info = revenueCalculationService.findProjectById(projectId);
		List<RevenueParameterDto> defaultParams = revenueCalculationService.getDefaultBasicParameter();
		response = new ProjectResponse(info, defaultParams);
			List<ExcelData> loadData = revenueCalculationService.findLoadDataByProjectId(projectId);
			response.setLoadData(loadData);
		} catch (Exception ignore) {
			response.setLoadData(new ArrayList<>());
		}
		return ResponseResult.success(response);
	}

	@ApiOperation(value = "获取负载详情")
	@RequestMapping(value = "/getProjectLoadData", method = {RequestMethod.GET})
	public ResponseResult<List<ExcelData>> getProjectLoadData(@RequestParam("id") String projectId, @RequestParam("startTime") String st,
	                                                          @RequestParam("endTime") String et) throws ParseException {
		List<ExcelData> loadData = revenueCalculationService.findLoadDataByProjectIdAndTime(projectId, st, et);
		return ResponseResult.success(loadData);
	}

	@ApiOperation(value = "获取项目列表")
	@RequestMapping(value = "/getProjectList", method = {RequestMethod.GET})
	public ResponseResult<List<JSONObject>> getProjectList() {
		List<JSONObject> projects = revenueCalculationService.getProjectIds();
		return ResponseResult.success(projects);
	}

	@ApiOperation(value = "获取项目数量")
	@RequestMapping(value = "/getProjectCount", method = {RequestMethod.GET})
	public ResponseResult<Long> getProjectCount() {
		Long count = revenueCalculationService.getProjectCount();
		return ResponseResult.success(count);
	}


	@ApiOperation(value = "查询全年电价数据")
	@RequestMapping(value = "/queryYearly", method = {RequestMethod.GET})
	public String queryDataYearly(@RequestParam("city") String city, @RequestParam("type1") String type1, @RequestParam("type2") String type2,
	                              @RequestParam("voltage1") String voltage1, @RequestParam("voltage2") String voltage2) {
		String response = "";
		try {
			response = revenueCalculationService.getElectricityPrice(city, type1, type2, voltage1, voltage2);
		} catch (Exception e) {
			return response;
		}
		return response;
	}


	@ApiOperation(value = "查询全量城市用电制度")
	@RequestMapping(value = "/queryCityEleSystem", method = {RequestMethod.GET})
	public String queryCityEleSystem() {
		return revenueCalculationService.getCityInfo();
	}


	@ApiOperation(value = "查询全量城市用电制度")
	@RequestMapping(value = "/queryElectricityType", method = {RequestMethod.GET})
	public ResponseResult<List<EleNodeInfo>> queryElectricityType(@RequestParam() String city, @RequestParam(required = false) String type1,
	                                                              @RequestParam(required = false) String type2) {
		return ResponseResult.success(revenueCalculationService.queryCityInfo(city, type1, type2));
	}

	@ApiOperation(value = "收益测算-算法返回预测电量")
	@RequestMapping(value = "/predictEnergy", method = {RequestMethod.POST})
	public ResponseResult predictEnergy(@RequestBody PredictEnergyCommand command) {
		int capacityListSize = command.getCapacityList().size();
		log.info("算法预测电量项目ID:{}，预测电量数:{}", command.getProjectId(), capacityListSize);
		if (capacityListSize == 0) {
			RevenueEstVO vo = new RevenueEstVO();
			vo.setResult(false);
			reportMap.put(command.getProjectId(), vo);
			log.error("算法侧调用接口出错：测算失败，无法对目前的负荷数据和电价地区进行测算,请重选电价地区或检查负荷数据!");
			return ResponseResult.error("测算失败，无法对目前的负荷数据和电价地区进行测算,请重选电价地区或检查负荷数据!");
		}
		RevenueEstimationUtils utils = new RevenueEstimationUtils();
		RevenueProjectInfo info = revenueCalculationService.findProjectById(command.getProjectId());
		ElectricityParameter eleParam = JSONObject.parseObject(info.getElectricityParameter(), ElectricityParameter.class);

		String eleString = queryDataYearly(info.getArea(), eleParam.getType1(), eleParam.getType2(), eleParam.getVol1(), eleParam.getVol2());
		ResponseResult result = JSONObject.parseObject(eleString, ResponseResult.class);
		if (result.getCode() != 200) {
			RevenueEstVO vo = new RevenueEstVO();
			vo.setResult(false);
			reportMap.put(command.getProjectId(), vo);
			log.error("算法侧调用接口出错：电价查询有误 , query param :{}", eleParam);
			return ResponseResult.error("电价查询有误");
		}
		ElectricityQueryYearlyResponse electricityData = JSONObject.parseObject(result.getData().toString(), ElectricityQueryYearlyResponse.class);
		if (electricityData.getPricesList().size() != 12) {
			RevenueEstVO vo = new RevenueEstVO();
			vo.setResult(false);
			reportMap.put(command.getProjectId(), vo);
			log.error("算法侧调用接口出错：此地区缺失部分电价数据，请补充电价信息");
			return ResponseResult.error("此地区缺失部分电价数据，请补充电价信息");
		}
		List<RevenueLoadDto> list = new ArrayList<>();
		Map<String, Double> map = utils.powerBasicParameter(list, command, electricityData);
		int batchSize = 1000;
		CompletableFuture.runAsync(() -> {
			try {
				for (int i = 0; i < list.size(); i += batchSize) {
					int end = Math.min(i + batchSize, list.size());
					List<RevenueLoadDto> batchList = list.subList(i, end);
					revenueLoadDateRepository.saveAll(batchList);
				}
			} catch (Exception e) {
				log.error("存储预测电量失败", e);
			}
		});
		Double peakValleyIncome = map.get("peakValleyIncome");
//		log.info("peakValleyIncome:{}", peakValleyIncome);
		RevenueEstDTO estDTO = new RevenueEstDTO(peakValleyIncome, JSONObject.parseObject(info.getFundamentalParameter(),
				FundamentalParameter.class));
		info.setIntermediateResult(String.valueOf(peakValleyIncome));
		revenueCalculationService.save(info);
		RevenueEstVO revenueEstVO = utils.reportEstEasy(estDTO);
		reportMap.put(command.getProjectId(), revenueEstVO);
		return ResponseResult.success(revenueEstVO);
	}

	@ApiOperation(value = "收益测算-第一次查询预测报告")
	@RequestMapping(value = "/firstPredictEnergyQuery", method = {RequestMethod.POST})
	public ResponseResult firstPredictEnergyQuery(@RequestBody PredictEnergyCommand command) {
		RevenueEstVO estVO = reportMap.get(command.getProjectId());
		if (estVO == null) {
			return ResponseResult.errorHoldOn("请稍等，正在测算中");
		} else if (!estVO.isResult()) {
			return ResponseResult.error("测算失败，无法规划对目前的负荷数据和电价地区进行测算,请重选电价地区或检查负荷数据");
		} else {
			reportMap.remove(command.getProjectId());
			reportCacheAvailable.remove(command.getProjectId());
			return ResponseResult.success(estVO);
		}
	}

	@ApiOperation(value = "收益测算-查询预测报告")
	@RequestMapping(value = "/predictEnergyQuery", method = {RequestMethod.POST})
	public ResponseResult<RevenueEstVO> predictEnergyQuery(@RequestBody PredictEnergyCommand command) {
		if (reportCacheAvailable.get(command.getProjectId()) != null && reportCacheAvailable.get(command.getProjectId())) {
			return firstPredictEnergyQuery(command);
		}
		try {
			RevenueProjectInfo info = revenueCalculationService.findProjectById(command.getProjectId());
			FundamentalParameter parameter = JSONObject.parseObject(info.getFundamentalParameter(), FundamentalParameter.class);
			RevenueEstDTO estDTO = new RevenueEstDTO(Double.valueOf(info.getIntermediateResult()), parameter);
			RevenueEstimationUtils utils = new RevenueEstimationUtils();
			RevenueEstVO revenueEstVO = utils.reportEstEasy(estDTO);
			return ResponseResult.success(revenueEstVO);
		} catch (Exception e) {
			return ResponseResult.error("测算数据缺失，请检查");
		}

	}

	public void requestTotPredictEnergy(RevenueRequestVO revenueRequestVO) {
		String result = okHttpPost("http://127.0.0.1:13360/profitSimulation", JSON.toJSONString(revenueRequestVO));
//		String result = okHttpPost("http://192.168.110.79:13360/profitSimulation", JSON.toJSONString(revenueRequestVO));
		log.info("算法返回测算结果：{}", result);
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
}
