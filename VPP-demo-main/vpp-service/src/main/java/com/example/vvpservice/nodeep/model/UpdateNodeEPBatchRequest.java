package com.example.vvpservice.nodeep.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Data
public class UpdateNodeEPBatchRequest {
	private String nodeId;

	private YearMonth date;

	private String city;

	private String vol1;

	private String type1;

	private List<price> prices;

	public String getNodeId() { return nodeId; }
	public void setNodeId(String nodeId) { this.nodeId = nodeId; }
	public YearMonth getDate() { return date; }
	public void setDate(YearMonth date) { this.date = date; }
	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }
	public String getVol1() { return vol1; }
	public void setVol1(String vol1) { this.vol1 = vol1; }
	public String getType1() { return type1; }
	public void setType1(String type1) { this.type1 = type1; }
	public List<price> getPrices() { return prices; }
	public void setPrices(List<price> prices) { this.prices = prices; }

	@Data
	public static class price{
		private String type;

		private BigDecimal price;

		public String getType() { return type; }
		public void setType(String type) { this.type = type; }
		public BigDecimal getPrice() { return price; }
		public void setPrice(BigDecimal price) { this.price = price; }
	}
}
