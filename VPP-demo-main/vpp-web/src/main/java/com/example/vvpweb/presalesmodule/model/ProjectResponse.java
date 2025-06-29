package com.example.vvpweb.presalesmodule.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpdomain.entity.RevenueLoadDto;
import com.example.vvpdomain.entity.RevenueParameterDto;
import com.example.vvpdomain.entity.RevenueProjectInfo;
import com.example.vvpservice.revenue.ExcelData;
import com.example.vvpservice.revenue.enums.EstimationParameterEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class ProjectResponse {

	private static final List<String> designParam = Arrays.asList(
			EstimationParameterEnum.cycleCount.toString(),
			EstimationParameterEnum.lifespan.toString(),
			EstimationParameterEnum.usableDepth.toString(),
			EstimationParameterEnum.batteryDegCoeff.toString(),
			EstimationParameterEnum.systemEfficiency.toString());

	private static final List<String> economicParam = Arrays.asList(
			EstimationParameterEnum.assessPeriod.toString(),
			EstimationParameterEnum.purchasePrice.toString(),
			EstimationParameterEnum.equipmentCostRatio.toString(),
			EstimationParameterEnum.engineeringCostRatio.toString(),
			EstimationParameterEnum.electricityUserShare.toString(),
			EstimationParameterEnum.mainTRatio5Y.toString(),
			EstimationParameterEnum.mainTRatio5_10Y.toString(),
			EstimationParameterEnum.mainTRatio10_25Y.toString(),
			EstimationParameterEnum.insuranceRate.toString(),
			EstimationParameterEnum.platformRate.toString(),
			EstimationParameterEnum.batteryReplRatio.toString()
	);

	private BasicInfo basicInfo = new BasicInfo();

	private AdvanceInfo advanceInfo = new AdvanceInfo();

	@Data
	static class BasicInfo {
		private String projectId;

		private String projectName;

		private String area;

		private String designCapacity;

		private String designPower;

		private String fileName;

		private String type1;

		private String type2;

		private String vol1;

		private String vol2;

		private List<ExcelData> loadData;
	}

	@Data
	static class AdvanceInfo {
		private JSONArray designParameter;

		private JSONArray economicalParameter;

	}

	public ProjectResponse() {


	}	public ProjectResponse(RevenueProjectInfo info, List<RevenueParameterDto> parameterDtos) {
		this.basicInfo.projectId = info.getId();
		this.basicInfo.projectName = info.getName();
		this.basicInfo.area = info.getArea();
		this.basicInfo.fileName = info.getFileName();

		Map<String, RevenueParameterDto> parameterDtoMap = parameterDtos.stream().collect(Collectors.toMap(RevenueParameterDto::getParam_id,
				Function.identity()));

		JSONObject eleParam = JSON.parseObject(info.getElectricityParameter());
		for (int i = 0; i < eleParam.size(); i++) {
			if (eleParam.get("type1") != null) {
				this.basicInfo.type1 = (String) eleParam.get("type1");
			}
			if (eleParam.get("type2") != null) {
				this.basicInfo.type2 = (String) eleParam.get("type2");
			}
			if (eleParam.get("vol1") != null) {
				this.basicInfo.vol1 = (String) eleParam.get("vol1");
			}
			if (eleParam.get("vol2") != null) {
				this.basicInfo.vol2 = (String) eleParam.get("vol2");
			}
		}

		JSONObject parameter = JSON.parseObject(info.getFundamentalParameter());
		JSONArray designParams = new JSONArray();
		JSONArray economicalParams = new JSONArray();
		parameter.keySet().forEach(o -> {
					if (o.equals(EstimationParameterEnum.designCapacity.toString())) {
						this.basicInfo.designCapacity = parameter.get(o).toString();
					}
					if (o.equals(EstimationParameterEnum.designPower.toString())) {
						this.basicInfo.designPower = parameter.get(o).toString();
					}
					if (designParam.contains(o)) {
						parameterDtoMap.get(o).setDefault_value((String) parameter.get(o));
						JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(parameterDtoMap.get(o)));
						if (jsonObject.get("unit").equals("%")) {
							jsonObject.put("default_value", Double.parseDouble((String) jsonObject.get("default_value")) * 100);
						}
						designParams.add(jsonObject);
					}
					if (economicParam.contains(o)) {
						parameterDtoMap.get(o).setDefault_value((String) parameter.get(o));
						JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(parameterDtoMap.get(o)));
						if (jsonObject.get("unit").equals("%")) {
							jsonObject.put("default_value", Double.parseDouble((String) jsonObject.get("default_value")) * 100);
						}
						economicalParams.add(jsonObject);
					}
				}
		);
		this.advanceInfo.designParameter = designParams;
		this.advanceInfo.economicalParameter = economicalParams;
	}

	public void setLoadData(List<ExcelData> data) {
		this.basicInfo.loadData = data;
	}

	public void setData(List<RevenueLoadDto> data) {
		List<ExcelData> list = new ArrayList<>();
		data.forEach(o -> {
			ExcelData d = new ExcelData();
			d.setDate(o.getTime());
			d.setPower(o.getPower());
			list.add(d);
		});
		this.basicInfo.loadData = list;
	}

}
