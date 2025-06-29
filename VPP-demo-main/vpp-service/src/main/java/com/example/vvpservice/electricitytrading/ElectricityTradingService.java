package com.example.vvpservice.electricitytrading;

import com.example.vvpservice.electricitytrading.model.ElectricityPrice;

import java.util.Date;
import java.util.List;

public interface ElectricityTradingService {
	List<ElectricityPrice> getElectricityPriceList(String node, Date date, Date et);


}
