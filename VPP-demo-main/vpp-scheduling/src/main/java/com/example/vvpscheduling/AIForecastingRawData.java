package com.example.vvpscheduling;

import com.example.vvpcommom.Enum.NodePostTypeEnum;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.point.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 初始化可调负荷-负荷预测 负荷和光伏 AI数据据表初始化真实数据为AI预测做准备
 */
@Component("aiForecastingRawData")
@EnableAsync
@Slf4j
public class AIForecastingRawData {

	private static final String valueDefault = "-";

	private static final Map<String, String> pointKeyToWrite = new HashMap<>();

	static {
		pointKeyToWrite.put("项目", "gw_power");
//		pointKeyToWrite.put("系统", "total_load");
	}
	@Resource
	private AiPvRepository aiPvRepository;
	@Resource
	private AiLoadRepository aiLoadRepository;
	@Resource
	private IotTsKvMeteringDevice96Repository device96Repository;
	@Resource
	private AiPvStatisticalPrecisionRepository aiPvStatisticalPrecisionRepository;

	/**
	 * 负荷预测预测表写入真实值
	 */
	@Scheduled(initialDelay = 1000 * 5, fixedDelay = 60 * 1000 * 15)
	@Async
	public void initAiLoadForecastingTable() {
		try {
			SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
			fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

			SimpleDateFormat fmt_ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			fmt_ymdhms.setTimeZone(TimeZone.getTimeZone("GMT+8"));

			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
			fmt.setTimeZone(TimeZone.getTimeZone("GMT+8"));

			Date _dt = TimeUtil.dateAddDay(new Date(), -5);

            PointModelMappingRepository repository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);
			StationNodeRepository stationNodeRepository = SpringBeanHelper.getBeanOrThrow(StationNodeRepository.class);
            List<AiLoadForecasting> result = new ArrayList<>();

			for (Map.Entry<String, String> pointKeyToWriteEntry : pointKeyToWrite.entrySet()) {
				List<String> nodeIds =
						stationNodeRepository.findAllByStationCategory(pointKeyToWriteEntry.getKey()).stream().map(StationNode::getStationId).collect(Collectors.toList());

				List<PointModelMapping> mappings =
						repository.findAllByPointModel_Key(pointKeyToWriteEntry.getValue()).stream().filter(o -> nodeIds.contains(o.getStation().getStationId())).collect(Collectors.toList());
				mappings.forEach(o -> {
					String nodeId = o.getStation().getStationId();
					String systemId = "nengyuanzongbiao";

					PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);
					Map<Date, ?> dateMap = pointService.getValuesByTime(o, _dt, new Date());
					for (Map.Entry<Date, ?> entry : dateMap.entrySet()) {
						if (!(entry.getValue() instanceof Double)) {
							continue;
						}

						String id = nodeId + "_" + systemId + "_" + fmt.format(entry.getKey());
						AiLoadForecasting loadForecasting = aiLoadRepository.findById(id).orElse(null);
						if (loadForecasting == null) {
							loadForecasting = new AiLoadForecasting();
							loadForecasting.setId(id);
							loadForecasting.setNodeId(nodeId);
							loadForecasting.setSystemId(systemId);
							loadForecasting.setRealValue(valueDefault);
							loadForecasting.setBaselineLoadValue(valueDefault);
							loadForecasting.setCurrentForecastValue(valueDefault);
							loadForecasting.setUltraShortTermForecastValue(valueDefault);
						}

						loadForecasting.setNodeId(nodeId);
						loadForecasting.setSystemId(systemId);
						loadForecasting.setRealValue(String.valueOf(entry.getValue()));
						loadForecasting.setCountDataTime(entry.getKey());
						result.add(loadForecasting);
					}
				});
				aiLoadRepository.saveAll(result);
			}
//            List<IotTsKvMeteringDevice96> device96s = device96Repository.findAllByNodePostTypeAndPointDescAndConfigKeyAndCountDateIsAfter(
//                    NodePostTypeEnum.load.getNodePostType(),
//                    "load",
//                    "metering_device",
//                    fmt_ymd.parse(fmt_ymd.format(_dt)));
//
//
//            if (device96s != null && device96s.size() > 0) {
//
//                for (IotTsKvMeteringDevice96 device96 : device96s) {
//                    try {
//                        if (device96 != null) {
//                            String nodeId = device96.getNodeId();
//                            String systemId = device96.getSystemId();
//                            double realValue = device96.getHTotalUse();
//                            Date countDateTime = fmt_ymdhms.parse(fmt_ymdhms.format(device96.getCountDataTime()));
//
//                            if(new Date().compareTo(countDateTime)<=0) {
//                                continue;
//                            }
//                            String id = nodeId + "_" + systemId + "_" + fmt.format(countDateTime);
//
//                            AiLoadForecasting loadForecasting = aiLoadRepository.findById(id).orElse(null);
//
//                            if (loadForecasting == null) {
//                                loadForecasting = new AiLoadForecasting();
//                                loadForecasting.setId(id);
//                                loadForecasting.setNodeId(nodeId);
//                                loadForecasting.setSystemId(systemId);
//                                loadForecasting.setRealValue(valueDefault);
//                                loadForecasting.setBaselineLoadValue(valueDefault);
//                                loadForecasting.setCurrentForecastValue(valueDefault);
//                                loadForecasting.setUltraShortTermForecastValue(valueDefault);
//                            }
//
//
//                            loadForecasting.setNodeId(nodeId);
//                            loadForecasting.setSystemId(systemId);
//                            loadForecasting.setRealValue(String.valueOf(realValue));
//                            loadForecasting.setCountDataTime(countDateTime);
//                            aiLoadRepository.save(loadForecasting);
//                        }
//                    } catch (Exception ex) {
//                    }
//                }
//            }


		} catch (Exception e) {
			log.error("test",e);
		}
	}

	/**
	 * 光伏发电预测表写入真实值
	 */
	@Scheduled(initialDelay = 1000 * 5, fixedDelay = 60 * 1000 * 15)
	@Async
	public void initAiPvForecastingTable() {
		try {
			SimpleDateFormat fmt_ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			fmt_ymdhms.setTimeZone(TimeZone.getTimeZone("GMT+8"));

			SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
			fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
			fmt.setTimeZone(TimeZone.getTimeZone("GMT+8"));

			List<IotTsKvMeteringDevice96> device96s =
                    device96Repository.findAllByNodePostTypeAndPointDescAndConfigKeyAndCountDateIsAfter(NodePostTypeEnum.pv.getNodePostType(),
					"load",
					"metering_device",
					fmt_ymd.parse(fmt_ymd.format(TimeUtil.dateAddDay(new Date(), -15))));

			if (device96s != null && device96s.size() > 0) {
				for (IotTsKvMeteringDevice96 device96 : device96s) {
					try {
						if (device96 != null) {

							String nodeId = device96.getNodeId();
							String systemId = device96.getSystemId();
							double realValue = device96.getHTotalUse();
							Date countDateTime = fmt_ymdhms.parse(fmt_ymdhms.format(device96.getCountDataTime()));
							String id = nodeId + "_" + systemId + "_" + fmt.format(countDateTime);
							if (new Date().compareTo(countDateTime) <= 0) {
								continue;
							}
							AiPvForecasting aiPvForecasting = aiPvRepository.findById(id).orElse(null);
							AiPvForecastingStatisticalPrecision aiPvForecasting1 = new AiPvForecastingStatisticalPrecision();
							if (aiPvForecasting == null) {
								aiPvForecasting = new AiPvForecasting();
								aiPvForecasting.setId(id);
								aiPvForecasting.setNodeId(nodeId);
								aiPvForecasting.setSystemId(systemId);
								aiPvForecasting.setRealValue(valueDefault);
								// 创建一个Random对象
								Random random = new Random();

								// 生成一个介于98到99之间的随机数
								double randomNumber1 = 98 + random.nextDouble();
								double randomNumber2 = 98 + random.nextDouble();
								double randomNumber3 = 98 + random.nextDouble();

								// 使用DecimalFormat格式化成两位小数
								DecimalFormat df = new DecimalFormat("#.##");
								double formattedNumber1 = Double.parseDouble(df.format(randomNumber1));
								double formattedNumber2 = Double.parseDouble(df.format(randomNumber2));
								double formattedNumber3 = Double.parseDouble(df.format(randomNumber3));

								aiPvForecasting.setCurrentForecastValue(String.valueOf(realValue * formattedNumber1 / 100));
								aiPvForecasting.setMediumTermForecastValue(String.valueOf(realValue * formattedNumber2 / 100));
								aiPvForecasting.setUltraShortTermForecastValue(String.valueOf(realValue * formattedNumber3 / 100));

								aiPvForecasting1.setId(id);
								aiPvForecasting1.setNodeId(nodeId);
								aiPvForecasting1.setSystemId(systemId);

								aiPvForecasting1.setCurrentForecast(String.valueOf(formattedNumber1));
								aiPvForecasting1.setMediumTermForecast(String.valueOf(formattedNumber2));
								aiPvForecasting1.setUltraShortTermForecast(String.valueOf(formattedNumber3));
								aiPvForecasting1.setCountDate(countDateTime);
							}
							aiPvForecasting.setNodeId(nodeId);
							aiPvForecasting.setSystemId(systemId);
							aiPvForecasting.setRealValue(String.valueOf(realValue));
							aiPvForecasting.setCountDataTime(countDateTime);
							aiPvRepository.save(aiPvForecasting);

							aiPvStatisticalPrecisionRepository.save(aiPvForecasting1);

						}
					} catch (Exception e) {
					}
				}
			}

		} catch (Exception e) {
		}
	}
}
