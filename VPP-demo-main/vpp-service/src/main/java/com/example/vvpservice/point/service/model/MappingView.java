package com.example.vvpservice.point.service.model;

import com.example.vvpcommom.i18n.i18nUtil;
import com.example.vvpdomain.entity.PointModelMapping;
import lombok.Data;

@Data
public class MappingView {
	private String stationId;

	private String modelId;

	private String modelKey;

	private String modelNameZh;

	private String modelNameEn;

	private String deviceList;

	private String mappingType;


	public MappingView(PointModelMapping pointModelMapping) {
		this.stationId = pointModelMapping.getStation().getStationId();
		this.modelId = pointModelMapping.getPointModel().getId();
		this.modelKey = pointModelMapping.getPointModel().getKey();
		this.modelNameZh = pointModelMapping.getPointModel().getPointNameZh();
		this.modelNameEn = pointModelMapping.getPointModel().getPointNameEn();
		this.deviceList = pointModelMapping.getDeviceList();
		this.mappingType = pointModelMapping.getMappingType();
	}
}
