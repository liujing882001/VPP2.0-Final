package com.example.vvpservice.alarm.AlarmStrategy.impl.indicator;

import com.example.vvpdomain.BiStorageEnergyResourcesRepository;
import com.example.vvpdomain.entity.BiStorageEnergyResources;
import com.example.vvpservice.alarm.AlarmStrategy.AlarmConditionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component("online")
public class DeviceOnlineStrategy implements AlarmConditionStrategy {

	public static final String ONLINE = "online";

	@Autowired
	private BiStorageEnergyResourcesRepository biStorageEnergyResourcesRepository;

	@Override
	public List<?> getConditionValues(String nodeId, Date ts, int count) {
		return Collections.nCopies(count, getOnline(nodeId));
	}


	private boolean getOnline(String nodeId) {
		BiStorageEnergyResources log = biStorageEnergyResourcesRepository.findByNodeId(nodeId);
		return log.getOnline();
	}
}
