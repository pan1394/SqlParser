package com.linkstec.excel.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		ExcelLogicProgress p = new ExcelLogicProgress();
		int initRow = 6;
		p.setInitRowNum(initRow);
		p.setRow(5);
		p.setWidth(10);
		List<Integer> headerRowIndex = new ArrayList<Integer>();
		for (BSQLSet set : sqlSet) {
			SQLCaseObject obj = SQLCaseObject.parse(set, tsql);
			Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
			ExcelUtils.setValueForBigCategorySQL(row, obj.fetchMethodName());

			headerRowIndex.add(p.getRow());

			List<String[]> conditions = obj.fetchConditions();
			for (int i = 0; i < conditions.size(); i++) {
				Row r = ExcelUtils.makeRowWithRowNum(sheet, p);
				if (i == 0) {
					ExcelUtils.setValueForLitCategorySQL(r, ExcelTemplate.SQLTemplate.LITTLE_CATEGORY_CONDITION);
				}
				ExcelUtils.setValueForCaseSQL(r, conditions.get(i)[0]);
				ExcelUtils.setValueForExpectedResultSQL(r, conditions.get(i)[1]);
			}

			List<String> items = obj.fetchItems();
			for (int i = 0; i < items.size(); i++) {
				Row r = ExcelUtils.makeRowWithRowNum(sheet, p);
				if (i == 0) {
					ExcelUtils.setValueForLitCategorySQL(r, ExcelTemplate.SQLTemplate.LITTLE_CATEGORY_ITEMS);
				}
				ExcelUtils.setValueForCaseSQL(r, items.get(i));
				String tmplate = "%sを取得する";
				ExcelUtils.setValueForExpectedResultSQL(r, String.format(tmplate, items.get(i)));
			}

			List<String[]> counts = obj.fetchCounts();
			for (int i = 0; i < counts.size(); i++) {
				Row r = ExcelUtils.makeRowWithRowNum(sheet, p);
				if (i == 0) {
					ExcelUtils.setValueForLitCategorySQL(r, ExcelTemplate.SQLTemplate.LITTLE_CATEGORY_NUMBER);
				}
				ExcelUtils.setValueForCaseSQL(r, counts.get(i)[0]);
				ExcelUtils.setValueForExpectedResultSQL(r, counts.get(i)[1]);
			}

			String order = obj.fetchOrders();
			Row r = ExcelUtils.makeRowWithRowNum(sheet, p);
			ExcelUtils.setValueForLitCategorySQL(r, ExcelTemplate.SQLTemplate.LITTLE_CATEGORY_ORDER);
			if ("なし".equals(order)) {
				ExcelUtils.setValueForCaseSQL(r, obj.fetchOrders());
				ExcelUtils.setValueForExpectedResultSQL(r, obj.fetchOrders());
			} else {
				String t1 = "ソート順：%s";
				String t2 = "%sを%sする";
				String[] tmp = order.split(" ");
				ExcelUtils.setValueForCaseSQL(r, String.format(t1, order));
				ExcelUtils.setValueForExpectedResultSQL(r, String.format(t2, tmp[0], tmp[1]));
			}
		}

		CellRangeAddress region = new CellRangeAddress(initRow, p.getRow(), 1, p.getWidth());
		ExcelUtils.borderForRegionCells(region, sheet, style);

		for (int rowIndex : headerRowIndex) {
			CellRangeAddress region1 = new CellRangeAddress(rowIndex, rowIndex, 3, p.getWidth());
			ExcelUtils.initRowsStyleForRegionCells(region1, sheet, style);
		}

		sheet.getWorkbook().setPrintArea(sheet.getWorkbook().getSheetIndex(sheet), 0, 49, 0, p.getRow() + 2);
	}

	private static void makeSql(boolean exp, int index, String text, ExcelStyles style, Sheet sheet,
			ExcelLogicProgress p, BMethod method) {

		Map<String, String> rawconditions = new HashMap<String, String>();
		Set<String> items = null;

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
