package com.example.vvpcommom.pointmodel;


/**
 * 设备点位标准化
 * */
public interface PointConstant {

	/**
	 * 数据类型：测点、计算点、属性、指标
	 * */
	interface DataType{

		/**
		 * 测点
		 * */
		String MEASURING_POINT = "MeasuringPoint";

		/**
		 * 计算点
		 * */
		String CALCULATION_POINT = "CalculationPoint";

		/**
		 * 属性
		 * */
		String PROPERTY = "Property";

		/**
		 * 指标
		 * */
		String INDICATOR = "Indicator";
	}

	interface MappingType{
		String TYPE_DEFAULT = "Default";

		String TYPE_MANUAL = "Manual";
	}
}
