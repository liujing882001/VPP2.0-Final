package com.example.vvpdomain.entity;

import com.example.vvpcommom.pointmodel.PointConstant;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Data
public class PointModelMapping {

	@Id
	private String mappingId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "station_id", nullable = false)
	private StationNode station;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "model_id", nullable = false)
	private PointModel pointModel;

	@Column(name = "device_list")
	private String deviceList;

	@Column(name = "mapping_type")
	private String mappingType;

	@Column(name = "calculation_formula")
	private String calculation_formula;

	public PointModelMapping() {
		this.mappingId = String.valueOf(UUID.randomUUID());
		this.mappingType = PointConstant.MappingType.TYPE_DEFAULT;
	}

	private String getDeviceSn(Device device) {
		if (device.getDeviceSn().matches(".*[+\\-*/].*")) {
			return device.getDeviceSn().replaceAll("[+\\-*/]", "_");
		}
		return device.getDeviceSn();
	}

	public PointModelMapping(StationNode station, PointModel pointModel, Device device, boolean isManual) {
		this.mappingId = String.valueOf(UUID.randomUUID());
		this.station = station;
		this.pointModel = pointModel;
		if (isManual) {
			this.mappingType = PointConstant.MappingType.TYPE_MANUAL;
		} else {
			this.mappingType = PointConstant.MappingType.TYPE_DEFAULT;
		}

		this.deviceList = device.getDeviceSn();
		this.calculation_formula = "data['" + getDeviceSn(device) + "." + pointModel.getKey() + "']";
	}
}
