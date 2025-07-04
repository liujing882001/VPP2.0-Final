package com.example.vvpservice.electricitytrading.model;

import com.example.vvpcommom.BigDecimalSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ElectricityPrice {
    public String getSTime() { return sTime; }
    public void setSTime(String sTime) { this.sTime = sTime; }
    public String getETime() { return eTime; }
    public void setETime(String eTime) { this.eTime = eTime; }

	@ApiModelProperty("字段名称")
	private String name;
	@ApiModelProperty("开始时间")
	private String sTime;
	
	@ApiModelProperty("结束时间")
	private String eTime;


	@ApiModelProperty("数据")
	private List<Price> priceList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Price> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<Price> priceList) {
		this.priceList = priceList;
	}

	@Data
	public static class Price {
		String ts;

		@JsonSerialize(using = BigDecimalSerialize.class)
		BigDecimal price;

		public String getTs() {
			return ts;
		}

		public void setTs(String ts) {
			this.ts = ts;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public void setPrice(BigDecimal price) {
			this.price = price;
		}
	}
}
