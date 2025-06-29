package com.example.vvpweb.point;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.StringUtils;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpservice.point.service.PointService;
import com.example.vvpweb.point.model.BindRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/point_model")
@CrossOrigin
@Api(value = "节点模型-数据点位", tags = {"节点模型-数据点位"})
@Slf4j
public class PointModelController {

	@Autowired
	PointService pointService;

	@ApiOperation("自动绑定数据点位")
	@UserLoginToken
	@RequestMapping(value = "auto_bind", method = {RequestMethod.POST})
	public ResponseResult<String> autoBind(@RequestBody BindRequest bindRequest) {
		try {
			if (StringUtils.isEmpty(bindRequest.getSystem())) {
				pointService.buildMappingAll(bindRequest.getStationId());
			} else {
				pointService.buildMappingAuto(bindRequest.getStationId());
			}
		} catch (Exception e) {
			log.error("bind failed", e);
			return ResponseResult.error(e.getMessage());
		}
		return ResponseResult.success("bind success");
	}

	@ApiOperation("查询数据点位")
	@UserLoginToken
	@RequestMapping(value = "values", method = {RequestMethod.GET})
	public ResponseResult<List<?>> getValue(@RequestParam String mappingId, @RequestParam Integer count) {
		return ResponseResult.success(pointService.getValues(mappingId, count));
	}

	@ApiOperation("查询数据点位")
	@UserLoginToken
	@RequestMapping(value = "values_by_time", method = {RequestMethod.GET})
	public ResponseResult<Map<Date, ?>> getValue(@RequestParam String mappingId, @RequestParam String st, @RequestParam String et) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		try {
			Date startTime = Date.from(LocalDateTime.parse(st, dateTimeFormatter).atZone(ZoneId.systemDefault()).toInstant());
			Date endTime = Date.from(LocalDateTime.parse(et, dateTimeFormatter).atZone(ZoneId.systemDefault()).toInstant());
			return ResponseResult.success(pointService.getValuesByTime(mappingId, startTime, endTime));
		} catch (Exception e) {
			log.error("search failed", e);
			return ResponseResult.error(e.getMessage());
		}
	}
}
