package com.example.vvpservice.nodeep.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ElectricityPriceResponse {

	private String city;

	/**
	 * 用电类型
	 */
	private String electricityCategory;


	/**
	 * 月份
	 */
	private int year;

	/**
	 * 月份
	 */
	private int month;

	/**
	 * 电压
	 */
	private String voltage;


	private List<priceData> priceList = new ArrayList<>();

	@Data
	public static class priceData {

		/**
		 * 开始时间:结束时间
		 */
		private String startDate;

		/**
		 * 时段分类：尖、峰、平、谷、深谷
		 */
		private String timeCategory;

		/**
		 * 价格
		 */
		private BigDecimal price;

		/**
		 * 用电量
		 */
		private String consumption;

	}
}
