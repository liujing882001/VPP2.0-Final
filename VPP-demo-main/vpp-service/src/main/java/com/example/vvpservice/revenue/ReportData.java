package com.example.vvpservice.revenue;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import lombok.Data;

@Data
@HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, verticalAlignment = VerticalAlignmentEnum.CENTER)
@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, verticalAlignment = VerticalAlignmentEnum.CENTER)
public class ReportData {

	@ExcelProperty("编号")
	private Long id;

	@ExcelProperty("分类")
	private String type;

	@ExcelProperty("项目")
	private String name;

	@ExcelProperty(value = "数值")
	private Double value;

	@ExcelProperty("单位")
	private String unit;

	@ExcelProperty("说明")
	private String remark;
}

