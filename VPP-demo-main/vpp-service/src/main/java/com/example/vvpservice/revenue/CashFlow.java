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
public class CashFlow {
	@ExcelProperty("序号")
	private Double index;

	@ExcelProperty("项目")
	private String project;

	@ExcelProperty("合计")
	private Double total;

	@ExcelProperty({"计算期", "第0年"})
	private Double year0;

	@ExcelProperty({"计算期", "第1年"})
	private Double year1;

	@ExcelProperty({"计算期", "第2年"})
	private Double year2;

	@ExcelProperty({"计算期", "第3年"})
	private Double year3;

	@ExcelProperty({"计算期", "第4年"})
	private Double year4;

	@ExcelProperty({"计算期", "第5年"})
	private Double year5;

	@ExcelProperty({"计算期", "第6年"})
	private Double year6;

	@ExcelProperty({"计算期", "第7年"})
	private Double year7;

	@ExcelProperty({"计算期", "第8年"})
	private Double year8;

	@ExcelProperty({"计算期", "第9年"})
	private Double year9;

	@ExcelProperty({"计算期", "第10年"})
	private Double year10;

	@ExcelProperty({"计算期", "第11年"})
	private Double year11;

	@ExcelProperty({"计算期", "第12年"})
	private Double year12;

	@ExcelProperty({"计算期", "第13年"})
	private Double year13;

	@ExcelProperty({"计算期", "第14年"})
	private Double year14;

	@ExcelProperty({"计算期", "第15年"})
	private Double year15;

	@ExcelProperty({"计算期", "第16年"})
	private Double year16;

	@ExcelProperty({"计算期", "第17年"})
	private Double year17;

	@ExcelProperty({"计算期", "第18年"})
	private Double year18;

	@ExcelProperty({"计算期", "第19年"})
	private Double year19;

	@ExcelProperty({"计算期", "第20年"})
	private Double year20;

	@ExcelProperty({"计算期", "第21年"})
	private Double year21;

	@ExcelProperty({"计算期", "第22年"})
	private Double year22;

	@ExcelProperty({"计算期", "第23年"})
	private Double year23;

	@ExcelProperty({"计算期", "第24年"})
	private Double year24;

	@ExcelProperty({"计算期", "第25年"})
	private Double year25;


	public CashFlow(Double index, String project, Double[] arr) {
		this.index = index;
		this.project = project;
		this.year0 = arr[0];
		this.year1 = 1 < arr.length ? arr[1] : 0.0;
		this.year2 = 2 < arr.length ? arr[2] : 0.0;
		this.year3 = 3 < arr.length ? arr[3] : 0.0;
		this.year4 = 4 < arr.length ? arr[4] : 0.0;
		this.year5 = 5 < arr.length ? arr[5] : 0.0;
		this.year6 = 6 < arr.length ? arr[6] : 0.0;
		this.year7 = 7 < arr.length ? arr[7] : 0.0;
		this.year8 = 8 < arr.length ? arr[8] : 0.0;
		this.year9 = 9 < arr.length ? arr[9] : 0.0;
		this.year10 = 10 < arr.length ? arr[10] : 0.0;
		this.year11 = 11 < arr.length ? arr[11] : 0.0;
		this.year12 = 12 < arr.length ? arr[12] : 0.0;
		this.year13 = 13 < arr.length ? arr[13] : 0.0;
		this.year14 = 14 < arr.length ? arr[14] : 0.0;
		this.year15 = 15 < arr.length ? arr[15] : 0.0;
		this.year16 = 16 < arr.length ? arr[16] : 0.0;
		this.year17 = 17 < arr.length ? arr[17] : 0.0;
		this.year18 = 18 < arr.length ? arr[18] : 0.0;
		this.year19 = 19 < arr.length ? arr[19] : 0.0;
		this.year20 = 20 < arr.length ? arr[20] : 0.0;
		this.year21 = 21 < arr.length ? arr[21] : 0.0;
		this.year22 = 22 < arr.length ? arr[22] : 0.0;
		this.year23 = 23 < arr.length ? arr[23] : 0.0;
		this.year24 = 24 < arr.length ? arr[24] : 0.0;
		this.year25 = 25 < arr.length ? arr[25] : 0.0;

		this.total =
				year0 + year1 + year2 + year3 + year4 + year5 + year6 + year7 + year8 + year9 + year10 + year11 + year12 + year13 + year14 + year15 + year16 + year17 + year18 + year19 + year20 + year21 + year22 + year23 + year24 + year25;
	}
}
