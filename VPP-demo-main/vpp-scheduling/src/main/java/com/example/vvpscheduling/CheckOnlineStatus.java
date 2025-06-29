package com.example.vvpscheduling;

import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.DeviceRepository;
import com.example.vvpdomain.IotTsKvLastRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.OnlineRuleRepository;
import com.example.vvpdomain.entity.Device;
import com.example.vvpdomain.entity.IotTsKvLast;
import com.example.vvpdomain.entity.Node;
import com.example.vvpdomain.entity.OnlineRule;
import com.example.vvpscheduling.util.text.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 设备及节点在线状态判定
 */
@Component("deviceStatusJob")
@EnableAsync
public class CheckOnlineStatus {
	private static Logger logger = LoggerFactory.getLogger(CheckOnlineStatus.class);
	@Resource
	IotTsKvLastRepository tsKvLastRepository;
	@Resource
	DeviceRepository deviceRepository;
	@Resource
	NodeRepository nodeRepository;

	/**
	 * 每分钟执行一次任务
	 */
	@Scheduled(initialDelay = 1000 * 5, fixedDelay = 60 * 1000)
	@Async
	public void checkDeviceStatus() {
		try {

			List<IotTsKvLast> iotTsKvLasts = tsKvLastRepository.findAll();
			if (iotTsKvLasts != null && iotTsKvLasts.size() > 0) {
				Map<String, List<IotTsKvLast>> countMap = iotTsKvLasts.stream()
						.filter(a -> StringUtils.isNotEmpty(a.getDeviceSn()))
						.collect(Collectors.groupingBy(IotTsKvLast::getDeviceSn));
				if (countMap != null && countMap.size() > 0) {
					countMap.keySet().stream().forEach(p -> {
						if (p != null) {
							List<IotTsKvLast> entities = countMap.get(p);
							if (entities != null && entities.size() > 0) {
								try {
									boolean online = true;
									String deviceSn = entities.get(0).getDeviceSn();
									for (IotTsKvLast entity : entities) {
										Date dt = TimeUtil.dateAddHours(entity.getTs(), 1);
										Date dt_now = new Date();
										boolean status = ((dt.getTime() < dt_now.getTime()) ? false : true);
										online = (online && status);
										if (!online) {
											break;
										}
									}
									Device entity = deviceRepository.findByDeviceSn(deviceSn);
									if (entity != null) {
										entity.setOnline(online);
										deviceRepository.save(entity);
									}
								} catch (Exception e) {
								}
							}
						}
					});
				}
			}
		} catch (Exception e) {
		}
	}


	/**
	 * 每分钟执行一次任务
	 */
	@Scheduled(initialDelay = 1000 * 5, fixedDelay = 60 * 1000)
	@Async
	public void checkNodeStatus() {
		List<Node> nodes = nodeRepository.findAll();
		if (nodes.isEmpty()) {
			return;
		}
		nodes.forEach(p -> {
			try {
				String nodeId = p.getNodeId();
				List<Device> devices = deviceRepository.findAllByNode_NodeId(nodeId);
				OnlineRuleRepository onlineRuleRepository = SpringBeanHelper.getBeanOrThrow(OnlineRuleRepository.class);
				OnlineRule rule = onlineRuleRepository.findByNodeId(nodeId);
				// 未配置规则，任一设备在线则节点在线
				if (rule == null || rule.getDeviceList().isEmpty()) {
					AtomicBoolean online = new AtomicBoolean(false);
					devices.forEach(o -> online.set(online.get() || o.getOnline()));
					p.setOnline(online.get());
				} else {
					Map<String, Device> deviceMap = devices.stream().collect(Collectors.toMap(Device::getDeviceId, Function.identity()));
					List<String> deviceList = Arrays.asList(rule.getDeviceList().split(","));
					AtomicBoolean online = new AtomicBoolean(true);
					deviceList.forEach(o -> online.set(online.get() && deviceMap.get(o).getOnline()));
					p.setOnline(online.get());
				}
			} catch (Exception e) {
				logger.error("在线状态判断错误,节点id：{}", p.getNodeId());
			}
		});
		nodeRepository.saveAll(nodes);
	}

}
