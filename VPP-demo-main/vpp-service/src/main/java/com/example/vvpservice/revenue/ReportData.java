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

	// Add missing methods manually since Lombok might not be working properly
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public void setId(long id) { this.id = id; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public Double getValue() { return value; }
	public void setValue(Double value) { this.value = value; }
	public String getUnit() { return unit; }
	public void setUnit(String unit) { this.unit = unit; }
	public String getRemark() { return remark; }
	public void setRemark(String remark) { this.remark = remark; }
}

