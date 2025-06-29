package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.YearMonth;

@Data
public class UpdateStorageShareProportionBatchModel {

	private YearMonth st;

	private YearMonth et;

	private String nodeId;

	private Double loadProp;

	private Double operatorProp;

	private Double powerUserProp;
}
