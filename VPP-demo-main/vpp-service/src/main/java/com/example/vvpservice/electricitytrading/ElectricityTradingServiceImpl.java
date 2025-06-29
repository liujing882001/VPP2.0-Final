package com.example.vvpservice.electricitytrading;

import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.CfgStorageEnergyStrategyRepository;
import com.example.vvpdomain.entity.CfgStorageEnergyStrategy;
import com.example.vvpservice.electricitytrading.model.ElectricityPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ElectricityTradingServiceImpl implements ElectricityTradingService {


	@Autowired
	CfgStorageEnergyStrategyRepository cfgStorageEnergyStrategyRepository;


	@Override
	public List<ElectricityPrice> getElectricityPriceList(String node, Date st, Date et) {
		List<ElectricityPrice> result = new ArrayList<>();
		ElectricityPrice electricityPrice = new ElectricityPrice();
		electricityPrice.setName("长协价格");
		Calendar st_c = Calendar.getInstance();
		st_c.setTime(st);
		Calendar et_c = Calendar.getInstance();
		et_c.setTime(et);

		List<CfgStorageEnergyStrategy> res = cfgStorageEnergyStrategyRepository.findAllByNodeIdAndSystemIdAndEffectiveDate(node, "nengyuanzongbiao", TimeUtil.getMonthStart(st));
		int day = (int) ((et.getTime() - st.getTime()) / 86400000);
		List<CfgStorageEnergyStrategy> resNetMon = Collections.emptyList();
		if (st_c.get(Calendar.MONTH) != et_c.get(Calendar.MONTH)) {
			resNetMon = cfgStorageEnergyStrategyRepository.findAllByNodeIdAndSystemIdAndEffectiveDate(node, "nengyuanzongbiao", TimeUtil.getMonthStart(et));
		}

		List<ElectricityPrice.Price> prices = new ArrayList<>();
		int st_mon = st_c.get(Calendar.MONTH);
		for (int i = 0; i <= day; i++) {

			if (st_mon != st_c.get(Calendar.MONTH)) {
				for (CfgStorageEnergyStrategy o : resNetMon) {
					getPrices(o, st_c, prices);
				}
			} else {
				for (CfgStorageEnergyStrategy o : res) {
					getPrices(o, st_c, prices);
				}
			}
			st_c.add(Calendar.DAY_OF_MONTH, 1);
		}

		st_c.setTime(st);
		prices.sort(Comparator.comparing(ElectricityPrice.Price::getTs));
		electricityPrice.setPriceList(prices);

		Random random = new Random();
		AtomicReference<Double> v = new AtomicReference<>((double) 0);

		ElectricityPrice electricityPrice2 = new ElectricityPrice();
		electricityPrice2.setName("日前出清价格");
		List<ElectricityPrice.Price> prices2 = new ArrayList<>();
		for (int i = 0; i <= day; i++) {

			for (CfgStorageEnergyStrategy o : res) {
				getRandomPrices(o, st_c, v, random, prices2);
			}
			st_c.add(Calendar.DAY_OF_MONTH, 1);
		}
		st_c.setTime(st);

		prices2.sort(Comparator.comparing(ElectricityPrice.Price::getTs));
		electricityPrice2.setPriceList(prices2);


		ElectricityPrice electricityPrice3 = new ElectricityPrice();
		electricityPrice3.setName("预测日前价格");
		List<ElectricityPrice.Price> prices3 = new ArrayList<>();
		for (int i = 0; i <= day; i++) {
			for (CfgStorageEnergyStrategy o : res) {
				getRandomPrices(o, st_c, v, random, prices3);
			}
			st_c.add(Calendar.DAY_OF_MONTH, 1);
		}
		st_c.setTime(st);
		prices3.sort(Comparator.comparing(ElectricityPrice.Price::getTs));
		electricityPrice3.setPriceList(prices3);

		ElectricityPrice electricityPrice4 = new ElectricityPrice();
		electricityPrice4.setName("实时出清价格");
		List<ElectricityPrice.Price> prices4 = new ArrayList<>();
		for (int i = 0; i <= day; i++) {

			for (CfgStorageEnergyStrategy o : res) {
				getRandomPrices(o, st_c, v, random, prices4);
			}
			st_c.add(Calendar.DAY_OF_MONTH, 1);
		}
		st_c.setTime(st);
		prices4.sort(Comparator.comparing(ElectricityPrice.Price::getTs));
		electricityPrice4.setPriceList(prices4);


		ElectricityPrice electricityPrice5 = new ElectricityPrice();
		electricityPrice5.setName("预测实时价格");
		List<ElectricityPrice.Price> prices5 = new ArrayList<>();
		for (int i = 0; i <= day; i++) {

			for (CfgStorageEnergyStrategy o : res) {
				getRandomPrices(o, st_c, v, random, prices5);
			}
			st_c.add(Calendar.DAY_OF_MONTH, 1);
		}
		prices5.sort(Comparator.comparing(ElectricityPrice.Price::getTs));
		electricityPrice5.setPriceList(prices5);

		result.add(electricityPrice);
		result.add(electricityPrice2);
		result.add(electricityPrice3);
		result.add(electricityPrice4);
		result.add(electricityPrice5);

		return result;
	}

	private static void getRandomPrices(CfgStorageEnergyStrategy o, Calendar st_c, AtomicReference<Double> v, Random random, List<ElectricityPrice.Price> prices) {
		List<String> time = Arrays.asList(o.getStime().split(":"));
		Calendar c = Calendar.getInstance();
		c.setTime(st_c.getTime());
		c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.get(0)));
		c.set(Calendar.MINUTE, Integer.parseInt(time.get(1)));
		for (int j = 0; j < 4; j++) {
			ElectricityPrice.Price p = new ElectricityPrice.Price();
			p.setTs(TimeUtil.toYmdHHmmStr_threadSafety(c.getTime()));
			c.add(Calendar.MINUTE, 15);
			v.set(random.nextDouble() * 0.6 - 0.3);
			p.setPrice(o.getPriceHour().add(BigDecimal.valueOf(v.get())));
			prices.add(p);
		}
	}

	private static void getPrices(CfgStorageEnergyStrategy o, Calendar st_c, List<ElectricityPrice.Price> prices) {
		List<String> time = Arrays.asList(o.getStime().split(":"));
		Calendar c = Calendar.getInstance();
		c.setTime(st_c.getTime());
		c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.get(0)));
		c.set(Calendar.MINUTE, Integer.parseInt(time.get(1)));
		for (int j = 0; j < 4; j++) {
			ElectricityPrice.Price p = new ElectricityPrice.Price();
			p.setTs(TimeUtil.toYmdHHmmStr_threadSafety(c.getTime()));
			c.add(Calendar.MINUTE, 15);
			p.setPrice(o.getPriceHour());
			prices.add(p);
		}
	}
}
