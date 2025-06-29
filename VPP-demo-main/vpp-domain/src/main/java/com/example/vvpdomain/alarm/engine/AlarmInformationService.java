package com.example.vvpdomain.alarm.engine;

import com.example.vvpdomain.StationNodeRepository;
import com.example.vvpdomain.alarm.info.AlarmInfo;
import com.example.vvpdomain.alarm.info.AlarmInfoRepository;
import com.example.vvpdomain.entity.StationNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AlarmInformationService {

	@Autowired
	private AlarmInfoRepository alarmInfoRepository;
	//可以让状态为空和类型-添加alarmType---添加分页方法

	@Autowired
	private StationNodeRepository stationNodeRepository;

	public Page<AlarmInfo> findAlarmInfoWin(List<String> stationId, Integer alarmStatus, Integer alarmLevel, Timestamp startTime, Timestamp endTime,
	                                        int pageNumber, int pageSize) {
		List<StationNode> stationNodes = stationNodeRepository.findAllByStationIdIn(stationId);
		if (CollectionUtils.isEmpty(stationNodes)) {
			return null;
		}
		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		List<StationNode> nodes = new ArrayList<>(stationNodes);
		stationNodes.forEach(stationNode -> {
			if (stationNode.getStationCategory().contains("项目")) {
				List<StationNode> node = stationNodeRepository.findAllByParentId(stationNode.getStationId());
				nodes.addAll(node);
			}
		});
		Page<AlarmInfo> info = alarmInfoRepository.findAlarmInfoWin(nodes.stream().map(StationNode::getStationId).collect(Collectors.toList()), alarmStatus,
				alarmLevel, startTime, endTime, pageable);
		if (info == null) {
			return Page.empty();
		} else {
			return info;
		}

	}
}
