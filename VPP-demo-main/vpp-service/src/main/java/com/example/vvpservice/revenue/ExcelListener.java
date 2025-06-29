package com.example.vvpservice.revenue;
import java.util.Date;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpdomain.RevenueLoadDateRepository;
import com.example.vvpdomain.entity.RevenueLoadDto;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelListener extends AnalysisEventListener<ExcelData> {

	private String projectId;

	private List<ExcelData> data;

	private Boolean saveToDataBse;

	ExcelListener(String projectId, List<ExcelData> data ,Boolean saveToDataBse) {
		this.projectId = projectId;
		this.data = data;
		this.saveToDataBse = saveToDataBse;
	}

	private static final int BATCH_COUNT = 10000;

	private List<ExcelData> cachedDataList = new ArrayList<>(BATCH_COUNT);

	@Override
	public void invoke(ExcelData data, AnalysisContext context) {
		if (data.getDate() == null || data.getPower() == null) {
			throw new IllegalArgumentException("数据不能为空");
		}
//		log.info("解析到一条数据:{}", data);
		cachedDataList.add(data);
		if (cachedDataList.size() >= BATCH_COUNT) {
			if (saveToDataBse) {
				new Thread(this::saveData);
			}
			this.data.addAll(cachedDataList);
			cachedDataList = new ArrayList<>(BATCH_COUNT);
		}
	}

	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		if (saveToDataBse) {
			new Thread(this::saveData);
		}
		this.data.addAll(cachedDataList);
		cachedDataList = new ArrayList<>();
	}


	/**
	 * 加上存储数据库
	 */
	private void saveData() {
		if (cachedDataList.isEmpty()) {
			return;
		}
		RevenueLoadDateRepository repository = SpringBeanHelper.getBeanOrThrow(RevenueLoadDateRepository.class);

		log.info("{}条数据，开始存储数据库！", cachedDataList.size());
		this.data.addAll(cachedDataList);
		List<RevenueLoadDto> data = new ArrayList<>();
		cachedDataList.forEach(o -> {
			RevenueLoadDto dto = new RevenueLoadDto();
			dto.setId(projectId + "_" + o.getDate().getTime());
			dto.setProjectId(projectId);
			dto.setTime(o.getDate());
			dto.setPower(o.getPower());
			data.add(dto);
		});
		repository.saveAll(data);
	}
}
