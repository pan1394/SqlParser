package com.linkstec.bee.core.codec.excel;

import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.IUnit;
import com.linkstec.bee.core.fw.logic.BConstructor;
import com.linkstec.bee.core.fw.logic.BMethod;

public class ExcelClassDesign {
	private int line = 8;
	private Sheet sheet;
	private ExcelStyles style;

	public ExcelClassDesign(Sheet sheet, BClass model, ExcelStyles style) {
		this.style = style;
		this.sheet = sheet;
		BValuable superClass = model.getSuperClass();
		List<BValuable> inters = model.getInterfaces();
		String name = model.getLogicName();
		String sc = "-";
		if (superClass != null) {
			sc = superClass.getBClass().getQualifiedName();
		}

		String ins = "-";
		if (inters.size() > 0) {
			ins = "";
			for (int i = 0; i < inters.size(); i++) {
				String s = inters.get(i).getBClass().getQualifiedName();
				if (i > 0) {
					ins = ins + "," + s;
				} else {
					ins = s;
				}

			}
		}

		sheet.getRow(3).getCell(7).setCellValue(name);
		sheet.getRow(4).getCell(7).setCellValue(sc);
		sheet.getRow(5).getCell(7).setCellValue(ins);
	}

	public Sheet getSheet() {
		return this.sheet;
	}

	public void createRows(BClass model) {

		Row row = ExcelUtils.getRow(sheet, line);
		ExcelUtils.getCell(row, 1).setCellValue(line - 7);

		String className = model.getLogicName();
		String name = "Others";
		if (className.toLowerCase().endsWith("validator")) {
			name = "Validator";
		} else if (className.toLowerCase().endsWith("dto")) {
			name = "Dto";
		} else if (className.toLowerCase().endsWith("logic")) {
			name = "Logic";
		} else if (className.toLowerCase().endsWith("dao")) {
			name = "Dao";
		} else if (className.toLowerCase().endsWith("controller")) {
			name = "Controller";
		} else if (className.toLowerCase().endsWith("commandform")) {
			name = "CommandForm";
		} else if (className.toLowerCase().endsWith("job")) {
			name = "Job";
		} else if (className.toLowerCase().endsWith("processor")) {
			name = "MessageProcessor";
		} else if (className.toLowerCase().endsWith("creator")) {
			name = "MessageCreator";
		} else if (className.toLowerCase().endsWith("spi")) {
			name = "SPI";
		} else if (className.toLowerCase().endsWith("calculator")) {
			name = "Calculator";
		} else if (className.toLowerCase().endsWith("utils")) {
			name = "Utility";
		}

		ExcelUtils.getCell(row, 3).setCellValue(name);
		ExcelUtils.getCell(row, 9).setCellValue(model.getPackage());
		ExcelUtils.getCell(row, 16).setCellValue(model.getLogicName());

	}

	public void makeMethods(List<ExcelMethod> methods) {

		for (ExcelMethod m : methods) {

			BMethod mthod = m.getMethod();

			Row row = this.sheet.getRow(line);
			if (row == null) {
				row = this.sheet.createRow(line);
			}

			CellRangeAddress range = new CellRangeAddress(line, line, 1, 2);
			ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.CENTER, style);
			ExcelUtils.getCell(row, 1).setCellValue(line - 7);

			range = new CellRangeAddress(line, line, 3, 8);
			ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT, style);

			range = new CellRangeAddress(line, line, 9, 15);
			ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT, style);

			range = new CellRangeAddress(line, line, 16, 22);
			ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT, style);

			range = new CellRangeAddress(line, line, 23, 29);
			ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT, style);

			range = new CellRangeAddress(line, line, 30, 37);
			ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT, style);

			range = new CellRangeAddress(line, line, 38, 42);
			ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT, style);

			range = new CellRangeAddress(line, line, 43, 47);
			ExcelUtils.borderRegionAndMerge(range, this.sheet, HorizontalAlignment.LEFT, style);

			Hyperlink link = sheet.getWorkbook().getCreationHelper().createHyperlink(HyperlinkType.DOCUMENT);
			link.setAddress("'" + m.getSheet().getSheetName() + "'!" + m.getAddress().formatAsString());
			link.setLabel(m.getSheet().getSheetName());

			if (mthod instanceof BConstructor) {
				ExcelUtils.getCell(row, 23).setCellValue("-");
				Cell cell = ExcelUtils.getCell(row, 30);
				cell.setCellValue("Constructor");
				// cell.setHyperlink(link);
			} else {
				ExcelUtils.getCell(row, 23).setCellValue(mthod.getLogicName());
				Cell cell = ExcelUtils.getCell(row, 30);
				cell.setCellValue("No." + ((IUnit) mthod).getNumber().getString());
				// cell.setHyperlink(link);
			}

			Cell cell = ExcelUtils.getCell(row, 38);
			cell.setCellValue(m.getSheet().getSheetName());
			cell.setHyperlink(link);

			String memo = "private";

			if (Modifier.isPublic(mthod.getModifier())) {
				memo = "public";
			}
			if (Modifier.isStatic(mthod.getModifier())) {
				memo = memo + " static";
			}
			if (mthod instanceof BConstructor) {
				memo = memo + " constructor";
			} else {
				memo = memo + " method";
			}

			ExcelUtils.getCell(row, 43).setCellValue(memo);
			line++;
		}
	}

	public int getRow() {
		return this.line;
	}

	public static class ExcelMethod {
		private BMethod method;
		private CellAddress address;
		private Sheet sheet;

		ExcelMethod(Sheet sheet) {
			this.sheet = sheet;
		}

		public Sheet getSheet() {
			return this.sheet;
		}

		public BMethod getMethod() {
			return method;
		}

		public void setMethod(BMethod method) {
			this.method = method;
		}

		public CellAddress getAddress() {
			return address;
		}

		public void setAddress(CellAddress address) {
			this.address = address;
		}

	}
}
