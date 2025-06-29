package com.example.vvpservice.revenue;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class ValidateListener extends AnalysisEventListener<Map<String,Object>> {
	@Override
	public void invoke(Map<String,Object> map, AnalysisContext context) {
	}

	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {

	}

	@Override
	public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
		String head1 = "时间点";
		String head2 = "有功功率(kW)";
		if (headMap.isEmpty()) {
			throw new IllegalArgumentException("导入的模板不符合，请检查后重新导入！");
		}
		if (!headMap.containsValue(head1) || !headMap.containsValue(head2)) {
			throw new IllegalArgumentException("导入的模板不符合，请检查后重新导入！");
		}
	}
}
