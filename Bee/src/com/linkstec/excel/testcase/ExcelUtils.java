package com.linkstec.excel.testcase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BAssignExpression;
import com.linkstec.bee.core.fw.logic.BAssignment;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BExpression;
import com.linkstec.bee.core.fw.logic.BExpressionLine;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.fw.logic.BLogiker;
import com.linkstec.bee.core.fw.logic.BMethod;
import com.linkstec.excel.testcase.ExcelLogicProgress.UnitPath;

public class ExcelUtils {
	public static void createDataSet(Sheet sheet, ExcelLogicProgress p, String source, String target, ExcelStyles style,
			IUnit unit) {
		UnitPath path = p.getUnitPath();
		boolean continuos = false;
		if (path.getParent() != null) {
			if (path.getParent().isContinuous()) {
				path.setAssignNumber(path.getAssignNumber() + 1);
				// p.setRow(p.getRow() - 1);
				p.decreaseUnit();
				continuos = true;
			} else {
				path.setAssignNumber(1);

			}
		}

		// title
		if (!continuos) {
			Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
			ExcelUtils.setValue(row, p.getCol(), "以下のとおりに値を設定する");
			ExcelUtils.setValueForCase(row, target + "の設定");
			ExcelUtils.setValueForExpectedResult(row, target + "に" + source + "を設定する");
			ExcelUtils.makeBlankRow(sheet, p);
		} else {
			Row row = ExcelUtils.makeRowWithRowNum(sheet, p);
			ExcelUtils.setValueForCase(row, target + "の設定");
			ExcelUtils.setValueForExpectedResult(row, target + "に" + source + "を設定する");
		}
		path.setContinuous(true);

//		int start = p.getCol() + 2;
//		int end = p.getWidth() - 1;
//		int middle = start + (end - start) / 2;
//
//		if (!continuos) {
//			// header
//			p.setRow(p.getRow() + 1);
//			Row row = sheet.createRow(p.getRow());
//
//			// No
//			Cell noCell = row.createCell(p.getCol() + 1);
//			ExcelUtils.fillAndBorderRegion(new CellRangeAddress(p.getRow(), p.getRow(), p.getCol() + 1, p.getCol() + 1),
//					sheet, style);
//			noCell.setCellValue("No");
//
//			// target value
//			Cell tvCell = row.createCell(start);
//			ExcelUtils.fillAndBorderRegion(new CellRangeAddress(p.getRow(), p.getRow(), start, middle), sheet, style);
//			tvCell.setCellValue("設定項目");
//			// source value
//			Cell svCell = row.createCell(middle + 1);
//			ExcelUtils.fillAndBorderRegion(new CellRangeAddress(p.getRow(), p.getRow(), middle + 1, end), sheet, style);
//			svCell.setCellValue("設定値");
//
//			p.setRow(p.getRow() + 1);
//		}
//
//		// values
//
//		List<String> list = new ArrayList<String>();
//		ExcelUtils.makeReturnLine(source, list, (int) ((end - middle) * 3));
//		int firstRow = p.getRow();
//		for (int i = 0; i < list.size(); i++) {
//
//			System.out.println();
//			Row row = sheet.createRow(p.getRow());
//			// No
//			Cell noCell = row.createCell(p.getCol() + 1);
//			// target value
//			Cell tvCell = row.createCell(start);
//			// source value
//			Cell svCell = row.createCell(middle + 1);
//			if (i == 0) {
//				noCell.setCellValue(path.getAssignNumber());
//				tvCell.setCellValue(target);
//			}
//			if (list.size() - 1 != i) {
//				p.setRow(p.getRow() + 1);
//			}
//			svCell.setCellValue(list.get(i));
//		}
//		int lastRow = p.getRow();
//
//		ExcelUtils.borderRegion(new CellRangeAddress(firstRow, lastRow, p.getCol() + 1, p.getCol() + 1), sheet, style);
//		ExcelUtils.borderRegion(new CellRangeAddress(firstRow, lastRow, start, middle), sheet, style);
//		ExcelUtils.borderRegion(new CellRangeAddress(firstRow, lastRow, middle + 1, end), sheet, style);

	}

	private static void makeReturnLine(String s, List<String> list, int maxLength) {
		String[] ss = null;
		if (s.indexOf("\r\n") > 0) {
			ss = s.split("\r\n");
		}
		if (ss != null) {
			list = new ArrayList<String>();
			for (String a : ss) {
				list.add(a);
			}
		}
		int length = s.length();
		if (length > maxLength) {
			int returnMark = maxLength;
			String s1 = s.substring(0, returnMark);
			list.add(s1);
			String s2 = s.substring(returnMark);

			makeReturnLine(s2, list, maxLength);

		} else {
			list.add(s);
		}
	}

	public static String createValuable(BValuable value, ExcelLogicProgress p, boolean logical) {
		if (value == null) {
			return null;
		}

		if (value instanceof BVariable) {

			BVariable var = (BVariable) value;
			BValuable index = var.getArrayIndex();
			BValuable array = var.getArrayObject();
			if (index != null && array != null) {
				return ExcelUtils.createValuable(array, p, logical) + "[" + ExcelUtils.createValuable(array, p, logical)
						+ "]";
			}
			String name = var.getName();
			if (logical) {
				name = var.getLogicName();
			}
			if (name == null) {
				return "";
			}
			if (name.equals("")) {
				return "\"\"";
			}
			return name;
		} else if (value instanceof BExpression) {
			BExpression expression = (BExpression) value;
			return ExcelUtils.createExpression(expression, p, logical);
		} else if (value instanceof BInvoker) {
			return ExcelUtils.createInvoker(value, p, logical);
		} else if (value instanceof BAssign) {
			BAssign assign = (BAssign) value;

			BValuable obj;
			if (assign instanceof BAssignment) {
				BAssignment bas = (BAssignment) assign;
				obj = bas.getLeft();
			} else {
				BAssignExpression bas = (BAssignExpression) assign;
				obj = bas.getLeft();
			}
			BValuable right = assign.getRight();
			String sl = ExcelUtils.createValuable(obj, p, logical);
			String sr = ExcelUtils.createValuable(right, p, logical);

			return sl + "=" + sr;
		} else if (value instanceof BExpressionLine) {
			BExpressionLine line = (BExpressionLine) value;

			BValuable left = line.getTrue();
			BValuable right = line.getFalse();
			BValuable ex = line.getCondition();
			String se = ExcelUtils.createValuable(ex, p, logical);
			String sl = ExcelUtils.createValuable(left, p, logical);
			String sr = ExcelUtils.createValuable(right, p, logical);
			if (logical) {
				return "if " + se + " then " + sl + " else " + sr;
			} else {
				return se + "?" + sl + ":" + sr;
			}
		} else if (value instanceof BConstructor) {
			return ExcelUtils.createConstructor(value, p);
		} else if (value instanceof BMethod) {
			BMethod method = (BMethod) value;
			return method.getLogicName();

		} else {
			throw new RuntimeException("Not Valuable!");
		}
	}

	public static String createInvoker(BValuable value, ExcelLogicProgress p, boolean logical) {
		BInvoker invoker = (BInvoker) value;
		BValuable parent = invoker.getInvokeParent();
		BValuable child = invoker.getInvokeChild();
		if (p.isDoInvoker()) {
			if (parent.getBClass().isData() || child instanceof BVariable) {

				if (!(child instanceof BConstructor)) {
					String sp = ExcelUtils.createValuable(parent, p, logical);
					String s = ExcelUtils.createValuable(child, p, logical);
					return sp + "." + s;
				}
			}
			IUnit unit = (IUnit) value;
			return unit.getNumber().getString();

		} else {
			boolean widthParameter = true;
			String sp = ExcelUtils.createValuable(parent, p, logical);
			if (sp.equals("this")) {
				sp = "";
			} else {
				sp = sp + ".";
			}
			String s = ExcelUtils.createValuable(child, p, logical);
			List<BValuable> parameters = invoker.getParameters();
			if ((child instanceof BConstructor)) {
				s = "";
				sp = "new " + parent.getBClass().getLogicName();
			} else {
				if (parent.getBClass().isData() || child instanceof BVariable) {
					widthParameter = false;
				}
			}
			String parameter = "";
			if (widthParameter) {
				parameter = "(";
				for (BValuable para : parameters) {
					String paraValue = ExcelUtils.createValuable(para, p, logical);
					if (parameter.equals("(")) {
						parameter = parameter + paraValue;
					} else {
						parameter = parameter + "," + paraValue;
					}
				}
				parameter = parameter + ")";
			} else {
				// sp = "return value of " + sp;
			}
			return sp + s + parameter;
		}
	}

	public static String createConstructor(BValuable value, ExcelLogicProgress p) {
		IUnit unit = (IUnit) value;
		if (unit.getNumber() != null) {

			return unit.getNumber().getString();
		}

		BConstructor c = (BConstructor) value;
		List<BParameter> list = c.getParameter();
		String parameters = "";

		if (list != null && list.size() > 0) {
			parameters = " width parameter ";
			boolean first = true;
			for (BParameter var : list) {
				if (first) {
					first = false;
				} else {
					parameters = parameters + ",";
				}

				parameters = parameters + var.getLogicName();
			}
		}

		return " instance of " + c.getReturn().getBClass().getLogicName() + parameters;
	}

	private static String createExpression(BExpression expression, ExcelLogicProgress p, boolean logical) {
		BValuable left = expression.getExLeft();
		BValuable right = expression.getExRight();
		String sl = ExcelUtils.createValuable(left, p, logical);
		String sr = ExcelUtils.createValuable(right, p, logical);
		String s = expression.getExMiddle().toString();
		BLogiker middle = expression.getExMiddle();

		if (middle.getLogicName().equals(BLogiker.EQUAL.getLogicName())) {
			if (logical) {
				s = " == ";
			} else {
				s = " is ";
			}
		} else if (middle.getLogicName().equals(BLogiker.NOTQUEAL.getLogicName())) {
			s = " is not ";
			if (logical) {
				s = " != ";
			} else {
				s = " is not ";
			}
		} else if (middle.getLogicName().equals(BLogiker.LOGICAND.getLogicName())) {
			if (logical) {
				s = " and ";
			} else {
				s = " 且つ ";
			}
		} else if (middle.getLogicName().equals(BLogiker.LOGICOR.getLogicName())) {
			if (logical) {
				s = " or ";
			} else {
				s = " または ";
			}
		} else if (middle.getLogicName().equals(BLogiker.INSTANCEOF.getLogicName())) {
			s = "　instanceof ";
		}

		return sl + s + sr;
	}

	public static void makeGray(Sheet sheet, int startrow, int startcolumn, int endrow, int endcolumn,
			ExcelStyles style) {
		CellStyle sytle = style.getGray();

		for (int i = startrow; i <= endrow; i++) {
			Row row = ExcelUtils.getRow(sheet, i);
			for (int j = startcolumn; j <= endcolumn; j++) {
				ExcelUtils.getCell(row, j).setCellStyle(sytle);
			}
		}
	}

	public static void fillAndBorderRegion(CellRangeAddress region, Sheet sheet, ExcelStyles style) {

		XSSFCellStyle olive = style.getOlive();
		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				row = sheet.createRow(i);
			}

			for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
				Cell cell = ExcelUtils.getCell(row, j);
				XSSFCellStyle s = (XSSFCellStyle) olive.clone();

				if (i == region.getFirstRow()) {
					s.setBorderTop(BorderStyle.THIN);
				}
				if (i == region.getLastRow()) {
					s.setBorderBottom(BorderStyle.THIN);
				}
				if (j == region.getFirstColumn()) {
					s.setBorderLeft(BorderStyle.THIN);
				}
				if (j == region.getLastColumn()) {
					s.setBorderRight(BorderStyle.THIN);
				}

				cell.setCellStyle(s);
			}

		}

	}

	public static void borderCell(Cell c, ExcelStyles style) {
		XSSFCellStyle cs = style.getBorder();
		cs.setWrapText(true);
		c.setCellStyle(cs);
	}

	public static void borderAdnFillCell(Cell c, ExcelStyles style) {
		XSSFCellStyle cs = style.getBorderAndGray();
		c.setCellStyle(cs);
	}

	public static void borderRegion(CellRangeAddress region, Sheet sheet, ExcelStyles style) {

		RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
		RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);

		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
			Row r = ExcelUtils.getRow(sheet, i);

			XSSFCellStyle left = (XSSFCellStyle) style.getLeftBoldLinedOnly().clone();
			XSSFCellStyle right = (XSSFCellStyle) style.getRightBoldLinedOnly().clone();

			if (i == region.getFirstRow()) {
				left.setBorderTop(BorderStyle.THIN);
				right.setBorderTop(BorderStyle.THIN);

			}

			if (i == region.getLastRow()) {
				left.setBorderBottom(BorderStyle.THIN);
				right.setBorderBottom(BorderStyle.THIN);

			}

			Cell cell = ExcelUtils.getCell(r, region.getFirstColumn());
			if (region.getFirstColumn() == region.getLastColumn()) {
				left.setBorderRight(BorderStyle.THIN);
			}

			cell.setCellStyle(left);

			if (region.getFirstColumn() != region.getLastColumn()) {
				cell = ExcelUtils.getCell(r, region.getLastColumn());
				cell.setCellStyle(right);
			}

		}

	}

	public static void borderForRegionCells(CellRangeAddress region, Sheet sheet, ExcelStyles style) {
		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
			Row row = ExcelUtils.getRow(sheet, i);
			for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
				ExcelUtils.borderCell(ExcelUtils.getCell(row, j), style);
			}
		}
	}

	public static void borderWithCenterAlignmentForRegionCells(CellRangeAddress region, Sheet sheet,
			ExcelStyles style) {
		stylesForRegionCells(region, sheet, style.getCenterAlignmentStyle());
	}

	public static void initRowsStyleForRegionCells(CellRangeAddress region, Sheet sheet, ExcelStyles style) {
		stylesForRegionCells(region, sheet, style.getHeaderRowStyle());
	}

	public static void stylesForRegionCells(CellRangeAddress region, Sheet sheet, XSSFCellStyle style) {
		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
			Row row = ExcelUtils.getRow(sheet, i);
			for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
				ExcelUtils.getCell(row, j).setCellStyle(style);
			}
		}
	}

	public static void borderRegionBottom(CellRangeAddress region, Sheet sheet) {

		RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
	}

	public static void borderRegionAndMerge(CellRangeAddress region, Sheet sheet, HorizontalAlignment align,
			ExcelStyles style) {
		// XSSFCellStyle cs = (XSSFCellStyle) sheet.getWorkbook().createCellStyle();

		sheet.addMergedRegionUnsafe(region);

		XSSFCellStyle left = (XSSFCellStyle) style.getMiddleLined().clone();
		left.setBorderLeft(BorderStyle.THIN);
		left.setBorderRight(BorderStyle.THIN);
		left.setBorderTop(BorderStyle.THIN);
		left.setBorderBottom(BorderStyle.THIN);
		left.setAlignment(align);
		left.setVerticalAlignment(VerticalAlignment.CENTER);
		left.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		left.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());

		for (int i = region.getFirstColumn(); i <= region.getLastColumn(); i++) {
			for (int j = region.getFirstRow(); j <= region.getLastRow(); j++) {
				Row row = ExcelUtils.getRow(sheet, j);
				Cell cell = ExcelUtils.getCell(row, i);
				cell.setCellStyle(left);
			}
		}

	}

	public static List<BInvoker> getLinkers(IUnit unit) {

		return unit.getLinkers();
	}

	public static Row getRow(Sheet sheet, int p) {
		Row row = sheet.getRow(p);
		if (row == null) {
			row = sheet.createRow(p);
		}
		return row;
	}

	public static Cell getCell(Row row, int p) {
		Cell cell = row.getCell(p);
		if (cell == null) {
			cell = row.createCell(p);
		}

		return cell;
	}

	public static String getReturnStr() {
		return "\r\n";
	}

	public static void setValueForNo(Row row, int num) {
		ExcelUtils.setValue(row, CaseColumn.NO.getColumn(), String.valueOf(num));
	}

	public static void setValueForServiceId(Row row, String value) {
		ExcelUtils.setValue(row, CaseColumn.SERVICE_ID.getColumn(), value);
	}

	public static void setValueForBigCategory(Row row, String value) {
		ExcelUtils.setValue(row, CaseColumn.BIG_CATEGORY.getColumn(), value);
	}

	public static void setValueForMidCategory(Row row, String value) {
		ExcelUtils.setValue(row, CaseColumn.MID_CATEGORY.getColumn(), value);
	}

	public static void setValueForLitCategory(Row row, String value) {
		ExcelUtils.setValue(row, CaseColumn.LITLE_CATEGORY.getColumn(), value);
	}

	public static void setValueForCase(Row row, String value) {
		ExcelUtils.setValue(row, CaseColumn.CASE.getColumn(), value);
	}

	public static void setValueForExpectedResult(Row row, String value) {
		ExcelUtils.setValue(row, CaseColumn.EXPECTED_RESULT.getColumn(), value);
	}

	public static void setValueForNoSQL(Row row, int num) {
		ExcelUtils.setValue(row, CaseSQLColumn.NO.getColumn(), String.valueOf(num));
	}

	public static void setValueForBigCategorySQL(Row row, String value) {
		ExcelUtils.setValue(row, CaseSQLColumn.BIG_CATEGORY.getColumn(), value);
	}

	public static void setValueForMidCategorySQL(Row row, String value) {
		ExcelUtils.setValue(row, CaseSQLColumn.MID_CATEGORY.getColumn(), value);
	}

	public static void setValueForLitCategorySQL(Row row, String value) {
		ExcelUtils.setValue(row, CaseSQLColumn.LITLE_CATEGORY.getColumn(), value);
	}

	public static void setValueForCaseSQL(Row row, String value) {
		ExcelUtils.setValue(row, CaseSQLColumn.CASE.getColumn(), value);
	}

	public static void setValueForExpectedResultSQL(Row row, String value) {
		ExcelUtils.setValue(row, CaseSQLColumn.EXPECTED_RESULT.getColumn(), value);
	}

	/**
	 * create a new blank row for the specified sheet
	 * 
	 * @param sheet
	 * @param p
	 * @return
	 */
	public static Row makeBlankRow(Sheet sheet, ExcelLogicProgress p) {
		p.setRow(p.getRow() + 1);
		Row row = sheet.createRow(p.getRow());
		System.out.println(
				String.format("创建了新行-->depth column: %d, row: %d, rownum: %d", p.getCol(), p.getRow(), p.getRowNum()));
		return row;
	}

	/**
	 * create a row with row number on first column
	 * 
	 * @param sheet
	 * @param p
	 * @return
	 */
	public static Row makeRowWithRowNum(Sheet sheet, ExcelLogicProgress p) {
		Row row = makeBlankRow(sheet, p);
		setValueForNo(row, p.getRowNum());
		return row;
	}

	public static void setValue(Row row, int column, String value) {
		Cell c = ExcelUtils.getCell(row, column);
		if (c != null) {
			c.setCellValue(value);
		} else {
			System.err.println(String.format("The cell for row:%d, column:%d is null", row.getRowNum(), column));
		}
	}

	/**
	 * Sheet复制
	 * 
	 * @param sourceSheet
	 * @param toSheet
	 * @param copyValueFlag
	 */
	public static Sheet copySheet(Workbook wb, Sheet sourceSheet, int from, int end, String name, String append) {
		int tempIndex = wb.getSheetIndex(ExcelTemplate.TEMPLATE_NAME);
		Sheet targetSheet = wb.cloneSheet(tempIndex);
		int start = from;
		int count = 0;
		for (int i = from; i <= end; i++) {
			Row r = sourceSheet.getRow(i);
			if (r == null)
				continue;
			Cell noCell = ExcelUtils.getCell(r, CaseColumn.NO.getColumn());
			if (!isEmptyCell(noCell)) {
				if (mergeRow(sourceSheet, i))
					continue;
				addNullForCase(r);
				updateExpectedResult(r, append);
				Row target = targetSheet.createRow(start++);
				noCell.setCellValue(String.valueOf(++count));
				copyRow(wb, r, target);
			}
		}
		wb.setSheetName(wb.getSheetIndex(targetSheet), ExcelTemplate.TEMPLATE_NAME + "_" + name);
		return targetSheet;
	}

	/**
	 * 行复制功能
	 * 
	 * @param fromRow
	 * @param toRow
	 */
	private static void copyRow(Workbook wb, Row fromRow, Row toRow) {
		for (Iterator<Cell> cellIt = fromRow.cellIterator(); cellIt.hasNext();) {
			Cell tmpCell = cellIt.next();
			int columnIdx = tmpCell.getColumnIndex();
			Cell newCell = toRow.createCell(columnIdx);
			String str = tmpCell.getStringCellValue();
			newCell.setCellValue(str);
		}
	}

	private static void addNullForCase(Row row) {
		Cell caseCell = ExcelUtils.getCell(row, CaseColumn.CASE.getColumn());
		if (isEmptyCell(caseCell)) {
			ExcelUtils.setValueForCase(row, ExcelTemplate.NULL_STRING);
		}
	}

	private static void updateExpectedResult(Row row, String append) {
		Cell resultCell = ExcelUtils.getCell(row, CaseColumn.EXPECTED_RESULT.getColumn());
		String result = resultCell.getStringCellValue();
		Pattern ptn = Pattern.compile("テーブル名：([A-Z])(\\d{4})");
		Matcher m = ptn.matcher(result);
		if (m.find()) {
			String tbl1 = "", tbl2 = "", tblName = "";
			tbl1 = m.group(1);
			tbl2 = m.group(2);
			tblName = m.group();
			if (append != null) {
				tblName = tbl1 + "_" + append + tbl2;
			}
			String last = result.replaceAll("テーブル名：([A-Z])(\\d{4})", "テーブル名：" + tblName.toLowerCase());
			resultCell.setCellValue(last);
		}
	}

	/**
	 * merge big category with the below
	 * 
	 * @param sourceSheet
	 * @param idx
	 * @return
	 */
	private static boolean mergeRow(Sheet sourceSheet, int idx) {
		Row targetRow = sourceSheet.getRow(idx);
		Cell resCell = ExcelUtils.getCell(targetRow, CaseColumn.EXPECTED_RESULT.getColumn());
		Cell bigCell = ExcelUtils.getCell(targetRow, CaseColumn.BIG_CATEGORY.getColumn());
		if (isEmptyCell(resCell) && !isEmptyCell(bigCell)) {
			String big = bigCell.getStringCellValue();
			Row nextRow = sourceSheet.getRow(idx + 1);
			ExcelUtils.setValue(targetRow, CaseColumn.NO.getColumn(), "");
			ExcelUtils.setValue(targetRow, CaseColumn.BIG_CATEGORY.getColumn(), "");
			ExcelUtils.setValue(nextRow, CaseColumn.BIG_CATEGORY.getColumn(), big);
			return true;
		}
		return false;
	}

	private static boolean isEmptyString(String str) {
		if (str == null || str.trim().length() == 0)
			return true;
		return false;
	}

	public static boolean isEmptyCell(Cell c) {
		if (c == null)
			return true;
		return isEmptyString(c.getStringCellValue());
	}
}
