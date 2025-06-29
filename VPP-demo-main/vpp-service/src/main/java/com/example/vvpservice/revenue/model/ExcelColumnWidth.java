package com.example.vvpservice.revenue.model;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;

import java.util.List;

public class ExcelColumnWidth extends AbstractColumnWidthStyleStrategy {
	@Override
	protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head,
	                              Integer relativeRowIndex, Boolean isHead) {
		if (isHead && cell.getRowIndex() == 0) { // isHead=true表示为表头，如果表头只有1行这里设置为0
			int columnWidth = cell.getStringCellValue().getBytes().length;
			int cellIndex = cell.getColumnIndex();
			switch (cellIndex) {
				case 0:
					columnWidth = 9;
					break;
				case 1:
					columnWidth = 12;
					break;
				case 2:
					columnWidth = 22;
					break;
				case 3:
					columnWidth = 12;
					break;
				case 4:
					columnWidth = 11;
					break;
				case 5:
					columnWidth = 30;
					break;
				default:
					columnWidth = 12;
					break;
			}

			if (columnWidth > 255) {
				columnWidth = 255;
			}
			writeSheetHolder.getSheet().setColumnWidth(cellIndex, columnWidth * 256);
		}
	}

}
