package com.linkstec.bee.core.codec.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.linkstec.bee.core.Debug;
import com.linkstec.bee.core.codec.excel.ExcelLogicProgress.UnitPath;
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

public class ExcelUtils {
	public static void createDataSet(Sheet sheet, ExcelLogicProgress p, String source, String target, ExcelStyles style, IUnit unit) {
		UnitPath path = p.getUnitPath();
		boolean continuos = false;
		if (path.getParent() != null) {
			if (path.getParent().isContinuous()) {
				path.setAssignNumber(path.getAssignNumber() + 1);
				p.setRow(p.getRow() - 1);
				p.decreaseUnit();
				continuos = true;
			} else {
				path.setAssignNumber(1);

			}
		}

		// title
		if (!continuos) {
			Row row = sheet.createRow(p.getRow());
			row.createCell(p.getCol()).setCellValue(((IUnit) unit).getNumber().toString() + "Sets values");
			p.setRow(p.getRow() + 1);
		}

		path.setContinuous(true);

		int start = p.getCol() + 2;
		int end = start + p.getWidth() - 2;
		int middle = start + (end - start) / 2;

		if (!continuos) {
			// header
			Row row = sheet.createRow(p.getRow());

			// No
			Cell noCell = row.createCell(p.getCol() + 1);
			ExcelUtils.fillAndBorderRegion(new CellRangeAddress(p.getRow(), p.getRow(), p.getCol() + 1, p.getCol() + 1), sheet, style);
			noCell.setCellValue("No");

			// target value
			Cell tvCell = row.createCell(start);
			ExcelUtils.fillAndBorderRegion(new CellRangeAddress(p.getRow(), p.getRow(), start, middle), sheet, style);
			tvCell.setCellValue("Target Value");
			// source value
			Cell svCell = row.createCell(middle + 1);
			ExcelUtils.fillAndBorderRegion(new CellRangeAddress(p.getRow(), p.getRow(), middle + 1, end), sheet, style);
			svCell.setCellValue("Source Value");

			p.setRow(p.getRow() + 1);
		}

		// values

		List<String> list = new ArrayList<String>();
		ExcelUtils.makeReturnLine(source, list, (int) ((end - middle) * 3));
		int firstRow = p.getRow();
		for (int i = 0; i < list.size(); i++) {
			Row row = sheet.createRow(p.getRow());
			// No
			Cell noCell = row.createCell(p.getCol() + 1);
			// target value
			Cell tvCell = row.createCell(start);
			// source value
			Cell svCell = row.createCell(middle + 1);
			if (i == 0) {
				noCell.setCellValue(path.getAssignNumber());
				tvCell.setCellValue(target);
			}
			if (list.size() - 1 != i) {
				p.setRow(p.getRow() + 1);
			}
			svCell.setCellValue(list.get(i));
		}
		int lastRow = p.getRow();

		ExcelUtils.borderRegion(new CellRangeAddress(firstRow, lastRow, p.getCol() + 1, p.getCol() + 1), sheet, style);
		ExcelUtils.borderRegion(new CellRangeAddress(firstRow, lastRow, start, middle), sheet, style);
		ExcelUtils.borderRegion(new CellRangeAddress(firstRow, lastRow, middle + 1, end), sheet, style);

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

	public static String createValuable(BValuable value, ExcelLogicProgress p) {
		if (value == null) {
			return null;
		}

		if (value instanceof BVariable) {

			BVariable var = (BVariable) value;
			BValuable index = var.getArrayIndex();
			BValuable array = var.getArrayObject();
			if (index != null && array != null) {
				return ExcelUtils.createValuable(array, p) + "[" + ExcelUtils.createValuable(array, p) + "]";
			}
			String name = var.getLogicName();

			// if (var.isClass()) {
			// name = var.getBClass().getLogicName();
			// }
			if (name == null) {
				return "";
			}
			if (name.equals("")) {
				return "\"\"";
			}
			return name;
		} else if (value instanceof BExpression) {
			BExpression expression = (BExpression) value;
			return ExcelUtils.createExpression(expression, p);
		} else if (value instanceof BInvoker) {
			return ExcelUtils.createInvoker(value, p);
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
			String sl = ExcelUtils.createValuable(obj, p);
			String sr = ExcelUtils.createValuable(right, p);
			return sl + "=" + sr;
		} else if (value instanceof BExpressionLine) {
			BExpressionLine line = (BExpressionLine) value;

			BValuable left = line.getTrue();
			BValuable right = line.getFalse();
			BValuable ex = line.getCondition();
			String se = ExcelUtils.createValuable(ex, p);
			String sl = ExcelUtils.createValuable(left, p);
			String sr = ExcelUtils.createValuable(right, p);
			return "if " + se + " then " + sl + " else " + sr;
		} else if (value instanceof BConstructor) {
			return ExcelUtils.createConstructor(value, p);
		} else if (value instanceof BMethod) {
			BMethod method = (BMethod) value;
			return method.getLogicName();

		} else {
			Debug.d();
			throw new RuntimeException("Not Valuable!");
		}
	}

	public static String createInvoker(BValuable value, ExcelLogicProgress p) {
		BInvoker invoker = (BInvoker) value;
		BValuable parent = invoker.getInvokeParent();
		BValuable child = invoker.getInvokeChild();
		if (p.isDoInvoker()) {
			if (parent.getBClass().isData() || child instanceof BVariable) {

				if (!(child instanceof BConstructor)) {
					String sp = ExcelUtils.createValuable(parent, p);
					String s = ExcelUtils.createValuable(child, p);
					return sp + "." + s;
				}
			}
			IUnit unit = (IUnit) value;
			return unit.getNumber().getString();

		} else {
			boolean widthParameter = true;
			String sp = ExcelUtils.createValuable(parent, p);
			if (sp.equals("this")) {
				sp = "";
			} else {
				sp = sp + ".";
			}
			String s = ExcelUtils.createValuable(child, p);
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
					String paraValue = ExcelUtils.createValuable(para, p);
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

	private static String createExpression(BExpression expression, ExcelLogicProgress p) {
		BValuable left = expression.getExLeft();
		BValuable right = expression.getExRight();
		String sl = ExcelUtils.createValuable(left, p);
		String sr = ExcelUtils.createValuable(right, p);
		String s = expression.getExMiddle().toString();
		BLogiker middle = expression.getExMiddle();

		if (middle.getLogicName().equals(BLogiker.EQUAL.getLogicName())) {
			s = " is ";
		} else if (middle.getLogicName().equals(BLogiker.NOTQUEAL.getLogicName())) {
			s = " is not ";
		} else if (middle.getLogicName().equals(BLogiker.LOGICAND.getLogicName())) {
			s = " and ";
		} else if (middle.getLogicName().equals(BLogiker.LOGICOR.getLogicName())) {
			s = " or ";
		} else if (middle.getLogicName().equals(BLogiker.INSTANCEOF.getLogicName())) {
			s = "'s type is ";
		}

		return sl + s + sr;
	}

	public static void makeGray(Sheet sheet, int startrow, int startcolumn, int endrow, int endcolumn, ExcelStyles style) {
		CellStyle sytle = style.getGray();

		for (int i = startrow; i <= endrow; i++) {
			Row row = ExcelUtils.getRow(sheet, i);
			for (int j = startcolumn; j <= endcolumn; j++) {
				ExcelUtils.getCell(row, j).setCellStyle(sytle);
			}
		}
	}

	public static void fillAndBorderRegion(CellRangeAddress region, Sheet sheet, ExcelStyles style) {
		XSSFCellStyle middle = style.getGrayAndMiddleBoldLined();

		XSSFCellStyle first = style.getGrayAndLeftBoldLined();

		XSSFCellStyle last = style.getGrayAndRightBoldLined();

		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				row = sheet.createRow(i);
			}

			for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
				Cell cell = ExcelUtils.getCell(row, j);
				if (j == region.getFirstColumn()) {
					cell.setCellStyle(first);
				} else if (j == region.getLastColumn()) {
					cell.setCellStyle(last);
				} else {
					cell.setCellStyle(middle);
				}
			}

		}

	}

	public static void borderCell(Cell c, ExcelStyles style) {
		XSSFCellStyle cs = style.getBorder();
		c.setCellStyle(cs);
	}

	public static void borderAdnFillCell(Cell c, ExcelStyles style) {
		XSSFCellStyle cs = style.getBorderAndGray();
		c.setCellStyle(cs);
	}

	public static void borderRegion(CellRangeAddress region, Sheet sheet, ExcelStyles style) {

		RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
		RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);

		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
			Row r = ExcelUtils.getRow(sheet, i);

			XSSFCellStyle left = (XSSFCellStyle) style.getLeftBoldLinedOnly().clone();
			XSSFCellStyle right = (XSSFCellStyle) style.getRightBoldLinedOnly().clone();

			if (i == region.getFirstRow()) {
				left.setBorderTop(BorderStyle.MEDIUM);
				right.setBorderTop(BorderStyle.MEDIUM);

			}

			if (i == region.getLastRow()) {
				left.setBorderBottom(BorderStyle.MEDIUM);
				right.setBorderBottom(BorderStyle.MEDIUM);

			}

			Cell cell = ExcelUtils.getCell(r, region.getFirstColumn());
			if (region.getFirstColumn() == region.getLastColumn()) {
				left.setBorderRight(BorderStyle.MEDIUM);
			}

			cell.setCellStyle(left);

			if (region.getFirstColumn() != region.getLastColumn()) {
				cell = ExcelUtils.getCell(r, region.getLastColumn());
				cell.setCellStyle(right);
			}
		}

	}

	public static void borderRegionBottom(CellRangeAddress region, Sheet sheet) {

		RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
	}

	public static void borderRegionAndMerge(CellRangeAddress region, Sheet sheet, HorizontalAlignment align, ExcelStyles style) {
		// XSSFCellStyle cs = (XSSFCellStyle) sheet.getWorkbook().createCellStyle();

		sheet.addMergedRegionUnsafe(region);

		XSSFCellStyle left = (XSSFCellStyle) style.getMiddleLined().clone();
		left.setBorderLeft(BorderStyle.THIN);
		left.setAlignment(align);

		XSSFCellStyle right = (XSSFCellStyle) style.getMiddleLined().clone();
		right.setAlignment(align);
		right.setBorderRight(BorderStyle.THIN);

		XSSFCellStyle middle = (XSSFCellStyle) style.getMiddleLined().clone();
		middle.setAlignment(align);

		Row row = ExcelUtils.getRow(sheet, region.getFirstRow());
		for (int i = region.getFirstColumn(); i <= region.getLastColumn(); i++) {
			Cell cell = ExcelUtils.getCell(row, i);
			if (i == region.getFirstColumn()) {
				cell.setCellStyle(left);
			} else if (i == region.getLastColumn()) {
				cell.setCellStyle(right);
			} else {
				cell.setCellStyle(middle);
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

}
