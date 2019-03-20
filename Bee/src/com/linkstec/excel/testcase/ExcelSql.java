package com.linkstec.excel.testcase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelSql {
	ExcelStyles style;

	public ExcelSql(Sheet sheet, List<SqlObject> sqls) throws IOException {
		this.style = new ExcelStyles(sheet.getWorkbook());
		this.process(sheet, sqls);
	}

	private void process(Sheet sheet, List<SqlObject> sqlObjects) throws IOException {
		int number = 1;
		int rowNumber = 7;
		// 每个sqlObject为一个sql语句
		for (SqlObject sqlObject : sqlObjects) {
			int indent = 0;
			int start = rowNumber;
			sheet.createRow(rowNumber);
			sheet.getRow(rowNumber).createCell(0).setCellValue("(" + number + ")" + sqlObject.getName());
			sheet.getRow(rowNumber).createCell(24).setCellValue("(" + number + ")" + sqlObject.getName());
			List<SqlUnit> units = sqlObject.getUnits();
			// unit为union all的单元
			for (SqlUnit unit : units) {
				rowNumber++;
				indent = 1;
				sheet.createRow(rowNumber);
				sheet.getRow(rowNumber).createCell(indent).setCellValue("SELECT");
				sheet.getRow(rowNumber).createCell(indent + 24).setCellValue("検索項目：");
				rowNumber++;
				sheet.createRow(rowNumber);
				List<SqlRow> Fields = unit.getFields();
				indent++;
				int count = 0;
				// 每个字段为一个row 其中有可能会有缺少内容的情况
				for (SqlRow row : Fields) {
					String left = "";
					String right = "";
					if (row.getNodes().size() > 1) {
						// 缺损判断
						left = row.getNodes().get(0).getAttr().get("logicName").toString() + "AS"
								+ row.getNodes().get(1).getAttr().get("logicName").toString();
						if (row.getNodes().get(1).getAttr().get("name").equals("")) {
							right = row.getNodes().get(0).getAttr().get("name").toString() + "AS"
									+ row.getNodes().get(1).getAttr().get("name").toString();
						} else {
							right = row.getNodes().get(0).getAttr().get("name").toString();
						}
					} else {
						left = row.getNodes().get(0).getAttr().get("logicName").toString();
						right = row.getNodes().get(0).getAttr().get("name").toString();
					}
					// 查询字段大于1，则要加逗号
					if (count > 0) {
						left = ", " + left;
						right = ", " + right;
					}
					sheet.getRow(rowNumber).createCell(indent).setCellValue(left);
					sheet.getRow(rowNumber).createCell(indent + 24).setCellValue(right);
					rowNumber++;
					count++;
					sheet.createRow(rowNumber);
				}
				indent = 1;
				sheet.getRow(rowNumber).createCell(indent).setCellValue("FROM");
				sheet.getRow(rowNumber).createCell(indent + 24).setCellValue("検索テーブル：");
				List<SqlRow> tables = unit.getTables();
				indent++;
				rowNumber++;
				sheet.createRow(rowNumber);
				for (SqlRow row : tables) {
					String left = row.getNodes().get(0).getAttr().get("logicName").toString();
					String right = row.getNodes().get(0).getAttr().get("name").toString();
					sheet.getRow(rowNumber).createCell(indent).setCellValue(left);
					sheet.getRow(rowNumber).createCell(indent + 24).setCellValue(right);
					rowNumber++;
					sheet.createRow(rowNumber);
				}

				indent = 1;
				sheet.getRow(rowNumber).createCell(indent).setCellValue("WHERE");
				sheet.getRow(rowNumber).createCell(indent + 24).setCellValue("検索条件：");
				List<SqlRow> where = unit.getWhere();
				indent++;
				rowNumber++;
				sheet.createRow(rowNumber);
				for (SqlRow row : where) {
					StringBuilder left = new StringBuilder();
					StringBuilder right = new StringBuilder();
					for (SqlNode node : row.getNodes()) {
						if (node.getAttr().get("type").toString().equals("keyword")) {
							if (node.isIndent()) {
								indent++;
							}
							left.append(node.getAttr().get("value").toString());
							right.append(node.getAttr().get("value").toString());
						} else if (node.getAttr().get("type").toString().equals("Field")) {
							if (node.isIndent()) {
								indent++;
							}
							left.append(node.getAttr().get("logicName").toString());
							right.append(node.getAttr().get("name").toString());
						} else if (node.getAttr().get("type").toString().equals("subsql")) {
							indent++;
							rowNumber = SubSql(sheet, rowNumber, indent, (SqlObject) node.getAttr().get("subsql"));
						}
					}
					if (!row.getNodes().get(0).getAttr().get("type").toString().equals("subsql")) {
						sheet.createRow(rowNumber);
						sheet.getRow(rowNumber).createCell(indent).setCellValue(left.toString());
						sheet.getRow(rowNumber).createCell(indent + 24).setCellValue(right.toString());
					}

					rowNumber++;
					sheet.createRow(rowNumber);
				}

				List<SqlRow> groups = unit.getGroup();
				if (groups.size() > 0) {
					indent = 1;
					StringBuilder left = new StringBuilder();
					StringBuilder right = new StringBuilder();
					sheet.getRow(rowNumber).createCell(indent).setCellValue("GROUP BY");
					sheet.getRow(rowNumber).createCell(indent + 24).setCellValue("GROUP BY");
					indent++;
					rowNumber++;
					sheet.createRow(rowNumber);
					count = 0;
					for (SqlRow row : groups) {
						if (count > 0) {
							left.append(",");
							right.append(",");
						}
						if (row.getNodes().get(0).getAttr().get("type").toString().equals("Field")) {
							left.append(row.getNodes().get(0).getAttr().get("logicName").toString());
							right.append(row.getNodes().get(0).getAttr().get("name").toString());
						} else {
							left.append(row.getNodes().get(0).getAttr().get("value").toString());
							right.append(row.getNodes().get(0).getAttr().get("value").toString());
						}
						count++;
					}
					sheet.getRow(rowNumber).createCell(indent).setCellValue(left.toString());
					sheet.getRow(rowNumber).createCell(indent + 24).setCellValue(right.toString());
					rowNumber++;
					sheet.createRow(rowNumber);
				}

				List<SqlRow> orders = unit.getOrder();
				if (orders.size() > 0) {
					indent = 1;
					sheet.getRow(rowNumber).createCell(indent).setCellValue("ORDER BY ");
					sheet.getRow(rowNumber).createCell(indent + 24).setCellValue("ORDER BY");
					StringBuilder left = new StringBuilder();
					StringBuilder right = new StringBuilder();
					indent++;
					rowNumber++;
					sheet.createRow(rowNumber);
					count = 0;
					for (SqlRow row : orders) {
						if (count > 0) {
							left.append(",");
							right.append(",");
						}
						if (row.getNodes().get(0).getAttr().get("type").toString().equals("Field")) {
							left.append(row.getNodes().get(0).getAttr().get("logicName").toString());
							right.append(row.getNodes().get(0).getAttr().get("name").toString());
						} else {
							left.append(row.getNodes().get(0).getAttr().get("value").toString());
							right.append(row.getNodes().get(0).getAttr().get("value").toString());
						}
						count++;
					}
					sheet.getRow(rowNumber).createCell(indent).setCellValue(left.toString());
					sheet.getRow(rowNumber).createCell(indent + 24).setCellValue(right.toString());
					rowNumber++;
					sheet.createRow(rowNumber);
				}
				sheet.getRow(rowNumber).createCell(1).setCellValue("UNION");
				sheet.getRow(rowNumber).createCell(1 + 24).setCellValue("UNION");
			}
			sheet.removeRow(sheet.getRow(rowNumber));
			CellRangeAddress classNameValue = new CellRangeAddress(start, rowNumber, 0, 23);
			ExcelUtils.borderRegion(classNameValue, sheet, style);
			classNameValue = new CellRangeAddress(start, rowNumber, 24, 49);
			ExcelUtils.borderRegion(classNameValue, sheet, style);
			number++;
		}
		sheet.getWorkbook().setPrintArea(sheet.getWorkbook().getSheetIndex(sheet), 0, 50, 0, rowNumber);
		FileOutputStream output = new FileOutputStream("d:\\workbook.xlsx");
		sheet.getWorkbook().write(output);
		output.flush();
	}

	// 子查询 递归基本和上面主查询类似 返回的是最后的行数
	private int SubSql(Sheet sheet, int rowNumber, int indent, SqlObject sqlObject) {
		List<SqlUnit> units = sqlObject.getUnits();
		for (SqlUnit unit : units) {
			sheet.getRow(rowNumber).createCell(indent).setCellValue("SELECT");
			sheet.getRow(rowNumber).createCell(indent + 24).setCellValue("SELECT");
			rowNumber++;
			sheet.createRow(rowNumber);
			List<SqlRow> Fields = unit.getFields();
			indent++;
			int count = 0;
			for (SqlRow row : Fields) {
				String left = "";
				String right = "";
				if (row.getNodes().size() > 1) {
					left = row.getNodes().get(0).getAttr().get("logicName").toString() + "AS"
							+ row.getNodes().get(1).getAttr().get("logicName").toString();
					if (row.getNodes().get(1).getAttr().get("name").equals("")) {
						right = row.getNodes().get(0).getAttr().get("name").toString() + "AS"
								+ row.getNodes().get(1).getAttr().get("name").toString();
					} else {
						right = row.getNodes().get(0).getAttr().get("name").toString();
					}
				} else {
					left = row.getNodes().get(0).getAttr().get("logicName").toString();
					right = row.getNodes().get(0).getAttr().get("name").toString();
				}
				if (count > 0) {
					left = ", " + left;
					right = ", " + right;
				}
				sheet.getRow(rowNumber).createCell(indent).setCellValue(left);
				sheet.getRow(rowNumber).createCell(indent + 24).setCellValue(right);
				rowNumber++;
				count++;
				sheet.createRow(rowNumber);
			}
			indent--;
			sheet.getRow(rowNumber).createCell(indent).setCellValue("FROM");
			sheet.getRow(rowNumber).createCell(indent + 24).setCellValue("FROM");
			List<SqlRow> tables = unit.getTables();
			indent++;
			rowNumber++;
			sheet.createRow(rowNumber);
			for (SqlRow row : tables) {
				String left = row.getNodes().get(0).getAttr().get("logicName").toString();
				String right = row.getNodes().get(0).getAttr().get("name").toString();
				sheet.getRow(rowNumber).createCell(indent).setCellValue(left);
				sheet.getRow(rowNumber).createCell(indent + 24).setCellValue(right);
				rowNumber++;
				sheet.createRow(rowNumber);
			}
			indent--;
			sheet.getRow(rowNumber).createCell(indent).setCellValue("WHERE");
			sheet.getRow(rowNumber).createCell(indent + 24).setCellValue("WHERE");
			List<SqlRow> where = unit.getWhere();
			indent++;
			for (SqlRow row : where) {
				StringBuilder left = new StringBuilder();
				StringBuilder right = new StringBuilder();
				for (SqlNode node : row.getNodes()) {
					if (node.getAttr().get("type").toString().equals("keyword")) {
						if (node.isIndent()) {
							indent++;
						}
						left.append(node.getAttr().get("value").toString());
						right.append(node.getAttr().get("value").toString());
					} else if (node.getAttr().get("type").toString().equals("Field")) {
						if (node.isIndent()) {
							indent++;
						}
						left.append(node.getAttr().get("logicName").toString());
						right.append(node.getAttr().get("name").toString());
					} else if (node.getAttr().get("type").toString().equals("subsql")) {
						indent++;
						rowNumber = SubSql(sheet, rowNumber, indent, (SqlObject) node.getAttr().get("subsql"));
					}
				}
				rowNumber++;
				sheet.createRow(rowNumber);
				sheet.getRow(rowNumber).createCell(indent).setCellValue(left.toString());
				sheet.getRow(rowNumber).createCell(indent + 24).setCellValue(right.toString());
			}
			List<SqlRow> groups = unit.getGroup();
			if (groups.size() > 0) {
				indent = 1;
				StringBuilder left = new StringBuilder();
				StringBuilder right = new StringBuilder();
				sheet.getRow(rowNumber).createCell(indent).setCellValue("GROUP BY");
				sheet.getRow(rowNumber).createCell(indent + 24).setCellValue("GROUP BY");
				indent++;
				rowNumber++;
				sheet.createRow(rowNumber);
				count = 0;
				for (SqlRow row : groups) {
					if (count > 0) {
						left.append(",");
						right.append(",");
					}
					if (row.getNodes().get(0).getAttr().get("type").toString().equals("Field")) {
						left.append(row.getNodes().get(0).getAttr().get("logicName").toString());
						right.append(row.getNodes().get(0).getAttr().get("name").toString());
					} else {
						left.append(row.getNodes().get(0).getAttr().get("value").toString());
						right.append(row.getNodes().get(0).getAttr().get("value").toString());
					}
					count++;
				}
				sheet.getRow(rowNumber).createCell(indent).setCellValue(left.toString());
				sheet.getRow(rowNumber).createCell(indent + 24).setCellValue(right.toString());
			}

			List<SqlRow> orders = unit.getOrder();
			if (orders.size() > 0) {
				indent = 1;
				sheet.getRow(rowNumber).createCell(indent).setCellValue("ORDER BY ");
				sheet.getRow(rowNumber).createCell(indent + 24).setCellValue("ORDER BY");
				StringBuilder left = new StringBuilder();
				StringBuilder right = new StringBuilder();
				indent++;
				rowNumber++;
				sheet.createRow(rowNumber);
				count = 0;
				for (SqlRow row : orders) {
					if (count > 0) {
						left.append(",");
						right.append(",");
					}
					if (row.getNodes().get(0).getAttr().get("type").toString().equals("Field")) {
						left.append(row.getNodes().get(0).getAttr().get("logicName").toString());
						right.append(row.getNodes().get(0).getAttr().get("name").toString());
					} else {
						left.append(row.getNodes().get(0).getAttr().get("value").toString());
						right.append(row.getNodes().get(0).getAttr().get("value").toString());
					}
					count++;
				}
				sheet.getRow(rowNumber).createCell(indent).setCellValue(left.toString());
				sheet.getRow(rowNumber).createCell(indent + 24).setCellValue(right.toString());
				rowNumber++;
				sheet.createRow(rowNumber);
			}
			rowNumber++;
			sheet.createRow(rowNumber);
			sheet.getRow(rowNumber).createCell(indent).setCellValue("UNION");
			sheet.getRow(rowNumber).createCell(indent + 24).setCellValue("UNION");
		}
		sheet.removeRow(sheet.getRow(rowNumber));
		rowNumber--;
		return rowNumber;
	}
}
