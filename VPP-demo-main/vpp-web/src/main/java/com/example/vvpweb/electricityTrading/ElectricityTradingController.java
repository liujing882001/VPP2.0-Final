package com.example.vvpweb.electricityTrading;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpdomain.TradePowerRepository;
import com.example.vvpdomain.entity.TradePower;
import com.example.vvpservice.electricitytrading.ElectricityTradingService;
import com.example.vvpservice.electricitytrading.model.ElectricityPrice;
import com.example.vvpweb.electricityTrading.model.PriceChartRequest;
import com.example.vvpweb.electricityTrading.model.PriceChartResponse;
import com.example.vvpweb.electricityTrading.model.TradePowerModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author yym
 */
@RequestMapping("/electricityTrading")
@CrossOrigin
@Api(value = "电力交易Copilot", tags = {"电力交易Copilot"})
@RestController
@Slf4j
public class ElectricityTradingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElectricityTradingController.class);

	@Autowired
	TradePowerRepository tradePowerRepository;

	@Autowired
	ElectricityTradingService electricityPrice;

	@ApiOperation("查询电力交易任务")
	@RequestMapping(value = "/getTasks", method = {RequestMethod.GET})
	public ResponseResult<PageModel> getTasks(HttpServletResponse response,
	                                          @RequestParam(value = "pageNumber", required = false) Integer number,
	                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {

		PageModel pageModel = new PageModel();
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (now.after(calendar.getTime())) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		// 不分页查询的情况，返回到一页上
		if (number == null || pageSize == null) {
			List<TradePower> tradePowers = tradePowerRepository.findAll();
			tradePowers = tradePowers.stream().filter(o-> o.getSTime().after(calendar.getTime())).collect(Collectors.toList());
			pageModel.setContent(tradePowers);
			pageModel.setNumber(1);
			pageModel.setTotalPages(1);
			pageModel.setPageSize(tradePowers.size());
			pageModel.setTotalElements(tradePowers.size());
			return ResponseResult.success(pageModel);
		}
		// 分页查询
		pageModel.setNumber(number);
		pageModel.setPageSize(pageSize);
		try {
			Specification<TradePower> spec = (root, criteriaQuery, cb) -> cb.greaterThan(root.get("sTime"), calendar.getTime());

			PageRequest pgRequest = PageRequest.of(number - 1, pageSize);
			Page<TradePower> data = tradePowerRepository.findAll(spec,pgRequest);

			List<TradePowerModel> list = new ArrayList<>();
			data.forEach(d -> {
				TradePowerModel newModel = new TradePowerModel();
				BeanUtils.copyProperties(d, newModel);
				list.add(newModel);
			});

			pageModel.setContent(list);
			pageModel.setTotalPages(data.getTotalPages());
			pageModel.setTotalElements((int) data.getTotalElements());
			return ResponseResult.success(pageModel);
		} catch (Exception e) {
			LOGGER.error("查询任务状态出错", e);
			return ResponseResult.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), pageModel);
		}
	}

	@ApiOperation("查询电力交易任务数")
	@RequestMapping(value = "/getTaskCount", method = {RequestMethod.GET})
	public ResponseResult<Integer> getTaskCount(HttpServletResponse response) {

		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (now.after(calendar.getTime())) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		int count = tradePowerRepository.getTaskCount(calendar.getTime());
		return ResponseResult.success(count);
	}

	@ApiOperation("查询价格图表")
	@RequestMapping(value = "/priceChart", method = {RequestMethod.POST})
	public PriceChartResponse priceChart(HttpServletResponse response, @RequestBody PriceChartRequest request) {
		PriceChartResponse priceChartResponse = new PriceChartResponse();
		List<ElectricityPrice> res = electricityPrice.getElectricityPriceList(request.getNodeId(), request.getSt(), request.getEt());
		priceChartResponse.setPriceLists(res);
		return priceChartResponse;
	}

}
