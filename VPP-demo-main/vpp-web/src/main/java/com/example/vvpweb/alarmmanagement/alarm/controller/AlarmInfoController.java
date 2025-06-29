package com.example.vvpweb.alarmmanagement.alarm.controller;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.i18n.i18nUtil;
import com.example.vvpdomain.alarm.engine.AlarmInformationService;
import com.example.vvpdomain.alarm.info.AlarmInfo;
import com.example.vvpservice.alarm.AlarmConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@RequestMapping("/alarmInformation")
public class AlarmInfoController {


	@Autowired
	private AlarmInformationService alarmInformationService;


	@GetMapping("/findAlarmWin")
	public ResponseResult<PageModel> findAlarmInfor(
			@RequestParam(name = "stationId") List<String> stationIds,
			@RequestParam(name = "alarmStatus", required = false) Integer alarmStatus,
			@RequestParam(name = "alarmLevel", required = false) Integer alarmLevel,
			@RequestParam(name = "startTime") Timestamp startTime,
			@RequestParam(name = "endTime") Timestamp endTime,
			@RequestParam(defaultValue = "1") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize) {

		Page<AlarmInfo> page = alarmInformationService.findAlarmInfoWin(stationIds, alarmStatus, alarmLevel, startTime, endTime, pageNumber - 1,
				pageSize);
		if (page == null){
			return ResponseResult.error("station node not found");
		}
		PageModel pageModel = new PageModel();
		List<AlarmInfo> content = page.getContent();
		int totalPages = page.getTotalPages();
		int totalElements = (int) page.getTotalElements();
		int size = page.getSize();
		pageModel.setNumber(pageNumber);//更改页码
		pageModel.setPageSize(size);
		pageModel.setContent(content);
		pageModel.setTotalPages(totalPages);
		pageModel.setTotalElements(totalElements);
		return ResponseResult.success(pageModel);
	}

	@RequestMapping(value = "alarmEnum", method = {RequestMethod.GET})
	public ResponseResult<Map<Object, String>> getAlarmType(@RequestParam(value = "name") String name) {
		Map<Object, String> result = new HashMap<>();
		switch (name) {
			case "type":
				result = printInterfaceConstants(AlarmConstant.AlarmType.class);
				break;
			case "level":
				result = printInterfaceConstants(AlarmConstant.AlarmLevel.class);
				break;
			case "status":
				result = printInterfaceConstants(AlarmConstant.AlarmStatus.class);
				break;
		}
		return ResponseResult.success(result);
	}

	public static Map<Object, String> printInterfaceConstants(Class<?> clazz) {
		Map<Object, String> map = new HashMap<>();
		if (!clazz.isInterface()) {
			System.out.println(clazz.getName() + " 不是一个接口.");
			return map;
		}

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
				try {
					map.put(field.get(null), i18nUtil.getMessage(field.getName()));
				} catch (IllegalAccessException e) {
					log.error("print interface constant fail.", e);
				}
			}
		}
		return map;
	}

}
