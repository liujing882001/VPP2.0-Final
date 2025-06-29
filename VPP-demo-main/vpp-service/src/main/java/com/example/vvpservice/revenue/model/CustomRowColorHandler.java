package com.example.vvpservice.revenue.model;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.*;

public class CustomRowColorHandler implements RowWriteHandler {

	@Override
	public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
		// 跳过表头
		if (isHead || row == null) {
			return;
		}

		// 检查序号为 1、2、3 的行
		if (row.getRowNum() == 2 || row.getRowNum() == 4 || row.getRowNum() == 8) {
			// 设置背景颜色为黄色
			for (Cell cell : row) {
				CellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
				cellStyle.cloneStyleFrom(cell.getCellStyle()); // 复制原有样式

				// 设置背景颜色
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
				cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				cell.setCellStyle(cellStyle); // 应用样式到单元格
			}
		}
	}
}
