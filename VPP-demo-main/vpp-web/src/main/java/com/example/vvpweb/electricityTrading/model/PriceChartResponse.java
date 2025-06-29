package com.example.vvpweb.electricityTrading.model;

import com.example.vvpservice.electricitytrading.model.ElectricityPrice;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PriceChartResponse {

	List<ElectricityPrice> priceLists = new ArrayList<>();

}
