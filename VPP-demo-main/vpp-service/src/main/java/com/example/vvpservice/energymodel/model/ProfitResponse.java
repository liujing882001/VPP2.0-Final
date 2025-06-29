package com.example.vvpservice.energymodel.model;

import com.example.vvpcommom.DoubleSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yym
 */
@Data
public class ProfitResponse {

	private String name;

	private String type;

	List<ProfitValue> dataList = new ArrayList<>();

	@Data
	public static class ProfitValue {
		private String time;

		@JsonSerialize(using = DoubleSerialize.class)
		private Double value;


		public ProfitValue(){
		}
		public ProfitValue(ProfitValue value){
			this.time = value.getTime();
			this.value= value.getValue();

		}
	}
}
