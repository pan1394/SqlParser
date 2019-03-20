package com.linkstec.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.linkstec.bee.core.fw.BModule;
import com.linkstec.bee.core.fw.basic.BSQLSet;
import com.linkstec.bee.core.fw.basic.ITableSql;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BMethod;

public class ExcelSqlSheet {
	public static void createSQL(BModule module, BProject project, ExcelStyles style, Sheet sheet, List<BSQLSet> sqlSet,
			ITableSql tsql) {
		// List<BInvoker> invokers = new ArrayList<BInvoker>();

		ExcelLogicProgress p = new ExcelLogicProgress();
		p.setRow(7);
		p.setWidth(24);

		int index = 0;

		for (BSQLSet set : sqlSet) {
			index++;

			int row = p.getRow();
			String sql = set.getModel().getSQL(tsql);
			String exp = set.getModel().getSQLExp(tsql);
			p.setCol(0);

			// sql
			makeSql(false, index, sql, style, sheet, p, set.getMethod());

			int sqlend = p.getRow();

			// exp
			p.setRow(row);
			p.setCol(24);
			makeSql(true, index, exp, style, sheet, p, set.getMethod());

			int expEnd = p.getRow();

			int end = Math.max(sqlend, expEnd);

			// end++;
			p.setRow(end);

			CellRangeAddress region = new CellRangeAddress(row, end - 1, 0, 23);
			ExcelUtils.borderRegion(region, sheet, style);
			region = new CellRangeAddress(row, end - 1, 24, 48);
			ExcelUtils.borderRegion(region, sheet, style);

		}

		// CellRangeAddress region = new CellRangeAddress(0, 50, 0, p.getRow() + 2);
		// ExcelUtils.borderRegion(region, sheet, style);
		sheet.getWorkbook().setPrintArea(sheet.getWorkbook().getSheetIndex(sheet), 0, 49, 0, p.getRow() + 2);
	}

	private static void makeSql(boolean exp, int index, String text, ExcelStyles style, Sheet sheet,
			ExcelLogicProgress p, BMethod method) {
		if (text.indexOf("\r\n") >= 0) {
			String[] sqls = text.split("\r\n");

			Row title = ExcelUtils.getRow(sheet, p.getRow());
			Cell cell = ExcelUtils.getCell(title, p.getCol());

			cell.setCellValue("(" + index + ")" + method.getName());
			cell.setCellStyle(style.getBasicStyle());

			p.setRow(p.getRow() + 1);

			if (!exp) {
				title = ExcelUtils.getRow(sheet, p.getRow());
				cell = ExcelUtils.getCell(title, p.getCol() + 1);
				cell.setCellValue(method.getLogicName());
				cell.setCellStyle(style.getBasicStyle());
			}
			p.setRow(p.getRow() + 2);

			p.setCol(p.getCol() + 1);

			for (String sql : sqls) {
				if (!sql.trim().equals("")) {
					Row row = ExcelUtils.getRow(sheet, p.getRow());
					int indent = 0;
					while (sql.startsWith("\t")) {
						sql = sql.substring(1);
						indent++;
					}

					Cell c = ExcelUtils.getCell(row, indent + p.getCol());
					c.setCellStyle(style.getBasicStyle());
					c.setCellValue(sql);

					p.setRow(p.getRow() + 1);
				}
			}

			p.setRow(p.getRow() + 1);

		}
	}
}
