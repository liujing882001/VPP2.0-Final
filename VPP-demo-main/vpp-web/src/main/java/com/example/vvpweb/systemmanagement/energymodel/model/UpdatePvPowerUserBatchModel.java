package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.time.YearMonth;

@Data
public class UpdatePvPowerUserBatchModel {

	private YearMonth st;

	private YearMonth et;

	private String nodeId;

	/**
	 * 电力用户比例
	 */
	private Double powerUserProp;
	/**
	 * 资产方比例
	 */
	private Double loadProp;

	/**
	 * 运营方比例
	 */
	private Double operatorProp;
}
